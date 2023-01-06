import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import kotlin.random.*

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun randomColor() = RGBA(Random.nextInt(), Random.nextInt(), Random.nextInt())

fun String.toBricks(stage: Stage, ball: Ball): List<Brick> {
    val pattern = this.cleanString()
    val numCols = pattern.maxOf { it.length }
    val numRows = pattern.count()
    val topBufferSpace = 50
    val bricksBottomCutoff = stage.height / 2
    val brickWidth = stage.width / numCols
    val brickHeight = (bricksBottomCutoff - topBufferSpace) / (numRows + 1)
    val sideBrickBuffer = 0.5
    val topBrickBuffer = 0.5

    return buildList {
        for (i in 0 until (numCols * numRows)) {
            add(
                Brick(
                    stage,
                    ball,
                    brickWidth,
                    brickHeight,
                    sideBrickBuffer,
                    topBrickBuffer,
                    Colors.GREEN,
                    (i % numCols) * brickWidth,
                    (i / numCols) * brickHeight + topBufferSpace
                )
            )
        }
    }.also { bricks ->
        pattern.joinToString("").forEachIndexed { index, char ->
            if (char != '#') {
                bricks[index].apply {
                    isDead = true
                    update()
                }
            }
        }
    }
}

fun String.cleanString(): List<String> {
    val firstLine = this.lines().indexOfFirst { it.trimStart().isNotEmpty() }
    val lastLine = this.lines().indexOfLast { it.trimStart().isNotEmpty() }

    val topBottom =
        this
            .lines()
            .slice(firstLine..lastLine)

    val firstCharIndex =
        topBottom.filter { it.trimStart().isNotEmpty() }.minOf { line -> line.indexOfFirst { it == '#' } }

    val patternSlice =
        topBottom
            .map {
                if (it.trimStart().isNotEmpty()) " ${it.substring(firstCharIndex)}"
                else it
            }
            .map { it.trimEnd() }

    val longestLine = patternSlice.maxOf { it.length }

    return patternSlice.map {
        it.padEnd(longestLine + 1, '.')
    }
}

suspend fun main() =
    Korge(width = 512, height = 512, bgcolor = Colors.BLACK) {
        val paddle =
            Paddle(this, width / 4, height / 64, 4, Colors.BLUE, (width / 2) - (width / 8), height - (height / 10))
        val ball = Ball(this, paddle, 4.0, 4.0, Colors.RED)

        val helloPattern = """
            # # ### #    #    ###
            # # #   #    #    # #
            ### ### #    #    # #
            # # #   #    #    # #
            # # ### #### #### ###
        """

        val typeSomethingPattern = """
                #######
                   #    #   # #####  ######
                   #     # #  #    # #
                   #      #   #    # #####
                   #      #   #####  #
                   #      #   #      #
                   #      #   #      ######

  #####
 #     #  ####  #    # ###### ##### #    # # #    #  ####
 #       #    # ##  ## #        #   #    # # ##   # #    #
  #####  #    # # ## # #####    #   ###### # # #  # #
       # #    # #    # #        #   #    # # #  # # #  ###
 #     # #    # #    # #        #   #    # # #   ## #    #
  #####   ####  #    # ######   #   #    # # #    #  ####

        """

        val bricks = typeSomethingPattern.toBricks(this, ball)
        val scoreboard = Scoreboard(this, bricks, bricks.count { it.isDead })
        val endScreen = EndScreen(this, ball, paddle, bricks)
        solidRect(width, 1.0, Colors.WHITE)
        solidRect(width, 1.0, Colors.WHITE).xy(0.0, height - 1.0)
        solidRect(1.0, height, Colors.WHITE)
        solidRect(1.0, height, Colors.WHITE).xy(width - 1.0, 0.0)

        addUpdater {
            paddle.update()
            ball.update()
            bricks.filter { !it.isDead }.forEach { it.update() }
            scoreboard.update()
            endScreen.update()
        }
    }
