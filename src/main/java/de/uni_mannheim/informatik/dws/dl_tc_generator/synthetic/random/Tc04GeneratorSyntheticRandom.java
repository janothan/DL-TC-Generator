package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;


/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: N<br/>
 * Named Edges: -<br/>
 * Pattern: (X E N) OR (N E X) <br/>
 * }
 */
public class Tc04GeneratorSyntheticRandom extends TcGeneratorSyntheticRandom {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc04GeneratorSyntheticRandom.class);


    public Tc04GeneratorSyntheticRandom(File directory) {
        super(directory);
    }
    public Tc04GeneratorSyntheticRandom(String directory) {
        super(directory);
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest,
                                             int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        final String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple = generateTriple(nodeIds, edgeIds);
                if(triple.subject.equals(targetNode)) {
                    positives.add(triple.object);
                }
                if(triple.object.equals(targetNode)){
                    positives.add(triple.subject);
                }
                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + ". \n");
                graph.addObjectTriple(triple);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    public String getTcId() {
        return "TC04";
    }
}
