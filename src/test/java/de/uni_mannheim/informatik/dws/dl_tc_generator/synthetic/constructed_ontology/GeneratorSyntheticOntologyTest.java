package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard.GeneratorSyntheticConstructedHard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        testFileExistence(GENERATION_DIR, "tc01", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc01", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc02", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc02", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc03", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc03", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc04", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc04", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc05", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc05", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc06", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc06", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc07", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc07", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc08", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc08", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc09", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc09", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc10", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc10", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc11", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc11", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc12", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc12", groupName, 11);

        File mergedGraph = new File(GENERATION_DIR, "graph.nt");
        assertTrue(mergedGraph.exists());
        assertTrue(mergedGraph.isFile());
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