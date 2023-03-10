import com.soywiz.korev.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*

data class Ball(
    val stage: Stage,
    val paddle: Paddle,
    val radius: Int,
    var speed: Int,
    val color: RGBA,
    var x: Double = paddle.x,
    var y: Double = paddle.y - (2 * radius),
    var angle: Angle = 45.0.degrees,
) {
    val ballCircle = stage.circle(radius.toDouble(), color).xy(x, y)

    fun update() {
        val ph = paddle.height / 2
        angle = when {
            (ballCircle.x in (paddle.paddleRect.x - radius..paddle.paddleRect.x + paddle.width - radius)
                && ballCircle.y in ((paddle.paddleRect.y - (2 * radius))..(paddle.paddleRect.y + ph + radius))
                && angle < 180.degrees
                ) -> {
                stage.input.keys.also { keys ->
                    when {
                        keys[Key.LEFT] -> angle += 10.degrees
                        keys[Key.RIGHT] -> angle -= 10.degrees
                    }
                }
                normalizeTo360Range(360 - angle.degrees).degrees
            }

            (ballCircle.x <= 0 || ballCircle.x >= stage.width - (2 * radius)) ->
                normalizeTo360Range(180.0 - angle.degrees).degrees

            (ballCircle.y <= 0) ->
                normalizeTo360Range(360 - angle.degrees).degrees

            else -> angle
        }

//            || ballCircle.y >= stage.height - (2 * radius)

        ballCircle.x += speed * cos(angle)
        ballCircle.y += speed * sin(angle)

        ballCircle.apply {
            this@Ball.stage.also { ballStage ->
                when {
                    x < 0 -> ballCircle.x = 0.0
                    x > ballStage.width - (2 * radius) -> ballCircle.x = ballStage.width - (2 * radius)
                    y < 0 -> ballCircle.y = 0.0
                }
            }
        }
    }
}
