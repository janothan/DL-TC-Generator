package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.*;
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
import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSyntheticTest.testFileExistence;
import static org.junit.jupiter.api.Assertions.*;

class TcGeneratorSyntheticConstructedHardTest {


    private static final String DIR_6 = "./synthetic_constructed_hard_tc06h";

    /**
     * A list of all synthetic generators which can be used in multiple test cases.
     */
    private static final List<TcGeneratorSynthetic> generatorList = new ArrayList<>();

    static {
        generatorList.add(new Tc06GeneratorSyntheticConstructedHard(DIR_6));
    }

    @BeforeAll
    static void setUp(){
        cleanUp();
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
        File graphFile= new File(generatedDirectory, "graph.nt");
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

        Util.delete(DIR_6);

    }

}