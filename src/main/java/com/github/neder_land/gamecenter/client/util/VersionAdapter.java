package com.github.neder_land.gamecenter.client.util;

import com.google.gson.*;
import neder_land.lib.Version;

import java.lang.reflect.Type;

public class VersionAdapter implements JsonSerializer<Version>, JsonDeserializer<Version> {
    @Override
    public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Version.parse(json.getAsString()).orElseThrow(() -> new JsonParseException("Wrong format of version!"));
    }

    @Override
    public JsonElement serialize(Version src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
