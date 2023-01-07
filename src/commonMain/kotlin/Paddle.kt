import com.soywiz.korev.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

data class Paddle(
    val stage: Stage,
    val width: Int,
    val height: Int,
    var speed: Int,
    val color: RGBA,
    var x: Double = 0.0,
    var y: Double = 0.0,
) {
    val paddleRect = stage.solidRect(width, height, color).xy(x, y)

    fun update() {
        stage.input.keys.also { keys ->
            when {
                keys[Key.LEFT] && paddleRect.x > 0 -> paddleRect.x -= speed
                keys[Key.RIGHT] && paddleRect.x < stage.width - width -> paddleRect.x += speed
            }
        }
    }
}
