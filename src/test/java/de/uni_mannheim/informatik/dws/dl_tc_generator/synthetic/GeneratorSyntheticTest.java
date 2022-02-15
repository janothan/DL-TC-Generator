package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticTest {


    private static final String GENERATION_DIR = "./genSyntheticTestDir";
    private static final String GENERATION_DIR_2 = "./genSyntheticTestDir2";

    @BeforeAll
    public static void setUp(){
        tearDown();
    }

    @Test
    void generateTestCases(){
        GeneratorSynthetic generator = new GeneratorSynthetic(GENERATION_DIR);
        generator.setSizes(new int[]{10, 11});
        generator.setNumberOfEdges(10);
        generator.generateTestCases();

        testFileExistence(GENERATION_DIR, "tc01", 10);
        testFileExistence(GENERATION_DIR, "tc01", 11);
        testFileExistence(GENERATION_DIR, "tc02", 10);
        testFileExistence(GENERATION_DIR, "tc02", 11);
        testFileExistence(GENERATION_DIR, "tc03", 10);
        testFileExistence(GENERATION_DIR, "tc03", 11);
        testFileExistence(GENERATION_DIR, "tc04", 10);
        testFileExistence(GENERATION_DIR, "tc04", 11);
        testFileExistence(GENERATION_DIR, "tc05", 10);
        testFileExistence(GENERATION_DIR, "tc05", 11);
        testFileExistence(GENERATION_DIR, "tc06", 10);
        testFileExistence(GENERATION_DIR, "tc06", 11);
        testFileExistence(GENERATION_DIR, "tc07", 10);
        testFileExistence(GENERATION_DIR, "tc07", 11);
        testFileExistence(GENERATION_DIR, "tc08", 10);
        testFileExistence(GENERATION_DIR, "tc08", 11);
        testFileExistence(GENERATION_DIR, "tc09", 10);
        testFileExistence(GENERATION_DIR, "tc09", 11);
        testFileExistence(GENERATION_DIR, "tc10", 10);
        testFileExistence(GENERATION_DIR, "tc10", 11);
        testFileExistence(GENERATION_DIR, "tc11", 10);
        testFileExistence(GENERATION_DIR, "tc11", 11);
        testFileExistence(GENERATION_DIR, "tc12", 10);
        testFileExistence(GENERATION_DIR, "tc12", 11);
    }

    @Test
    void generateTestCasesWithSelector(){
        GeneratorSynthetic generator = new GeneratorSynthetic(GENERATION_DIR_2);
        generator.setSizes(new int[]{10});
        generator.setIncludeOnlyCollection("tc01", "tc02");
        generator.setNumberOfEdges(10);
        generator.generateTestCases();

        testFileExistence(GENERATION_DIR_2, "tc01", 10);
        testFileExistence(GENERATION_DIR_2, "tc02", 10);

        File tc03_10 = Paths.get(GENERATION_DIR_2, "tc03", "synthetic", "10").toFile();
        assertFalse(tc03_10.exists());
    }

    static void testFileExistence(String generationDirectory, String tcName, int numberTest){
        File resultDir = new File(generationDirectory);
        assertTrue(resultDir.exists());
        assertTrue(resultDir.isDirectory());

        File tc03_10 = Paths.get(resultDir.getAbsolutePath(), tcName, "synthetic", "" + numberTest).toFile();
        assertTrue(tc03_10.exists(), "File not found: " + tc03_10.getAbsolutePath());
        assertTrue(tc03_10.isDirectory());

        File tc03_10graph = Paths.get(resultDir.getAbsolutePath(), tcName, "synthetic", "graph.nt").toFile();
        assertTrue(tc03_10graph.exists(), "File not found: " + tc03_10graph.getAbsolutePath());
        assertTrue(tc03_10graph.isFile());

        File tc03_20test = Paths.get(tc03_10.getAbsolutePath(), "train_test", "test.txt").toFile();
        assertTrue(tc03_20test.exists(), "File not found: " + tc03_20test);
        assertTrue(tc03_20test.isFile());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(GENERATION_DIR);
        Util.delete(GENERATION_DIR_2);
    }

}