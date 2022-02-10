package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.IGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GeneratorSynthetic implements IGenerator {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorSynthetic.class);

    /**
     * The default separator that is to be used.
     */
    private static final String DEFAULT_SEPARATOR = "\t";

    /**
     * The separator for the data files (e.g. train.txt).
     */
    private String separator = DEFAULT_SEPARATOR;

    /**
     * The generated directory must not exist yet.
     */
    private final File generatedDirectory;

    /**
     * The sizes of the test cases.
     */
    private int[] sizes = {50, 500, 5000};

    Set<ISyntheticTcGenerator> generatorSet;

    public GeneratorSynthetic(File directoryToGenerate){
        this.generatedDirectory = directoryToGenerate;
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01SyntheticGenerator(new File(generatedDirectory, "tc01")));

    }

    public GeneratorSynthetic(String directoryToGeneratePath){
        this(new File(directoryToGeneratePath));
    }

    @Override
    public void generateTestCases() {
        LOGGER.info("Starting test case generation.");
        if(generatedDirectory.mkdirs()){
            LOGGER.info("Created directory: " + generatedDirectory.getAbsolutePath());
        }
        for(ISyntheticTcGenerator g : generatorSet){
            g.generate();
        }

    }
}
