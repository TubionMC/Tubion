/**
 * Wrapper class for Text-related utilities.
 * 1.19+
 */
package io.github.apricotfarmer11.mods.tubion.multiport;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface TextUtils {
    static MutableText literal(String source) {
        return Text.literal(source);
    }
    static MutableText translatable(String key) {
        return Text.translatable(key);
    }
}
