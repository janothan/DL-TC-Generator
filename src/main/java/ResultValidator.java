import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * A class to check the generated result (inconsistencies etc.).
 */
public class ResultValidator {


    /**
     * Constructor
     *
     * @param resultDirectory The directory that is to be checked.
     */
    public ResultValidator(File resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public ResultValidator(String resultDirectory){
        this(new File(resultDirectory));
    }

    /**
     * The result directory that shall be validated.
     */
    private final File resultDirectory;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultValidator.class);

    public boolean validate() {
        Map<String, List<String>> warnings = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();

        File[] resultDirectoryFiles = resultDirectory.listFiles();
        if (resultDirectoryFiles == null) {
            LOGGER.error("Cannot retrieve children of the resultDirectory. Aborting analysis.");
            return false;
        }

        for (File testCaseCollection : resultDirectoryFiles) {
            String collectionName = testCaseCollection.getName();

            File[] testCaseCollectionFiles = testCaseCollection.listFiles();
            if (testCaseCollectionFiles == null) {
                LOGGER.error("An error occurred while trying to obtain the children of test case collection: "
                        + collectionName + "\nAborting analysis.");
                return false;
            }
            for (File testCaseDirectory : testCaseCollectionFiles) {
                String testCaseName = testCaseDirectory.getName();

                File[] quantDirectoryFiles = testCaseDirectory.listFiles();
                if (quantDirectoryFiles == null) {
                    LOGGER.error("An error occurred while trying to obtain the children of " + testCaseName
                            + " (" + collectionName + ")");
                    return false;
                }

                for (File quantDirectory : quantDirectoryFiles) {
                    String quantName = quantDirectory.getName();
                    int idealSize = Integer.parseInt(quantName);

                    String tcIdentifier = collectionName + "-" + testCaseName + "-" + quantName;

                    File negatives = new File(quantDirectory, "negatives.txt");
                    if (!negatives.exists()) {
                        String errorMessage = "The following directory is missing the file 'negatives.txt':\n" +
                                quantDirectory.getAbsolutePath();
                        LOGGER.error(errorMessage);
                        addItem(errors, tcIdentifier, errorMessage);
                        continue;
                    }

                    File positives = new File(quantDirectory, "positives.txt");
                    if (!positives.exists()) {
                        String errorMessage = "The following directory is missing the file 'positives.txt':\n" +
                                quantDirectory.getAbsolutePath();
                        LOGGER.error(errorMessage);
                        addItem(errors, tcIdentifier, errorMessage);
                        continue;
                    }

                    File hardNegatives = new File(quantDirectory, "negatives_hard.txt");
                    boolean isHardNegatives = hardNegatives.exists();

                    Set<String> positiveUris = Util.readUtf8FileIntoSet(positives);
                    positiveUris.remove("");
                    Set<String> negativeUris = Util.readUtf8FileIntoSet(negatives);
                    negativeUris.remove("");
                    Set<String> hardNegativeUris = null;

                    if (isHardNegatives) {
                        hardNegativeUris = Util.readUtf8FileIntoSet(hardNegatives);
                        hardNegativeUris.remove("");
                    }

                    for (String negUri : negativeUris) {
                        if (positiveUris.contains(negUri) && !negUri.trim().equals("")) {
                            String errorMessage = "'" + negUri + "' is contained in the positive and in the negative file:\n"
                                    + positives.getAbsolutePath() + "\n"
                                    + negatives.getAbsolutePath();
                            LOGGER.error(errorMessage);
                            addItem(errors, tcIdentifier, errorMessage);
                            return false;
                        }
                    }

                    for (String posUri : positiveUris) {
                        if (negativeUris.contains(posUri) && !posUri.trim().equals("")) {
                            String errorMessage = "'" + posUri + "' is contained in the positive and in the negative file:\n"
                                    + positives.getAbsolutePath() + "\n"
                                    + negatives.getAbsolutePath();
                            LOGGER.error(errorMessage);
                            addItem(errors, tcIdentifier, errorMessage);
                            return false;
                        }
                    }


                    int positivesSize = positiveUris.size();
                    String positivesMessage = "# Positives in " + tcIdentifier + ": " + positivesSize;
                    if (positivesSize >= idealSize) {
                        LOGGER.info(positivesMessage);
                    } else {
                        LOGGER.warn(positivesMessage);
                        addItem(warnings, tcIdentifier, positivesMessage);
                    }


                    int negativesSize = negativeUris.size();
                    String negativesMessage = "# Negatives in " + tcIdentifier + ": " + negativesSize;
                    if (negativesSize >= idealSize) {
                        LOGGER.info(negativesMessage);
                    } else {
                        LOGGER.warn(negativesMessage);
                        addItem(warnings, tcIdentifier, negativesMessage);
                    }

                    if (isHardNegatives) {
                        int hardNegativesSize = hardNegativeUris.size();
                        String hnMessage = "# Hard Negatives in " + tcIdentifier + ": " + hardNegativesSize;
                        if (hardNegativesSize >= idealSize) {
                            LOGGER.info(hnMessage);
                        } else {
                            LOGGER.warn(hnMessage);
                            addItem(warnings, tcIdentifier, hnMessage);
                        }
                    }

                }
            }
        }

        if (warnings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : warnings.entrySet()) {
                sb.append("⚠️").append(entry.getKey()).append("\n");
                for (String value : entry.getValue()) {
                    sb.append("\t").append(value).append("\n");
                }
            }
            LOGGER.warn("The following warnings occurred (" + warnings.size() +"):\n" + sb);
        }

        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
                sb.append("⛔️️").append(entry.getKey()).append("\n");
                for (String value : entry.getValue()) {
                    sb.append("\t").append(value).append("\n");
                }
            }
            LOGGER.error("The following errors occurred (" + errors.size() + "):\n" + sb);
        }

        return true;
    }

    private static void addItem(Map<String, List<String>> map, String key, String value){
        if(map.get(key) == null){
            List<String> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        } else {
            map.get(key).add(value);
        }
    }

    public static void main(String[] args) {
        ResultValidator validator = new ResultValidator("./result");
        if(validator.validate()){
            System.out.println("Validation completed. No errors.");
        } else {
            System.out.println("ERRORS OCCURRED. CHECK LOG.");
        }

    }

}
