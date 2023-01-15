package io.github.apricotfarmer11.mods.tubion.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> TubionConfigManager.getConfigurationBuilder().setParentScreen(parent).build();
    }
}
