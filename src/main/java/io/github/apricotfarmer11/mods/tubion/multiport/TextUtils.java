/**
 * Wrapper class for Text-related utilities.
 * Used to support <1.19 builds
 */
package io.github.apricotfarmer11.mods.tubion.multiport;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public interface TextUtils {
    static MutableText literal(String source) {
        return new LiteralText(source);
    }
    static MutableText translatable(String key) {
        return new TranslatableText(key);
    }
}
