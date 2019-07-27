package com.example.physmin

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*  ╔════╦════════════════════════════╦═════════════════════════════╗
    ║    ║ FONT FAMILY                ║ TTF FILE                    ║
    ╠════╬════════════════════════════╬═════════════════════════════╣
    ║  1 ║ casual                     ║ ComingSoon.ttf              ║
    ║  2 ║ cursive                    ║ DancingScript-Regular.ttf   ║
    ║  3 ║ monospace                  ║ DroidSansMono.ttf           ║
    ║  4 ║ sans-serif                 ║ Roboto-Regular.ttf          ║
    ║  5 ║ sans-serif-black           ║ Roboto-Black.ttf            ║
    ║  6 ║ sans-serif-condensed       ║ RobotoCondensed-Regular.ttf ║
    ║  7 ║ sans-serif-condensed-light ║ RobotoCondensed-Light.ttf   ║
    ║  8 ║ sans-serif-light           ║ Roboto-Light.ttf            ║
    ║  9 ║ sans-serif-medium          ║ Roboto-Medium.ttf           ║
    ║ 10 ║ sans-serif-smallcaps       ║ CarroisGothicSC-Regular.ttf ║
    ║ 11 ║ sans-serif-thin            ║ Roboto-Thin.ttf             ║
    ║ 12 ║ serif                      ║ NotoSerif-Regular.ttf       ║
    ║ 13 ║ serif-monospace            ║ CutiveMono.ttf              ║
    ╚════╩════════════════════════════╩═════════════════════════════╝   */


fun isDev() = BuildConfig.FLAVOR.contains("dev")

class Singleton<O, V>(initializer: () -> V): ReadOnlyProperty<O, V?> {
    private var initializer: (() -> V)? = initializer
    var value: V? = null

    override fun getValue(thisRef: O, property: KProperty<*>): V? {
        if (value == null)
            value = initializer!!()

        return value
    }
}