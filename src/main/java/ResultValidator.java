import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

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

    private File resultDirectory;
    private static Logger LOGGER = LoggerFactory.getLogger(ResultValidator.class);

    public boolean validate() {
        for (File testCaseCollection : resultDirectory.listFiles()) {
            String collectionName = testCaseCollection.getName();
            for (File testCaseDirectory : testCaseCollection.listFiles()) {
                String testCaseName = testCaseDirectory.getName();
                for (File quantDirectory : testCaseDirectory.listFiles()) {
                    String quantName = quantDirectory.getName();

                    File negatives = new File(quantDirectory, "negatives.txt");
                    if (!negatives.exists()) {
                        LOGGER.error("The following directory is missing the file 'negatives.txt':\n" +
                                quantDirectory.getAbsolutePath());
                        continue;
                    }

                    File positives = new File(quantDirectory, "positives.txt");
                    if (!positives.exists()) {
                        LOGGER.error("The following directory is missing the file 'positives.txt':\n" +
                                quantDirectory.getAbsolutePath()
                        );
                        continue;
                    }

                    File hardNegatives = new File(quantDirectory, "negatives_hard.txt");
                    boolean isHardNegatives = hardNegatives.exists();

                    Set<String> positiveUris = Util.readUtf8FileIntoSet(positives);
                    Set<String> negativeUris = Util.readUtf8FileIntoSet(negatives);
                    Set<String> hardNegativeUris = null;

                    if(isHardNegatives){
                        hardNegativeUris = Util.readUtf8FileIntoSet(hardNegatives);
                    }

                    for (String negUri : negativeUris) {
                        if (positiveUris.contains(negUri)) {
                            LOGGER.error("'" + negUri + "' is contained in the positive and in the negative file:\n"
                                    + positives.getAbsolutePath() + "\n"
                                    + negatives.getAbsolutePath()
                            );
                            return false;
                        }
                    }

                    for (String posUri : positiveUris){
                        if (negativeUris.contains(posUri)){
                            LOGGER.error("'" + posUri + "' is contained in the positive and in the negative file:\n"
                                    + positives.getAbsolutePath() + "\n"
                                    + negatives.getAbsolutePath()
                            );
                            return false;
                        }
                    }

                    LOGGER.info("# Positives in " + collectionName + "-" + testCaseName + "-" + quantName + ": "
                            + positiveUris.size());
                    LOGGER.info("# Negatives in " + collectionName + "-" + testCaseName + "-" + quantName + ": "
                            + negativeUris.size());
                    if(isHardNegatives){
                        LOGGER.info("# Hard Negatives in " + collectionName + "-" + testCaseName + "-" + quantName +
                                ": " + hardNegativeUris.size());
                    }

                }
            }
        }
        return true;
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
