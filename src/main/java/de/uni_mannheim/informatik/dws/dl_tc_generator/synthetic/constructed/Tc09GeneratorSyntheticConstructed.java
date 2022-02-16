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
 * Pattern: (X E Y) AND (X E Z)<br/>
 * }
 */
public class Tc09GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc09GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc09GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc09GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc09GeneratorSyntheticConstructed.class);


    @Override
    public String getTcId() {
        return "TC09";
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
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                final String randomObject1 = Util.randomDrawFromSet(nodeIds);
                final String randomObject2 = Util.randomDrawFromSet(nodeIds);
                writer.write(randomSubject + " " + targetEdge + " " + randomObject1 + " . \n");
                writer.write(randomSubject + " " + targetEdge + " " + randomObject2 + " . \n");
                graph.addObjectTriple(new Triple(randomSubject, targetEdge, randomObject1));
                graph.addObjectTriple(new Triple(randomSubject, targetEdge, randomObject2));
                positives.add(randomSubject);
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
                        Set<Triple> triples = graph.getObjectTriplesWithSubjectPredicate(node, targetEdge);
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
