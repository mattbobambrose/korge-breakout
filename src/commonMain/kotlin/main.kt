import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import kotlin.random.*

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun randomColor() = RGBA(Random.nextInt(), Random.nextInt(), Random.nextInt())

fun patternSetter(pattern: String, bricks: List<Brick>) {
    pattern.forEachIndexed { index, char ->
        if (char != '#') {
            bricks[index].apply {
                isDead = true
                update()
            }
            println("Dead brick index: $index")
            println("Dead brick x:${bricks[index].brickRect.x}")
        } else {
            println("Alive brick index: $index")
            println("Alive brick x:${bricks[index].brickRect.x}")
        }
    }
}

suspend fun main() =
    Korge(width = 512, height = 512, bgcolor = Colors.BLACK) {
        val paddle =
            Paddle(this, width / 4, height / 64, 4, Colors.BLUE, (width / 2) - (width / 8), height - (height / 10))
        val ball = Ball(this, paddle, 4.0, 4.0, Colors.RED)

        val bricks = mutableListOf<Brick>()

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

        val pattern = helloPattern

        val firstLine = pattern.lines().indexOfFirst { it.trimStart().isNotEmpty() }
        val lastLine = pattern.lines().indexOfLast { it.trimStart().isNotEmpty() }

        val topBottom =
            pattern
                .lines()
                .slice(firstLine..lastLine)

        val firstCharIndex =
            topBottom.filter { it.trimStart().isNotEmpty() }.map { line -> line.indexOfFirst { it == '#' } }
                .minOrNull()!!
        println(firstCharIndex)

        val patternSlice =
            topBottom
                .map {
                    if (it.trimStart().isNotEmpty()) " ${it.substring(firstCharIndex)}"
                    else it
                }
                .map { it.trimEnd() }

        val longestLine = patternSlice.maxOf { it.length }

        val newPattern = patternSlice.map { it.padEnd(longestLine + 1, '.') }

        println(newPattern.joinToString("\n"))
        val numCols = newPattern.maxOf { it.length }
        val numRows = newPattern.count()

        val topBufferSpace = 50
        val bricksBottomCutoff = height / 2
        val brickWidth = width / numCols
        val brickHeight = (bricksBottomCutoff - topBufferSpace) / (numRows + 1)
//        val sideBrickBuffer = 0.0
//        val topBrickBuffer = 0.0
//        val sideBrickBuffer = 0.1
//        val topBrickBuffer = 0.1
//        val sideBrickBuffer = 0.5
//        val topBrickBuffer = 0.5
        val sideBrickBuffer = 0.9
        val topBrickBuffer = 0.9

        for (i in 0 until (numCols * numRows)) {
            bricks.add(
                Brick(
                    this,
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

//        patternSetter(newPattern.joinToString(""), bricks)
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
