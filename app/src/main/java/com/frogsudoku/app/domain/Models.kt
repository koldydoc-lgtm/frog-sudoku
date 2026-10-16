package com.frogsudoku.app.domain

enum class Difficulty(val title: String, val visibleCells: Int) {
    NOVICE("Новичок", 52),
    EASY("Легкий", 46),
    MEDIUM("Средний", 38),
    HARD("Сложный", 30),
    EXPERT("Эксперт", 25),
    MASTER("Мастер", 20)
}

data class SudokuCell(
    val row: Int,
    val col: Int,
    val value: Int = 0,
    val solution: Int = 0,
    val isInitial: Boolean = false,
    val isError: Boolean = false,
    val notes: Set<Int> = emptySet()
)

data class OpponentState(
    val connected: Boolean = false,
    val progressPercent: Int = 0,
    val lives: Int = 3,
    val isFinished: Boolean = false
)

enum class GameStatus { PLAYING, PAUSED, WON, LOST }
