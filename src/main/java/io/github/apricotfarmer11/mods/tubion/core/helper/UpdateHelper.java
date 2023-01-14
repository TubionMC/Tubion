package io.github.apricotfarmer11.mods.tubion.core.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class UpdateHelper {
    public static String getLatestVersion() throws IOException {
        String json = IOUtils.toString(URI.create("https://api.modrinth.com/v2/project/E6BMMeJm/version"), Charset.forName("UTF-8"));
        JsonElement elt = JsonParser.parseString(json);
        return elt.getAsJsonArray().get(0).getAsJsonObject().get("version_number").getAsString().split("-")[0];
    }
}
