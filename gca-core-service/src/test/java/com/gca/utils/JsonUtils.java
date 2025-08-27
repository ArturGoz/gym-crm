package com.gca.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

@UtilityClass
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(Object obj) {
        try {
            return objectMapper
                    .findAndRegisterModules()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    public static <T> T readJson(String path, Class<T> clazz) {
        try {
            String content = Files.readString(Paths.get("src/test/resources/json/" + path));
            return objectMapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to read JSON from '%s'", path), e);
        }
    }
}
