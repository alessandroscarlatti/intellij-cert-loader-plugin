package com.scarlatti.certloader.services;

import com.scarlatti.certloader.ui.model.KeyStore;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Wednesday, 3/7/2018
 */
public class DefaultKeyStoreService {

    /**
     * Try to find the default JDK key stores on this machine.
     *
     * Will search in the java home directory
     *
     * @return list of default key stores
     */
    public static List<KeyStore> getDefaultKeyStores() {

        String javaHome = System.getProperty("java.home");

        System.out.printf("Searching for key stores in JAVA_HOME %s%n", javaHome);

        List<String> keyStorePaths = new ArrayList<>();

        Path searchStartPath = Paths.get(javaHome);

        for (int i = 0; i < 3; i++) {

            if (searchStartPath.getFileName().toString().equalsIgnoreCase("java")) break;

            if (searchStartPath.getParent() != null) {
                searchStartPath = searchStartPath.getParent();
            }
        }

        try {
            Files.walkFileTree(searchStartPath, new HashSet<>(Collections.singletonList(FileVisitOption.FOLLOW_LINKS)),
                    Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path path , BasicFileAttributes attrs) throws IOException {

                            if (path.getFileName().toString().equals("cacerts")) {
                                System.out.printf("Visiting file %s\n", path);
                                keyStorePaths.add(path.toString());
                            }

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file , IOException e) throws IOException {
                            System.err.printf("Visiting failed for %s\n", file);

                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException {
                            System.out.printf("About to visit directory %s\n", dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error getting default key stores.", e);
        }

        return buildKeyStores(keyStorePaths);
    }

    public static List<KeyStore> buildKeyStores(List<String> paths) {
        List<KeyStore> keyStores = new ArrayList<>();

        for (String path : paths) {

            String keyStoreName = getKeyStoreName(path);

            if (keyStoreName.startsWith("jdk")) {
                keyStores.add(new KeyStore(
                    true,
                    keyStoreName,
                    path,
                    "changeit"
                ));
            }
        }

        return keyStores;
    }

    public static String getKeyStoreName(String path) {
        List<String> pieces = Arrays.asList(path.split("\\\\"));

        String name = Paths.get(path).getFileName().toString();

        for (String piece : pieces) {
            if (piece.startsWith("jdk") || piece.startsWith("jre")) {
                name = piece;
                break;
            }
        }

        return name;
    }
}
