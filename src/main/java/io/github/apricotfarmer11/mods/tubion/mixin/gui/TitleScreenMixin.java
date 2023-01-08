package io.github.apricotfarmer11.mods.tubion.mixin.gui;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Shadow @Final private RotatingCubeMapRenderer backgroundRenderer;

    @Unique
    private static final CubeMapRenderer TUBNET_PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("tubion:textures/gui/title/background/panorama"));
    @Unique
    private static final RotatingCubeMapRenderer TUBNET_ROTATING_PANORAMA_CUBE_MAP = new RotatingCubeMapRenderer(TUBNET_PANORAMA_CUBE_MAP);
    @Unique
    private RotatingCubeMapRenderer REAL;
    @Inject(at = @At("RETURN"), method = "loadTexturesAsync", cancellable = true)
    private static void loadTexturesAsync(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.setReturnValue(
                CompletableFuture.allOf(
                        cir.getReturnValue(),
                        TUBNET_PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor)
                )
        );
    }
    @Redirect(at = @At(value = "FIELD", opcode = Opcodes.H_GETFIELD, target = "Lnet/minecraft/client/gui/screen/TitleScreen;backgroundRenderer:Lnet/minecraft/client/gui/RotatingCubeMapRenderer;"), method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
    private RotatingCubeMapRenderer getBackgroundRenderer(TitleScreen instance) {
        if (TubionMod.getConfig().customPanorama) {
            return TUBNET_ROTATING_PANORAMA_CUBE_MAP;
        }
        return REAL;
    }
    @Redirect(at = @At(value = "FIELD", opcode = Opcodes.H_PUTFIELD, target = "Lnet/minecraft/client/gui/screen/TitleScreen;backgroundRenderer:Lnet/minecraft/client/gui/RotatingCubeMapRenderer;"), method = "Lnet/minecraft/client/gui/screen/TitleScreen;<init>(Z)V")
    private void setBackgroundRenderer(TitleScreen instance, RotatingCubeMapRenderer value) {
        REAL = value;
    }
}