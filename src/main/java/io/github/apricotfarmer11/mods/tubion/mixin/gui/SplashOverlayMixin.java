package io.github.apricotfarmer11.mods.tubion.mixin.gui;

import io.github.apricotfarmer11.mods.tubion.config.TubionConfigManager;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.misc.TubnetLogoTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.util.math.ColorHelper.Argb;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;


@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Shadow
    private static final int MOJANG_RED = ColorHelper.Argb.getArgb(255, 239, 50, 61);
    @Shadow
    private static final int MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);
    @Unique
    private static final int TUBNET_BLUE = Argb.getArgb(255, 34, 181, 248);
    @Unique
    private static final Identifier TUBNET_LOGO = new Identifier("tubion:textures/gui/title/tubnet.png");

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo ci) {
        // We use TubNet.connecting since the race condition
        if (TubnetCore.getInstance().connecting || TubnetCore.getInstance().connected) {
            client.getTextureManager().registerTexture(TUBNET_LOGO, new TubnetLogoTexture());
            ci.cancel();
        }
    }
    @Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/gui/screen/SplashOverlay;LOGO:Lnet/minecraft/util/Identifier;"), method = "Lnet/minecraft/client/gui/screen/SplashOverlay;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
    private Identifier getTubnetLogo() {
        if ((TubnetCore.getInstance().connecting || TubnetCore.getInstance().connected) && TubionConfigManager.getConfig().customLoadingScreen) {
            return TUBNET_LOGO;
        } else {
            return new Identifier("textures/gui/title/mojangstudios.png");
        }
    }
    @Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/gui/screen/SplashOverlay;BRAND_ARGB:Ljava/util/function/IntSupplier;"), method = "Lnet/minecraft/client/gui/screen/SplashOverlay;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
    private IntSupplier getColorValue() {
        if ((TubnetCore.getInstance().connecting || TubnetCore.getInstance().connected) && TubionConfigManager.getConfig().customLoadingScreen) {
            return () -> TUBNET_BLUE;
        } else {
            return () ->
                    //#if MC>=11902
                    //$$ (Boolean)MinecraftClient.getInstance().options.getMonochromeLogo().getValue()
                    //#else
                    (Boolean)MinecraftClient.getInstance().options.monochromeLogo
                    //#endif
                            ? MONOCHROME_BLACK : MOJANG_RED;
        }
    }
}