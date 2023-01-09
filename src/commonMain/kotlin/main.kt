import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.lang.*
import com.soywiz.korio.net.http.*
import kotlinx.serialization.json.*
import kotlin.random.*

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun randomColor(backgroundColor: RGBA): RGBA {
    val r = Random.nextInt(0, 255)
    val g = Random.nextInt(0, 255)
    val b = Random.nextInt(0, 255)
    return if (r in backgroundColor.r - 100..backgroundColor.r + 100
        && b in backgroundColor.b - 100..backgroundColor.b + 100
        && g in backgroundColor.g - 100..backgroundColor.g + 100
    ) {
        randomColor(backgroundColor)
    } else {
        RGBA(r, g, b)
    }
}

fun String.toBricks(
    stage: Stage,
    ball: Ball,
    color: RGBA,
    sideBuffer: Double,
    verticalBuffer: Double,
    backgroundColor: RGBA,
    randomColors: Boolean
): List<Brick> {
    val pattern = this.cleanString()
    val numCols = pattern.maxOf { it.length }
    val numRows = pattern.count()
    val topBufferSpace = 50
    val bricksBottomCutoff = stage.height / 2
    val brickWidth = stage.width / numCols
    val brickHeight = (bricksBottomCutoff - topBufferSpace) / (numRows + 1)

    return buildList {
        for (i in 0 until (numCols * numRows)) {
            add(
                Brick(
                    stage,
                    ball,
                    brickWidth,
                    brickHeight,
                    sideBuffer,
                    verticalBuffer,
                    if (!randomColors) {
                        color
                    } else {
                        randomColor(backgroundColor)
                    },
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

suspend fun main() {
    val client = createHttpClient()
    val sessionId = Environment["sessionId"] ?: -1
    val resp = client.request(Http.Method.GET, "/config?sessionId=$sessionId")
    val json = Json.parseToJsonElement(resp.readAllString())
    val ballRadius = json.jsonObject["ballRadius"]?.jsonPrimitive?.int ?: error("No ballRadius")
    val ballSpeed = json.jsonObject["ballSpeed"]?.jsonPrimitive?.int ?: error("No ballSpeed")
    val ballColor = json.jsonObject["ballColor"]?.jsonPrimitive?.content ?: error("No ballColor")
    val paddleWidth = json.jsonObject["paddleWidth"]?.jsonPrimitive?.int ?: error("No paddleWidth")
    val paddleHeight = json.jsonObject["paddleHeight"]?.jsonPrimitive?.int ?: error("No paddleHeight")
    val paddleSpeed = json.jsonObject["paddleSpeed"]?.jsonPrimitive?.int ?: error("No paddleSpeed")
    val paddleColor = json.jsonObject["paddleColor"]?.jsonPrimitive?.content ?: error("No paddleColor")
    val brickColor = json.jsonObject["brickColor"]?.jsonPrimitive?.content ?: error("No brickColor")
    val brickSideBufferSpace =
        json.jsonObject["brickSideBufferSpace"]?.jsonPrimitive?.double ?: error("No brickSideBufferSpace")
    val brickVerticalBufferSpace =
        json.jsonObject["brickVerticalBufferSpace"]?.jsonPrimitive?.double ?: error("No brickVerticalBufferSpace")
    val brickPattern = json.jsonObject["brickPattern"]?.jsonPrimitive?.content ?: error("No brickPattern")
    val windowWidth = json.jsonObject["windowWidth"]?.jsonPrimitive?.int ?: error("No windowWidth")
    val windowHeight = json.jsonObject["windowHeight"]?.jsonPrimitive?.int ?: error("No windowHeight")
    val backgroundColor = json.jsonObject["backgroundColor"]?.jsonPrimitive?.content ?: error("No backgroundColor")
    val bricksRandomColors =
        json.jsonObject["bricksRandomColors"]?.jsonPrimitive?.boolean ?: error("No bricksRandomColors")

    Korge(width = windowWidth, height = windowHeight, bgcolor = Colors[backgroundColor]) {
        val paddle =
            Paddle(
                this,
                paddleWidth,
                paddleHeight,
                paddleSpeed,
                Colors[paddleColor],
                (width / 2) - (paddleWidth / 2),
                height - (height / 10)
            )
        val ball = Ball(this, paddle, ballRadius, ballSpeed, Colors[ballColor], width / 2, paddle.y)

        val bricks =
            brickPattern.toBricks(
                this,
                ball,
                Colors[brickColor],
                brickSideBufferSpace,
                brickVerticalBufferSpace,
                Colors[backgroundColor],
                bricksRandomColors
            )
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
}
