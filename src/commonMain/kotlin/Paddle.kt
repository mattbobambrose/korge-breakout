import com.soywiz.korev.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

data class Paddle(
    val stage: Stage,
    val width: Double,
    val height: Double,
    var speed: Int,
    val color: RGBA,
    var x: Double = 0.0,
    var y: Double = 0.0,
) {
    val paddleRect = stage.solidRect(width, height, color).xy(x, y)

    fun update() {
        stage.input.keys.also { keys ->
            when {
                keys[Key.LEFT] -> paddleRect.x -= speed
                keys[Key.RIGHT] -> paddleRect.x += speed
            }
        }
    }
}
