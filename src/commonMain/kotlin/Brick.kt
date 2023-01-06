import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*

data class Brick(
    val stage: Stage,
    val ball: Ball,
    val width: Double,
    val height: Double,
    val sideBuffer: Double,
    val topBuffer: Double,
    var color: RGBA,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var isDead: Boolean = false,
) {
    var brickRect = stage.solidRect((1.0 - sideBuffer) * width, (1.0 - topBuffer) * height, color)
        .xy(x + ((sideBuffer * width) / 2), y + ((topBuffer * height) / 2))

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
            brickRect.color = Colors.TRANSPARENT_BLACK
        }
    }

    val isGoingUp: Boolean
        get() = ball.angle > 180.degrees

    val isGoingDown: Boolean
        get() = ball.angle < 180.degrees

    val isGoingLeft: Boolean
        get() = ball.angle > 90.degrees && ball.angle < 270.degrees

    val isGoingRight: Boolean
        get() = ball.angle < 90.degrees || ball.angle > 270.degrees

    val brickX = brickRect.x
    val brickY = brickRect.y
    val brickWidth = brickRect.width
    val brickHeight = brickRect.height

    val isUnderBrick: Boolean
        get() = ball.ballCircle.y in (brickY..brickY + brickHeight)
    val isAboveBrick: Boolean
        get() = ball.ballCircle.y in (brickY - (2 * ball.radius)..brickY)
    val isLeftOfBrick: Boolean
        get() = ball.ballCircle.x in (brickX - (2 * ball.radius)..brickX)
    val isRightOfBrick: Boolean
        get() = ball.ballCircle.x in (brickX + brickWidth - (2 * ball.radius)..brickX + brickWidth)

    private fun isBottomHit() =
        !isDead && ball.ballCircle.x in (brickX - ball.radius..brickX + brickWidth - ball.radius) && isUnderBrick && isGoingUp

    private fun isTopHit() =
        !isDead && ball.ballCircle.x in (brickX - ball.radius..brickX + brickWidth - ball.radius) && isAboveBrick && isGoingDown

    private fun isLeftHit() =
        !isDead && isLeftOfBrick && ball.ballCircle.y in (brickY - ball.radius..brickY + brickHeight - ball.radius) && isGoingRight

    private fun isRightHit() =
        !isDead && isRightOfBrick && ball.ballCircle.y in (brickY - ball.radius..brickY + brickHeight - ball.radius) && isGoingLeft
}
