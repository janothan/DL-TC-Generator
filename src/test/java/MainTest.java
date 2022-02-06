import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(MainTest.class);
    private static final String RESULT_DIR_1 = "./result-dir-1";
    private static final String RESULT_DIR_2 = "./result-dir-2";

    @Test
    void mainTest() {
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        Main.main(new String[]{"-d", RESULT_DIR_1, "-q", testQueryFileStr, "-t", "60"});

        try {
            File resultFile = new File(RESULT_DIR_1, "tc1/cities/50/positives.txt");
            assertTrue(resultFile.exists());
            String content = Util.readUtf8(resultFile);
            assertTrue(content.contains("\n"));
            resultFile = new File(RESULT_DIR_1, "tc2/cities/500/positives.txt");
            assertTrue(resultFile.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void mainTestWithSize() {
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        Main.main(new String[]{"-d", RESULT_DIR_2, "-q", testQueryFileStr, "-s", "40", "60"});

        try {
            File resultFile = new File(RESULT_DIR_2, "tc1/cities/40/positives.txt");
            assertTrue(resultFile.exists());
            String content = Util.readUtf8(resultFile);
            assertTrue(content.contains("\n"));
            resultFile = new File(RESULT_DIR_2, "tc2/cities/60/positives.txt");
            assertTrue(resultFile.exists());

            // negative testing
            resultFile = new File(RESULT_DIR_2, "tc1/cities/50/positives.txt");
            assertFalse(resultFile.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @AfterAll
    static void cleanUp(){
        Util.delete(RESULT_DIR_1);
        Util.delete(RESULT_DIR_2);
    }
}