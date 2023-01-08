package io.github.apricotfarmer11.mods.tubion.misc;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface ChatHudMixin$VisibleMessageGetter {
    //#if MC>=11902
    //$$ List<ChatHudLine.Visible>
    //#else
    List<ChatHudLine>
    //#endif
    getVisibleMessages();
}