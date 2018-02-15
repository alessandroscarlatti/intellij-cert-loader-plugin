package com.scarlatti.certloader.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 2/14/2018
 *
 * This repository service will use Jackson JSON object mapping to save
 * data to a local storage path.
 */
public class LocalRepository<T> implements Repository<T> {

    private ObjectMapper objectMapper;
    private String path;
    private TypeReference<T> type;
    private T defaultValue;

    /**
     * Create the service to save to a particular path
     * @param path the path in which to save the object.
     *             The path need not already exist.
     */
    public LocalRepository(String path, ObjectMapper objectMapper, TypeReference<T> type, T defaultValue) {
        this.objectMapper = objectMapper;
        this.path = path;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public void save(T savable) {
        byte[] data = null;

        // make sure that the path exists and is writable
        instantiatePathIfNecessary();

        try {
            data = objectMapper.writeValueAsBytes(savable);
            Files.write(Paths.get(path), data);
        } catch (Exception e) {
            System.err.println("Error saving data: " + new String(data));
            e.printStackTrace();
        }
    }

    @Override
    public T retrieve() {
        instantiatePathIfNecessary();

        try {

            String data = new String(Files.readAllBytes(Paths.get(path)));

            if (data.isEmpty()) {
                return defaultValue;
            }

            return objectMapper.readValue(data, type);
        } catch (Exception e) {
            throw new RuntimeException("Error reading path: " + path, e);
        }
    }

    private void instantiatePathIfNecessary() {
        Path path = Paths.get(this.path);

        if (!Files.exists(path)) {
            try {
                System.out.println("Creating path to: " + path);
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (Exception e) {
                System.err.println("Error creating save path.");
                e.printStackTrace();
            }
        }

        if (!Files.exists(path)) {
            throw new IllegalStateException("File does not exist at path: " + path);
        }

        if (!Files.isWritable(path)) {
            throw new IllegalStateException("File does not exist at path: " + path);
        }
    }
}
