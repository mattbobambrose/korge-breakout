import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import kotlin.random.*

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun randomColor() = RGBA(Random.nextInt(), Random.nextInt(), Random.nextInt())

suspend fun main() =
    Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
        val paddle =
            Paddle(this, width / 4, height / 64, 4, Colors.BLUE, (width / 2) - (width / 8), height - (height / 10))
        val ball = Ball(this, paddle, 10.0, 4.0, Colors.RED)

        val bricks = mutableListOf<Brick>()
        val scoreboard = Scoreboard(this, bricks)

        for (i in 0 until 40) {
            bricks.add(
                Brick(
                    this, ball, width / 11, height / 20, Colors.GREEN, (i % 10) * width / 10 + (width / 220),
                    ((i / 10) * height / 15) + 50
                )
            )
        }

        addUpdater {
            paddle.update()
            ball.update()
            scoreboard.update()
            bricks.filter { !it.isDead }.forEach { it.update() }
        }

        class MyScene : Scene() {
            override suspend fun SContainer.sceneMain() {
                val minDegrees = (-16).degrees
                val maxDegrees = (+16).degrees

                val image = image(resourcesVfs["korge.png"].readBitmap()) {
                    rotation = maxDegrees
                    anchor(.5, .5)
                    scale(0.8)
                    position(256, 256)
                }

                while (true) {
                    image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
                    image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
                }
            }
        }
    }
