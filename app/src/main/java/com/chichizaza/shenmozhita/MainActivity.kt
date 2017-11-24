package com.chichizaza.shenmozhita

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView

import com.chichizaza.shenmozhita.board.BoardView
import com.chichizaza.shenmozhita.solver.Board
import java.util.*

class MainActivity : AppCompatActivity() {

    private var dirty = false
    private lateinit var boardData: Array<IntArray>
    private lateinit var board: Board
    private lateinit var boardView: BoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById(R.id.boardView)
        val width = 6
        val height = 5
        board = Board.generateRandomBoard(width, height)
        boardData = board.copy().boardData
//        val board = Board(arrayOf(
//                intArrayOf(1, 6, 1, 2, 1, 2),
//                intArrayOf(2, 1, 3, 2, 3, 4),
//                intArrayOf(4, 6, 1, 1, 2, 5),
//                intArrayOf(5, 4, 3, 2, 4, 5),
//                intArrayOf(3, 3, 1, 2, 5, 2)))
        Log.d("roger_tag", "combo: ${board.evaluateCombo().size}, withDrop: ${board.evaluateComboWithDrop()}")
        //board.evaluateComboWithDrop()

        boardView.board = board
        boardView.gridSize = IntSize(width, height)

        val comboTextView: TextView = findViewById(R.id.comboTextView)
        boardView.comboListener = { combo ->
            dirty = true
            comboTextView.text = "${combo}C"
        }

        val solveBtn: Button = findViewById(R.id.solveButton)
        solveBtn.setOnClickListener {
            dirty = true
            val solution = board.copy().solve()
            Log.d("roger_tag", "solved: ${Arrays.toString(solution.toTypedArray())}")
            boardView.showSolution(solution)
        }
//                Board(arrayOf(
//                intArrayOf(1, 6, 1, 2, 1, 2),
//                intArrayOf(2, 1, 3, 2, 3, 4),
//                intArrayOf(4, 6, 1, 1, 2, 5),
//                intArrayOf(5, 4, 3, 2, 4, 5),
//                intArrayOf(3, 3, 1, 2, 5, 2)))

//        var data = arrayOf(
//                intArrayOf(1, 6, 1, 2, 1, 2),
//                intArrayOf(2, 1, 3, 2, 3, 4),
//                intArrayOf(4, 6, 1, 1, 2, 5),
//                intArrayOf(5, 4, 3, 2, 4, 5),
//                intArrayOf(3, 3, 1, 2, 5, 2))
//        var board = Board(data)
//        Board.logBoard(board)
//        var t = System.currentTimeMillis()
//        Log.d("roger_tag", Arrays.toString(board.solve().toTypedArray()))
//        t = System.currentTimeMillis() - t
//        Log.d("roger_tag", "time: " + t)
//        Board.logBoard(board)

//        var data = arrayOf(
//                intArrayOf(1, 1, 1, 2, 2, 2),
//                intArrayOf(2, 1, 3, 2, 4, 4),
//                intArrayOf(4, 1, 3, 1, 2, 1),
//                intArrayOf(5, 4, 3, 2, 2, 3),
//                intArrayOf(3, 3, 3, 2, 2, 2))
//        var board = Board(data)
//        Board.logBoard(board)
//        var result = board.evaluateCombo()
//
//        for (combo in result) {
//            val sb = StringBuilder()
//            for (pair in combo) {
//                sb.append(pair).append(" ")
//            }
//            Log.d("roger_tag", Arrays.toString(combo.toTypedArray()))
//        }
//
//        data = arrayOf(
//                intArrayOf(1, 1, 1, 2, 2, 1),
//                intArrayOf(2, 4, 1, 1, 1, 4),
//                intArrayOf(4, 2, 3, 3, 2, 1),
//                intArrayOf(5, 4, 3, 1, 2, 3),
//                intArrayOf(3, 5, 1, 2, 1, 2))
//        board = Board(data)
//        Board.logBoard(board)
//        result = board.evaluateCombo()
//
//        for (combo in result) {
//            val sb = StringBuilder()
//            for (pair in combo) {
//                sb.append(pair).append(" ")
//            }
//            Log.d("roger_tag", Arrays.toString(combo.toTypedArray()))
//        }
//
//        data = arrayOf(
//                intArrayOf(1, 1, 1, 2, 2, 1),
//                intArrayOf(2, 4, 1, 1, 3, 4),
//                intArrayOf(4, 2, 3, 1, 2, 1),
//                intArrayOf(5, 4, 3, 1, 2, 3),
//                intArrayOf(3, 5, 1, 2, 1, 2))
//        board = Board(data)
//        Board.logBoard(board)
//        result = board.evaluateCombo()
//
//        for (combo in result) {
//            val sb = StringBuilder()
//            for (pair in combo) {
//                sb.append(pair).append(" ")
//            }
//            Log.d("roger_tag", Arrays.toString(combo.toTypedArray()))
//        }
    }

    override fun onBackPressed() {
        if (dirty) {
            board.boardData = boardData.copy()
            boardView.showSolution = false
            boardView.initBoardView(board)
            dirty = false

        } else {
            super.onBackPressed()
        }
    }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }
}
