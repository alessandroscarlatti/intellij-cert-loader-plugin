package com.scarlatti.certloader.ui.model;

import java.security.cert.X509Certificate;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class Cert {
    private int key;
    private boolean selected;
    private String companyName;
    private String locationName;
    private X509Certificate rawCert;
    private String url;

    public Cert() {
    }

    public Cert(int key, boolean selected, String companyName, String locationName) {
        this.key = key;
        this.selected = selected;
        this.companyName = companyName;
        this.locationName = locationName;
    }

    public Cert(int key, boolean selected, String companyName, String locationName, X509Certificate rawCert, String url) {
        this.key = key;
        this.selected = selected;
        this.companyName = companyName;
        this.locationName = locationName;
        this.rawCert = rawCert;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Cert{" +
            "key=" + key +
            ", selected=" + selected +
            ", companyName='" + companyName + '\'' +
            ", locationName='" + locationName + '\'' +
            '}';
    }

    public int getKey() {
        return key;
    }

    public void setKey(int id) {
        this.key = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public X509Certificate getRawCert() {
        return rawCert;
    }

    public void setRawCert(X509Certificate rawCert) {
        this.rawCert = rawCert;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
