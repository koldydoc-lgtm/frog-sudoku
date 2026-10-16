package com.frogsudoku.app.domain

import kotlin.random.Random

object SudokuGenerator {
    fun generate(difficulty: Difficulty, seed: Long = System.currentTimeMillis()): List<List<SudokuCell>> {
        val random = Random(seed)
        val solutionBoard = Array(9) { IntArray(9) }

        for (i in 0 until 9 step 3) fillBox(solutionBoard, i, i, random)
        solveBoard(solutionBoard, random)

        val puzzleBoard = Array(9) { r -> IntArray(9) { c -> solutionBoard[r][c] } }
        val totalToRemove = 81 - difficulty.visibleCells
        var removed = 0

        for (pos in (0 until 81).shuffled(random)) {
            if (removed >= totalToRemove) break
            val r = pos / 9
            val c = pos % 9
            val backup = puzzleBoard[r][c]
            puzzleBoard[r][c] = 0

            val copyForCheck = Array(9) { row -> puzzleBoard[row].clone() }
            if (countSolutions(copyForCheck, 2) != 1) {
                puzzleBoard[r][c] = backup
            } else {
                removed++
            }
        }

        return List(9) { r ->
            List(9) { c ->
                val v = puzzleBoard[r][c]
                SudokuCell(r, c, v, solutionBoard[r][c], v != 0)
            }
        }
    }

    private fun fillBox(board: Array<IntArray>, rStart: Int, cStart: Int, random: Random) {
        val nums = (1..9).shuffled(random)
        var idx = 0
        for (i in 0 until 3) for (j in 0 until 3) board[rStart + i][cStart + j] = nums[idx++]
    }

    private fun isSafe(board: Array<IntArray>, r: Int, c: Int, num: Int): Boolean {
        for (x in 0 until 9) if (board[r][x] == num || board[x][c] == num) return false
        val sr = r - r % 3
        val sc = c - c % 3
        for (i in 0 until 3) for (j in 0 until 3) if (board[i + sr][j + sc] == num) return false
        return true
    }

    private fun solveBoard(board: Array<IntArray>, random: Random): Boolean {
        for (r in 0 until 9) for (c in 0 until 9) {
            if (board[r][c] == 0) {
                for (num in (1..9).shuffled(random)) {
                    if (isSafe(board, r, c, num)) {
                        board[r][c] = num
                        if (solveBoard(board, random)) return true
                        board[r][c] = 0
                    }
                }
                return false
            }
        }
        return true
    }

    private fun countSolutions(board: Array<IntArray>, limit: Int): Int {
        for (r in 0 until 9) for (c in 0 until 9) {
            if (board[r][c] == 0) {
                var count = 0
                for (num in 1..9) {
                    if (isSafe(board, r, c, num)) {
                        board[r][c] = num
                        count += countSolutions(board, limit - count)
                        board[r][c] = 0
                        if (count >= limit) return count
                    }
                }
                return count
            }
        }
        return 1
    }
}
