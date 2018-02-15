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
 *
 * This class is generated as a singleton by IntelliJ during initialization.
 * This configuration is provided in the plugin configuration xml.
 */
@State(
    name = "CertLoader", storages = {
    @Storage(
        id = "com.scarlatti.certLoader",
        file = "$APP_CONFIG$/com.scarlatti.certLoader.xml")
})
public class PluginStateWrapper implements PersistentStateComponent<AppState> {

    private AppState state = AppState.defaultState();

    @Nullable
    @Override
    public AppState getState() {
        return state;
    }

    @Override
    public void loadState(final AppState state) {
        this.state = state;
    }
}
