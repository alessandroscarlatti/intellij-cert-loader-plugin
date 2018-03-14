package com.scarlatti.certloader.services;

import com.scarlatti.certloader.exceptions.ProcessAbortedException;
import com.scarlatti.certloader.ui.controls.CertListWrapper;
import com.scarlatti.certloader.ui.controls.URLToolbar;
import com.scarlatti.certloader.ui.model.Cert;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Sunday, 2/11/2018
 */
public class LoadCertService extends URLToolbar.AbstractLoadAction {

    private Thread loadingThread;
    private CertListWrapper certListWrapper;
    private CountDownLatch latch;
    private ActionCompletedCallback successCallback;
    private ActionCompletedCallback errorCallback;
    private String url;
    private List<X509Certificate> rawCerts;
    private List<Cert> certs;
    private Exception exception;

    public LoadCertService(CertListWrapper certListWrapper) {
        this.certListWrapper = certListWrapper;
    }

    public void reset() {
        loadingThread = null;
        latch = null;
        successCallback = null;
        errorCallback = null;
        url = null;
        rawCerts = null;
        certs = null;
        exception = null;
    }

    @Override
    public void load(String url, ActionCompletedCallback successCallback, ActionCompletedCallback errorCallback) {
        // do loading here...
        reset();
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        this.url = url;
        latch = new CountDownLatch(1);
        loadingThread = new Thread(this::download, "LoadCertService");
        loadingThread.start();

        // now wait for the countdown latch...
        // we should proceed when:
        // 1) the downloader has finished and there are results in the expected location...
        // 2) OR the progress bar has timed out and there are no results...
        // 3) OR the connection had an error and there are no results...
        // 4) OR the user canceled the download and there are no results...

        try {
            latch.await();

            // now determine which case we are in :)
            if (exception == null && certs != null) {
                // now we know we have some certs!
                certListWrapper.stopLoading();
                certListWrapper.listCerts(url, certs);
                this.successCallback.callback();
            } else if (exception != null) {
                exception.printStackTrace();  // TODO when this is caught, we should fire off the cancel action
                certListWrapper.stopLoading();
                errorCallback.callback();
                certListWrapper.error(url, exception);
            } else {
                System.err.println("Error downloading certificate(s).  Please try again!");
            }



        } catch (InterruptedException e) {
            System.err.println("Unknown interruption while downloading certificate(s).");
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromFile(String path, byte[] bytes, ActionCompletedCallback successCallback, ActionCompletedCallback errorCallback) {

        // do loading here...
        reset();
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        this.url = path;
        latch = new CountDownLatch(1);
        loadingThread = new Thread(this::loadFromFile, "LoadCertService");
        loadingThread.start();

        // now wait for the countdown latch...
        // we should proceed when:
        // 1) the downloader has finished and there are results in the expected location...
        // 2) OR the progress bar has timed out and there are no results...
        // 3) OR the connection had an error and there are no results...
        // 4) OR the user canceled the download and there are no results...

        try {
            latch.await();

            // now determine which case we are in :)
            if (exception == null && certs != null) {
                // now we know we have some certs!
                certListWrapper.stopLoading();
                certListWrapper.listCerts(url, certs);
                this.successCallback.callback();
            } else if (exception != null) {
                exception.printStackTrace();  // TODO when this is caught, we should fire off the cancel action
                certListWrapper.stopLoading();
                errorCallback.callback();
                certListWrapper.error(url, exception);
            } else {
                System.err.println("Error downloading certificate(s).  Please try again!");
            }

        } catch (InterruptedException e) {
            System.err.println("Unknown interruption while downloading certificate(s).");
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            certListWrapper.loading(() -> {
                exception = new ProcessAbortedException("Connection attempt timed out.");
                latch.countDown();
            });

            rawCerts = DownloadCertService.downloadCertsFromFile(Files.readAllBytes(Paths.get(url)));
            certs = buildViewModelCerts(rawCerts);
            latch.countDown();
        } catch (Exception e) {
            exception = e;
            latch.countDown();
        }
    }

    public void download() {
        try {
            certListWrapper.loading(() -> {
                exception = new ProcessAbortedException("Connection attempt timed out.");
                latch.countDown();
            });

            rawCerts = DownloadCertService.downloadCerts(url);
            certs = buildViewModelCerts(rawCerts);
            latch.countDown();
        } catch (Exception e) {
            exception = e;
            latch.countDown();
        }
    }

    @Override
    public void cancel(ActionCompletedCallback callback) {
        // cancel loading here...
        latch.countDown();
        certListWrapper.welcome();
        callback.callback();
    }

    /**
     * Convert to View Model certs.  TODO get the correct info!
     * @param rawCerts
     * @return
     */
    public List<Cert> buildViewModelCerts(List<X509Certificate> rawCerts) {
        List<Cert> certs = new ArrayList<>();

        int i = 0;
        for (X509Certificate cert : rawCerts) {
            certs.add(new Cert(
                i,
                true,
                cert.getIssuerDN().getName(),
                cert.getIssuerDN().getName(),
                cert,
                url
            ));
            i++;
        }

        return certs;
    }


}
