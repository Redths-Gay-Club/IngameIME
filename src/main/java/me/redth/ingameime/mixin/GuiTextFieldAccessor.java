package me.redth.ingameime.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiTextField.class)
public interface GuiTextFieldAccessor {
    @Accessor
    int getLineScrollOffset();

    @Accessor
    void setSelectionEnd(int selectionEnd);

    @Accessor("text")
    void setTextForce(String text);

    @Accessor("fontRendererInstance")
    FontRenderer getFontRenderer();
}
