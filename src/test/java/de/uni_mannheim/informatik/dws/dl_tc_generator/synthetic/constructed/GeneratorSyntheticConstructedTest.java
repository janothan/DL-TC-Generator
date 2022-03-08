package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSyntheticTest.testFileExistence;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticConstructedTest {


    private static final String GENERATION_DIR = "./genSyntheticConstructedTestDir";
    private static final String GENERATION_DIR_2 = "./genSyntheticConstructedTestDir2";
    private static final String MERGED_GRAPH = "./merged_graph_constructed.nt";

    @BeforeAll
    public static void setUp() {
        tearDown();
    }

    @Test
    void generateTestCases() {
        GeneratorSynthetic generator = new GeneratorSyntheticConstructed(GENERATION_DIR);
        generator.setSizes(new int[]{10, 11});
        generator.setNumberOfEdges(10);
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

    @AfterAll
    public static void tearDown() {
        Util.delete(GENERATION_DIR);
        Util.delete(GENERATION_DIR_2);
        Util.delete(MERGED_GRAPH);
    }

}