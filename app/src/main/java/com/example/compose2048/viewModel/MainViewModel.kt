package com.example.compose2048.viewModel

import androidx.annotation.IntRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.max
import androidx.lifecycle.ViewModel
import com.example.compose2048.extension.*
import com.example.compose2048.models.*
import com.example.compose2048.repository.MainRepository
import com.google.android.material.math.MathUtils

const val GRID_SIZE = FOUR
private const val NUM_INITIAL_TILES = TWO
private val EMPTY_GRID = (ZERO until GRID_SIZE).map { arrayOfNulls<Tile?>(GRID_SIZE).toList() }

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var grid: List<List<Tile?>> = EMPTY_GRID
    var gridTileMovements by mutableStateOf<List<GridTileMovement>>(listOf())
        private set
    var currentScore by mutableStateOf(mainRepository.currentScore)
        private set
    var bestScore by mutableStateOf(mainRepository.bestScore)
        private set
    var isGameOver by mutableStateOf(false)
        private set
    var moveCount by mutableStateOf(0)
        private set

    init {
        val savedGrid = mainRepository.grid
        if (savedGrid == null) {
            startNewGame()
        } else {
            grid = savedGrid.map { tiles -> tiles.map { if (it == null) null else Tile(it) } }
            gridTileMovements = savedGrid.flatMapIndexed { row, tiles ->
                tiles.mapIndexed { col, it ->
                    if (it == null) null else GridTileMovement.noop(GridTile(Cell(row, col), Tile(it)))
                }
            }.filterNotNull()
            currentScore = mainRepository.currentScore
            bestScore = mainRepository.bestScore
            isGameOver = checkIsGameOver(this.grid)
        }
    }

    fun startNewGame() {
        gridTileMovements = (ZERO until NUM_INITIAL_TILES).mapNotNull { createRandomAddedTile(EMPTY_GRID) }
        val addedGridTiles = gridTileMovements.map { it.toGridTile }
        grid = EMPTY_GRID.map { row, col, _ -> addedGridTiles.find { row == it.cell.row && col == it.cell.col }?.tile }
        currentScore = ZERO
        isGameOver = false
        moveCount = ZERO
        mainRepository.saveState(grid, currentScore, bestScore)
    }

    fun move(direction: Direction) {
        var (updatedGrid, updatedGridTileMovements) = makeMove(grid, direction)

        if (!hasGridChanged(updatedGridTileMovements)) {
            return
        }

        val scoreIncrement = updatedGridTileMovements.filter { it.fromGridTile == null }.sumBy { it.toGridTile.tile.num }
        currentScore += scoreIncrement
        bestScore = max(bestScore, currentScore)

        updatedGridTileMovements = updatedGridTileMovements.toMutableList()
        val addedTileMovement = createRandomAddedTile(updatedGrid)
        if (addedTileMovement != null) {
            val (cell, tile) = addedTileMovement.toGridTile
            updatedGrid = updatedGrid.map { r, c, it -> if (cell.row == r && cell.col == c) tile else it }
            updatedGridTileMovements.add(addedTileMovement)
        }

        this.grid = updatedGrid
        this.gridTileMovements = updatedGridTileMovements.sortedWith { a, _ -> if (a.fromGridTile == null) ONE else -ONE }
        this.isGameOver = checkIsGameOver(grid)
        this.moveCount++
        this.mainRepository.saveState(this.grid, this.currentScore, this.bestScore)
    }
}

private fun createRandomAddedTile(grid: List<List<Tile?>>): GridTileMovement? {
    val emptyCells = grid.flatMapIndexed { row, tiles ->
        tiles.mapIndexed { col, it -> if (it == null) Cell(row, col) else null }.filterNotNull()
    }
    val emptyCell = emptyCells.getOrNull(emptyCells.indices.random()) ?: return null
    return GridTileMovement.add(GridTile(emptyCell, if (Math.random() < 0.9f) Tile(TWO) else Tile(FOUR)))
}

