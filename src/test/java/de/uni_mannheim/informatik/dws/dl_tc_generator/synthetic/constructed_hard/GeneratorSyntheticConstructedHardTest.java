package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSyntheticTest.testFileExistence;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticConstructedHardTest {


    private static final String GENERATION_DIR = "./genSyntheticConstructedHardTestDir";
    private static final String GENERATION_DIR_2 = "./genSyntheticConstructedHardTestDir2";
    private static final String MERGED_GRAPH = "./merged_graph_constructed_hard.nt";

    @BeforeAll
    public static void setUp() {
        tearDown();
    }

    @Test
    void generateTestCases() {
        GeneratorSyntheticConstructedHard generator = new GeneratorSyntheticConstructedHard(GENERATION_DIR);
        generator.setSizes(new int[]{10, 11});
        generator.setNumberOfEdges(15);
        generator.setNodesFactor(10);
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
        testFileExistence(GENERATION_DIR, "tc06h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc06h", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc07h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc07h", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc08h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc08h", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc09h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc09h", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc10h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc10h", groupName, 11);
        testFileExistence(GENERATION_DIR, "tc11h", groupName, 10);
        testFileExistence(GENERATION_DIR, "tc11h", groupName, 11);
        /*
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

         */

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