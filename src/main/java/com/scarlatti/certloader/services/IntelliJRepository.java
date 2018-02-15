package com.scarlatti.certloader.services;

import com.intellij.openapi.components.PersistentStateComponent;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 2/14/2018
 * <p>
 * This repository service will use an IntelliJ saving mechanism.
 */
public class IntelliJRepository<T> implements Repository<T> {

    private PersistentStateComponent<T> repo;

    public IntelliJRepository(PersistentStateComponent<T> repo) {
        this.repo = repo;
    }

    @Override
    public void save(T savable) {
        repo.loadState(savable);
    }

    @Override
    public T retrieve() {
        return repo.getState();
    }
}
