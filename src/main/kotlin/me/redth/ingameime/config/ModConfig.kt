package me.redth.ingameime.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.redth.ingameime.IngameIME

object ModConfig : Config(Mod(IngameIME.NAME, ModType.UTIL_QOL), "${IngameIME.MODID}.json") {
    @Color(name = "Composing Background Color")
    var composingBackgroundColor = OneColor(0xFF5500FF.toInt())

    @Switch(name = "Debug")
    var debug = false
}