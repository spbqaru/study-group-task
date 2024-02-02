package com.example.demo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import static java.util.Objects.nonNull;

/**
 * Utility class for Json conversion back and forth.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonConversionUtils {

    private final static ClassLoader CLASS_LOADER = Thread.currentThread()
            .getContextClassLoader();

    public static <T> T getJsonAndConvert(String fileName, TypeReference<T> valueTypeRef) {
        var json = JsonConversionUtils.getJson(fileName);
        return JsonConversionUtils.convertJsonToObject(json, valueTypeRef);
    }

    public static String getJson(String fileName) {
        String content = null;
        try {
            var fileResource = CLASS_LOADER.getResource(fileName);
            if (nonNull(fileResource)) {
                var filePath = new File(fileResource.getFile()).getAbsolutePath();
                content = FileUtils.readFileToString(new File(filePath.replaceAll("%20", " ")), Charset.defaultCharset());
            }
        } catch (IOException e) {
            log.error("Error occurred while getJson for the file: " + fileName, e);
        }
        return content;
    }

    public static <T> T convertJsonToObject(String json, TypeReference<T> valueTypeRef) {
        T convertedObj = null;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.registerModule(new Jdk8Module());
            convertedObj = objectMapper.readValue(json, valueTypeRef);
        } catch (IOException e) {
            log.error("Error occurred while convertJsonToObject for the json: " + json, e);
        }
        return convertedObj;
    }

    public static String convertObjectToJson(Object o) {
        String convertedObj = null;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.registerModule(new Jdk8Module());
            convertedObj = objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            log.error("Error occurred while convertObjectToJson for the object: " + o, e);
        }
        return convertedObj;
    }
}
