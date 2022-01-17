import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {


    @Test
    void readUtf8(){
        String result = Util.readUtf8(Util.loadFile("testfile.txt"));
        assertNotNull(result);
        assertTrue(result.contains("Hello World!\n"));
        assertTrue(result.contains("Visit Europe."));
        System.out.println(result);
    }

    @Test
    void readUtf8FileIntoSet(){
        Set<String> result = Util.readUtf8FileIntoSet(Util.loadFile("testfile.txt"));
        assertNotNull(result);
        assertTrue(result.contains("Hello World!"));
        assertTrue(result.contains("Visit Europe."));
        assertEquals(2, result.size());
    }
}