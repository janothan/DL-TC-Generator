package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc10GeneratorSyntheticConstructed.isAccidentallyPositive;


/**
 * Test Case Form:
 * {@code
 * Positive: X<br/>
 * Named Nodes: -<br/>
 * Named Edges: E<br/>
 * Pattern: (Y E X) AND (Z E X) <br/>
 * }
 */
public class Tc10GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    public Tc10GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc10GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc10GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc10GeneratorSyntheticConstructedHard.class);


    @Override
    public String getTcId() {
        return "TC10h";
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
                if (randomSubject1.equals(randomSubject2)) {
                    // just redraw
                    continue;
                }
                writer.write(randomSubject1 + " " + targetEdge + " " + randomObject + " . \n");
                writer.write(randomSubject2 + " " + targetEdge + " " + randomObject + " . \n");
                graph.addObjectTriple(new Triple(randomSubject1, targetEdge, randomObject));
                graph.addObjectTriple(new Triple(randomSubject2, targetEdge, randomObject));
                positives.add(randomObject);
            }

            // generate negatives
            while(negatives.size() < nodesOfInterest) {
                final String randomObject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomObject)){
                    continue;
                }
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                Triple triple = new Triple(randomSubject, targetEdge, randomObject);
                if(!isAccidentallyPositive(triple, targetEdge, graph)){
                    writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                    graph.addObjectTriple(triple);
                    negatives.add(randomObject);
                }
            }

            // randomly add statements
            for (String node : nodeIds) {
                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    if (isAccidentallyPositive(triple, targetEdge, graph)) {
                        i--;
                    } else {
                        writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                        graph.addObjectTriple(triple);
                    }
                }
            }

        } catch (
                IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

}
