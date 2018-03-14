package com.scarlatti.certloader.ui.model;

import java.io.Serializable;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 2/12/2018
 */
public class KeyStore implements Serializable {
    private transient boolean selected;
    private String name;
    private String path;
    private String password;

    public KeyStore() {
    }

    public KeyStore(boolean selected, String name, String path, String password) {
        this.selected = selected;
        this.name = name;
        this.path = path;
        this.password = password;
    }

    public KeyStore(KeyStore other) {
        this.selected = other.selected;
        this.name = other.name;
        this.path = other.path;
        this.password = other.password;
    }

    @Override
    public String toString() {
        return "KeyStore{" +
            "selected=" + selected +
            ", name='" + name + '\'' +
            ", path='" + path + '\'' +
            ", password='" + password + '\'' +
            '}';
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
