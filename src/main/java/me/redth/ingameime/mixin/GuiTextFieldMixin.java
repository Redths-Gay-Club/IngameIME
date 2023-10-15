package me.redth.ingameime.mixin;

import me.redth.ingameime.hooks.TextFieldHook;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin {
    @Inject(method = "drawTextBox", at = @At("HEAD"))
    public void ingameIme_drawTextBoxHead(CallbackInfo ci) {
        TextFieldHook.INSTANCE.preDrawTextBox(this);
    }

    @Inject(method = "drawTextBox", at = @At("TAIL"))
    public void ingameIme_drawTextBoxTail(CallbackInfo ci) {
        TextFieldHook.INSTANCE.postDrawTextBox(this);
    }
}
