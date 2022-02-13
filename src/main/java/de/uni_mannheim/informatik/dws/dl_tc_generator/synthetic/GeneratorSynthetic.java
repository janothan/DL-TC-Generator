package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.IGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Generator coordinating the generation of all synthetic test cases.
 * For the individual test case generation, see {@link SyntheticGenerator} and its children.
 */
public class GeneratorSynthetic implements IGenerator {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorSynthetic.class);

    /**
     * The separator for the data files (e.g. train.txt).
     */
    private String separator = Defaults.CSV_SEPARATOR;

    /**
     * The generated directory must not exist yet.
     */
    private final File generatedDirectory;

    /**
     * The sizes of the test cases.
     */
    private int[] sizes = Defaults.SIZES;

    Set<SyntheticGenerator> generatorSet;

    public GeneratorSynthetic(File directoryToGenerate) {
        this.generatedDirectory = directoryToGenerate;
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01SyntheticGenerator(
                        Paths.get(generatedDirectory.getAbsolutePath(), "tc01", "synthetic").toFile()
                )
        );
        generatorSet.add(new Tc02SyntheticGenerator(
                        Paths.get(generatedDirectory.getAbsolutePath(), "tc02", "synthetic").toFile()
                )
        );
        generatorSet.add(new Tc03SyntheticGenerator(
                        Paths.get(generatedDirectory.getAbsolutePath(), "tc03", "synthetic").toFile()
                )
        );
        generatorSet.add(new Tc04SyntheticGenerator(
                        Paths.get(generatedDirectory.getAbsolutePath(), "tc04", "synthetic").toFile()
                )
        );
        generatorSet.add(new Tc05SyntheticGenerator(
                        Paths.get(generatedDirectory.getAbsolutePath(), "tc05", "synthetic").toFile()
                )
        );

        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSynthetic(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    @Override
    public void generateTestCases() {
        LOGGER.info("Starting test case generation.");
        if (generatedDirectory.mkdirs()) {
            LOGGER.info("Created directory: " + generatedDirectory.getAbsolutePath());
        }
        for (SyntheticGenerator g : generatorSet) {
            g.generate();
        }

    }

    public int[] getSizes() {
        return sizes;
    }

    public void setSizes(int[] sizes) {
        generatorSet.forEach(x -> x.setSizes(sizes));
        this.sizes = sizes;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        generatorSet.forEach(x -> x.setSeparator(separator));
        this.separator = separator;
    }

    public File getGeneratedDirectory() {
        return generatedDirectory;
    }
}
