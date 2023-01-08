package io.github.apricotfarmer11.mods.tubion.mixin.misc;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.feature.TubnetTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.resource.ResourcePack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Nullable public abstract ServerInfo getCurrentServerEntry();

    @ModifyArg(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReload;"), index = 3)
    public List<ResourcePack> updatePackList(List<ResourcePack> packs) {
        ServerInfo entry = this.getCurrentServerEntry();
        final List<ResourcePack> packs1 = new ArrayList<>(packs);
        if (entry != null) {
            if (TubnetCore.getInstance().connected && TubionMod.getConfig().enableTubnetTweaks) {
                ResourcePack tweaks = TubnetTweaks.getTubnetTweaksResourcePack();
                if (tweaks != null) packs1.add(tweaks);
            }
        }
        return packs1;
    }
}
