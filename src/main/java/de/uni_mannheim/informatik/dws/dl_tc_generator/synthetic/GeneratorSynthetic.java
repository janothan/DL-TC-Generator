package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.IGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Generator coordinating the generation of all synthetic test cases.
 * For the individual test case generation, see {@link TcGeneratorSynthetic} and its children.
 */
public abstract class GeneratorSynthetic implements IGenerator {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorSynthetic.class);

    /**
     * The separator for the data files (e.g. train.txt).
     */
    protected String separator = Defaults.CSV_SEPARATOR;

    /**
     * The generated directory must not exist yet.
     */
    protected final File generatedDirectory;

    /**
     * The sizes of the test cases.
     */
    protected int[] sizes = Defaults.SIZES;

    protected int numberOfEdges = Defaults.NUMBER_OF_EDGES;

    protected int nodesFactor = Defaults.NODE_FACTOR;

    protected Set<String> includeOnlyCollection;

    protected Set<TcGeneratorSynthetic> generatorSet;

    public GeneratorSynthetic(File directoryToGenerate) {
        this.generatedDirectory = directoryToGenerate;
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
        for (TcGeneratorSynthetic g : generatorSet) {
            LOGGER.info("Processing " + g.getTcId());
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
        LOGGER.info("Merging graphs...");
        mergeGraphsToOne(generatedDirectory);
    }

    /**
     * Given a result directory of synthetic test cases, this method writes a large graph and places it in the
     * specified synthetic results directory.
     * @param syntheticDirectory The result directory.
     */
    public static void mergeGraphsToOne(File syntheticDirectory){
        mergeGraphsToOne(syntheticDirectory, new File(syntheticDirectory, "graph.nt"));
    }

    /**
     * Given a result directory of synthetic test cases, this method writes a large graph.
     * @param syntheticDirectory The result directory.
     * @param fileToWrite The new, large graph file that shall be written.
     */
    public static void mergeGraphsToOne(File syntheticDirectory, File fileToWrite) {
        if(fileToWrite == null){
            LOGGER.error("File to write must not be null. ABORTING operation.");
            return;
        }
        if(fileToWrite.exists()){
            LOGGER.error("The fileToWrite must not yet exist. ABORTING operation.");
            return;
        }
        if(syntheticDirectory == null || !syntheticDirectory.exists() || syntheticDirectory.isFile()){
            LOGGER.error("The syntheticDirectory is null, does not exist, or is no directory. ABORTING operation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), StandardCharsets.UTF_8))) {

            // we first loop over the synthetic directory
            File[] tcFiles = syntheticDirectory.listFiles();
            if(tcFiles == null){
                LOGGER.error("No test cases found. ABORTING operation.");
                return;
            }
            for(File tcFile : tcFiles){
                File graphFile = Paths.get(tcFile.getAbsolutePath(), "synthetic", "graph.nt").toFile();
                if(!graphFile.exists()){
                    LOGGER.warn("No graph.nt file found in test case directory: " + tcFile.getAbsolutePath());
                    continue;
                }
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(graphFile),
                        StandardCharsets.UTF_8))) {
                   String line;
                   while((line = reader.readLine()) != null){
                       writer.write(line + "\n");
                   }
                } catch (IOException e) {
                    LOGGER.error("An error occurred while reading file: " + graphFile.getAbsolutePath(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
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

    public Set<TcGeneratorSynthetic> getGeneratorSet() {
        return generatorSet;
    }

    public void setGeneratorSet(Set<TcGeneratorSynthetic> generatorSet) {
        this.generatorSet = generatorSet;
    }

    public int getNodesFactor() {
        return nodesFactor;
    }

    public void setNodesFactor(int nodesFactor) {
        generatorSet.forEach(x -> x.setTotalNodesFactor(nodesFactor));
        this.nodesFactor = nodesFactor;
    }

    /**
     * Synthetic test cases only have one test case group.
     * Depending on the synthetic generator, the group may have varying names.
     *
     * @return The test case group name used by this generator.
     */
    public abstract String getTcGroupName();
}
