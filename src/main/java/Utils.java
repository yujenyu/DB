import core.DBEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    public static void listDbRootDir() throws IOException {
        String dbRootDir = DBEngine.getDbRootDir();

        // System.out.println("DEBUG: " + dbRootDir);
        Files.list(new File(dbRootDir).toPath())
                .forEach(path -> {
                    System.out.println("dir: " + path);
                });
    }
}
