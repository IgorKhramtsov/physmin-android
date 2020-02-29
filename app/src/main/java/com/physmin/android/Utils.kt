package com.physmin.android

import android.view.View
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*  ╔════╦════════════════════════════╦═════════════════════════════╗
    ║    ║ FONT FAMILY                ║ TTF FILE                    ║
    ╠════╬════════════════════════════╬═════════════════════════════╣
    ║  1 ║ casual                     ║ ComingSoon.ttf              ║
    ║  2 ║ cursive                    ║ DancingScript-Regular.ttf   ║
    ║  3 ║ monospace                  ║ DroidSansMono.ttf           ║
    ║  4 ║ sans-serif                 ║ roboto_regular.ttf          ║
    ║  5 ║ sans-serif-black           ║ roboto_black.ttf            ║
    ║  6 ║ sans-serif-condensed       ║ RobotoCondensed-Regular.ttf ║
    ║  7 ║ sans-serif-condensed-light ║ RobotoCondensed-Light.ttf   ║
    ║  8 ║ sans-serif-light           ║ roboto_light.ttf            ║
    ║  9 ║ sans-serif-medium          ║ roboto_medium.ttf           ║
    ║ 10 ║ sans-serif-smallcaps       ║ CarroisGothicSC-Regular.ttf ║
    ║ 11 ║ sans-serif-thin            ║ roboto_thin.ttf             ║
    ║ 12 ║ serif                      ║ NotoSerif-Regular.ttf       ║
    ║ 13 ║ serif-monospace            ║ CutiveMono.ttf              ║
    ╚════╩════════════════════════════╩═════════════════════════════╝   */


fun isDev() = BuildConfig.FLAVOR.contains("dev")
const val pickableGroupTag = "pickableGroup"
const val settableGroupTag = "settableGroup"

interface API {
    val getExercise: String
    val getExam: String
    val getTest: String
    val getUserProgress: String
    val sendBundleStats: String
}

data class API_prod(override val getExercise: String = "api-v1-getExerciseBundle",
                    override val getExam: String = "api-v1-getExamBundle",
                    override val getTest: String = "getTest",
                    override val getUserProgress: String = "api-v1-getUserProgress",
                    override val sendBundleStats: String = "api-v1-sendBundleStats"
): API

data class API_debug(override val getExercise: String = "api-v1-getExerciseBundle",
                     override val getExam: String = "api-v1-getExamBundle",
                     override val getTest: String = "getTestDev",
                     override val getUserProgress: String = "api-v1-getUserProgress",
                     override val sendBundleStats: String = "api-v1-sendBundleStats"
): API

fun View.Show() {
    this.visibility = View.VISIBLE
}

fun View.Hide() {
    this.visibility = View.GONE
}

class Singleton<O, V>(initializer: () -> V): ReadOnlyProperty<O, V?> {
    private var initializer: (() -> V)? = initializer
    var value: V? = null

    override fun getValue(thisRef: O, property: KProperty<*>): V? {
        if (value == null)
            value = initializer!!()

        return value
    }
}
