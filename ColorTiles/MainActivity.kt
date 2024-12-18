package com.example.myapplication

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Массив для хранения плиток
    private lateinit var tiles: Array<Array<ToggleButton>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация массива плиток
        tiles = Array(4) { Array(4) { ToggleButton(this) } }

        // Получаем ссылки на ряды из разметки
        val rowLayouts = arrayOf(
            findViewById<LinearLayout>(R.id.rowLayout1),
            findViewById<LinearLayout>(R.id.rowLayout2),
            findViewById<LinearLayout>(R.id.rowLayout3),
            findViewById<LinearLayout>(R.id.rowLayout4)
        )

        // Создаем плитки и добавляем их в разметку
        var idCounter = 1
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val tile = ToggleButton(this).apply {
                    id = idCounter++
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1f
                    ).apply {
                        setMargins(4, 4, 4, 4) // Отступы между плитками
                    }
                    textOff = ""
                    textOn = ""
                    setOnClickListener { onTileClick(i, j) }
                }

                rowLayouts[i].addView(tile)
                tiles[i][j] = tile
            }
        }

        // Генерируем начальную конфигурацию
        generateSolvableBoard()
    }

    // Генерация начального игрового поля (гарантированно разрешимого)
    private fun generateSolvableBoard() {
        // Изначально делаем все плитки белыми
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                tiles[i][j].apply {
                    setBackgroundColor(getColor(android.R.color.white))
                    isChecked = false
                }
            }
        }

        // Выполняем случайные ходы, чтобы создать игровое поле
        repeat((5..10).random()) { // Случайное количество ходов от 5 до 10
            val randomRow = (0 until 4).random()
            val randomCol = (0 until 4).random()
            toggleTileColors(randomRow, randomCol)
        }
    }

    // Обработка клика на плитке
    private fun onTileClick(row: Int, col: Int) {
        toggleTileColors(row, col)

        // Проверяем, не победили ли мы
        if (isGameWon()) {
            Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show()
        }
    }

    // Переключение цвета плитки и затронутых плиток
    private fun toggleTileColors(row: Int, col: Int) {
        // Переключаем цвет нажатой плитки
        toggleTileColor(row, col)

        // Переключаем цвет плиток в том же ряду
        for (i in 0 until 4) {
            if (i != col) toggleTileColor(row, i)
        }

        // Переключаем цвет плиток в той же колонке
        for (j in 0 until 4) {
            if (j != row) toggleTileColor(j, col)
        }
    }

    // Переключение цвета конкретной плитки
    private fun toggleTileColor(row: Int, col: Int) {
        val tile = tiles[row][col]
        val newColor = if (tile.isChecked) {
            getColor(android.R.color.white) // Белый
        } else {
            getColor(android.R.color.black) // Черный
        }
        tile.apply {
            setBackgroundColor(newColor)
            isChecked = !isChecked
        }
    }

    // Проверка победы (все плитки одного цвета)
    private fun isGameWon(): Boolean {
        val firstTileState = tiles[0][0].isChecked
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (tiles[i][j].isChecked != firstTileState) {
                    return false
                }
            }
        }
        return true
    }
}
