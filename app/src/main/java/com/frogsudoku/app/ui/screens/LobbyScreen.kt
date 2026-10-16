package com.frogsudoku.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun LobbyScreen(
    onCreateRoom: (Difficulty) -> Unit,
    onJoinRoom: (String) -> Unit,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(WarmBackground).padding(20.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = DeepFrogGreen) }
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                FrogLifeView(true, 50.dp)
                Text("VS", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DeepFrogGreen)
                FrogLifeView(true, 50.dp)
            }
            Text("Дуэль 1 на 1", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = DeepFrogGreen)

            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MintCream), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Создать комнату", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onCreateRoom(Difficulty.MEDIUM) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Сгенерировать 6-значный код")
                    }
                }
            }

            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MintCream), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Войти по коду комнаты", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = code,
                        onValueChange = { if (it.length <= 6) code = it.uppercase() },
                        label = { Text("Код (одинаковый seed у обоих)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onJoinRoom(code) }, enabled = code.length >= 4, modifier = Modifier.fillMaxWidth()) {
                        Text("Войти в комнату")
                    }
                }
            }
        }
    }
}
