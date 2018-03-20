package com.scarlatti.certloader.plugin;

import com.scarlatti.certloader.ui.model.KeyStore;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Friday, 2/9/2018
 */
public class AppState {
    private String mostRecentUrl;
    private List<KeyStore> keyStores;
    private String settingsFile;

    public static AppState defaultState() {
        AppState appState = new AppState();
        appState.mostRecentUrl = Defaults.mostRecentUrl();
        appState.keyStores = Defaults.keyStores();

        return appState;
    }

    public AppState() {
        keyStores = new ArrayList<>();
        settingsFile = Paths.get(System.getProperty("user.home"), ".certloader.settings.json").toString();
    }

    private static class Defaults {

        public static String mostRecentUrl() {
            return "www.google.com";
        }

        public static List<KeyStore> keyStores() {
            return Collections.emptyList();
        }

        public static class KeyStores {

            public static KeyStore sample1() {
                return new KeyStore(
                    true,
                    "JDK 8.1",
                    "C:/Users/pc/Desktop/cacerts",
                    "changeit"
                );
            }

            public static KeyStore sample2() {
                return new KeyStore(
                    true,
                    "JDK 7.2",
                    "C:/Users/pc/Desktop/cacerts",
                    "changeit"
                );
            }
        }
    }

    @Override
    public String toString() {
        return "AppState{" +
            "mostRecentUrl='" + mostRecentUrl + '\'' +
            ", keyStores=" + keyStores +
            '}';
    }

    public String getMostRecentUrl() {
        return mostRecentUrl;
    }

    public void setMostRecentUrl(String mostRecentUrl) {
        this.mostRecentUrl = mostRecentUrl;
    }

    public List<KeyStore> getKeyStores() {
        return keyStores;
    }

    public void setKeyStores(List<KeyStore> keyStores) {
        this.keyStores = keyStores;
    }

    public String getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(String settingsFile) {
        this.settingsFile = settingsFile;
    }
}
