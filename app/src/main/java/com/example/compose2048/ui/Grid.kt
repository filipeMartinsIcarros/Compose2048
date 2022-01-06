package com.example.compose2048.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose2048.extension.*
import com.example.compose2048.models.GridTileMovement
import com.example.compose2048.theme.AppTheme
import kotlin.math.min

private val GRID_TILE_RADIUS = 4.dp

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    gridTileMovements: List<GridTileMovement>,
    moveCount: Int,
) {
    BoxWithConstraints(modifier) {
        val width = with(LocalDensity.current) { maxWidth.toPx() }
        val height = with(LocalDensity.current) { maxHeight.toPx() }
        val tileMarginPx = with(LocalDensity.current) { 4.dp.toPx() }
        val tileSizePx =
            ((min(width, height) - tileMarginPx * (GRID_SIZE - ONE)) / GRID_SIZE).coerceAtLeast(0f)
        val tileSizeDp = Dp(tileSizePx / LocalDensity.current.density)
        val tileOffsetPx = tileSizePx + tileMarginPx
        val emptyTileColor = getEmptyTileColor()
        Box(
            modifier = Modifier.drawBehind {
                // Draw the background empty tiles.
                for (row in ZERO until GRID_SIZE) {
                    for (col in ZERO until GRID_SIZE) {
                        drawRoundRect(
                            color = emptyTileColor,
                            topLeft = Offset(col * tileOffsetPx, row * tileOffsetPx),
                            size = Size(tileSizePx, tileSizePx),
                            cornerRadius = CornerRadius(GRID_TILE_RADIUS.toPx()),
                        )
                    }
                }
            }
        ) {
            for (gridTileMovement in gridTileMovements) {
                val (fromGridTile, toGridTile) = gridTileMovement
                val fromScale = if (fromGridTile == null) ZERO_FLOAT else ONE_FLOAT
                val toOffset =
                    Offset(toGridTile.cell.col * tileOffsetPx, toGridTile.cell.row * tileOffsetPx)
                val fromOffset = fromGridTile?.let {
                    Offset(
                        it.cell.col * tileOffsetPx,
                        it.cell.row * tileOffsetPx
                    )
                } ?: toOffset

                key(toGridTile.tile.id) {
                    GridTileText(
                        num = toGridTile.tile.num,
                        size = tileSizeDp,
                        fromScale = fromScale,
                        fromOffset = fromOffset,
                        toOffset = toOffset,
                        moveCount = moveCount,
                    )
                }
            }
        }
    }
}

@Composable
private fun GridTileText(
    num: Int,
    size: Dp,
    fromScale: Float,
    fromOffset: Offset,
    toOffset: Offset,
    moveCount: Int,
) {
    val animatedScale = remember { Animatable(fromScale) }
    val animatedOffset = remember { Animatable(fromOffset, Offset.VectorConverter) }
    Text(
        text = "$num",
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                scaleX = animatedScale.value,
                scaleY = animatedScale.value,
                translationX = animatedOffset.value.x,
                translationY = animatedOffset.value.y,
            )
            .background(
                color = getTileColor(num),
                shape = RoundedCornerShape(GRID_TILE_RADIUS),
            )
            .wrapContentSize(),
        color = Color.White,
        fontSize = 18.sp,
    )
    LaunchedEffect(moveCount) {
        animatedScale.snapTo(if (moveCount == ZERO) ONE_FLOAT else fromScale)
        animatedScale.animateTo(
            ONE_FLOAT, tween(
                durationMillis = DURATION_MILLIS_200, delayMillis = DEFAULT_DELAY_MILLIS
            )
        )
        animatedOffset.animateTo(toOffset, tween(durationMillis = DURATION_MILLIS_100))
    }
}

@Composable
private fun getTileColor(num: Int): Color {
    return when (num) {
        TWO -> AppTheme.color.color2
        FOUR -> AppTheme.color.color4
        EIGHT -> AppTheme.color.color8
        SIXTEEN -> AppTheme.color.color16
        THIRTY_TWO -> AppTheme.color.color32
        SIXTY_FOUR -> AppTheme.color.color64
        ONE_HUNDRED_TWENTY_EIGHT -> AppTheme.color.color128
        TWO_HUNDRED_FIFTY_SIX -> AppTheme.color.color256
        FIVE_HUNDRED_AND_TWELVE -> AppTheme.color.color512
        THOUSAND_AND_TWENTY_FOUR -> AppTheme.color.color1024
        TWO_THOUSAND_FORTY_EIGHT -> AppTheme.color.color2048
        FOUR_THOUSAND_NINETY_SIX -> AppTheme.color.color4096
        EIGHT_THOUSAND_AND_NINETY_TWO -> AppTheme.color.color8192
        SIXTEEN_THOUSAND_THREE_HUNDRED_AND_EIGHTY_FOUR -> AppTheme.color.color16384
        else -> Color.Black
    }
}

@Composable
private fun getEmptyTileColor(): Color {
    return AppTheme.color.colorEmptyTitle
}