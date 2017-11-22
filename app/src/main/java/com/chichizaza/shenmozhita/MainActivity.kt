package com.chichizaza.shenmozhita

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.chichizaza.shenmozhita.R.id.boardView
import com.chichizaza.shenmozhita.solver.Board

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boardView: BoardView = findViewById(boardView)
        boardView.board = Board.generateRandomBoard(6, 5)
        boardView.gridSize = IntSize(6, 5)
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
}
