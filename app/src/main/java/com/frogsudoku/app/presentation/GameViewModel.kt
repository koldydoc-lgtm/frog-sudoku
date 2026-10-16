package com.frogsudoku.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frogsudoku.app.data.MultiplayerRepository
import com.frogsudoku.app.data.RoomData
import com.frogsudoku.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class GameUiState(
    val board: List<List<SudokuCell>> = emptyList(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val selectedCell: Pair<Int, Int>? = null,
    val lives: Int = 3,
    val isPencilMode: Boolean = false,
    val elapsedSeconds: Int = 0,
    val status: GameStatus = GameStatus.PLAYING,
    val isMultiplayer: Boolean = false,
    val roomCode: String = "",
    val myProgressPercent: Int = 0,
    val canUndo: Boolean = false,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null
)

class GameViewModel : ViewModel() {
    private val multiplayerRepo = MultiplayerRepository()
    val opponentState = multiplayerRepo.opponentState

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val historyStack = ArrayDeque<List<List<SudokuCell>>>()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            opponentState.collect { opp ->
                val s = _uiState.value
                if (s.isMultiplayer && s.status == GameStatus.PLAYING && opp.connected) {
                    if (opp.lives <= 0) {
                        _uiState.value = s.copy(status = GameStatus.WON)
                        timerJob?.cancel()
                    } else if (opp.isFinished || opp.progressPercent >= 100) {
                        _uiState.value = s.copy(status = GameStatus.LOST)
                        timerJob?.cancel()
                    }
                }
            }
        }
    }

    fun startSinglePlayer(difficulty: Difficulty) {
        multiplayerRepo.leaveRoom()
        generateAndStart(difficulty, System.currentTimeMillis(), false, "")
    }

    fun createMultiplayerRoom(difficulty: Difficulty, onReady: () -> Unit) {
        multiplayerRepo.createRoom(difficulty) { room ->
            startFromRoom(room, onReady)
        }
    }

    fun joinMultiplayerRoom(code: String, onReady: () -> Unit) {
        multiplayerRepo.joinRoom(code) { room ->
            startFromRoom(room, onReady)
        }
    }

    private fun startFromRoom(room: RoomData, onReady: () -> Unit) {
        val diff = runCatching { Difficulty.valueOf(room.difficultyName) }.getOrDefault(Difficulty.MEDIUM)
        generateAndStart(diff, room.seed, true, room.roomCode, onReady)
    }

    private fun generateAndStart(
        difficulty: Difficulty, seed: Long, isMulti: Boolean, code: String, onDone: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            val board = withContext(Dispatchers.Default) { SudokuGenerator.generate(difficulty, seed) }
            historyStack.clear()
            _uiState.value = GameUiState(
                board = board, difficulty = difficulty, lives = 3,
                isMultiplayer = isMulti, roomCode = code,
                myProgressPercent = calcProgress(board)
            )
            startTimer()
            onDone?.invoke()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.status == GameStatus.PLAYING) {
                    _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
                }
            }
        }
    }

    fun selectCell(r: Int, c: Int) {
        if (_uiState.value.status == GameStatus.PLAYING) _uiState.value = _uiState.value.copy(selectedCell = r to c)
    }

    fun togglePencilMode() {
        _uiState.value = _uiState.value.copy(isPencilMode = !_uiState.value.isPencilMode)
    }

    fun togglePause() {
        val s = _uiState.value.status
        if (s == GameStatus.PLAYING) _uiState.value = _uiState.value.copy(status = GameStatus.PAUSED)
        else if (s == GameStatus.PAUSED) _uiState.value = _uiState.value.copy(status = GameStatus.PLAYING)
    }

    fun onNumberInput(num: Int) {
        val state = _uiState.value
        if (state.status != GameStatus.PLAYING) return
        val (r, c) = state.selectedCell ?: return
        val cell = state.board[r][c]
        if (cell.isInitial || (cell.value == cell.solution && cell.value != 0)) return

        if (historyStack.size >= 30) historyStack.removeFirst()
        historyStack.addLast(state.board)

        if (state.isPencilMode) {
            val notes = if (cell.notes.contains(num)) cell.notes - num else cell.notes + num
            val nextBoard = updateCell(state.board, r, c) { it.copy(value = 0, isError = false, notes = notes) }
            _uiState.value = state.copy(board = nextBoard, canUndo = true)
        } else {
            val ok = num == cell.solution
            val lives = if (ok) state.lives else (state.lives - 1).coerceAtLeast(0)
            val nextBoard = updateCell(state.board, r, c) { it.copy(value = num, isError = !ok, notes = emptySet()) }
            val solved = nextBoard.flatten().all { it.value == it.solution }
            val status = when {
                lives <= 0 -> GameStatus.LOST
                solved -> GameStatus.WON
                else -> GameStatus.PLAYING
            }
            if (status != GameStatus.PLAYING) timerJob?.cancel()
            val prog = calcProgress(nextBoard)
            _uiState.value = state.copy(board = nextBoard, lives = lives, status = status, myProgressPercent = prog, canUndo = true)
            if (state.isMultiplayer) multiplayerRepo.updateMyState(prog, lives, status == GameStatus.WON)
        }
    }

    fun eraseSelectedCell() {
        val state = _uiState.value
        val (r, c) = state.selectedCell ?: return
        val cell = state.board[r][c]
        if (cell.isInitial || (cell.value == cell.solution && cell.value != 0)) return
        historyStack.addLast(state.board)
        val nextBoard = updateCell(state.board, r, c) { it.copy(value = 0, isError = false, notes = emptySet()) }
        _uiState.value = state.copy(board = nextBoard, canUndo = true)
    }

    fun undo() {
        if (historyStack.isEmpty()) return
        val prev = historyStack.removeLast()
        _uiState.value = _uiState.value.copy(board = prev, canUndo = historyStack.isNotEmpty())
    }

    private fun updateCell(b: List<List<SudokuCell>>, r: Int, c: Int, fn: (SudokuCell) -> SudokuCell) =
        b.mapIndexed { ri, row -> row.mapIndexed { ci, cell -> if (ri == r && ci == c) fn(cell) else cell } }

    private fun calcProgress(b: List<List<SudokuCell>>) =
        ((b.flatten().count { it.value != 0 && it.value == it.solution } / 81f) * 100).toInt()

    fun leaveGame() {
        timerJob?.cancel()
        multiplayerRepo.leaveRoom()
    }
}
