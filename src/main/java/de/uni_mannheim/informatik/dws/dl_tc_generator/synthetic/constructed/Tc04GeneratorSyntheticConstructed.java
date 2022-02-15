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
 * Named Nodes: N<br/>
 * Named Edges: -<br/>
 * Pattern: (X E N) OR (N E X) <br/>
 * }
 */
public class Tc04GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc04GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc04GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc04GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc04GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC04";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int avgTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        Set<String> nodeIds = generateNodeIds(totalNodes);
        Set<String> edgeIds = generateEdgeIds(totalEdges);
        final String targetNode = nodeIds.iterator().next();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // let's generate positives
            while (positives.size() < nodesOfInterest) {
                String positiveNode = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(positiveNode)){
                    // the node is already positive
                    continue;
                }
                int s0o1 = random.nextInt(2);
                String randomEdge = Util.randomDrawFromSet(edgeIds);

                if(s0o1 == 0){
                    writer.write(  positiveNode + " " + randomEdge + " " + targetNode + " . \n");
                    graph.addObjectTriple(new Triple(positiveNode, randomEdge, targetNode));
                } else {
                    writer.write( targetNode + " " + randomEdge + " " + positiveNode + " . \n");
                    graph.addObjectTriple(new Triple(targetNode, randomEdge, positiveNode));
                }
                positives.add(positiveNode);
            }

            // let's add random triples / write negatives
            for(String node : nodeIds){

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for(int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    if(
                            ( triple.subject.equals(targetNode) && !positives.contains(triple.object) )
                            || ( triple.object.equals(targetNode) && !positives.contains(triple.subject) )

                    ){
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
