package com.frogsudoku.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogsudoku.app.domain.SudokuCell
import com.frogsudoku.app.ui.theme.*

@Composable
fun SudokuBoardView(
    board: List<List<SudokuCell>>,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedValue = selectedCell?.let { (r, c) -> board.getOrNull(r)?.getOrNull(c)?.value } ?: 0

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MintCream)
            .border(3.dp, DeepFrogGreen, RoundedCornerShape(16.dp))
            .drawWithContent {
                drawContent()
                val cellSize = size.width / 9f
                for (i in 1..8) {
                    val pos = i * cellSize
                    val isThick = i % 3 == 0
                    val strokeW = if (isThick) 5.5f else 1.8f
                    val col = if (isThick) DeepFrogGreen else SageGreen.copy(alpha = 0.65f)
                    drawLine(col, Offset(pos, 0f), Offset(pos, size.height), strokeW)
                    drawLine(col, Offset(0f, pos), Offset(size.width, pos), strokeW)
                }
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            for (r in 0 until 9) {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    for (c in 0 until 9) {
                        val cell = board[r][c]
                        val isSelected = selectedCell?.first == r && selectedCell.second == c
                        val isSameZone = selectedCell != null && (
                            selectedCell.first == r || selectedCell.second == c ||
                            (selectedCell.first / 3 == r / 3 && selectedCell.second / 3 == c / 3)
                        )
                        val isSameNum = selectedValue != 0 && cell.value == selectedValue

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                cell.isError -> ErrorCoral.copy(alpha = 0.35f)
                                isSelected -> SageGreen.copy(alpha = 0.70f)
                                isSameNum -> SoftMoss.copy(alpha = 0.85f)
                                isSameZone -> SoftMoss.copy(alpha = 0.38f)
                                else -> Color.Transparent
                            },
                            animationSpec = tween(150), label = ""
                        )

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().background(bgColor).clickable { onCellClick(r, c) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.value != 0) {
                                Text(
                                    text = cell.value.toString(),
                                    fontSize = 22.sp,
                                    fontWeight = if (cell.isInitial) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = when {
                                        cell.isError -> ErrorCoral
                                        cell.isInitial -> TextDarkMoss
                                        else -> DeepFrogGreen
                                    }
                                )
                            } else if (cell.notes.isNotEmpty()) {
                                Column(Modifier.fillMaxSize().padding(2.dp), Arrangement.SpaceEvenly) {
                                    for (nr in 0 until 3) {
                                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                                            for (nc in 0 until 3) {
                                                val n = nr * 3 + nc + 1
                                                Text(
                                                    text = if (cell.notes.contains(n)) n.toString() else " ",
                                                    fontSize = 8.sp, lineHeight = 9.sp,
                                                    color = DeepFrogGreen, textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
