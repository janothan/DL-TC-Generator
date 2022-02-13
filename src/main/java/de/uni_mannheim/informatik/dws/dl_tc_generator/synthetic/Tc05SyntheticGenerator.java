package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

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
 * Pattern: (X E1 S E2 N) OR (N E1 S E2 X) <br/>
 * }
 */
public class Tc05SyntheticGenerator extends SyntheticGenerator {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc04SyntheticGenerator.class);

    public Tc05SyntheticGenerator(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc05SyntheticGenerator(File directory) {
        super(directory);
    }

    public Tc05SyntheticGenerator(String directory) {
        super(directory);
    }

    @Override
    void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple1 = generateTriple(nodeIds, edgeIds);
                Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);

                if(triple1.subject.equals(targetNode)){
                    positives.add(triple2.object);
                }
                if(triple2.object.equals(targetNode)){
                    positives.add(triple1.subject);
                }

                writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + ". \n");
                writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + ". \n");
                graph.addObjectTriple(triple1);
                graph.addObjectTriple(triple2);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    String getTcId() {
        return "TC05";
    }
}
