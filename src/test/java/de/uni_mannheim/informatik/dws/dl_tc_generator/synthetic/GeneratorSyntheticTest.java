package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.ResultValidator.isOverlapFree;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticTest {


    private static final String GENERATION_DIR = "./genSyntheticTestDir";

    @BeforeAll
    public static void setUp(){
        tearDown();
    }

    @Test
    void generateTestCases(){
        GeneratorSynthetic generator = new GeneratorSynthetic(GENERATION_DIR);
        generator.setSizes(new int[]{10, 20});
        generator.generateTestCases();

        testFileExistance("tc01", 10);
        testFileExistance("tc01", 20);
        testFileExistance("tc03", 10);
        testFileExistance("tc03", 20);
    }

    static void testFileExistance(String tcName, int numberTest){
        File resultDir = new File(GENERATION_DIR);
        assertTrue(resultDir.exists());
        assertTrue(resultDir.isDirectory());

        File tc03_10 = Paths.get(resultDir.getAbsolutePath(), tcName, "synthetic", "" + numberTest).toFile();
        assertTrue(tc03_10.exists());
        assertTrue(tc03_10.isDirectory());

        File tc03_10graph = Paths.get(resultDir.getAbsolutePath(), tcName, "synthetic", "graph.nt").toFile();
        assertTrue(tc03_10graph.exists());
        assertTrue(tc03_10graph.isFile());

        File tc03_20test = Paths.get(tc03_10.getAbsolutePath(), "train_test", "test.txt").toFile();
        assertTrue(tc03_20test.exists());
        assertTrue(tc03_20test.isFile());
    }

    @AfterAll
    public static void tearDown(){
        Util.delete(GENERATION_DIR);
    }

}