package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard.GeneratorSyntheticConstructedHard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSyntheticTest.testFileExistence;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticOntologyTest {


    private static final String GENERATION_DIR = "./SyntheticOntologyTest";

    @BeforeAll
    public static void setUp() {
        tearDown();
    }

    @Test
    void generateTestCases() {
        GeneratorSyntheticOntology generator = new GeneratorSyntheticOntology(
                new File(GENERATION_DIR),
                10,
                10,
                10,
                5,
                2,
                new int[]{10,11});
        generator.generateTestCases();
        final String groupName = generator.getTcGroupName();
        testOntologyExistence(GENERATION_DIR, "tc01", groupName);
        testFileExistence(GENERATION_DIR, "tc01", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc01", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc02", groupName);
        testFileExistence(GENERATION_DIR, "tc02", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc02", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc03", groupName);
        testFileExistence(GENERATION_DIR, "tc03", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc03", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc04", groupName);
        testFileExistence(GENERATION_DIR, "tc04", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc04", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc05", groupName);
        testFileExistence(GENERATION_DIR, "tc05", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc05", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc06", groupName);
        testFileExistence(GENERATION_DIR, "tc06", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc06", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc07", groupName);
        testFileExistence(GENERATION_DIR, "tc07", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc07", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc08", groupName);
        testFileExistence(GENERATION_DIR, "tc08", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc08", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc09", groupName);
        testFileExistence(GENERATION_DIR, "tc09", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc09", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc10", groupName);
        testFileExistence(GENERATION_DIR, "tc10", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc10", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc11", groupName);
        testFileExistence(GENERATION_DIR, "tc11", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc11", groupName, 11);
        testOntologyExistence(GENERATION_DIR, "tc12", groupName);
        testFileExistence(GENERATION_DIR, "tc12", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc12", groupName, 11);

        File mergedGraph = new File(GENERATION_DIR, "graph.nt");
        assertTrue(mergedGraph.exists());
        assertTrue(mergedGraph.isFile());
    }

    void testOntologyExistence(String generationDirectory, String tcName, String tcGroupName){
        File resultDir = new File(generationDirectory);
        assertTrue(resultDir.exists());
        assertTrue(resultDir.isDirectory());
        File tcOntology = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, "ontology.nt").toFile();
        assertTrue(tcOntology.exists(), "File not found: " + tcOntology.getAbsolutePath());
        assertTrue(tcOntology.isFile());
        File dglkeFile = Paths.get(resultDir.getAbsolutePath(), tcName, tcGroupName, Defaults.DEFAULT_DGL_KE_DIR).toFile();
        assertTrue(dglkeFile.exists());
    }

    @Test
    void getTcGroupName() {
        GeneratorSyntheticConstructedHard generator = new GeneratorSyntheticConstructedHard(GENERATION_DIR);
        assertNotNull(generator.getTcGroupName());
    }

    @AfterAll
    public static void tearDown() {
        Util.delete(GENERATION_DIR);
    }

}