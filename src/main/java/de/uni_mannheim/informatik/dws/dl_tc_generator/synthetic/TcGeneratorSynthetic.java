package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.TrainTestSplit;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random.Tc01GeneratorSyntheticRandom;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.Util.writeUtf8;
import static de.uni_mannheim.informatik.dws.jrdf2vec.util.Util.randomDrawFromSet;

/**
 * Abstract super class for each individual test case synthetic generator.
 * The overall coordination of generation is performed through {@link GeneratorSynthetic}.
 */
public abstract class TcGeneratorSynthetic {


    public TcGeneratorSynthetic(File directory, int[] sizes) {
        positives = new HashSet<>();
        graph = new TripleDataSetMemory();
        this.directory = directory;
        this.sizes = sizes;
    }

    /**
     * Convenience Constructor
     *
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public TcGeneratorSynthetic(File directory) {
        this(directory, Defaults.SIZES);
    }

    /**
     * Convenience Constructor
     *
     * @param directory The directory to be created. The directory must not exist yet.
     */
    public TcGeneratorSynthetic(String directory) {
        this(new File(directory));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TcGeneratorSynthetic.class);

    /**
     * Get the negatives.
     *
     * @return Set of negatives.
     */
    public Set<String> getNegatives() {
        Set<String> graphNodes = graph.getObjectNodes();
        Set<String> result = new HashSet<>(graphNodes);
        result.removeAll(positives);
        return result;
    }

    boolean checkAndMakeDirectory() {
        if (directory.exists()) {
            LOGGER.error("The directory exists already. ABORTING generation.");
            return false;
        }
        if (directory.mkdirs()) {
            LOGGER.info("Created directory: " + directory.getAbsolutePath());
        }
        return true;
    }

    /**
     * Complete set of positive URIs.
     */
    protected final Set<String> positives;

    /**
     * The generated graph.
     */
    protected final TripleDataSetMemory graph;

    /**
     * The directory in which will be created.
     * All generated files are generated into this directory.
     */
    protected final File directory;

    /**
     * The separator for the data files (e.g. train.txt).
     */
    protected String separator = Defaults.CSV_SEPARATOR;

    /**
     * Size groups in which the synthetic test case shall appear.
     */
    protected int[] sizes;

    /**
     * Default train-test split ratio.
     */
    protected TrainTestSplit trainTestSplit = Defaults.TRAIN_TEST_SPLIT;

    /**
     * The maximum number of edges that generated graph may have at most.
     */
    protected int numberOfEdges = Defaults.NUMBER_OF_EDGES;

    /**
     * The multiple of total nodes.
     */
    protected int totalNodesFactor = Defaults.NODE_FACTOR;

    /**
     * Generate the test case.
     */
    public void generate(){
        if(!checkAndMakeDirectory()){
            return;
        }

        int nodesOfInterest = Arrays.stream(sizes).reduce(0, Integer::sum);

        LOGGER.info("Writing graph for " + this.getTcId());
        writeGraphAndSetPositives(new File(directory, "graph.nt"), totalNodesFactor * nodesOfInterest,
                nodesOfInterest, numberOfEdges);

        List<String> posList = positives.stream().toList();
        List<String> negList = getNegatives().stream().toList();

        int listStart = 0;
        for(int s : sizes){
            File sFile = new File(directory.getAbsolutePath(), "" + s);

            if(sFile.mkdirs()){
                LOGGER.info("Created new file: " + sFile.getAbsolutePath());
            }

            List<String> currentPosList = posList.subList(listStart, listStart + s);
            List<String> currentNegList = negList.subList(listStart, listStart + s);

            // writing positives
            Util.writeListToFile( new File(sFile, "positives.txt"), currentPosList);

            // writing negatives
            Util.writeListToFile( new File(sFile, "negatives.txt"), currentNegList);

            listStart = s;

            Util.writeTrainTestFiles(currentPosList, currentNegList, Paths.get(directory.getAbsolutePath(), "" + s),
                    trainTestSplit, separator, false);
        }
        if(getConfigurationParameters() != null){
            File configFile = new File(directory.getAbsolutePath(), "configuration.txt");
            writeUtf8(configFile, getConfigurationParameters());
        }
    }

    /**
     * Config log to persist specific configurations.
     * Lines added to the config log will appear in a per test-case configuration.txt file.
     * Use the logger for logging entries and the config log for configuration parameters so that it can be
     * understood with which parameters the test case was generated.
     */
    protected StringBuilder configLog = null;

    /**
     * Return some string configuration parameters if there are any.
     * @return Configuration parameters.
     */
    public String getConfigurationParameters(){
        if(configLog == null){
            return null;
        }
        return configLog.toString();
    }

