package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.ResultValidator.isOverlapFree;
import static org.junit.jupiter.api.Assertions.*;

class SyntheticGeneratorTest {

    private static final String DIR_0 = "./synthetic_tc";
    private static final String DIR_1 = "./synthetic_tc01";
    private static final String DIR_2 = "./synthetic_tc02";
    private static final String DIR_3 = "./synthetic_tc03";
    private static final String DIR_4 = "./synthetic_tc04";
    private static final String DIR_5 = "./synthetic_tc05";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<SyntheticGenerator> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc01SyntheticGenerator(DIR_1));
        generatorList.add(new Tc02SyntheticGenerator(DIR_2));
        generatorList.add(new Tc03SyntheticGenerator(DIR_3));
        generatorList.add(new Tc04SyntheticGenerator(DIR_4));
        generatorList.add(new Tc05SyntheticGenerator(DIR_5));
    }

    /**
     * Testing some boundary conditions of all synthetic generators.
     */
    @Test
    void testAllSyntheticGenerators() {
        for (SyntheticGenerator generator : generatorList) {
            testGenerator(generator);
        }
    }

    public static void testGenerator(SyntheticGenerator generator, int... sizes){
        if(sizes == null || sizes.length == 0){
            fail("Wrong sizes parameter. Must contain elements.");
        }
        generator.setSizes(sizes);
        generator.generate();
        File generatedDirectory = generator.getDirectory();
        assertTrue(generatedDirectory.exists());
        assertTrue(generatedDirectory.isDirectory());
        File graphFile= new File(generatedDirectory, "graph.nt");
        assertTrue(graphFile.exists());
        assertTrue(graphFile.isFile());

        for(int size : sizes) {
            File posFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "positives.txt").toFile();
            File negFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "negatives.txt").toFile();
            assertTrue(isOverlapFree(posFile, negFile));

            // test number of negative concepts:
            List<String> negList = Util.readUtf8FileIntoList(negFile);
            assertEquals(size, negList.size());

            assertTrue(posFile.exists() && posFile.isFile());
            assertTrue(negFile.exists() && negFile.isFile());
            File ttFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "train_test").toFile();
            assertTrue(ttFile.exists() && ttFile.isDirectory());
            File trainFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "train_test", "train.txt").toFile();
            assertTrue(trainFile.exists() && trainFile.isFile());
            File testFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "train_test", "test.txt").toFile();
            assertTrue(testFile.exists() && testFile.isFile());
        }
    }

    /**
     * Some assertions on the specified generator.
     * @param generator The generator to be tested.
     */
    public static void testGenerator(SyntheticGenerator generator){
        testGenerator(generator, 50, 100);
    }

    @Test
    void testUniqueName(){
        Set<String> ids = new HashSet<>();
        generatorList.forEach(x -> assertNotNull(x.getTcId()));
        generatorList.forEach(x -> ids.add(x.getTcId()));
        assertEquals(ids.size(), generatorList.size());
    }

    @AfterAll
    public static void cleanUp() {
        Util.delete(DIR_0);
        Util.delete(DIR_1);
        Util.delete(DIR_2);
        Util.delete(DIR_3);
        Util.delete(DIR_4);
        Util.delete(DIR_5);
    }

}