private fun makeMove(grid: List<List<Tile?>>, direction: Direction): Pair<List<List<Tile?>>, List<GridTileMovement>> {
    val numRotations = when (direction) {
        Direction.WEST -> ZERO
        Direction.SOUTH -> ONE
        Direction.EAST -> TWO
        Direction.NORTH -> THREE
    }

    var updatedGrid = grid.rotate(numRotations)

    val gridTileMovements = mutableListOf<GridTileMovement>()

    updatedGrid = updatedGrid.mapIndexed { currentRowIndex, _ ->
        val tiles = updatedGrid[currentRowIndex].toMutableList()
        var lastSeenTileIndex: Int? = null
        var lastSeenEmptyIndex: Int? = null
        for (currentColIndex in tiles.indices) {
            val currentTile = tiles[currentColIndex]
            if (currentTile == null) {
                if (lastSeenEmptyIndex == null) {
                    lastSeenEmptyIndex = currentColIndex
                }
                continue
            }

            val currentGridTile = GridTile(getRotatedCellAt(currentRowIndex, currentColIndex, numRotations), currentTile)

            if (lastSeenTileIndex == null) {
                if (lastSeenEmptyIndex == null) {
                    gridTileMovements.add(GridTileMovement.noop(currentGridTile))
                    lastSeenTileIndex = currentColIndex
                } else {
                    val targetCell = getRotatedCellAt(currentRowIndex, lastSeenEmptyIndex, numRotations)
                    val targetGridTile = GridTile(targetCell, currentTile)
                    gridTileMovements.add(GridTileMovement.shift(currentGridTile, targetGridTile))

                    tiles[lastSeenEmptyIndex] = currentTile
                    tiles[currentColIndex] = null
                    lastSeenTileIndex = lastSeenEmptyIndex
                    lastSeenEmptyIndex++
                }
            } else {
                if (tiles[lastSeenTileIndex]!!.num == currentTile.num) {
                    val targetCell = getRotatedCellAt(currentRowIndex, lastSeenTileIndex, numRotations)
                    gridTileMovements.add(GridTileMovement.shift(currentGridTile, GridTile(targetCell, currentTile)))

                    val addedTile = currentTile * TWO
                    gridTileMovements.add(GridTileMovement.add(GridTile(targetCell, addedTile)))

                    tiles[lastSeenTileIndex] = addedTile
                    tiles[currentColIndex] = null
                    lastSeenTileIndex = null
                    if (lastSeenEmptyIndex == null) {
                        lastSeenEmptyIndex = currentColIndex
                    }
                } else {
                    if (lastSeenEmptyIndex == null) {
                        gridTileMovements.add(GridTileMovement.noop(currentGridTile))
                    } else {
                        val targetCell = getRotatedCellAt(currentRowIndex, lastSeenEmptyIndex, numRotations)
                        val targetGridTile = GridTile(targetCell, currentTile)
                        gridTileMovements.add(GridTileMovement.shift(currentGridTile, targetGridTile))

                        tiles[lastSeenEmptyIndex] = currentTile
                        tiles[currentColIndex] = null
                        lastSeenEmptyIndex++
                    }
                    lastSeenTileIndex++
                }
            }
        }
        tiles
    }

    updatedGrid = updatedGrid.rotate(MathUtils.floorMod(-numRotations, GRID_SIZE))

    return Pair(updatedGrid, gridTileMovements)
}

private fun <T> List<List<T>>.rotate(@IntRange(from = 0, to = 3) numRotations: Int): List<List<T>> {
    return map { row, col, _ ->
        val (rotatedRow, rotatedCol) = getRotatedCellAt(row, col, numRotations)
        this[rotatedRow][rotatedCol]
    }
}

private fun getRotatedCellAt(row: Int, col: Int, @IntRange(from = 0, to = 3) numRotations: Int): Cell {
    return when (numRotations) {
        ZERO -> Cell(row, col)
        ONE -> Cell(GRID_SIZE - ONE - col, row)
        TWO -> Cell(GRID_SIZE - ONE - row, GRID_SIZE - ONE - col)
        THREE -> Cell(col, GRID_SIZE - ONE - row)
        else -> throw IllegalArgumentException("numRotations must be an integer in [0,3]")
    }
}

private fun <T> List<List<T>>.map(transform: (row: Int, col: Int, T) -> T): List<List<T>> {
    return mapIndexed { row, rowTiles -> rowTiles.mapIndexed { col, it -> transform(row, col, it) } }
}

private fun checkIsGameOver(grid: List<List<Tile?>>): Boolean {
    return Direction.values().none { hasGridChanged(makeMove(grid, it).second) }
}

private fun hasGridChanged(gridTileMovements: List<GridTileMovement>): Boolean {
    return gridTileMovements.any {
        val (fromTile, toTile) = it
        fromTile == null || fromTile.cell != toTile.cell
    }
}