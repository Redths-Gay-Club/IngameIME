package me.redth.ingameime

import me.redth.ingameime.config.ModConfig
import me.redth.ingameime.main.TextFieldFocusChecker
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(modid = IngameIME.MODID, name = IngameIME.NAME, version = IngameIME.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object IngameIME {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        ModConfig.initialize()
        MinecraftForge.EVENT_BUS.register(TextFieldFocusChecker)
    }
}