package me.redth.ingameime.main

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.redth.ingameime.config.ModConfig
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object TextFieldFocusChecker {
    private var lastEnabled = false
    private var tempHasTextFieldFocused: Boolean = false
    private var hasTextFieldFocused: Boolean = false

    @SubscribeEvent
    fun onRender(e: TickEvent.RenderTickEvent) {
        syncIMEOnToggle()

        if (!ModConfig.enabled) return
        when (e.phase!!) {
            TickEvent.Phase.START -> preRender()
            TickEvent.Phase.END -> postRender()
        }
    }

    private fun preRender() {
        tempHasTextFieldFocused = false
    }

    private fun postRender() {
        if (hasTextFieldFocused == tempHasTextFieldFocused) return
        hasTextFieldFocused = tempHasTextFieldFocused
        NativeHandler.setIMEEnabled(hasTextFieldFocused)
        if (ModConfig.debug) UChat.chat("ime enabled: $hasTextFieldFocused")
    }

    private fun syncIMEOnToggle() {
        if (lastEnabled == ModConfig.enabled) return
        lastEnabled = ModConfig.enabled
        NativeHandler.setIMEEnabled(!lastEnabled)
    }

    fun setHasFocusedTextFieldTrue() {
        tempHasTextFieldFocused = true
    }

    fun setCandidateWindowIngamePosition(ingameX: Int, ingameY: Int) {
        val scale = ScaledResolution(mc).scaleFactor
        NativeHandler.candidateX = ingameX * scale
        NativeHandler.candidateY = ingameY * scale
    }
}