package com.scarlatti.certloader.plugin;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Friday, 2/9/2018
 */
public class PluginState {
    private String mostRecentUrl;
    private JVM mostRecentJVM;

    public static PluginState defaultState() {
        PluginState pluginState  = new PluginState();
        pluginState.mostRecentUrl = "www.google.com";
        pluginState.mostRecentJVM = new JVM("silly jvm", "C:/somewhere");

        return pluginState;
    }

    public static class JVM {
        private String name;
        private String path;

        public JVM() {
        }

        public JVM(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public String getMostRecentUrl() {
        return mostRecentUrl;
    }

    public void setMostRecentUrl(String mostRecentUrl) {
        this.mostRecentUrl = mostRecentUrl;
    }

    public JVM getMostRecentJVM() {
        return mostRecentJVM;
    }

    public void setMostRecentJVM(JVM mostRecentJVM) {
        this.mostRecentJVM = mostRecentJVM;
    }
}
