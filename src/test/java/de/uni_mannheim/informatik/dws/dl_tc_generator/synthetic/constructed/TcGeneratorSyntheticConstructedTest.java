package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
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

public class TcGeneratorSyntheticConstructedTest {


    @BeforeAll
    static void setUp(){
        cleanUp();
    }

    private static final String DIR_0 = "./synthetic_constructed_tc";
    private static final String DIR_1 = "./synthetic_constructed_tc01";
    private static final String DIR_2 = "./synthetic_constructed_tc02";
    private static final String DIR_3 = "./synthetic_constructed_tc03";
    private static final String DIR_4 = "./synthetic_constructed_tc04";
    private static final String DIR_5 = "./synthetic_constructed_tc05";
    private static final String DIR_6 = "./synthetic_constructed_tc06";
    private static final String DIR_7 = "./synthetic_constructed_tc07";
    private static final String DIR_8 = "./synthetic_constructed_tc08";
    private static final String DIR_9 = "./synthetic_constructed_tc09";
    private static final String DIR_10 = "./synthetic_constructed_tc10";
    private static final String DIR_11 = "./synthetic_constructed_tc11";
    private static final String DIR_12 = "./synthetic_constructed_tc12";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<TcGeneratorSynthetic> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc01GeneratorSyntheticConstructed(DIR_1));
        generatorList.add(new Tc02GeneratorSyntheticConstructed(DIR_2));
        generatorList.add(new Tc03GeneratorSyntheticConstructed(DIR_3));
        generatorList.add(new Tc04GeneratorSyntheticConstructed(DIR_4));
        generatorList.add(new Tc05GeneratorSyntheticConstructed(DIR_5));
        generatorList.add(new Tc06GeneratorSyntheticConstructed(DIR_6));
        generatorList.add(new Tc07GeneratorSyntheticConstructed(DIR_7));
        generatorList.add(new Tc08GeneratorSyntheticConstructed(DIR_8));
        generatorList.add(new Tc09GeneratorSyntheticConstructed(DIR_9));
        generatorList.add(new Tc10GeneratorSyntheticConstructed(DIR_10));
        generatorList.add(new Tc11GeneratorSyntheticConstructed(DIR_11));
        generatorList.add(new Tc12GeneratorSyntheticConstructed(DIR_12));
    }

    /**
     * Testing some boundary conditions of all synthetic generators.
     */
    @Test
    void testAllSyntheticGenerators() {
        for (TcGeneratorSynthetic generator : generatorList) {
            generator.setNumberOfEdges(10);
            testGenerator(generator, 100, 200);
        }
    }

    public static void testGenerator(TcGeneratorSynthetic generator, int... sizes){
        if(sizes == null || sizes.length == 0){
            fail("Wrong sizes parameter. Must contain elements.");
        }
        generator.setSizes(sizes);
        generator.generate();
        File generatedDirectory = generator.getDirectory();
        assertTrue(generatedDirectory.exists());
        assertTrue(generatedDirectory.isDirectory());
        File graphFile = new File(generatedDirectory, "graph.nt");
        assertTrue(graphFile.exists());
        assertTrue(graphFile.isFile());

        for(int size : sizes) {
            File posFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "positives.txt").toFile();
            File negFile = Paths.get(generatedDirectory.getAbsolutePath(), "" + size, "negatives.txt").toFile();
            assertTrue(isOverlapFree(posFile, negFile));

            // test number of negative concepts:
            List<String> negList = Util.readUtf8FileIntoList(negFile);
            assertEquals(size, negList.size(), "Insufficient number of negatives in file " + negFile.getAbsolutePath());

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
    public static void testGenerator(TcGeneratorSynthetic generator){
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
        Util.delete(DIR_6);
        Util.delete(DIR_7);
        Util.delete(DIR_8);
        Util.delete(DIR_9);
        Util.delete(DIR_10);
        Util.delete(DIR_11);
        Util.delete(DIR_12);
    }
}