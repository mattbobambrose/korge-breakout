import com.soywiz.korge.view.*

data class EndScreen(
    val stage: Stage,
    val ball: Ball,
    val paddle: Paddle,
    val bricks: List<Brick>,
) {
    val endText: Text = stage.text("Game Over", 70.0).visible(false)
    fun update() {
        when {
            ball.ballCircle.y > stage.height -> {
                endText.apply {
                    text = "Game Over"
                    visible = true
                    xy(100.0, 170.0)
                }
                ball.speed = 0.0
                paddle.speed = 0
            }

            bricks.all { it.isDead } -> {
                endText.apply {
                    text = "You Win!"
                    visible = true
                    xy(120.0, 170.0)
                }
                ball.speed = 0.0
                paddle.speed = 0
            }
        }
    }
}
