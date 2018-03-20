package com.scarlatti.certloader.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.scarlatti.certloader.plugin.AppState;
import com.scarlatti.certloader.services.LocalRepository;
import com.scarlatti.certloader.services.Repository;
import com.scarlatti.certloader.ui.Utils;

import java.nio.file.Paths;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 2/14/2018
 */
public class LocalConfig extends AbstractModule {
    @Override
    protected void configure() {
        Utils.setDarculaLaf();
    }

    @Provides
    @Singleton
    @Named("localSettingsFile")
    public String localSettingsFile() {
        return Paths.get(System.getProperty("user.home"), ".CertLoader", "settings.json").toString();
    }

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public AppState defaultAppState() {
        return AppState.defaultState();
    }

    @Provides
    @Singleton
    public Repository<AppState> repo(ObjectMapper objectMapper, @Named("localSettingsFile") String localSettingsFile, AppState defaultAppState) {
        return new LocalRepository<>(localSettingsFile, objectMapper, new TypeReference<AppState>(){}, defaultAppState);
    }
}
