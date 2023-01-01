import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*

data class Brick(
    val stage: Stage,
    val ball: Ball,
    val width: Double,
    val height: Double,
    var color: RGBA,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var isDead: Boolean = false,
) {
    var brickRect = stage.solidRect(width, height, color).xy(x, y)

    fun update() {
        ball.angle =
            when {
                (isBottomHit() || isTopHit()) -> {
                    isDead = true
                    normalizeTo360Range(360 - ball.angle.degrees).degrees
                }

                (isLeftHit() || isRightHit()) -> {
                    isDead = true
                    normalizeTo360Range(180 - ball.angle.degrees).degrees
                }

                else -> ball.angle
            }
        if (isDead) {
            brickRect.color = Colors.TRANSPARENT_BLACK
        }
    }

    private fun isBottomHit() =
        !isDead && ball.ballCircle.x in (x..x + width) && ball.ballCircle.y in (y..y + height) && ball.angle > 180.degrees

    private fun isTopHit() =
        !isDead && ball.ballCircle.x in (x..x + width) && ball.ballCircle.y in (y - (2 * ball.radius)..y) && ball.angle < 180.degrees

    private fun isLeftHit() =
        !isDead && ball.ballCircle.x in (x - (2 * ball.radius)..x) && ball.ballCircle.y in (y..y + height) && (ball.angle < 90.degrees || ball.angle > 270.degrees)

    private fun isRightHit() =
        !isDead && ball.ballCircle.x in (x + width - ball.radius..x + width) && ball.ballCircle.y in (y..y + height) && ball.angle.degrees in 90.0..270.0
}
