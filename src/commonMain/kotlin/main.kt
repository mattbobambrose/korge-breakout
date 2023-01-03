import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import kotlin.random.*

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun randomColor() = RGBA(Random.nextInt(), Random.nextInt(), Random.nextInt())

suspend fun main() =
    Korge(width = 512, height = 512, bgcolor = Colors.BLACK) {
        val paddle =
            Paddle(this, width / 4, height / 64, 4, Colors.BLUE, (width / 2) - (width / 8), height - (height / 10))
        val ball = Ball(this, paddle, 10.0, 4.0, Colors.RED)

        val bricks = mutableListOf<Brick>()

        for (i in 0 until 1) {
            bricks.add(
                Brick(
                    this, ball, width / 11, height / 20, Colors.GREEN, (i % 10) * width / 10 + (width / 220),
                    ((i / 10) * height / 15) + 50
                )
            )
        }
        val scoreboard = Scoreboard(this, bricks, bricks.count { it.isDead })
        val endScreen = EndScreen(this, ball, paddle, bricks)
        solidRect(width, 1.0, Colors.WHITE)
        solidRect(width, 1.0, Colors.WHITE).xy(0.0, height - 1.0)
        solidRect(1.0, height, Colors.WHITE)
        solidRect(1.0, height, Colors.WHITE).xy(width - 1.0, 0.0)

        addUpdater {
            paddle.update()
            ball.update()
            scoreboard.update()
            bricks.filter { !it.isDead }.forEach { it.update() }
            endScreen.update()
        }
    }
