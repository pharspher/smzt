package com.chichizaza.shenmozhita

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.chichizaza.shenmozhita.solver.Piece

class PieceView(private val piece: Piece, x: Float, y: Float, size: Float) {
    private val padding: Float = size * 0.05f

    var centerX: Float = x + size / 2f
    var centerY: Float = y + size / 2f
    var radius: Float = size / 2f - padding

    var liftOffset: Float = radius / 3

    var lift: Boolean = false

    fun draw(canvas: Canvas, paint: Paint) {
        when (piece.attr) {
            Piece.Blue -> { paint.color = Color.BLUE }
            Piece.Red -> { paint.color = Color.RED }
            Piece.Green -> { paint.color = Color.GREEN }
            Piece.Light -> { paint.color = Color.YELLOW }
            Piece.Dark -> { paint.color = Color.MAGENTA }
            Piece.Heart -> { paint.color = Color.parseColor("#FFC0CB") }
        }

        if (lift) {
            canvas.drawCircle(centerX + liftOffset, centerY - liftOffset, radius, paint)

        } else {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }
}