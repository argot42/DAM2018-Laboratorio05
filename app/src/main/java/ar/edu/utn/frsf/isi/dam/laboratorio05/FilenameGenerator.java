package ar.edu.utn.frsf.isi.dam.laboratorio05;

import java.io.File;
import java.io.IOException;

public abstract class FilenameGenerator {
    public static File audioFilename (File path, String fileName) throws IOException {
        return File.createTempFile(
                fileName,
                ".3gp",
                path
        );
    }

    public static File imageFilename (File path, String fileName) throws IOException {
        return File.createTempFile(
                fileName,           /* prefix */
                ".jpg",     /* suffix */
                path                /* directory */
        );
    }
}
