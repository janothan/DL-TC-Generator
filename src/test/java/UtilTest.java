import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {


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
}