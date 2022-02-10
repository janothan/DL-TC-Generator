package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class Tc01SyntheticGeneratorTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc01SyntheticGeneratorTest.class);

    private static final String TC01_FILE_STR = "./tc01-generated";

    @Test
    void generate() {
        File f = new File(TC01_FILE_STR);
        Tc01SyntheticGenerator generator = new Tc01SyntheticGenerator(f);
        generator.setSizes(new int[]{50, 100});

        generator.generate();
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        File posFile = Paths.get(f.getAbsolutePath(), "50", "positives.txt").toFile();
        File negFile = Paths.get(f.getAbsolutePath(), "50", "negatives.txt").toFile();
        assertTrue(posFile.exists() && posFile.isFile());
        assertTrue(negFile.exists() && negFile.isFile());
        File ttFile = Paths.get(f.getAbsolutePath(), "50", "train_test").toFile();
        assertTrue(ttFile.exists() && ttFile.isDirectory());
        File trainFile = Paths.get(f.getAbsolutePath(), "50", "train_test", "train.txt").toFile();
        assertTrue(trainFile.exists() && trainFile.isFile());
        File testFile = Paths.get(f.getAbsolutePath(), "100", "train_test", "test.txt").toFile();
        assertTrue(testFile.exists() && testFile.isFile());
    }



    @AfterAll
    public static void cleanUp(){
        Util.delete(TC01_FILE_STR);
    }
}