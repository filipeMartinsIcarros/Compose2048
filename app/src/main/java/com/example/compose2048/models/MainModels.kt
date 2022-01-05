package com.example.compose2048.models

data class Cell(val row: Int, val col: Int)

enum class Direction { NORTH, SOUTH, EAST, WEST }

data class GridTile(val cell: Cell, val tile: Tile)

data class Tile constructor(val num: Int, val id: Int) {
    companion object {
        private var tileIdCounter = 0
    }

    constructor(num: Int) : this(num, tileIdCounter++)

    operator fun times(operand: Int): Tile = Tile(num * operand)
}

data class GridTileMovement(val fromGridTile: GridTile?, val toGridTile: GridTile) {
    companion object {
        fun add(gridTile: GridTile): GridTileMovement {
            return GridTileMovement(null, gridTile)
        }
        fun shift(fromGridTile: GridTile, toGridTile: GridTile): GridTileMovement {
            return GridTileMovement(fromGridTile, toGridTile)
        }
        fun noop(gridTile: GridTile): GridTileMovement {
            return GridTileMovement(gridTile, gridTile)
        }
    }
}