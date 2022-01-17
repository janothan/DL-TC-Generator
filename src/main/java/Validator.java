import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

public class Validator {


    /**
     * Constructor
     *
     * @param resultDirectory The directory that is to be checked.
     */
    public Validator(File resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public Validator(String resultDirectory){
        this(new File(resultDirectory));
    }

    private File resultDirectory;
    private static Logger LOGGER = LoggerFactory.getLogger(Validator.class);

    public boolean validate() {
        for (File testCaseCollection : resultDirectory.listFiles()) {
            for (File testCaseDirectory : testCaseCollection.listFiles()) {
                for (File quantDirectory : testCaseDirectory.listFiles()) {
                    File negatives = new File(quantDirectory, "negatives.txt");
                    if (!negatives.exists()) {
                        LOGGER.warn("The following directory is missing the file 'negatives.txt':\n" +
                                quantDirectory.getAbsolutePath());
                        continue;
                    }
                    File positives = new File(quantDirectory, "positives.txt");
                    if (!positives.exists()) {
                        LOGGER.warn("The following directory is missing the file 'positives.txt':\n" +
                                quantDirectory.getAbsolutePath()
                        );
                        continue;
                    }
                    Set<String> positiveUris = Util.readUtf8FileIntoSet(positives);
                    Set<String> negativeUris = Util.readUtf8FileIntoSet(negatives);
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

                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Validator validator = new Validator("./resultTC5");
        if(validator.validate()){
            System.out.println("Validation completed. No errors.");
        } else {
            System.out.println("ERRORS OCCURRED. CHECK LOG.");
        }

    }

}
