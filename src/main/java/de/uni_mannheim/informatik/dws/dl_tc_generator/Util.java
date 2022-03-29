package de.uni_mannheim.informatik.dws.dl_tc_generator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A collection of useful static methods.
 */
public class Util {


    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static List<String> readUtf8FileIntoList(File fileToRead) {
        String fileContent = readUtf8(fileToRead);
        return new ArrayList<>(Arrays.asList(fileContent.split("\n")));
    }

    public static Set<String> readUtf8FileIntoSet(File fileToRead) {
        String fileContent = readUtf8(fileToRead);
        return new HashSet<>(Arrays.asList(fileContent.split("\n")));
    }

    /**
     * Reads the contents of a UTF-8 encoded file.
     *
     * @param fileToRead The file that shall be read.
     * @return File contents. An empty string in case of an error.
     */
    public static String readUtf8(File fileToRead) {
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
     * Writes the given set to a file. The file will be UTF-8 encoded.
     * @param set Set to write.
     * @param fileToWrite File to be written.
     */
    public static void writeSetToFile(Set set, File fileToWrite){
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), "UTF-8"));
            set.stream().forEach(x -> {
                try {
                    writer.write(x.toString() + "\n");
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            });
            LOGGER.info("Writing file " + fileToWrite);
            writer.flush();
            writer.close();
        } catch (IOException ioe){
            LOGGER.error("Problem writing file " + fileToWrite);
            ioe.printStackTrace();
        }
    }

    /**
     * Persisted in UTF-8.
     *
     * @param fileToWrite File that shall be written.
     * @param listToWrite List that shall be written.
     */
    public static void writeListToFile(File fileToWrite, List<String> listToWrite) {
        if(fileToWrite == null){
            LOGGER.error("Cannot write file because the provided `fileToWrite` is null.");
            return;
        }
        if(listToWrite == null){
            LOGGER.error("Cannot write the file because the provided `listToWrite` is null.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), StandardCharsets.UTF_8))) {
            for (String line : listToWrite) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.error(
                    "An error occurred while trying to persist file '" + fileToWrite.getAbsolutePath() + "'.",
                    fnfe);
        } catch (IOException e) {
            LOGGER.error("An IO exception occurred while trying to write file '" + fileToWrite.getAbsolutePath() +
                    "'.", e);
        }
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


    public static void delete(File deleteFile) {
        if (!deleteFile.exists()) {
            return;
        }
        if (deleteFile.isFile()) {
            if (deleteFile.delete()) {
                LOGGER.info("Delete file: '" + deleteFile.getName() + "'");
            }
        }
        try {
            FileUtils.deleteDirectory(deleteFile);
        } catch (IOException e) {
            LOGGER.warn("An exception occurred while trying to remove directory: '" + deleteFile.getAbsolutePath() + "'");
        }
    }

    public static void delete(String filePath) {
        delete(new File(filePath));
    }

    /**
     * Draw a random value from a set. This method is thread-safe.
     *
     * @param setToDrawFrom The set from which shall be drawn.
     * @param <T> Type
     * @return Drawn value of type T.
     */
    public static<T> T randomDrawFromSet(Set<T> setToDrawFrom) {
        int randomNumber = ThreadLocalRandom.current().nextInt(setToDrawFrom.size());
        int i = 0;
        for(T t : setToDrawFrom){
            if(i == randomNumber){
                return t;
            }
            i++;
        }
        return null;
    }

    /**
     * Writes the train and test files.
     *
     * @param positives  The positive URIs.
     * @param negatives  The negative URIs.
     * @param resultsDir The directory the train test files shall be written to.
     * @param isHard     True if hard case, else false.
     */
    public static void writeTrainTestFiles(Collection<String> positives, Collection<String> negatives, Path resultsDir,
                                           TrainTestSplit trainTestSplit, String separator, boolean isHard) {

        // let's write the train/test split
        int positivesSize = positives.size();
        int negativesSize = negatives.size();
        int minNumber = Math.min(positivesSize, negativesSize);
        if (minNumber != positivesSize || minNumber != negativesSize) {
            String hardString = isHard ? " hard" : "";
            LOGGER.warn("There are " + positivesSize + " positives and " + negativesSize + hardString + " negatives. " +
                    "In order to obtain a balanced data set, only " + minNumber + " of each will be " +
                    "used.");
        } else if (minNumber == 0){
            LOGGER.error("There are no Positives or Negatives. Skipping write operation for train/test split.");
            return;
        }
        double trainShare =
                trainTestSplit.trainSplit() / (trainTestSplit.testSplit() + trainTestSplit.trainSplit());

        int position = 0;

        // if position < switchToTest -> write train file!
        int switchToTest = (int) Math.floor(minNumber * trainShare);

        String subdirectory = isHard ? "train_test_hard" : "train_test";
        File trainTestDirectory = Paths.get(resultsDir.toString(), subdirectory).toFile();
        if (trainTestDirectory.mkdirs()) {
            LOGGER.info("Created directory '" + trainTestDirectory.getAbsolutePath() + "'.");
        }
        File trainFile = Paths.get(trainTestDirectory.getAbsolutePath(),
                "train.txt").toFile();
        File testFile = Paths.get(trainTestDirectory.getAbsolutePath(),
                "test.txt").toFile();

        try (
                OutputStreamWriter testWriter = new OutputStreamWriter(
                        new FileOutputStream(testFile), StandardCharsets.UTF_8);
                OutputStreamWriter trainWriter = new OutputStreamWriter(
                        new FileOutputStream(trainFile), StandardCharsets.UTF_8);
        ) {
            // writing positives
            for (String uri : positives) {
                if (position == minNumber) {
                    break;
                }
                if (position < switchToTest) {
                    // write to train file
                    trainWriter.write(uri + separator + "1\n");
                } else {
                    // write to test file
                    testWriter.write(uri + separator + "1\n");
                }
                position++;
            }
            // writing negatives
            position = 0;
            for (String uri : negatives) {
                if (position == minNumber) {
                    break;
                }
                if (position < switchToTest) {
                    // write to train file
                    trainWriter.write(uri + separator + "0\n");
                } else {
                    // write to test file
                    testWriter.write(uri + separator + "0\n");
                }
                position++;
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("File not found exception occurred.", fnfe);
        } catch (IOException e) {
            LOGGER.error("IOException occurred.", e);
        }
    }

    /**
     * This method will remove a leading less-than and a trailing greater-than sign (tags).
     * @param stringToBeEdited The string that is to be edited.
     * @return String without tags.
     */
    public static String removeTags(String stringToBeEdited){
        if(stringToBeEdited.startsWith("<")) stringToBeEdited = stringToBeEdited.substring(1);
        if(stringToBeEdited.endsWith(">")) stringToBeEdited = stringToBeEdited.substring(0, stringToBeEdited.length() - 1);
        return stringToBeEdited;
    }

    /**
     * Writes the text to a file using UTF-8 encoding.
     * @param fileToWrite The file to write.
     * @param textToWrite Text to write. Each array component is a new line. A line break will be added at the end.
     */
    public static void writeUtf8(String fileToWrite, String... textToWrite) {
        writeUtf8(new File(fileToWrite), textToWrite);
    }

    /**
     * Writes the text to a file using UTF-8 encoding.
     * @param fileToWrite The file to write.
     * @param textToWrite Text to write. Each array component is a new line. A line break will be added at the end.
     */
    public static void writeUtf8(File fileToWrite, String... textToWrite) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), StandardCharsets.UTF_8))) {
            for(String line : textToWrite){
                writer.write(line + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
