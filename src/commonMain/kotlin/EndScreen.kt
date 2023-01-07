import com.soywiz.korge.view.*

data class EndScreen(
    val stage: Stage,
    val ball: Ball,
    val paddle: Paddle,
    val bricks: List<Brick>,
) {
    val endText: Text = stage.text("Game Over", 70.0).visible(false)
    val halfStageWidth = stage.width / 2
    val halfStageHeight = stage.height / 2
    fun update() {
        when {
            ball.ballCircle.y > stage.height -> {
                endText.apply {
                    text = "Game Over"
                    visible = true
                    xy(halfStageWidth - endText.width / 2, halfStageHeight - endText.height / 2)
                }
                ball.speed = 0
                paddle.speed = 0
            }

            bricks.all { it.isDead } -> {
                endText.apply {
                    text = "You Win!"
                    visible = true
                    xy(halfStageWidth - endText.width / 2, halfStageHeight - endText.height / 2)
                }
                ball.speed = 0
                paddle.speed = 0
            }
        }
    }
}
