import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    private static final String RESULT_DIR_1 = "./result-dir-1";
    private static final String RESULT_DIR_2 = "./result-dir-2";
    private static final String RESULT_DIR_3 = "./result-dir-3";
    private static final String RESULT_DIR_4 = "./result-dir-4";
    private static final String RESULT_DIR_NOT_WRITTEN = "./result-dir-never-exists";


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
    void mainTcc() {
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        final String tcSelector = "tc1";
        Main.main(new String[]{"-d", RESULT_DIR_3, "-q", testQueryFileStr, "-tcc", tcSelector, "-s", "50"});

        try {
            File resultDir = new File(RESULT_DIR_3);
            assertTrue(resultDir.exists());
            File[] files = resultDir.listFiles();
            assertNotNull(files);

            for(File f : files){
                assertEquals(tcSelector, f.getName());
            }

            File resultFile = new File(RESULT_DIR_3, "tc1/cities/50/positives.txt");
            assertTrue(resultFile.exists());
            String content = Util.readUtf8(resultFile);
            assertTrue(content.contains("\n"));

            resultFile = new File(RESULT_DIR_3, "tc1/species");
            assertTrue(resultFile.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void mainTcg(){
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        final String tcgSelector = "cities";
        Main.main(new String[]{"-d", RESULT_DIR_4, "-q", testQueryFileStr, "-tc", tcgSelector, "-s", "50"});

        try {
            File resultDir = new File(RESULT_DIR_4);
            assertTrue(resultDir.exists());

            File tc1file = new File(RESULT_DIR_4, "tc1");
            assertTrue(tc1file.exists());

            File tc2file = new File(RESULT_DIR_4, "tc2");
            assertTrue(tc2file.exists());

            File resultFile = new File(RESULT_DIR_4, "tc1/cities");
            assertTrue(resultFile.exists());

            resultFile = new File(RESULT_DIR_4, "tc1/cities/50/positives.txt");
            assertTrue(resultFile.exists());
            String content = Util.readUtf8(resultFile);
            assertTrue(content.contains("\n"));

            File resultFileNotExists = new File(RESULT_DIR_4, "tc1/species");
            assertFalse(resultFileNotExists.exists());
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    void negativeTimeout(){
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        Main.main(new String[]{"-d", RESULT_DIR_NOT_WRITTEN, "-q", testQueryFileStr, "-t", "my-timeout"});
        File rDir = new File(RESULT_DIR_NOT_WRITTEN);
        assertFalse(rDir.exists());
    }

    @Test
    void negativeSizes1(){
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        Main.main(new String[]{"-d", RESULT_DIR_NOT_WRITTEN, "-q", testQueryFileStr, "-s", "10", "20", "-100"});
        File rDir = new File(RESULT_DIR_NOT_WRITTEN);
        assertFalse(rDir.exists());
    }

    @Test
    void negativeSizes2(){
        File testQueryFile = Util.loadFile("testQueries");
        assertNotNull(testQueryFile);

        String testQueryFileStr = testQueryFile.getAbsolutePath();
        Main.main(new String[]{"-d", RESULT_DIR_NOT_WRITTEN, "-q", testQueryFileStr, "-s", "10", "20", "invalid"});
        File rDir = new File(RESULT_DIR_NOT_WRITTEN);
        assertFalse(rDir.exists());
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
        Util.delete(RESULT_DIR_3);
        Util.delete(RESULT_DIR_4);
    }
}