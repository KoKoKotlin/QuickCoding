package me.kokokotlin.main

import me.kokokotlin.main.drawing.Drawable
import me.kokokotlin.main.drawing.WindowHandler
import me.kokokotlin.main.engine.Entity
import me.kokokotlin.main.engine.UpdateHandler
import java.awt.Color
import java.awt.Graphics
import kotlin.random.Random

val GW = 100
val GH = 100

val cellSize = 10

val W = GW * cellSize
val H = GH * cellSize

var buff1 = Array<Boolean>(GW * GH) {
    Random.nextBoolean()
}

val buff2 = Array<Boolean>(GW * GH) {
    false
}

object GridHandler : Drawable, Entity {

    override fun draw(g: Graphics) {
        g.color = Color.BLACK

        for(i in 0 until GW * GH) {
            g.drawRect(i % GW * cellSize, i / GW * cellSize, cellSize, cellSize)

            if(buff1[i])
                g.fillRect(i % GW * cellSize, i / GW * cellSize, cellSize, cellSize)
        }
    }

    override fun update(delta: Double) {

        for(i in 0 until GW * GH) {
            val x = i % GW
            val y = i / GW

            if(x !in listOf(0, GW - 1) && y !in listOf(0, GH - 1)) {
                buff2[i] = getNextState(i)
            } else {
                buff2[i] = buff1[i]
            }
        }

        buff1 = buff2.clone()
    }
}

fun getNextState(index: Int): Boolean {
    val isAlive = buff1[index]

    var neighbors = 0
    for(i in -1..1) {
        for(j in -1..1) {
            val nIndex = index + i + j * GW
            if(index != nIndex) {
                neighbors += if(buff1[nIndex]) 1 else 0
            }
        }
    }

    return if(isAlive) neighbors in listOf(2, 3) else neighbors == 3
}

fun main(args: Array<String>) {
    val window = WindowHandler(W, H, "GameOfLive")
    val updateHandler = UpdateHandler()

    window.entities.add(GridHandler)
    updateHandler.entities.add(GridHandler)

}