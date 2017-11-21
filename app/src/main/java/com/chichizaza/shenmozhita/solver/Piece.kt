package com.chichizaza.shenmozhita.solver

class Piece(var attr: Int, var constraint: Constraint?) {
    companion object {
        val Blue = 1
        val Red = 2
        val Green = 3
        val Light = 4
        val Dark = 5
        var Heart = 6
    }
}

fun Int.p(constraint: Constraint? = null): Piece {
    return Piece(this, constraint)
}