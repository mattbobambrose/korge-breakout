import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*

data class Ball(
    val stage: Stage,
    val paddle: Paddle,
    val radius: Double,
    var speed: Double,
    val color: RGBA,
    var x: Double = paddle.x,
    var y: Double = paddle.y - paddle.height / 2 - radius,
    var angle: Angle = 45.0.degrees,
) {
    val ballCircle = stage.circle(radius, color).xy(x, y)

    fun update() {
        val ph = paddle.height / 2
        angle = when {
            (ballCircle.x in (paddle.paddleRect.x - radius..paddle.paddleRect.x + paddle.width - radius)
                && ballCircle.y in ((paddle.paddleRect.y - paddle.height - radius)..(paddle.paddleRect.y + ph + radius))
                && angle < 180.degrees
                ) -> normalizeTo360Range(360 - angle.degrees).degrees

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
//                    y > ballStage.height - (2 * radius) -> ballCircle.y = ballStage.height - (2 * radius)
                }
            }
        }
    }
}
