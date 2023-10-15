package me.redth.ingameime.hooks

import cc.polyfrost.oneconfig.utils.dsl.substringSafe
import me.redth.ingameime.config.ModConfig
import me.redth.ingameime.main.NativeHandler
import me.redth.ingameime.main.TextFieldFocusChecker
import me.redth.ingameime.mixin.GuiTextFieldAccessor
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

object TextFieldHook {
    private var originalCursorPosition: Int = 0
    private var originalSelectionEnd: Int = 0
    private var originalText: String = ""
    private var originalLeft: Int = 0
    private var originalTop: Int = 0
    private var originalRight: Int = 0
    private var originalBottom: Int = 0
    private var savedOriginal: Boolean = false

    fun Any.preDrawTextBox() {
        if (!ModConfig.enabled) return
        if (this !is GuiTextField) return
        if (this !is GuiTextFieldAccessor) return
        if (!visible) return
        if (!isFocused) return

        TextFieldFocusChecker.setHasFocusedTextFieldTrue()

        val composingText = NativeHandler.composingString ?: return
        val composingCursor = NativeHandler.composingCursor ?: composingText.length

        originalCursorPosition = cursorPosition
        originalSelectionEnd = selectionEnd
        originalText = text
        savedOriginal = true

        val lineScrollOffset = lineScrollOffset
        val hasBackground = enableBackgroundDrawing
        val (cursorStart, cursorEnd) = sortTwoInt(originalCursorPosition, originalSelectionEnd)

        val previewText = originalText.replaceRange(
            startIndex = cursorStart,
            endIndex = cursorEnd,
            replacement = composingText
        )

        val newCursor = cursorStart + composingCursor

        setTextForce(previewText)
        cursorPosition = newCursor
        setSelectionEnd(newCursor)

        var textX = xPosition
        var textY = yPosition

        if (hasBackground) textX += 4
        if (hasBackground) textY += height / 2 - 4

        val preCursorText = originalText.substringSafe(
            startIndex = lineScrollOffset,
            endIndex = cursorStart
        )

        val selectionStartX = fontRenderer.getStringWidth(preCursorText)
        val composingTextWidth = fontRenderer.getStringWidth(composingText)

        originalLeft = textX + selectionStartX
        originalTop = textY - 1
        originalRight = textX + (selectionStartX + composingTextWidth).coerceAtMost(width)
        originalBottom = textY + fontRenderer.FONT_HEIGHT + 1

        val modifiedPreCursorText = text.substringSafe(startIndex = lineScrollOffset, endIndex = cursorPosition)
        val candidateWindowIngameX = textX + fontRenderer.getStringWidth(modifiedPreCursorText)
        val candidateWindowIngameY = textY + fontRenderer.FONT_HEIGHT + 1

        TextFieldFocusChecker.setCandidateWindowIngamePosition(candidateWindowIngameX, candidateWindowIngameY)
    }

    fun Any.postDrawTextBox() {
        if (!ModConfig.enabled) return
        if (this !is GuiTextField) return
        if (this !is GuiTextFieldAccessor) return
        if (!savedOriginal) return

        drawComposingBackground(originalLeft, originalTop, originalRight, originalBottom)

        setTextForce(originalText)
        cursorPosition = originalCursorPosition
        setSelectionEnd(originalSelectionEnd)

        savedOriginal = false
    }

    private fun sortTwoInt(a: Int, b: Int) = if (a <= b) a to b else b to a

    private fun drawComposingBackground(left: Int, top: Int, right: Int, bottom: Int) {
        val color = ModConfig.composingBackgroundColor
        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f, color.blue.toFloat() / 255f, color.alpha.toFloat() / 255f)
        GlStateManager.disableTexture2D()
        GlStateManager.enableColorLogic()
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE)

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        worldRenderer.begin(7, DefaultVertexFormats.POSITION)
        worldRenderer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldRenderer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldRenderer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        worldRenderer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()

        tessellator.draw()

        GlStateManager.disableColorLogic()
        GlStateManager.enableTexture2D()
    }
}
