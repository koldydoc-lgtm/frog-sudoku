package com.frogsudoku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frogsudoku.app.presentation.GameViewModel
import com.frogsudoku.app.ui.screens.GameScreen
import com.frogsudoku.app.ui.screens.LobbyScreen
import com.frogsudoku.app.ui.screens.MenuScreen
import com.frogsudoku.app.ui.theme.FrogSudokuTheme

private enum class NavScreen { MENU, LOBBY, GAME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogSudokuTheme {
                val vm: GameViewModel = viewModel()
                val state by vm.uiState.collectAsState()
                val opp by vm.opponentState.collectAsState()
                var screen by remember { mutableStateOf(NavScreen.MENU) }

                when (screen) {
                    NavScreen.MENU -> MenuScreen(
                        onStartSinglePlayer = { vm.startSinglePlayer(it); screen = NavScreen.GAME },
                        onOpenMultiplayerLobby = { screen = NavScreen.LOBBY }
                    )
                    NavScreen.LOBBY -> LobbyScreen(
                        onCreateRoom = { vm.createMultiplayerRoom(it) { screen = NavScreen.GAME } },
                        onJoinRoom = { vm.joinMultiplayerRoom(it) { screen = NavScreen.GAME } },
                        onBack = { screen = NavScreen.MENU }
                    )
                    NavScreen.GAME -> GameScreen(
                        uiState = state, opponentState = opp,
                        onCellClick = vm::selectCell, onNumberClick = vm::onNumberInput,
                        onEraseClick = vm::eraseSelectedCell, onUndoClick = vm::undo,
                        onTogglePencil = vm::togglePencilMode, onTogglePause = vm::togglePause,
                        onRestart = { vm.startSinglePlayer(state.difficulty) },
                        onExitToMenu = { vm.leaveGame(); screen = NavScreen.MENU }
                    )
                }
            }
        }
    }
}
