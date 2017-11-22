package com.chichizaza.shenmozhita

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.chichizaza.shenmozhita.solver.Piece

class PieceView(var piece: Piece, x: Float, y: Float, size: Float) {
    private val padding: Float = size * 0.05f

    var centerX: Float = x + size / 2f
    var centerY: Float = y + size / 2f
    var radius: Float = size / 2f - padding

    var liftOffset: Float = radius / 3

    var lift: Boolean = false

    fun draw(canvas: Canvas, paint: Paint) {
        when (piece.attr) {
            Piece.Crushed -> { return }
            Piece.Blue -> { paint.color = Color.parseColor("#52b7e1") }
            Piece.Red -> { paint.color = Color.parseColor("#d43515") }
            Piece.Green -> { paint.color = Color.parseColor("#1dca3c") }
            Piece.Light -> { paint.color = Color.parseColor("#fcdd37") }
            Piece.Dark -> { paint.color = Color.parseColor("#d540f4") }
            Piece.Heart -> { paint.color = Color.parseColor("#e473a9") }
        }

        if (lift) {
            canvas.drawCircle(centerX + liftOffset, centerY - liftOffset, radius, paint)

        } else {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }
}