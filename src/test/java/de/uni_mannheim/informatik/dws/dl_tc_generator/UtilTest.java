package de.uni_mannheim.informatik.dws.dl_tc_generator;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(UtilTest.class);

    @Test
    void writeListToFile(){
        List<String> myList = new ArrayList<>();
        myList.add("Hello");
        myList.add("World!");
        File fileToWrite = new File("./testWriteFile.txt");
        fileToWrite.deleteOnExit();
        Util.writeListToFile(fileToWrite, myList);
        String content = Util.readUtf8(fileToWrite);
        assertTrue(content.contains("Hello\n"));
        assertTrue(content.contains("World!"));

        // negative case: File null
        Util.writeListToFile(null, myList);

        // negative cases: List null
        File fileNotToWrite = new File("./doNotWriteTest.txt");
        fileNotToWrite.deleteOnExit();
        Util.writeListToFile(fileNotToWrite, null);
        assertFalse(fileNotToWrite.exists());
    }

    @Test
    void readUtf8error(){
        // non-existing file
        String result = Util.readUtf8(new File("./does-not-exist.txt"));
        assertEquals("", result);

        // null case
        result = Util.readUtf8(null);
        assertEquals("", result);
    }

    @Test
    void readUtf8(){
        File testfile = Util.loadFile("testFile.txt");
        assertNotNull(testfile, "Test file 'testfile.txt' not found.");
        String result = Util.readUtf8(testfile);
        assertNotNull(result);
        assertTrue(result.contains("Hello World!\n"));
        assertTrue(result.contains("Visit Europe."));
        System.out.println(result);
    }

    @Test
    void readUtf8FileIntoSet(){
        File testfile = Util.loadFile("testFile.txt");
        assertNotNull(testfile, "Test file 'testfile.txt' not found.");

        Set<String> result = Util.readUtf8FileIntoSet(testfile);
        assertNotNull(result);
        assertTrue(result.contains("Hello World!"));
        assertTrue(result.contains("Visit Europe."));
        assertEquals(2, result.size());
    }

    @Test
    void randomDrawFromSet(){
        Set<String> set = new HashSet<>(Arrays.asList("A", "B", "C"));
        int aCount = 0;
        int bCount = 0;
        int cCount = 0;

        for (int i = 0; i < 1000; i++) {
            String drawValue = Util.randomDrawFromSet(set);
            assertNotNull(drawValue);
            switch (drawValue) {
                case "A" -> aCount++;
                case "B" -> bCount++;
                case "C" -> cCount++;
                default -> fail("Invalid value: " + drawValue);
            }
        }
        assertTrue(aCount > 0, "A was never drawn.");
        assertTrue(bCount > 0, "B was never drawn.");
        assertTrue(cCount > 0, "C was never drawn.");
        LOGGER.info("A : B : C  :   " + aCount + " : " + bCount + " : " + cCount);
    }
}