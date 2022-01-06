package com.example.compose2048.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.compose2048.extension.ONE_FLOAT
import com.example.compose2048.extension.atan3
import com.example.compose2048.extension.buildConstraints
import com.example.compose2048.models.Direction
import com.example.compose2048.models.GridTileMovement

@Composable
fun MainUi(
    gridTileMovements: List<GridTileMovement>,
    currentScore: Int,
    bestScore: Int,
    moveCount: Int,
    isGameOver: Boolean,
    onNewGameRequested: () -> Unit,
    onSwipeListener: (direction: Direction) -> Unit,
) {
   var totalDragDistance: Offset = Offset.Zero
    var shouldShowNewGameDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jogo 2048") },
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.primaryVariant,
                actions = {
                    IconButton(onClick = { shouldShowNewGameDialog = true }) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            )
        }
    ) {
        BoxWithConstraints {
            val isPortrait = maxWidth < maxHeight
            ConstraintLayout(
                constraintSet = buildConstraints(isPortrait),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragStart = {
                                totalDragDistance = Offset.Zero
                            },
                            onDragEnd = {
                                val swipeAngle = atan3(totalDragDistance.x, -totalDragDistance.y)
                                onSwipeListener(
                                    when {
                                        45 <= swipeAngle && swipeAngle < 135 -> Direction.NORTH
                                        135 <= swipeAngle && swipeAngle < 225 -> Direction.WEST
                                        225 <= swipeAngle && swipeAngle < 315 -> Direction.SOUTH
                                        else -> Direction.EAST
                                    }
                                )
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                totalDragDistance += dragAmount
                            }
                        )
                    },
            ) {
                Grid(
                    modifier = Modifier
                        .aspectRatio(ONE_FLOAT)
                        .padding(16.dp)
                        .layoutId("gameGrid"),
                    gridTileMovements = gridTileMovements,
                    moveCount = moveCount,
                )
                Text(
                    text = "$currentScore",
                    modifier = Modifier.layoutId("currentScoreText"),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = "Pontos",
                    modifier = Modifier.layoutId("currentScoreLabel"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = "$bestScore",
                    modifier = Modifier.layoutId("bestScoreText"),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = "Melhor Pontuação",
                    modifier = Modifier.layoutId("bestScoreLabel"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
    if (isGameOver) {
        Dialog(
            title = "Fim de Jogo",
            message = "Começar um novo jogo?",
            onConfirmListener = { onNewGameRequested.invoke() },
            onDismissListener = {},
        )
    } else if (shouldShowNewGameDialog) {
        Dialog(
            title = "Começar um novo jogo?",
            message = "Começar um novo jogo apagará seu jogo atual",
            onConfirmListener = {
                onNewGameRequested.invoke()
                shouldShowNewGameDialog = false
            },
            onDismissListener = {
                shouldShowNewGameDialog = false
            },
        )
    }
}