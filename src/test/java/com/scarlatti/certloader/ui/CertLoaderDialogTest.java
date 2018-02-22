package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.plugin.InstallAction;
import com.scarlatti.certloader.plugin.LoadAction;
import com.scarlatti.certloader.ui.controls.CertList;
import com.scarlatti.certloader.ui.controls.CertLoaderDialog;
import com.scarlatti.certloader.ui.controls.URLToolbar;
import com.scarlatti.certloader.ui.model.Cert;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class CertLoaderDialogTest {

    @Test
    public void displayDialog() {
        TestUtils.DisplayJPanel(() -> {

            List<Cert> certs = Arrays.asList(
                CertListTest.Data.Certs.sample1(),
                CertListTest.Data.Certs.sample2(),
                CertListTest.Data.Certs.sample3(),
                CertListTest.Data.Certs.sample4()
            );

            CertLoaderDialog certLoaderDialog = new CertLoaderDialog();
            certLoaderDialog.getUrlToolbar().setLoadAction(new URLToolbar.AbstractLoadAction() {

                Thread loadingThread;

                @Override
                public void load(String url, ActionCompletedCallback callback, ActionCompletedCallback errorCallback) {
                    // do loading here...

                    loadingThread = new Thread(() -> {
                        certLoaderDialog.getCertListWrapper().loading(() -> {
                            System.out.println("Timeout!");
                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            callback.callback();
                            return;
                        }
                        certLoaderDialog.getCertListWrapper().listCerts(url, certs, CertList.noOpInstallCallback());
                        callback.callback();
                    });

                    loadingThread.start();
                }

                @Override
                public void cancel(ActionCompletedCallback callback) {
                    // cancel loading here...
                    loadingThread.interrupt();
                    certLoaderDialog.getCertListWrapper().welcome();
                    callback.callback();
                }
            });

            return certLoaderDialog.getJPanel();
        });
    }

    @Test
    public void testDownloadCerts() {
        TestUtils.DisplayJPanel(() -> {
            CertLoaderDialog certLoaderDialog = new CertLoaderDialog();
            certLoaderDialog.getJPanel().revalidate();
            certLoaderDialog.getUrlToolbar().setLoadAction(
                new LoadAction(certLoaderDialog.getCertListWrapper())
            );

            certLoaderDialog.getCertListWrapper().getCertList().setInstallCallback(certs -> {
                new InstallAction(certLoaderDialog.getJPanel()).install(certs, certLoaderDialog.getAppManager().getListKeyStores().getCheckedKeyStores());
            });

            return certLoaderDialog.getJPanel();
        });
    }

//    public void revalidateContainer(Container container) {
//        for (Component c : container.getComponents()) {
//            if (c instanceof Container) {
//                revalidateContainer((Container) c);
//            }
//        }
//
//        container.revalidate();
//    }
}
