package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.dl_tc_generator.TrainTestSplit;
import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.TripleDataSetMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.jrdf2vec.util.Util.randomDrawFromSet;

public class Tc01SyntheticGenerator implements ISyntheticTcGenerator {


    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc01SyntheticGenerator.class);

    /**
     * Complete set of positive URIs.
     */
    private final Set<String> positives;

    /**
     * Incomplete set of negative URIs.
     */
    private final Set<String> negatives;

    /**
     * The generated graph.
     */
    private final TripleDataSetMemory graph;

    /**
     * The directory in which will be created.
     * All generated files are generated into this directory.
     */
    private final File directory;

    /**
     * The default separator that is to be used.
     */
    private static final String DEFAULT_SEPARATOR = "\t";

    /**
     * The separator for the data files (e.g. train.txt).
     */
    String separator = DEFAULT_SEPARATOR;

    int[] sizes = new int[]{50, 500, 5000};

    /**
     * Default train-test split ratio.
     */
    private TrainTestSplit trainTestSplit = new TrainTestSplit(0.2, 0.8);

    public Tc01SyntheticGenerator(File directory){
        positives = new HashSet<>();
        negatives = new HashSet<>();
        graph = new TripleDataSetMemory();
        this.directory = directory;
    }

    /**
     * The directory that shall be generated. The directory must not exist yet.
     */
    public void generate(){
        if(directory.exists()){
            LOGGER.error("The directory exists already. ABORTING generation.");
            return;
        }
        if(directory.mkdirs()){
            LOGGER.info("Created directory: " + directory.getAbsolutePath());
        }
        int nodesOfInterest = Arrays.stream(sizes).reduce(0, Integer::sum);
        writeGraph(new File(directory, "graph.nt"), 5 * nodesOfInterest, nodesOfInterest, 50);
        getNegatives(nodesOfInterest);

        List<String> posList = positives.stream().toList();
        List<String> negList = negatives.stream().toList();

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

    }

    public Set<String> getNegatives(int number){
        for(Triple triple : graph.getAllObjectTriples()){
            if(!positives.contains(triple.subject)){
                negatives.add(triple.subject);
            }
            if(negatives.size() == number){
                return negatives;
            }
        }
        return negatives;
    }

    public void writeGraph(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges){

        if(fileToBeWritten.exists()){
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        Set<String> nodeIds = new HashSet<>();
        Set<String> edgeIds = new HashSet<>();

        // initialize nodes
        for( int i = 0; i < totalNodes; i++ ) {
            nodeIds.add("<N" + i + ">");
        }

        // initialize edges
        for ( int i = 0; i < totalEdges; i++ ){
            edgeIds.add("<E" + i + ">");
        }

        String targetEdge = "<E0>";

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                String s = randomDrawFromSet(nodeIds);
                String p = randomDrawFromSet(edgeIds);
                String o = randomDrawFromSet(nodeIds);
                if(p.equals(targetEdge)) {
                    positives.add(s);
                }
                writer.write(s + " " + p + " " + o + ". \n");
                graph.addObjectTriple(new Triple(s, p, o));
            }

            // in some cases we end up with too few negatives, we generate additional negatives in this case
            int currentNegativesSize = getNegatives(nodesOfInterest).size();
            if(currentNegativesSize < nodesOfInterest){
                LOGGER.warn("Insufficient amount of negatives. Creating negatives artificially.");
                final int delta = nodesOfInterest - currentNegativesSize;
                Set<String> newNodeIds = new HashSet<>();
                final int oldNodeIdsize = nodeIds.size();
                for (int i = oldNodeIdsize; i <= delta + oldNodeIdsize; i++){
                    String nId = "<N" + i + ">";
                    nodeIds.add(nId);
                    newNodeIds.add(nId);
                }
                while(negatives.size() < nodesOfInterest){
                    String p = randomDrawFromSet(edgeIds);
                    if(p.equals(targetEdge)) {
                        continue;
                    }
                    String s = randomDrawFromSet(newNodeIds);
                    String o = randomDrawFromSet(nodeIds);
                    writer.write(s + " " + p + " " + o + " . \n");
                    graph.addObjectTriple(new Triple(s, p, o));

                    // wire our subject back in the graph
                    String s2 = randomDrawFromSet(nodeIds);
                    String p2 = randomDrawFromSet(edgeIds);
                    writer.write(s2 + " " + p2 + " " + s + " . \n");
                    graph.addObjectTriple(new Triple(s2, p2, o));

                    negatives.add(s);
                }
            }


        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
        return;
    }


    public String getSeparator() {
        return separator;
    }

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

    public void setTrainTestSplit(TrainTestSplit trainTestSplit) {
        this.trainTestSplit = trainTestSplit;
    }
}
