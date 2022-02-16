package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
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
 * Named Nodes: -<br/>
 * Named Edges: E<br/>
 * Pattern: (Y E X) AND (Z E X) <br/>
 * }
 */
public class Tc10GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc10GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc10GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc10GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc10GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC10";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int avgTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        final String targetEdge = edgeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            // generate positives
            while (positives.size() < nodesOfInterest) {
                final String randomObject = Util.randomDrawFromSet(nodeIds);
                final String randomSubject1 = Util.randomDrawFromSet(nodeIds);
                final String randomSubject2 = Util.randomDrawFromSet(nodeIds);
                if(randomSubject1.equals(randomSubject2)){
                    // just redraw
                    continue;
                }
                writer.write(randomSubject1 + " " + targetEdge + " " + randomObject + ". \n");
                writer.write(randomSubject2 + " " + targetEdge + " " + randomObject + ". \n");
                graph.addObjectTriple(new Triple(randomSubject1, targetEdge, randomObject));
                graph.addObjectTriple(new Triple(randomSubject2, targetEdge, randomObject));
                positives.add(randomObject);
            }

            // generating negatives randomly
            for(String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    boolean isWrite = true;
                    if(triple.predicate.equals(targetEdge)
                    ){
                        Set<Triple> triples = graph.getObjectTriplesWithPredicateObject(targetEdge, triple.object);
                        if(triples != null && triples.size() >= 1){
                            isWrite = false;
                        }
                    }
                    if(isWrite){
                        writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                        graph.addObjectTriple(triple);
                    }
                }
            }


        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }
}
