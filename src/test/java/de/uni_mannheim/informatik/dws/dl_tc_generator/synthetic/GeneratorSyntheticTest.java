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

        File tcDir = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, "" + numberTest).toFile();
        assertTrue(tcDir.exists(), "File not found: " + tcDir.getAbsolutePath());
        assertTrue(tcDir.isDirectory());

        File tcGraph = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, "graph.nt").toFile();
        assertTrue(tcGraph.exists(), "File not found: " + tcGraph.getAbsolutePath());
        assertTrue(tcGraph.isFile());

        File tcTrainTest = Paths.get(tcDir.getAbsolutePath(), "train_test", "test.txt").toFile();
        assertTrue(tcTrainTest.exists(), "File not found: " + tcTrainTest);
        assertTrue(tcTrainTest.isFile());
    }
}