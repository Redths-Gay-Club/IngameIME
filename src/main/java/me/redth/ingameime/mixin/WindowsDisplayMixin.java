package me.redth.ingameime.mixin;

import me.redth.ingameime.main.NativeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "org.lwjgl.opengl.WindowsDisplay", remap = false)
public abstract class WindowsDisplayMixin {
    @Shadow
    private static long defWindowProc(long hwnd, int msg, long wParam, long lParam) {
        return 0;
    }

    @Redirect(method = "doHandleMessage", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/WindowsDisplay;defWindowProc(JIJJ)J"))
    private long onMessage(long hwnd, int message, long wParam, long lParam) {
        Long value = NativeHandler.INSTANCE.onMessage(hwnd, message, wParam, lParam);
        if (value != null) return value;
        return defWindowProc(hwnd, message, wParam, lParam);
    }
}
