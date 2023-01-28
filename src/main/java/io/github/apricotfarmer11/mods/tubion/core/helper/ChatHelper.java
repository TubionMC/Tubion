package io.github.apricotfarmer11.mods.tubion.core.helper;

import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class ChatHelper {
    public static MutableText getChatPrefixWithFeature(MutableText feature) {
        final MutableText prefix = TextUtils.literal("Tubion").formatted(Formatting.BOLD, Formatting.GOLD);
        return prefix
                .append(
                        TextUtils.literal("|")
                                .formatted(Formatting.BLUE)
                )
                .append(feature)
                .append(" ")
                .append(TextUtils.literal(">").formatted(Formatting.BLUE))
                .append(
                        TextUtils.literal(" ").formatted(Formatting.RESET)
                );
    }
}
