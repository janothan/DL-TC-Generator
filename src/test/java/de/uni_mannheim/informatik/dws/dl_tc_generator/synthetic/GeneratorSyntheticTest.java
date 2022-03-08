package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorSyntheticTest {

    /**
     * Some helper test method.
     * @param generationDirectory The generation directory.
     * @param tcName The test case name.
     * @param numberTest The test number.
     */
    public static void testFileExistence(String generationDirectory, String tcName, String tcGroupName, int numberTest
                                          ) {
        File resultDir = new File(generationDirectory);
        assertTrue(resultDir.exists());
        assertTrue(resultDir.isDirectory());

        File tc03_10 = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, "" + numberTest).toFile();
        assertTrue(tc03_10.exists(), "File not found: " + tc03_10.getAbsolutePath());
        assertTrue(tc03_10.isDirectory());

        File tc03_10graph = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, "graph.nt").toFile();
        assertTrue(tc03_10graph.exists(), "File not found: " + tc03_10graph.getAbsolutePath());
        assertTrue(tc03_10graph.isFile());

        File tc03_20test = Paths.get(tc03_10.getAbsolutePath(), "train_test", "test.txt").toFile();
        assertTrue(tc03_20test.exists(), "File not found: " + tc03_20test);
        assertTrue(tc03_20test.isFile());
    }



}