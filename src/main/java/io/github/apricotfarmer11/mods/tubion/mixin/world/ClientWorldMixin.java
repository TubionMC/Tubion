package io.github.apricotfarmer11.mods.tubion.mixin.world;

import io.github.apricotfarmer11.mods.tubion.event.WorldEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(ClientPlayNetworkHandler netHandler, ClientWorld.Properties properties, RegistryKey registryRef, RegistryEntry registryEntry, int loadDistance, int simulationDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        WorldEvents.INIT.invoker().onWorldInit();
    }
}
