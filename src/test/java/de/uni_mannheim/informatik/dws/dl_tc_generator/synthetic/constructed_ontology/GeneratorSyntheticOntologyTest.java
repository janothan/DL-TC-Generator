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
        GeneratorSyntheticOntology generator = new GeneratorSyntheticOntology(GENERATION_DIR);
        generator.setSizes(new int[]{10, 11});
        generator.setNumberOfEdges(15);
        generator.setNodesFactor(10);
        generator.generateTestCases();
        final String groupName = generator.getTcGroupName();
        testFileExistence(GENERATION_DIR, "tc01", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc01", groupName, 11);

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