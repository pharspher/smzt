package com.chichizaza.shenmozhita

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.chichizaza.shenmozhita.solver.Board
import com.chichizaza.shenmozhita.solver.Piece
import com.chichizaza.shenmozhita.solver.p

class BoardView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var gridSize = IntSize(6, 5)
        set(value) {
            gridSize.width = value.width
            gridSize.height = value.height
        }

    var board: Board? = null

    private var viewSize: FloatSize = FloatSize(0f, 0f)
    private var pieceSize: Float = 0f
    private var pieceViewList: MutableList<PieceView> = ArrayList()

    private var lifted: PieceView? = null
    private var liftedGrid: Pair<Int, Int>? = null
    private var liftOffset: Pair<Int, Int> = Pair(0, 0)

    private val paint: Paint by lazy { Paint() }

    init {
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        board?.let {
            initBoardView(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth: Float = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        pieceSize = (viewWidth / gridSize.width.toFloat())
        val viewHeight: Float = pieceSize * gridSize.height

        viewSize.set(viewWidth, viewHeight)
        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            var lifted: PieceView? = null
            for (pieceView in pieceViewList) {
                if (pieceView.lift) {
                    lifted = pieceView

                } else {
                    pieceView.draw(it, paint)
                }
            }
            lifted?.draw(it, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (lifted == null) {
                    val grid = regressToGrid(event.x, event.y)
                    liftedGrid = grid
                    lifted = pieceViewList[grid.first * gridSize.width + grid.second]
                    lifted?.let {
                        it.lift = true
                        liftOffset = Pair((it.centerX - event.x).toInt(), (it.centerY - event.y).toInt())
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                lifted?.let {
                    it.centerX = event.x + liftOffset.first
                    it.centerY = event.y + liftOffset.second

                    val grid = regressToGrid(event.x, event.y)
                    liftedGrid?.let {
                        if (grid.first != it.first || grid.second != it.second) {
                            Log.d("roger_tag", "current: $grid, lifted: $it")
                            swapWithAnimation(it, grid) {

                            }
                            liftedGrid = grid
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                lifted?.let {
                    val grid = regressToGrid(event.x, event.y)
                    val gridCenter = gridCenter(grid)
                    it.centerX = gridCenter.first
                    it.centerY = gridCenter.second
                }
                lifted?.lift = false
                lifted = null
                liftedGrid = null
            }
        }

        invalidate()
        return true
    }

    private fun regressToGrid(x: Float, y: Float): Pair<Int, Int> {
        return Pair((y / pieceSize).toInt(), (x / pieceSize).toInt())
    }

    private fun initBoardView(board: Board) {
        val data = board.boardData
        for (row in 0 until board.boardHeight) {
            for (col in 0 until board.boardWidth) {
                val piece: Piece = data[row][col].p()
                pieceViewList.add(PieceView(piece, pieceSize * col, pieceSize * row, pieceSize))
            }
        }
        invalidate()
    }

    private fun swapWithAnimation(swapper: Pair<Int, Int>, swappee: Pair<Int, Int>, callback: () -> Unit) {
        val swappeePiece = pieceView(swappee)
        val swappeeCenter = gridCenter(swappee)
        val liftedCenter = gridCenter(swapper)
        val animatorX = ValueAnimator.ofFloat(swappeeCenter.first, liftedCenter.first)
        val animatorY = ValueAnimator.ofFloat(swappeeCenter.second, liftedCenter.second)
        animatorX.addUpdateListener { animatedValue ->
            val value: Float = animatedValue.animatedValue as Float
            swappeePiece.centerX = value
            invalidate()
        }
        animatorY.addUpdateListener { animatedValue ->
            val value: Float = animatedValue.animatedValue as Float
            swappeePiece.centerY = value
            invalidate()
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorY)
        animatorSet.duration = 100
        animatorSet.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                swap(swapper, swappee)
                callback()
            }
        })
        animatorSet.start()
    }

    private fun swap(swapper: Pair<Int, Int>, swappee: Pair<Int, Int>) {
        val swapperIdx = swapper.first * gridSize.width + swapper.second
        val swappeeIdx = swappee.first * gridSize.width + swappee.second
        val tmp = pieceViewList[swapperIdx]
        pieceViewList[swapperIdx] = pieceViewList[swappeeIdx]
        pieceViewList[swappeeIdx] = tmp
    }

    private fun pieceView(grid: Pair<Int, Int>): PieceView {
        return pieceViewList[grid.first * gridSize.width + grid.second]
    }

    private fun gridCenter(grid: Pair<Int, Int>): Pair<Float, Float> {
        return Pair(pieceSize * grid.second + pieceSize / 2f, pieceSize * grid.first + pieceSize / 2f)
    }
}
