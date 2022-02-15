package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.IGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
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

    private int numberOfEdges = 10;

    private int nodesFactor = 4;

    private Set<String> includeOnlyCollection;

    Set<SyntheticGenerator> generatorSet;

    public GeneratorSynthetic(File directoryToGenerate) {
        this.generatedDirectory = directoryToGenerate;
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", "synthetic").toFile()
        ));
        generatorSet.add(new Tc02SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", "synthetic").toFile()
        ));
        generatorSet.add(new Tc03SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc03", "synthetic").toFile()
        ));
        generatorSet.add(new Tc04SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc04", "synthetic").toFile()
        ));
        generatorSet.add(new Tc05SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc05", "synthetic").toFile()
        ));
        generatorSet.add(new Tc06SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06", "synthetic").toFile()
        ));
        generatorSet.add(new Tc07SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07", "synthetic").toFile()
        ));
        generatorSet.add(new Tc08SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08", "synthetic").toFile()
        ));
        generatorSet.add(new Tc09SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc09", "synthetic").toFile()
        ));
        generatorSet.add(new Tc10SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc10", "synthetic").toFile()
        ));
        generatorSet.add(new Tc11SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc11", "synthetic").toFile()
        ));
        generatorSet.add(new Tc12SyntheticGenerator(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc12", "synthetic").toFile()
        ));

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
            if (includeOnlyCollection != null
                    &&
                    !(
                            includeOnlyCollection.contains(g.getTcId())
                                    || includeOnlyCollection.contains(g.getTcId().toLowerCase(Locale.ROOT))
                                    || includeOnlyCollection.contains(g.getTcId().toUpperCase(Locale.ROOT))
                    )
            ) {
                continue;
            }
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

    /**
     * If set, the generation method will only consider test case
     * collections (i.e., directories) with that name - for example, if there are tc01, tc02, ... and the set contains
     * tc01 and tc02, then only the latter two will be generated.
     *
     * @param includeOnlyCollection The collection that shall be included.
     */

    public void setIncludeOnlyCollection(Set<String> includeOnlyCollection) {
        this.includeOnlyCollection = includeOnlyCollection;
    }

    /**
     * This method will override the current set.
     *
     * @param includeOnlyCollection Values for the set.
     */
    @Override
    public void setIncludeOnlyCollection(String... includeOnlyCollection) {
        this.includeOnlyCollection = new HashSet<>();
        this.includeOnlyCollection.addAll(Arrays.stream(includeOnlyCollection).toList());
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

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        generatorSet.forEach(x -> x.setNumberOfEdges(numberOfEdges));
        this.numberOfEdges = numberOfEdges;
    }

    public Set<SyntheticGenerator> getGeneratorSet() {
        return generatorSet;
    }

    public void setGeneratorSet(Set<SyntheticGenerator> generatorSet) {
        this.generatorSet = generatorSet;
    }

    public int getNodesFactor() {
        return nodesFactor;
    }

    public void setNodesFactor(int nodesFactor) {
        generatorSet.forEach(x -> x.setTotalNodesFactor(nodesFactor));
        this.nodesFactor = nodesFactor;
    }
}
