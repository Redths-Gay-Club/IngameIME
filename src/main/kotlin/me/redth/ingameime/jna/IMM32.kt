package me.redth.ingameime.jna

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser.POINT

object IMM32 {
    const val WM_IME_STARTCOMPOSITION = 0x10D
    const val WM_IME_NOTIFY = 0x282
    const val GCS_COMPSTR = 0x8
    const val GCS_CURSORPOS = 0x80
    const val IMN_OPENCANDIDATE = 0x5L
    const val CFS_CANDIDATEPOS = 0x40

    init {
        Native.register("imm32")
    }

    private external fun ImmGetContext(hwnd: HWND): HIMC?
    private external fun ImmAssociateContext(hwnd: HWND, himc: HIMC?): HIMC?
    private external fun ImmReleaseContext(hwnd: HWND, himc: HIMC?): Boolean
    private external fun ImmCreateContext(): HIMC
    private external fun ImmDestroyContext(himc: HIMC?): Boolean
    private external fun ImmGetCompositionStringW(himc: HIMC, info: Int, buffer: CharArray?, size: Int): Int
    private external fun ImmSetCandidateWindow(himc: HIMC, form: CANDIDATEFORM): Int

    fun HWND.getContext() = ImmGetContext(this)
    fun HWND.associateContext(himc: HIMC?) = ImmAssociateContext(this, himc)
    fun HWND.associateNewContext() = ImmCreateContext().also { associateContext(it) }
    fun HWND.releaseContext(himc: HIMC?) = ImmReleaseContext(this, himc)

    inline fun <reified T> HWND.useHIMC(action: HIMC.() -> T): T? {
        val himc = getContext() ?: return null
        val result = action(himc)
        releaseContext(himc)
        return result
    }

    class HIMC : HANDLE {
        constructor() : super()
        constructor(pointer: Pointer) : super(pointer)

        fun getComposingString(): String? {
            try {
                val length = ImmGetCompositionStringW(this, GCS_COMPSTR, null, 0)
                if (length <= 0) return null

                val buffer = CharArray(length / 2)

                ImmGetCompositionStringW(this, GCS_COMPSTR, buffer, length)

                return String(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun setCandidateWindowPosition(x: Int, y: Int) {
            val form = CANDIDATEFORM()
            form.dwStyle = CFS_CANDIDATEPOS
            form.ptCurrentPos = POINT(x, y)
            ImmSetCandidateWindow(this, form)
        }

        fun getComposingCursor() = ImmGetCompositionStringW(this, GCS_CURSORPOS, null, 0).takeIf { it >= 0 }

        fun destroy() = ImmDestroyContext(this)
    }

    class CANDIDATEFORM : Structure {
        @JvmField
        var dwIndex: Int = 0

        @JvmField
        var dwStyle: Int = 0

        @JvmField
        var ptCurrentPos: POINT = POINT()

        @JvmField
        var rcArea: RECT = RECT()

        constructor() : super()
        constructor(pointer: Pointer) : super(pointer)
    }
}
