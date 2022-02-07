import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A collection of useful static methods.
 */
public class Util {


    private static Logger LOGGER = LoggerFactory.getLogger(Util.class);


    static List<String> readUtf8FileIntoList(File fileToRead) {
        String fileContent = readUtf8(fileToRead);
        return new ArrayList<>(Arrays.asList(fileContent.split("\n")));
    }

    static Set<String> readUtf8FileIntoSet(File fileToRead) {
        String fileContent = readUtf8(fileToRead);
        return new HashSet<>(Arrays.asList(fileContent.split("\n")));
    }

    /**
     * Reads the contents of a UTF-8 encoded file.
     *
     * @param fileToRead The file that shall be read.
     * @return File contents. An empty string in case of an error.
     */
    static String readUtf8(File fileToRead) {
        if (fileToRead == null){
            LOGGER.error("The fileToRead is null. Returning an empty string.");
            return "";
        }
        if(!fileToRead.exists()){
            LOGGER.error("The provided fileToRead does not exist. Returning an empty string.");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead),
                StandardCharsets.UTF_8))) {
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine);
                sb.append("\n");
            }
        } catch (FileNotFoundException e) {
            LOGGER.info("File not found.", e);
        } catch (IOException e) {
            LOGGER.info("IOException occurred.", e);
        }
        return sb.toString();
    }

    /**
     * Helper function to load files in class path that contain spaces.
     *
     * @param fileName Name of the file.
     * @return File in case of success, else null.
     */
    public static File loadFile(String fileName) {
        if(fileName == null){
            return null;
        }

        try {
            URL resultUri = Util.class.getClassLoader().getResource(fileName);
            assertNotNull(resultUri);
            File result = FileUtils.toFile(resultUri.toURI().toURL());
            assertTrue(result.exists(), "Required resource not available.");
            return result;
        } catch (URISyntaxException | MalformedURLException exception) {
            exception.printStackTrace();
            fail("Could not load file.", exception);
            return null;
        }
    }

    public static void delete(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (file.delete()) {
                LOGGER.info("Delete file: '" + file.getName() + "'");
            }
        }
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            LOGGER.warn("An exception occurred while trying to remove directory: '" + file.getAbsolutePath() + "'");
        }
    }

}
