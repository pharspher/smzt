package com.chichizaza.shenmozhita.solver

import android.graphics.Point
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class Board(var boardData: Array<IntArray>) {

    var tmpBoardData = boardData.copy()

    companion object {
        private val TAG = Board::class.java.simpleName

        private val defaultBoardWidth = 6
        private val defaultBoardHeight = 5

        fun generateRandomBoard(width: Int, height: Int): Board {
            val boardData = Array(height) { IntArray(width) }
            val random = Random()
            for (i in 0 until height) {
                for (j in 0 until width) {
                    boardData[i][j] = 1 + random.nextInt(6)
                }
            }
            return Board(boardData)
        }

        fun logBoard(board: Board) {
            Log.d(TAG, "--------------")
            for (i in 0 until board.boardHeight) {
                val builder = StringBuilder()
                for (j in 0 until board.boardWidth) {
                    if (board.boardData[i][j] == Piece.Crushed) {
                        builder.append(". ")
                    } else {
                        builder.append(board.boardData[i][j]).append(" ")
                    }
                }
                Log.d(TAG, builder.toString())
            }
        }
    }

    var boardHeight = boardData.size
    var boardWidth = boardData[0].size

    fun copy() :Board {
        return Board(boardData.copy())
    }

    fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    private fun markCombo(boardData: Array<IntArray>): MutableList<MutableSet<Pair<Int, Int>>> {
        val combos: MutableList<MutableSet<Pair<Int, Int>>> = ArrayList()
        for (row in 0 until boardHeight) {
            val rowArray = boardData[row]
            var i = 0
            var j = i + 1
            while (i < boardWidth) {
                if (rowArray[i] == Piece.Crushed) {
                    i++
                    continue
                }
                if (j < boardWidth && rowArray[i] == rowArray[j]) {
                    j++
                } else {
                    if ((j - 1) - i + 1 >= 3) {
                        //combos.add((i until j).set { Pair(row, it) }
                        combos.add((i until j).map { Pair(row, it) }.toMutableSet())
                    }
                    i = j
                    j = i + 1
                    if (j >= boardWidth) {
                        break
                    }
                }
            }
        }

        for (col in 0 until boardWidth) {
            val colArray = (0 until boardHeight).map { boardData[it][col] }
            var i = 0
            var j = i + 1
            while (i < boardHeight) {
                if (colArray[i] == Piece.Crushed) {
                    i++
                    continue
                }

                if (j < boardHeight && colArray[i] == colArray[j]) {
                    j++
                } else {
                    if ((j - 1) - i + 1 >= 3) {
                        combos.add((i until j).map { Pair(it, col) }.toMutableSet())
                    }
                    i = j
                    j = i + 1
                }
            }
        }

//        for (combo in combos) {
//            val sb = StringBuilder()
//            for (pair in combo) {
//                sb.append(pair).append(" ")
//            }
//            Log.d("roger_tag", sb.toString())
//        }

        return combos
    }

    fun evaluateCombo(): MutableList<MutableSet<Pair<Int, Int>>> {
        val result: MutableList<MutableSet<Pair<Int, Int>>> = ArrayList()

        val combos = markCombo(boardData)
        for (i in 0 until combos.size - 1) {
            var merged = false
            for (j in i + 1 until combos.size) {
                if (canBeMerged(combos[i], combos[j])) {
                    combos[j].addAll(combos[i])
                    merged = true
                    break
                }
            }
            if (!merged) {
                result.add(combos[i])
            }
        }
        if (combos.size > 0) {
            result.add(combos[combos.size - 1])
        }
        return result
    }

    fun evaluateComboWithDrop(): Int {
        for (i in 0 until boardData.size) {
            for (j in 0 until boardData[i].size) {
                tmpBoardData[i][j] = boardData[i][j]
            }
        }

        var combos: List<Set<Pair<Int, Int>>> = ArrayList()
        var comboCount = 0
        var fallCount = 0

        while (true) {
            combos = evaluateCombo()
            if (combos.isEmpty()) {
                break
            }

            comboCount += combos.size
            fallCount++
            crushPositions(combos.flatMap { it.map { Point(it.second, it.first) } })
        }

//        while (!crushPositions(combos.flatMap { it.map { Point(it.second, it.first) } }).isEmpty()) {
//            combos = evaluateCombo()
//            comboCount += combos.size
//            fallCount++
//            Log.d("roger_tag", "$fallCount: " + comboCount)
//        }

        for (i in 0 until boardData.size) {
            for (j in 0 until boardData[i].size) {
                boardData[i][j] = tmpBoardData[i][j]
            }
        }
        return comboCount
    }

    fun crushPositions(positionList: List<Point>): List<Pair<Point, Point>> {
        val dropSteps: ArrayList<Pair<Point, Point>> = ArrayList()
        for (pos in positionList) {
            boardData[pos.y][pos.x] = Piece.Crushed
        }

        for (col in 0 until boardWidth) {
            var write = boardHeight - 1
            for (row in boardHeight - 1 downTo 0) {
                if (boardData[row][col] != Piece.Crushed) {
                    dropSteps.add(Pair(Point(col, row), Point(col, write)))
                    boardData[write][col] = boardData[row][col]
                    write--
                }
            }
            for (row in write downTo 0) {
                boardData[row][col] = Piece.Crushed
            }
        }
        return dropSteps
    }

    private fun canBeMerged(set1: MutableSet<Pair<Int, Int>>, set2: MutableSet<Pair<Int, Int>>): Boolean {
        for (entry in set1) {
            for (entry2 in set2) {
                val vDist = Math.abs(entry.first - entry2.first)
                val hDist = Math.abs(entry.second - entry2.second)
                if (vDist + hDist <= 1 &&
                        boardData[entry.first][entry.second] == boardData[entry2.first][entry2.second]) {
                    return true
                }
            }
        }
        return false
    }

    fun solve(): List<Pair<Int, Int>> {
        val steps: MutableList<Pair<Int, Int>> = ArrayList()
        for (i in 0 until boardHeight) {
            for (j in 0 until boardWidth) {
                Log.d("roger_tag", "try ($i, $j)")
                if (solve(Pair(i, j), -1, steps, 0, 16)) {
                    steps.add(0, Pair(i, j))
//                    Log.d("roger_tag", "solved: ${Arrays.toString(steps.toTypedArray())}")
//                    Log.d("roger_tag", "merged: ${Arrays.toString(mergeSteps(steps).toTypedArray())}")
                    return mergeSteps(steps)
                }
            }
        }
        return mergeSteps(steps)
    }

    private fun mergeSteps(list: MutableList<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val result: LinkedList<Pair<Int, Int>> = LinkedList()
        if (list.size < 2) {
            return list
        }

        var offset = 0

        var first = list[0]
        var second = list[1]
        result.offer(first)
        result.offer(second)
        for (i in 2 until list.size) {
            if (first.first == second.first && second.first == list[i].first) {
                result.pollLast()
                result.offer(list[i])
            } else if (first.second == second.second && second.second == list[i].second) {
                result.pollLast()
                result.offer(list[i])
            } else {
//                var isH = second.first == first.first
//                val last = result.pollLast()
//                if (isH) {
//                    result.offer(Pair(last.first, last.second + offset))
//                    result.offer(Pair(list[i].first, list[i].second + offset))
//                } else {
//                    result.offer(Pair(last.first + offset, last.second))
//                    result.offer(Pair(list[i].first + offset, list[i].second))
//                }
                result.offer(list[i])
                var isH = second.first == first.first
                if (isH) {
                    Log.d("roger_tag", "change to vertical")
                } else {
                    Log.d("roger_tag", "change to horizontal")
                }
                //offset += 1
            }
            first = second
            second = list[i]
        }

        return result
    }

    // left 0, top = 1, right = 2, bottom = 3
    private fun solve(start: Pair<Int, Int>, avoidDirection: Int, steps: MutableList<Pair<Int, Int>>, step: Int, stepLimit: Int = 40): Boolean {
        if (step > stepLimit) {
            return false
        }

        if (evaluateComboWithDrop() >= 6) {
            return true
        }

        if (start.second > 0 && avoidDirection != 0) {
            val newPos = Pair(start.first, start.second - 1)
            swap(start, newPos)
            if (solve(newPos, 2, steps, step + 1, stepLimit)) {
                steps.add(0, newPos)
                return true
            } else {
                swap(start, newPos)
            }
        }

        if (start.second < boardWidth - 1 && avoidDirection != 2) {
            val newPos = Pair(start.first, start.second + 1)
            swap(start, newPos)
            if (solve(newPos, 0, steps, step + 1, stepLimit)) {
                steps.add(0, newPos)
                return true
            } else {
                swap(start, newPos)
            }
        }

        if (start.first > 0 && avoidDirection != 1) {
            val newPos = Pair(start.first - 1, start.second)
            swap(start, newPos)
            if (solve(newPos, 3, steps, step + 1, stepLimit)) {
                steps.add(0, newPos)
                return true
            } else {
                swap(start, newPos)
            }
        }

        if (start.first < boardHeight - 1 && avoidDirection != 3) {
            val newPos = Pair(start.first + 1, start.second)
            swap(start, newPos)
            if (solve(newPos, 1, steps, step + 1, stepLimit)) {
                steps.add(0, newPos)
                return true
            } else {
                swap(start, newPos)
            }
        }

        return false
    }

    private fun swap(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
        val tmp = boardData[p1.first][p1.second]
        boardData[p1.first][p1.second] = boardData[p2.first][p2.second]
        boardData[p2.first][p2.second] = tmp
    }
}