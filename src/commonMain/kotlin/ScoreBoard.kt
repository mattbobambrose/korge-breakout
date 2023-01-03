import com.soywiz.korge.view.*

data class Scoreboard(val stage: Stage, val bricks: List<Brick>, val originalDeadBalls: Int) {
    val scoreText = stage.text("Score: $score", 10.0)
    fun update() {
        scoreText.text = "Score: $score"
    }

    private val score: Int get() = bricks.count { it.isDead } - originalDeadBalls
}
