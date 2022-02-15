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
 * Pattern: (X E Y) OR (Y E X) <br/>
 * }
 */
public class Tc03GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc03GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc03GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc03GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc02GeneratorSyntheticConstructed.class);

    @Override
    public String getTcId() {
        return "TC03";
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

            // let's generate positives
            while (positives.size() < nodesOfInterest) {
                String positiveNode = Util.randomDrawFromSet(nodeIds);
                if(positives.contains(positiveNode)){
                    // the node is already positive
                    continue;
                }
                int s0o1 = random.nextInt(2);
                String randomOther = Util.randomDrawFromSet(nodeIds);

                if(s0o1 == 0){
                    writer.write(  positiveNode + " " + targetEdge + " " + randomOther + " . \n");
                    graph.addObjectTriple(new Triple(positiveNode, targetEdge, randomOther));
                } else {
                    writer.write( randomOther + " " + targetEdge + " " + positiveNode + " . \n");
                    graph.addObjectTriple(new Triple(randomOther, targetEdge, positiveNode));
                }
                positives.add(positiveNode);
            }

            // let's add random triples / write negatives
            for(String node : nodeIds){

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for(int i = 0; i < tripleNumber; i++) {
                    Triple triple = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    if(triple.predicate.equals(targetEdge)
                            && !(positives.contains(triple.object) || positives.contains(triple.subject))
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
