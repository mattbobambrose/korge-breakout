import com.soywiz.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "com.sample.demo"

// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    targetJs()
    //targetDesktop()
    //targetIos()
    //targetAndroidIndirect() // targetAndroidDirect()

    serializationJson()
    //targetAndroidDirect()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("com.soywiz.korlibs.korma:korma:2.2.0")
            }
        }
    }
}

dependencies {
    add("commonMainApi", project(":deps"))
    //add("commonMainApi", project(":korge-dragonbones"))
}

