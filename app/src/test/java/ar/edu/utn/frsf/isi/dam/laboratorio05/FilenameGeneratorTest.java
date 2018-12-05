package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.service.autofill.FieldClassification;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ar.edu.utn.frsf.isi.dam.laboratorio05.FilenameGenerator.audioFilename;
import static ar.edu.utn.frsf.isi.dam.laboratorio05.FilenameGenerator.imageFilename;
import static org.junit.Assert.*;

public class FilenameGeneratorTest {

    @Test
    public void audioFilenameTest() {
        try {
            final TemporaryFolder testDir = new TemporaryFolder();
            testDir.create();
            final File f = testDir.newFolder();

            Pattern p = Pattern.compile(f + "/bar_" + "\\d*" + ".3gp");

            String filenameResult = audioFilename(
                    f,
                    "bar_"
            ).getAbsolutePath();

            Matcher m = p.matcher(filenameResult);

            assertTrue(m.matches());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void imageFilenameTest() {
        try {
            final TemporaryFolder testDir = new TemporaryFolder();
            testDir.create();
            final File f = testDir.newFolder("FOOBAR");

            Pattern p = Pattern.compile(f + "/test_" + "\\d*" + ".jpg");

            String filenameResult = imageFilename(
                    f,
                    "/test_"
            ).getAbsolutePath();

            Matcher m = p.matcher(filenameResult);
            
            assertTrue(m.matches());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}