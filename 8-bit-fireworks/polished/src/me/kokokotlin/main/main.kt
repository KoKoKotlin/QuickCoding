package me.kokokotlin.main

import me.kokokotlin.main.drawing.Drawable
import me.kokokotlin.main.drawing.WindowHandler
import me.kokokotlin.main.engine.Entity
import me.kokokotlin.main.engine.UpdateHandler
import me.kokokotlin.main.sound.Sound
import java.awt.Color
import java.awt.Graphics
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor

const val W = 1000
const val H = 1000
const val CELL_SIZE = 10

val sounds = mutableListOf<Sound>()

class vec2f(val x: Double, val y: Double) {
    operator fun plus(o: vec2f) = vec2f(x + o.x, y + o.y)
    operator fun times(s: Double) = vec2f(x * s, y * s)
}

class Firework(val isParticle: Boolean) : Entity, Drawable {
    val r = Random()
    var pos = vec2f(r.nextDouble() * W, H.toDouble())
    var vel = vec2f(0.0, r.nextDouble() * -4.0 - 1.0)

    var exploded = false
    var done = false

    var color = Color(r.nextInt(255), r.nextInt(255), r.nextInt(255))

    val explosionHeight = r.nextDouble() * H / 2 + 150.0
    val particles = mutableListOf<Firework>()

    var timer = 0.0

    init {
        if (!isParticle) {
            for (i in 0 until r.nextInt(75) + 25) {
                val f = Firework(true)
                f.vel = vec2f(r.nextDouble() * 5 - 2.5, r.nextDouble() * 5 - 2.5)
                f.pos = vec2f(pos.x, explosionHeight)
                particles.add(f)
            }
        }
    }

    override fun draw(g: Graphics) {
        if (!exploded) {
            var x = floor(pos.x).toInt()
            var y = floor(pos.y).toInt()

            x -= x % CELL_SIZE
            y -= y % CELL_SIZE

            if (isParticle) {
                val alpha = 255 - timer.toInt()
                g.color = Color(color.red, color.green, color.blue, if(alpha > 0) alpha else 0)
            } else {
                g.color = color
            }


            g.fillRect(x, y, CELL_SIZE, CELL_SIZE)
        } else {
            for (p in particles) {
                p.draw(g)
            }
        }
    }

    override fun update(delta: Double) {
        if(isParticle) timer += delta * 3.0

        if (!exploded)
            pos += vel
        else {
            for (p in particles) {
                p.update(delta)
            }

            if (timer == 0.0) {
                sounds[(Random().nextInt(sounds.size))].play()
            }

            timer += delta * 3.0

            if (timer >= 255) {
                done = true
            }
        }

        if (pos.y <= explosionHeight && !isParticle) {
            exploded = true
        }
    }
}

object Grid : Entity, Drawable {

    val fireworks = CopyOnWriteArrayList<Firework>()

    init {
        for(i in 0 until Random().nextInt(25) + 10) {
            fireworks.add(Firework(false))
        }
     }

    override fun draw(g: Graphics) {

        /*
        g.color = Color.BLACK
        for (i in 0 until (W / CELL_SIZE)) {
            for (j in 0 until (H / CELL_SIZE)) {
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE)
            }
        }
        */

        for (f in fireworks) {
            f.draw(g)
        }
    }

    override fun update(delta: Double) {

        if(Random().nextDouble() < .05)
            fireworks.add(Firework(false))


        for (f in fireworks) {
            f.update(delta)
        }

        fireworks.removeIf { it.done }
    }
}

fun main() {

    sounds.add(Sound(Paths.get("res", "fireworks1.wav")))
    sounds.add(Sound(Paths.get("res", "fireworks2.wav")))
    sounds.add(Sound(Paths.get("res", "fireworks3.wav")))


    val w = WindowHandler(1000, 1000, "fireworks")
    val u = UpdateHandler()

    w.entities.add(Grid)
    u.entities.add(Grid)
}