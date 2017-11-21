package com.chichizaza.shenmozhita

open class Size<T>(var width: T, var height: T) {
    override fun toString(): String {
        return "(h: $height, w: $width)"
    }

    fun set(width: T, height: T) {
        this.width = width
        this.height = height
    }
}

class IntSize(width: Int, height: Int) : Size<Int>(width, height)
class FloatSize(width: Float, height: Float) : Size<Float>(width, height)