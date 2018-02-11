package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.ui.controls.CertList;
import com.scarlatti.certloader.ui.controls.URLToolbar;
import com.scarlatti.certloader.ui.model.Cert;
import org.junit.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/10/2018
 */
public class CertListTest {

    @Test
    public void displayCertList() {
        TestUtils.DisplayJPanel(() ->
            new CertList().getJPanel()
        );
    }

    @Test
    public void displayCertListWithData() {
        TestUtils.DisplayJPanel(() -> {
            CertList certList = new CertList();
            certList.addCert(Data.Certs.sample1());
            certList.addCert(Data.Certs.sample2());
            certList.addCert(Data.Certs.sample3());
            certList.addCert(Data.Certs.sample4());

            return certList.getJPanel();
        });
    }

    @Test
    public void displayCertListWithDataAndURL() {
        TestUtils.DisplayJPanel(() -> {

            List<Cert> certs = Arrays.asList(
                Data.Certs.sample1(),
                Data.Certs.sample2(),
                Data.Certs.sample3(),
                Data.Certs.sample4()
            );

            CertList certList = new CertList("what.com", certs);

            return certList.getJPanel();
        });
    }

    @Test
    public void displayCertListDisabled() {

        List<Cert> certs = Arrays.asList(
            Data.Certs.sample1(),
            Data.Certs.sample2(),
            Data.Certs.sample3(),
            Data.Certs.sample4()
        );

        TestUtils.DisplayJPanel(() -> {

            CertList certList = new CertList("what.com", certs);
            certList.setEnabled(false);

            return certList.getJPanel();
        });
    }

    static class Data {

        static class Certs {
            static Cert sample1() {
                return new Cert(
                    0, true, "me.com", "whoville"
                );
            }

            static Cert sample2() {
                return new Cert(
                    1, true, "scarlatti.com", "dinotown"
                );
            }

            static Cert sample3() {
                return new Cert(
                    2, true, "booneville.com", "dingletown"
                );
            }

            static Cert sample4() {
                return new Cert(
                    3, true, "what.com", "bixby"
                );
            }
        }
    }
}
