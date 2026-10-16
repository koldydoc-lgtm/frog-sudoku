package com.frogsudoku.app.data

import com.frogsudoku.app.domain.Difficulty
import com.frogsudoku.app.domain.OpponentState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

data class RoomData(
    val roomCode: String = "",
    val seed: Long = 0L,
    val difficultyName: String = Difficulty.MEDIUM.name
)

class MultiplayerRepository {
    private val _opponentState = MutableStateFlow(OpponentState())
    val opponentState: StateFlow<OpponentState> = _opponentState
    private var simJob: Job? = null

    private fun codeToDeterministicSeed(code: String): Long {
        var h = 1125899906842597L
        for (ch in code) h = 31 * h + ch.code
        return h
    }

    fun createRoom(difficulty: Difficulty, onRoomReady: (RoomData) -> Unit) {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val code = (1..6).map { chars.random() }.joinToString("")
        val seed = codeToDeterministicSeed(code)
        startOpponentSync()
        onRoomReady(RoomData(code, seed, difficulty.name))
    }

    fun joinRoom(code: String, onSuccess: (RoomData) -> Unit) {
        val clean = code.trim().uppercase()
        val seed = codeToDeterministicSeed(clean)
        startOpponentSync()
        onSuccess(RoomData(clean, seed, Difficulty.MEDIUM.name))
    }

    private fun startOpponentSync() {
        simJob?.cancel()
        simJob = CoroutineScope(Dispatchers.IO).launch {
            delay(1800)
            var prog = 0
            var oppLives = 3
            _opponentState.value = OpponentState(true, 0, 3, false)
            while (prog < 100 && oppLives > 0) {
                delay(Random.nextLong(3500, 6500))
                if (Random.nextFloat() < 0.12f) {
                    oppLives = (oppLives - 1).coerceAtLeast(0)
                } else {
                    prog = (prog + Random.nextInt(4, 10)).coerceAtMost(100)
                }
                _opponentState.value = OpponentState(true, prog, oppLives, prog >= 100)
            }
        }
    }

    fun updateMyState(progressPercent: Int, lives: Int, isFinished: Boolean) {}

    fun leaveRoom() {
        simJob?.cancel()
        _opponentState.value = OpponentState()
    }
}
