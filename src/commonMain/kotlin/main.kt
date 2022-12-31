import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*

suspend fun main() =
    Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
//	val sceneContainer = sceneContainer()
//	sceneContainer.changeTo({ MyScene() })
        val ball = circle(radius = 25.0, fill = Colors.RED).xy(50, 50)
        // val paddle = solidRect(100, 20, Colors.BLUE).xy(50, 450)

//        addUpdater {
//            val xy = input.mouse
//            val buttons: Int = input.mouseButtons
//            if (buttons == 1) {
//                ball.x = xy.x
//                ball.y = xy.y
//            }
//        }

        input.keys.also { keys ->
            println("Hello World!")
            when {
                keys[Key.LEFT] -> ball.x -= 1
                keys[Key.RIGHT] -> ball.x += 1
                keys[Key.UP] -> ball.y -= 1
                keys[Key.DOWN] -> ball.y += 1
            }
        }

    }

class MyScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val minDegrees = (-16).degrees
        val maxDegrees = (+16).degrees

        val image = image(resourcesVfs["korge.png"].readBitmap()) {
            rotation = maxDegrees
            anchor(.5, .5)
            scale(0.8)
            position(256, 256)
        }

        while (true) {
            image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
            image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
        }
    }
}
