package com.scarlatti.certloader.services;

import java.util.Scanner;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 7/15/2019
 */
public class CertLoaderUtils {

    public static String resourceStr(String path) {
        return new Scanner(CertLoaderUtils.class.getResourceAsStream(path)).useDelimiter("\\Z").next();
    }
}
