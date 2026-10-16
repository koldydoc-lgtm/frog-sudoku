package com.frogsudoku.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogsudoku.app.domain.Difficulty
import com.frogsudoku.app.ui.components.FrogLifeView
import com.frogsudoku.app.ui.theme.*

@Composable
fun MenuScreen(onStartSinglePlayer: (Difficulty) -> Unit, onOpenMultiplayerLobby: () -> Unit) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }

    Column(
        modifier = Modifier.fillMaxSize().background(WarmBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(10.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                FrogLifeView(true, 54.dp)
                FrogLifeView(true, 68.dp)
                FrogLifeView(true, 54.dp)
            }
            Spacer(Modifier.height(14.dp))
            Text("Судоку с жабками", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = DeepFrogGreen)
            Text("Пастельный жабий уют", fontSize = 15.sp, color = TextDarkMoss.copy(alpha = 0.7f))
        }

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MintCream),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Уровень сложности", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepFrogGreen)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(175.dp)
                ) {
                    items(Difficulty.entries) { diff ->
                        FilterChip(
                            selected = diff == selectedDifficulty,
                            onClick = { selectedDifficulty = diff },
                            label = {
                                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(diff.title, fontWeight = FontWeight.Bold)
                                    Text("${diff.visibleCells} кл.", fontSize = 11.sp)
                                }
                            }
                        )
                    }
                }
            }
        }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onStartSinglePlayer(selectedDifficulty) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Играть (${selectedDifficulty.title})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onOpenMultiplayerLobby,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Дуэль 1 на 1 (По коду комнаты)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
