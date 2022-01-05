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
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
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
                title = { Text("Teste") },
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
                        .aspectRatio(1f)
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
                    text = "Score",
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
                    text = "Best",
                    modifier = Modifier.layoutId("bestScoreLabel"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
    if (isGameOver) {
        Dialog(
            title = "Game over",
            message = "Start a new game?",
            onConfirmListener = { onNewGameRequested.invoke() },
            onDismissListener = {},
        )
    } else if (shouldShowNewGameDialog) {
        Dialog(
            title = "Start a new game?",
            message = "Starting a new game will erase your current game",
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


private fun buildConstraints(isPortrait: Boolean): ConstraintSet {
    return ConstraintSet {
        val gameGrid = createRefFor("gameGrid")
        val currentScoreText = createRefFor("currentScoreText")
        val currentScoreLabel = createRefFor("currentScoreLabel")
        val bestScoreText = createRefFor("bestScoreText")
        val bestScoreLabel = createRefFor("bestScoreLabel")

        if (isPortrait) {
            constrain(gameGrid) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }
            constrain(currentScoreText) {
                start.linkTo(gameGrid.start, 16.dp)
                top.linkTo(gameGrid.bottom)
            }
            constrain(currentScoreLabel) {
                start.linkTo(currentScoreText.start)
                top.linkTo(currentScoreText.bottom)
            }
            constrain(bestScoreText) {
                end.linkTo(gameGrid.end, 16.dp)
                top.linkTo(gameGrid.bottom)
            }
            constrain(bestScoreLabel) {
                end.linkTo(bestScoreText.end)
                top.linkTo(bestScoreText.bottom)
            }
        } else {
            constrain(gameGrid) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(currentScoreText) {
                start.linkTo(currentScoreLabel.start)
                bottom.linkTo(currentScoreLabel.top)
            }
            constrain(currentScoreLabel) {
                start.linkTo(bestScoreText.start)
                bottom.linkTo(bestScoreText.top)
            }
            constrain(bestScoreText) {
                start.linkTo(bestScoreLabel.start)
                bottom.linkTo(bestScoreLabel.top)
            }
            constrain(bestScoreLabel) {
                start.linkTo(gameGrid.end)
                bottom.linkTo(gameGrid.bottom, 16.dp)
            }
            createHorizontalChain(gameGrid, bestScoreLabel, chainStyle = ChainStyle.Packed)
        }
    }
}

private fun atan3(x: Float, y: Float): Float {
    var degrees = Math.toDegrees(kotlin.math.atan2(y, x).toDouble()).toFloat()
    if (degrees < 0) {
        degrees += 360
    }
    return degrees
}