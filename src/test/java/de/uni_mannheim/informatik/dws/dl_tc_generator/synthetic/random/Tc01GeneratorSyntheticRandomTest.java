package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.ResultValidator.isOverlapFree;
import static org.junit.jupiter.api.Assertions.*;

class Tc01GeneratorSyntheticRandomTest {


    //private static final Logger LOGGER = LoggerFactory.getLogger(Tc01SyntheticGeneratorTest.class);

    private static final String TC01_FILE_STR = "./tc01-generated";

    @Test
    void generate() {
        File f = new File(TC01_FILE_STR);
        Tc01GeneratorSyntheticRandom generator = new Tc01GeneratorSyntheticRandom(f);
        generator.setSizes(new int[]{50, 100});

        generator.generate();
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        File posFile = Paths.get(f.getAbsolutePath(), "100", "positives.txt").toFile();
        File negFile = Paths.get(f.getAbsolutePath(), "100", "negatives.txt").toFile();

        assertTrue(isOverlapFree(posFile, negFile));

        // test number of negative concepts:
        List<String> negList = Util.readUtf8FileIntoList(negFile);
        assertEquals(100, negList.size());

        assertTrue(posFile.exists() && posFile.isFile());
        assertTrue(negFile.exists() && negFile.isFile());
        File ttFile = Paths.get(f.getAbsolutePath(), "50", "train_test").toFile();
        assertTrue(ttFile.exists() && ttFile.isDirectory());
        File trainFile = Paths.get(f.getAbsolutePath(), "50", "train_test", "train.txt").toFile();
        assertTrue(trainFile.exists() && trainFile.isFile());
        File testFile = Paths.get(f.getAbsolutePath(), "100", "train_test", "test.txt").toFile();
        assertTrue(testFile.exists() && testFile.isFile());
    }

    @Test
    void setGetNumberOfEdges(){
        Tc01GeneratorSyntheticRandom generator = new Tc01GeneratorSyntheticRandom(new File(TC01_FILE_STR));
        assertEquals(Defaults.NUMBER_OF_EDGES, generator.getNumberOfEdges());
        generator.setNumberOfEdges(-10);
        assertEquals(Defaults.NUMBER_OF_EDGES, generator.getNumberOfEdges());
        generator.setNumberOfEdges(10);
        assertEquals(10, generator.getNumberOfEdges());
    }

    @AfterAll
    public static void cleanUp(){
        Util.delete(TC01_FILE_STR);
    }
}