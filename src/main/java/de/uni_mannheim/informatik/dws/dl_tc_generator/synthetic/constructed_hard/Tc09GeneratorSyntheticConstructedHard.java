package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc09GeneratorSyntheticConstructed.isAccidentallyPositive;

/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: -<br/>
 * Named Edges: E<br/>
 * Pattern: (X E Y) AND (X E Z)<br/>
 * }
 */
public class Tc09GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    public Tc09GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc09GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc09GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc09GeneratorSyntheticConstructedHard.class);


    @Override
    public String getTcId() {
        return "TC09h";
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

            // generate hard negatives
            while (negatives.size() < nodesOfInterest) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomObject = Util.randomDrawFromSet(nodeIds);

                Triple t = new Triple(randomSubject, targetEdge, randomObject);
                if(!isAccidentallyPositive(t, targetEdge, graph)) {
                    negatives.add(randomSubject);
                    writer.write(t.subject + " " + t.predicate + " " + t.object + " . \n");
                    graph.addObjectTriple(t);
                }
            }

            // generating random triples
            for(String node : nodeIds) {

                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);

                    if(isAccidentallyPositive(triple, targetEdge, graph)){
                        i--;
                    } else {
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
