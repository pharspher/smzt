package com.chichizaza.shenmozhita

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.chichizaza.shenmozhita.solver.Board
import com.chichizaza.shenmozhita.solver.Piece
import com.chichizaza.shenmozhita.solver.p
import java.util.*

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

    private var draggedView: PieceView? = null
    private var lastGridIdx: Point? = null
    private var initialTouchOffset = Point(0, 0)

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
        canvas?.run {
            var lifted: PieceView? = null
            for (pieceView in pieceViewList) {
                if (pieceView.lift) {
                    lifted = pieceView

                } else {
                    pieceView.draw(this, paint)
                }
            }
            lifted?.draw(this, paint)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (draggedView == null) {
                    startDragging(event.x, event.y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                draggedView?.takeIf {
                    event.x >= 0 && event.y >= 0 &&
                            event.x < viewSize.width && event.y < viewSize.height

                }?.let {
                    onDragging(it, event.x, event.y)
                }
            }

            MotionEvent.ACTION_UP -> {
                draggedView?.run { stopDragging(this, event.x, event.y) } ?: performClick()
                draggedView = null
                lastGridIdx = null

                startCrush()
            }
        }

        invalidate()
        return true
    }

    private fun startDragging(x: Float, y: Float) {
        lastGridIdx = toGridIndex(x, y).also { pos ->
            draggedView = pieceViewList[pos.y * gridSize.width + pos.x].also { pieceView ->
                pieceView.lift = true
                initialTouchOffset.set((pieceView.centerX - x).toInt(),
                        (pieceView.centerY - y).toInt())
            }
        }
    }

    private fun onDragging(piece: PieceView, x: Float, y: Float) {
        piece.centerX = x + initialTouchOffset.x
        piece.centerY = y + initialTouchOffset.y

        val currentGridIdx = toGridIndex(x, y)
        lastGridIdx?.takeIf {
            it.x != currentGridIdx.x || it.y != currentGridIdx.y

        }?.let {
            swapWithAnimation(it, currentGridIdx)
            lastGridIdx = currentGridIdx
        }
    }

    private fun stopDragging(piece: PieceView, x: Float, y: Float) {
        val gridCenter = gridCenter(toGridIndex(x, y))
        piece.centerX = gridCenter.x
        piece.centerY = gridCenter.y
        piece.lift = false
    }

    private fun toGridIndex(x: Float, y: Float): Point {
        return Point((x / pieceSize).toInt(), (y / pieceSize).toInt())
    }

    private fun initBoardView(board: Board) {
        val data = board.boardData
        for (row in 0 until board.boardHeight) {
            for (col in 0 until board.boardWidth) {
                val piece: Piece = data[row][col].p()
                pieceViewList.add(PieceView(piece, pieceSize * col,
                        pieceSize * row, pieceSize))
            }
        }
        invalidate()
    }

    private fun swapWithAnimation(src: Point, dst: Point, onComplete: (() -> Unit)? = null) {
        val srcCenter = gridCenter(src)

        val dstView = pieceView(dst)
        val dstCenter = gridCenter(dst)

        val dst2SrcAnimX = ValueAnimator.ofFloat(dstCenter.x, srcCenter.x)
        dst2SrcAnimX.addUpdateListener {
            dstView.centerX = it.animatedValue as Float
            invalidate()
        }

        val dst2SrcAnimY = ValueAnimator.ofFloat(dstCenter.y, srcCenter.y)
        dst2SrcAnimY.addUpdateListener {
            dstView.centerY = it.animatedValue as Float
            invalidate()
        }

        val animSet = AnimatorSet().setDuration(100)
        animSet.playTogether(dst2SrcAnimX, dst2SrcAnimY)
        animSet.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                swap(src, dst)
                onComplete?.run { this() }
            }
        })
        animSet.start()
    }

    private fun swap(src: Point, dst: Point) {
        val srcIdx = src.y * gridSize.width + src.x
        val dstIdx = dst.y * gridSize.width + dst.x

        run {
            val tmp = pieceViewList[srcIdx]
            pieceViewList[srcIdx] = pieceViewList[dstIdx]
            pieceViewList[dstIdx] = tmp
        }

        board?.let {
            val tmp = it.boardData[src.y][src.x]
            it.boardData[src.y][src.x] = it.boardData[dst.y][dst.x]
            it.boardData[dst.y][dst.x] = tmp
        }
    }

    private fun pieceView(gridIdx: Point): PieceView {
        return pieceViewList[gridIdx.y * gridSize.width + gridIdx.x]
    }

    private fun gridCenter(gridPos: Point): PointF {
        return PointF(pieceSize * gridPos.x + pieceSize / 2f,
                pieceSize * gridPos.y + pieceSize / 2f)
    }

    private val crushQueue: LinkedList<Set<Pair<Int, Int>>> = LinkedList()

    private fun startCrush() {
        board?.evaluateCombo()?.let { combos ->
            for (combo in combos) {
                crushQueue.offer(combo)
            }
        }
        crushNext()
    }

    private fun crushNext() {
        crushQueue.peek()?.let { combo ->
            crushQueue.pop()
            if (combo.isEmpty()) {
                return
            }
            val first = combo.iterator().next()
            val initRadius = pieceView(Point(first.second, first.first)).radius

            val animator = ValueAnimator.ofFloat(initRadius, 0f)
            animator.duration = 80
            animator.addUpdateListener {
                val value = it.animatedValue as Float

                combo.map { pieceView(Point(it.second, it.first)) }.forEach { it.radius = value }

                invalidate()
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    handler.postDelayed({
                        crushNext()
                    }, 150)
                }
            })
            animator.start()
        }
    }
}
