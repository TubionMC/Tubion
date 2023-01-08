package io.github.apricotfarmer11.mods.tubion.misc;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
//#if MC>=11902
import net.minecraft.text.Text;
//#endif

import java.io.IOException;
import java.io.InputStream;

public class TubnetLogoTexture extends ResourceTexture {
    public TubnetLogoTexture() {
        super(new Identifier("tubion:textures/gui/title/tubnet.png"));
    }

    protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ModContainer modContainer = FabricLoader.getInstance().getModContainer("tubion").get();
        ModResourcePack resourcePack = ModNioResourcePack.create(
                //#if MC>=11902
                //$$ new Identifier("tubion"),
                //#endif
                //#if MC>=11903
                //$$ Text.of("Tubion"),
                //#else
                "Tubion",
                //#endif
                modContainer, "", ResourceType.CLIENT_RESOURCES, ResourcePackActivationType.ALWAYS_ENABLED
        );

        try {
            InputStream inputStream =
                    //#if MC>=11902
                    (InputStream)
                            //#endif
                            resourcePack.open(ResourceType.CLIENT_RESOURCES, new Identifier("tubion:textures/gui/title/tubnet.png"));

            ResourceTexture.TextureData var5;
            try {
                var5 = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
            } catch (Throwable var8) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if (inputStream != null) {
                inputStream.close();
            }

            return var5;
        } catch (IOException var9) {
            return new ResourceTexture.TextureData(var9);
        }
    }
}