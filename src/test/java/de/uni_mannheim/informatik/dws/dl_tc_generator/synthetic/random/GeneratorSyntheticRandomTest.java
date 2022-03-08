package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSyntheticTest.testFileExistence;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorSyntheticRandomTest {


    private static final String GENERATION_DIR = "./genSyntheticRandomTestDir";
    private static final String GENERATION_DIR_2 = "./genSyntheticRandomTestDir2";
    private static final String MERGED_GRAPH = "./merged_graph_random.nt";

    @BeforeAll
    public static void setUp() {
        tearDown();
    }

    @Test
    void generateTestCases() {
        GeneratorSynthetic generator = new GeneratorSyntheticRandom(GENERATION_DIR);
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

    @Test
    void generateTestCasesWithSelector() {
        GeneratorSynthetic generator = new GeneratorSyntheticRandom(GENERATION_DIR_2);
        generator.setSizes(new int[]{10});
        generator.setIncludeOnlyCollection("tc01", "tc02");
        generator.setNumberOfEdges(10);
        generator.generateTestCases();

        testFileExistence(GENERATION_DIR_2, "tc01", generator.getTcGroupName(), 10);
        testFileExistence(GENERATION_DIR_2, "tc02", generator.getTcGroupName(), 10);

        File tc03_10 = Paths.get(GENERATION_DIR_2, "tc03", "synthetic", "10").toFile();
        assertFalse(tc03_10.exists());
    }

    @Test
    void mergeGraphsToOne() {
        File testSynDir = Util.loadFile("./genSyntheticTestDir");
        assertTrue(testSynDir.exists(), "The test resource 'genSyntheticTestDir' could not be found.");
        assertTrue(testSynDir.isDirectory(), "The test resource 'genSyntheticTestDir' is not a directory.");
        File mergedGraphFile = new File(MERGED_GRAPH);
        assertFalse(mergedGraphFile.exists());
        GeneratorSynthetic.mergeGraphsToOne(testSynDir, mergedGraphFile);
        assertTrue(mergedGraphFile.exists());
        assertTrue(mergedGraphFile.isFile());
        String fileContent = Util.readUtf8(mergedGraphFile);
        assertTrue(fileContent.contains("<N_TC02_96>"));
        assertTrue(fileContent.contains("<N_TC08_47>"));
        assertTrue(fileContent.contains("<N_TC12_98>"));
        assertFalse(fileContent.contains("<NOT_EXITS_404>"));
    }

    @AfterAll
    public static void tearDown() {
        Util.delete(GENERATION_DIR);
        Util.delete(GENERATION_DIR_2);
        Util.delete(MERGED_GRAPH);
    }

}