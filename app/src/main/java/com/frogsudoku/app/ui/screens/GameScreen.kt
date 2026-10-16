package com.frogsudoku.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogsudoku.app.domain.GameStatus
import com.frogsudoku.app.domain.OpponentState
import com.frogsudoku.app.presentation.GameUiState
import com.frogsudoku.app.ui.components.FrogLifeView
import com.frogsudoku.app.ui.components.SudokuBoardView
import com.frogsudoku.app.ui.theme.*

@Composable
fun GameScreen(
    uiState: GameUiState,
    opponentState: OpponentState,
    onCellClick: (Int, Int) -> Unit,
    onNumberClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    onUndoClick: () -> Unit,
    onTogglePencil: () -> Unit,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit,
    onExitToMenu: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(WarmBackground).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = onExitToMenu) { Icon(Icons.Default.ArrowBack, null, tint = DeepFrogGreen) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (uiState.isMultiplayer) "КОД: ${uiState.roomCode}" else uiState.difficulty.title,
                    fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DeepFrogGreen
                )
                Text("%02d:%02d".format(uiState.elapsedSeconds / 60, uiState.elapsedSeconds % 60), fontSize = 14.sp)
            }
            IconButton(onClick = onTogglePause) {
                Icon(if (uiState.status == GameStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause, null, tint = DeepFrogGreen)
            }
        }

        if (uiState.isMultiplayer) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MintCream), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Вы: ${uiState.myProgressPercent}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepFrogGreen)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Соперник: ${opponentState.progressPercent}%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            for (i in 1..3) FrogLifeView(i <= opponentState.lives, 18.dp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { opponentState.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = SageGreen, trackColor = PaperBackground
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            for (i in 1..3) FrogLifeView(i <= uiState.lives, 46.dp)
        }

        Box(Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
            if (uiState.status == GameStatus.PAUSED) {
                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MintCream)) {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        FrogLifeView(true, 70.dp)
                        Spacer(Modifier.height(12.dp))
                        Text("Пауза (поле скрыто)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onTogglePause) { Text("Продолжить") }
                    }
                }
            } else if (uiState.board.isNotEmpty()) {
                SudokuBoardView(uiState.board, uiState.selectedCell, onCellClick)
            }
        }

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
            AssistChip(onClick = onUndoClick, enabled = uiState.canUndo, label = { Text("Отмена") }, leadingIcon = { Icon(Icons.Default.Undo, null) })
            AssistChip(onClick = onEraseClick, label = { Text("Стереть") }, leadingIcon = { Icon(Icons.Default.Backspace, null) })
            FilterChip(
                selected = uiState.isPencilMode, onClick = onTogglePencil,
                label = { Text(if (uiState.isPencilMode) "Заметки: ВКЛ" else "Заметки") },
                leadingIcon = { Icon(Icons.Default.Edit, null) }
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (n in 1..9) {
                Button(
                    onClick = { onNumberClick(n) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isPencilMode) SoftMoss else DeepFrogGreen,
                        contentColor = if (uiState.isPencilMode) TextDarkMoss else MintCream
                    )
                ) {
                    Text(n.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (uiState.status == GameStatus.WON || uiState.status == GameStatus.LOST) {
        val win = uiState.status == GameStatus.WON
        AlertDialog(
            onDismissRequest = {},
            containerColor = MintCream,
            icon = { FrogLifeView(win, 60.dp) },
            title = { Text(if (win) "Ква-вау! Победа!" else "Игра окончена", fontWeight = FontWeight.ExtraBold) },
            text = { Text(if (win) "Судоку решено идеально!" else "Все 3 жабки потеряли жизни.") },
            confirmButton = { Button(onClick = onRestart) { Text("Заново") } },
            dismissButton = { TextButton(onClick = onExitToMenu) { Text("В меню") } }
        )
    }
}
