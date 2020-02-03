package com.physmin.android

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

class Singleton<O, V>(initializer: () -> V): ReadOnlyProperty<O, V?> {
    private var initializer: (() -> V)? = initializer
    var value: V? = null

    override fun getValue(thisRef: O, property: KProperty<*>): V? {
        if (value == null)
            value = initializer!!()

        return value
    }
}