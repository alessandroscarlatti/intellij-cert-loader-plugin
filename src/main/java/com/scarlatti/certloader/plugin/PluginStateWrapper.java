package com.scarlatti.certloader.plugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Friday, 2/9/2018
 */
@State(
    name = "CertLoader", storages = {
    @Storage(
        id = "com.scarlatti.certLoader",
        file = "$APP_CONFIG$/com.scarlatti.certLoader.xml")
})
public class PluginStateWrapper implements PersistentStateComponent<PluginState> {

    private PluginState state = PluginState.defaultState();

    @Nullable
    @Override
    public PluginState getState() {
        return state;
    }

    @Override
    public void loadState(final PluginState state) {
        this.state = state;
    }
}
