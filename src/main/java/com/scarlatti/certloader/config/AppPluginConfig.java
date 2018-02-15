package com.scarlatti.certloader.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.intellij.openapi.components.ServiceManager;
import com.scarlatti.certloader.plugin.AppState;
import com.scarlatti.certloader.plugin.PluginStateWrapper;
import com.scarlatti.certloader.services.IntelliJRepository;
import com.scarlatti.certloader.services.Repository;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 2/14/2018
 */
public class AppPluginConfig extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public Repository<AppState> repo() {
        return new IntelliJRepository<>(ServiceManager.getService(PluginStateWrapper.class));
    }
}
