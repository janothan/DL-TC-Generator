package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

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
 * Named Nodes: N<br/>
 * Named Edges: E<br/>
 * Pattern: (X E N) <br/>
 * }
 */
public class Tc06GeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructedHard {


    private static final Logger LOGGER = LoggerFactory.getLogger(Tc06GeneratorSyntheticConstructedHard.class);

    public Tc06GeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc06GeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public Tc06GeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }


    @Override
    public String getTcId() {
        return "TC06h";
    }

    /**
     * Writes a graph to file. Generates positives AND negatives.
     * @param fileToBeWritten The file that shall be written.
     * @param totalNodes Total node types in the graph.
     * @param nodesOfInterest Can be used to determine the number of desired positives and the number of desired
     *                        negatives.
     * @param totalEdges Total edge types in the graph.
     * @param avgTriplesPerNode Average triples per node.
     */
    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int avgTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        final String targetEdge = edgeIds.iterator().next();
        final String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {
            while (positives.size() < nodesOfInterest) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                writer.write(randomSubject + " " + targetEdge + " " + targetNode + " . \n");
                graph.addObjectTriple(new Triple(randomSubject, targetEdge, targetNode));
                positives.add(randomSubject);
            }


            while(negatives.size() < Math.ceil(nodesOfInterest/2.0)){
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomObject = Util.randomDrawFromSet(nodeIds);
                if(randomObject.equals(targetNode)){
                    continue;
                }
                // we now have
                // - a subject that is not positive
                // - an object that is not the target node
                // let's build the hard negative
                Triple triple = new Triple(randomSubject, targetEdge, randomObject);
                graph.addObjectTriple(triple.subject, triple.predicate, triple.object);
                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                negatives.add(triple.subject);
            }


            while(negatives.size() < nodesOfInterest) {
                final String randomSubject = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(randomSubject)){
                    continue;
                }
                final String randomEdge = Util.randomDrawFromSet(edgeIds);
                if(randomEdge.equals(targetEdge)){
                    continue;
                }
                // we now have
                //  - a subject that is not positive
                //  - an edge that is not the target edge
                // let's build the hard negative

                Triple triple = new Triple(randomSubject, randomEdge, targetNode);
                graph.addObjectTriple(triple.subject, triple.predicate, triple.object);
                writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                negatives.add(triple.subject);
            }


            // lastly, let's generate some random triples
            for(String node : nodeIds){

                // draw number of triples
                int tripleNumber = random.nextInt(getMaxTriplesPerNode() + 1);
                for(int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    if(
                            triple.predicate.equals(targetEdge) && triple.object.equals(targetNode)
                                    && !positives.contains(triple.subject)
                    )
                    {
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
