package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: Z<br/>
 * Named Edges: E1, E2<br/>
 * Pattern: (Y E1 X) AND (Y E2 Z) <br/>
 * }
 */
public class Tc08GeneratorSyntheticRandom extends TcGeneratorSyntheticRandom {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc08GeneratorSyntheticRandom.class);


    public Tc08GeneratorSyntheticRandom(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc08GeneratorSyntheticRandom(File directory) {
        super(directory);
    }

    public Tc08GeneratorSyntheticRandom(String directory) {
        super(directory);
    }

    @Override
    public void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        Iterator<String> edgeIterator = edgeIds.iterator();
        String targetEdge1 = edgeIterator.next();
        String targetEdge2 = edgeIterator.next();
        String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                Triple triple1 = generateTriple(nodeIds, edgeIds);
                Triple triple2 = generateTripleWithStartNode(triple1.subject, nodeIds, edgeIds);
                if(
                        triple1.predicate.equals(targetEdge1)
                        && triple2.predicate.equals(targetEdge2)
                        && triple2.object.equals(targetNode)
                ) {
                    positives.add(triple1.object);
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
    public String getTcId() {
        return "TC08";
    }
}
