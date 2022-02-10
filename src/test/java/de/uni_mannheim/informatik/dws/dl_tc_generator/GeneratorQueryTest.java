package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorQueryTest {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorQueryTest.class);

    @Test
    void generateTestCases(){
        final File resultDir = new File("./result-dir");
        resultDir.deleteOnExit();
        try {
            GeneratorQuery generator = new GeneratorQuery(Util.loadFile("testQueries"), resultDir);
            generator.generateTestCases();
            File resultFile = new File(resultDir, "tc1/cities/50/positives.txt");
            assertTrue(resultFile.exists());
            String content = Util.readUtf8(resultFile);
            assertTrue(content.contains("\n"));
            resultFile = new File(resultDir, "tc2/cities/500/positives.txt");
            assertTrue(resultFile.exists());

            // check for train/test split
            resultFile = new File(resultDir, "tc2/cities/500/train_test/test.txt");
            assertTrue(resultFile.exists());
            resultFile = new File(resultDir, "tc2/cities/500/train_test/train.txt");
            assertTrue(resultFile.exists());
            resultFile = new File(resultDir, "tc2/cities/500/train_test_hard/test.txt");
            assertTrue(resultFile.exists());
            resultFile = new File(resultDir, "tc2/cities/500/train_test_hard/train.txt");
            assertTrue(resultFile.exists());

            // make sure there are no hard cases for species
            resultFile = new File(resultDir, "tc1/species/500/train_test_hard");
            assertFalse(resultFile.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void getQueryResults(){
        final File resultDir = new File("./result-dir2");
        resultDir.deleteOnExit();
        try {
            GeneratorQuery generator = new GeneratorQuery(Util.loadFile("testQueries"), resultDir);
            List<String> result = generator.getQueryResults(Util.loadFile("testQuery.sparql"), 3);
            assertEquals(3, result.size());
        } catch (Exception e){
            fail(e);
        }
    }

    @AfterAll
    static void cleanUp(){
        Util.delete("./result-dir");
        Util.delete("./result-dir2");
    }


}