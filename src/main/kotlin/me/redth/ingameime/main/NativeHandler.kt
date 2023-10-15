package me.redth.ingameime.main

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.HWND
import me.redth.ingameime.config.ModConfig
import me.redth.ingameime.jna.IMM32
import me.redth.ingameime.jna.IMM32.associateContext
import me.redth.ingameime.jna.IMM32.associateNewContext
import me.redth.ingameime.jna.IMM32.getContext
import me.redth.ingameime.jna.IMM32.releaseContext
import me.redth.ingameime.jna.IMM32.useHIMC

object NativeHandler {
    private const val cancelDefaultCompositionWindowFromShowing = 0L

    private var lastHWND: HWND? = null

    val composingString get() = lastHWND?.useHIMC { getComposingString() }
    val composingCursor get() = lastHWND?.useHIMC { getComposingCursor() }
    var candidateX: Int = 0
    var candidateY: Int = 0

    fun onMessage(hwndAddress: Long, message: Int, wParam: Long, lParam: Long): Long? {
        if (!ModConfig.enabled) return null

        lastHWND = HWND(Pointer(hwndAddress))
        when (message) {
            IMM32.WM_IME_STARTCOMPOSITION -> return cancelDefaultCompositionWindowFromShowing
            IMM32.WM_IME_NOTIFY -> onImeNotify(wParam)
        }

        return null
    }

    fun setIMEEnabled(on: Boolean) = if (on) {
        enable()
    } else {
        disable()
    }

    private fun onImeNotify(wParam: Long) {
        when (wParam) {
            IMM32.IMN_OPENCANDIDATE -> lastHWND?.useHIMC {
                setCandidateWindowPosition(candidateX, candidateY)
            }
        }
    }

    private fun enable() {
        val hwnd = lastHWND ?: return
        val himc = hwnd.getContext() ?: hwnd.associateNewContext()
        hwnd.releaseContext(himc)
    }

    private fun disable() {
        val hwnd = lastHWND ?: return
        val himc = hwnd.associateContext(null) ?: return
        himc.destroy()
        hwnd.releaseContext(himc)
    }
}