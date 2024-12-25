package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt
import kotlin.random.Random

class GameView(ctx: Context) : View(ctx) {
    private var h = 1000; private var w = 1000 // значения по умолчанию, будут заменены в onLayout()
    private val paint = Paint() // краска, цвет и стиль отрисовки

    // Класс Circle
    data class Circle(var cx: Float, var cy: Float, val radius: Float, var color: Int) {
        fun isInside(x: Float, y: Float): Boolean {
            return sqrt((cx - x) * (cx - x) + (cy - y) * (cy - y)) <= radius
        }
    }

    // Массив кружков
    private val circles = mutableListOf<Circle>()
    private var targetColor: Int = Color.BLACK
    private var draggingCircle: Circle? = null

    private val holeRectLeft = 300f
    private val holeRectTop = 400f
    private val holeRectRight = 700f
    private val holeRectBottom = 600f

    init {
        generateCircles(5)
    }

    private fun generateCircles(count: Int) {
        circles.clear()
        for (i in 0 until count) {
            var newCircle: Circle
            do {
                val cx = Random.nextFloat() * w
                val cy = Random.nextFloat() * h
                val radius = Random.nextInt(50, 100).toFloat()
                val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
                newCircle = Circle(cx, cy, radius, color)
            } while (circles.any { circle ->
                    sqrt((circle.cx - newCircle.cx) * (circle.cx - newCircle.cx) +
                            (circle.cy - newCircle.cy) * (circle.cy - newCircle.cy)) < circle.radius + newCircle.radius
                })
            circles.add(newCircle)
        }
        if (circles.isNotEmpty()) {
            targetColor = circles[0].color 
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top; w = right - left
        generateCircles(5)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.drawColor(Color.WHITE)

        // Рисуем лузу
        paint.color = targetColor
        paint.style = Paint.Style.FILL
        canvas.drawRect(holeRectLeft, holeRectTop, holeRectRight, holeRectBottom, paint)

        // Рисуем кружки
        for (circle in circles) {
            paint.color = circle.color
            canvas.drawCircle(circle.cx, circle.cy, circle.radius, paint)
        }

        if (circles.isEmpty()) {
            paint.color = Color.BLACK
            paint.textSize = 60f
            canvas.drawText("Игра закончена", w / 4f, h / 2f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    draggingCircle = circles.find { circle -> circle.isInside(it.x, it.y) }
                }
                MotionEvent.ACTION_MOVE -> {
                    draggingCircle?.let { circle ->
                        circle.cx = it.x
                        circle.cy = it.y
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    draggingCircle?.let { circle ->
                        if (circle.cx in holeRectLeft..holeRectRight && circle.cy in holeRectTop..holeRectBottom && circle.color == targetColor) {
                            circles.remove(circle) 
                            targetColor = circles.firstOrNull()?.color ?: Color.BLACK 
                        }
                        draggingCircle = null
                        invalidate()
                    }
                }

                else -> {}
            }
        }
        return true
    }
}