    /**
     * Write the graph to the fileToBeWritten.
     * This method also fills the {@link Tc01GeneratorSyntheticRandom#positives}.
     *
     * @param fileToBeWritten The file that shall be written (must not exist yet).
     * @param totalNodes      The total number of nodes. The actual graph may not contain the full number.
     * @param nodesOfInterest The number of desired positives.
     * @param totalEdges      The number of edges. The actual graph may not contain the full number.
     */
    protected abstract void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest,
                                                      int totalEdges);

    /**
     * Get the ID of the test case such as "TC01".
     * <p>
     * Restrictions:
     * <ul>
     *    <li>The ID must be unique.</li>
     *    <li>The ID must not contain any spaces.</li>
     * </ul>
     *
     * @return A unique test case id.
     */
    public abstract String getTcId();

    /**
     * Adds additional node ids to the existing set
     * @param setToWhichWillBeAdded The set to which the node IDs will be added.
     * @param numberOfNewNodes The number of new nodes to be added.
     * @return The <em>newly generated</em> ids are returned!
     */
    protected Set<String> addAdditionalNodeIds(Set<String> setToWhichWillBeAdded, int numberOfNewNodes){
        Set<String> newNodeIds = generateNodeIds(numberOfNewNodes, setToWhichWillBeAdded.size());
        setToWhichWillBeAdded.addAll(newNodeIds);
        return newNodeIds;
    }

    /**
     * Generate node IDs that are unique to the generator.
     * @param numberOfNodes The number of node IDs to be generated.
     * @return A set of unique node IDs.
     */
    protected Set<String> generateNodeIds(int numberOfNodes){
        return generateNodeIds(numberOfNodes, 0);
    }

    protected Set<String> generateNodeIds(int numberOfNodes, int startId){
        Set<String> nodeIds = new HashSet<>();
        for( int i = startId; i < numberOfNodes + startId; i++ ) {
            nodeIds.add("<N_" + getTcId() + "_" + i + ">");
        }
        return nodeIds;
    }

    /**
     * Generates a random triple.
     * @param nodeIds Node IDs to draw from.
     * @param edgeIds Edge IDs to draw from.
     * @return Random triple
     */
    public static Triple generateTriple(Set<String> nodeIds, Set<String> edgeIds){
        String s = randomDrawFromSet(nodeIds);
        String p = randomDrawFromSet(edgeIds);
        String o = randomDrawFromSet(nodeIds);
        return new Triple(s, p, o);
    }

    /**
     * Generates a random triple.
     * @param startNode Starting node, must be part of {@code nodeIds}.
     * @param nodeIds Node IDs to draw from.
     * @param edgeIds Edge IDs to draw from.
     * @return Random triple
     */
    public static Triple generateTripleWithStartNode(String startNode, Set<String> nodeIds, Set<String> edgeIds){
        String p = randomDrawFromSet(edgeIds);
        String o = randomDrawFromSet(nodeIds);
        return new Triple(startNode, p, o);
    }

    /**
     * Generate edge IDs that are unique to the generator.
     * @param numberOfEdges The number od edge IDs to be generated.
     * @return A set of unique edge IDs.
     */
    protected Set<String> generateEdgeIds(int numberOfEdges){
        Set<String> edgeIds = new HashSet<>();
        for ( int i = 0; i < numberOfEdges; i++ ){
            edgeIds.add("<E_" + getTcId() + "_" + i + ">");
        }
        return edgeIds;
    }

    public String getSeparator() {
        return separator;
    }

    /**
     * Set the separator to be used in test/train files.
     *
     * @param separator CSV separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int[] getSizes() {
        return sizes;
    }

    public void setSizes(int[] sizes) {
        this.sizes = sizes;
    }

    public TrainTestSplit getTrainTestSplit() {
        return trainTestSplit;
    }

    /**
     * Set the train-test split.
     *
     * @param trainTestSplit The split.
     */
    public void setTrainTestSplit(TrainTestSplit trainTestSplit) {
        this.trainTestSplit = trainTestSplit;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    /**
     * Set the number of edges that the graph may have at most.
     *
     * @param numberOfEdges The number of edges.
     */
    public void setNumberOfEdges(int numberOfEdges) {
        if (numberOfEdges < 1) {
            LOGGER.error("Number of edges must be > 0. Doing nothing.");
            return;
        }
        this.numberOfEdges = numberOfEdges;
    }

    public File getDirectory() {
        return directory;
    }

    public int getTotalNodesFactor() {
        return totalNodesFactor;
    }

    public void setTotalNodesFactor(int totalNodesFactor) {
        if(totalNodesFactor < 2){
            LOGGER.error("The totalNodesFactor must be >= 2. Doing nothing.");
            return;
        }
        this.totalNodesFactor = totalNodesFactor;
    }

}
