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
 * Pattern: (X E1 S E2 N) OR (N E1 S E2 X) <br/>
 * }
 */
public class Tc05GeneratorSyntheticConstructed extends TcGeneratorSyntheticConstructed {


    public Tc05GeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc05GeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public Tc05GeneratorSyntheticConstructed(String directory) {
        super(directory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tc05GeneratorSyntheticConstructed.class);


    @Override
    public String getTcId() {
        return "TC05";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
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
                String randomEdge1 = Util.randomDrawFromSet(edgeIds);
                String randomEdge2 = Util.randomDrawFromSet(edgeIds);
                String randomNode1 = Util.randomDrawFromSet(nodeIds);
                String randomNode2 = Util.randomDrawFromSet(nodeIds);

                if(s0o1 == 0){
                    writer.write(  positiveNode + " " + randomEdge1 + " " + randomNode1 + " . \n");
                    writer.write(  randomNode1 + " " + randomEdge2 + " " + randomNode2 + " . \n");
                    graph.addObjectTriple(new Triple(positiveNode, randomEdge1, randomNode1));
                    graph.addObjectTriple(new Triple(randomNode1, randomEdge2, randomNode2));
                } else {
                    writer.write(   randomNode1 + " " + randomEdge1 + " " + randomNode2 + " . \n");
                    writer.write(  randomNode2 + " " + randomEdge2 + " " + positiveNode + " . \n");
                    graph.addObjectTriple(new Triple(randomNode1, randomEdge1, randomNode2));
                    graph.addObjectTriple(new Triple(randomNode2, randomEdge2, positiveNode));
                }
                positives.add(positiveNode);
            }

            // let's add random triples / write negatives
            for(String node : nodeIds){

                // draw number of triples
                int tripleNumber = random.nextInt(this.maxTriplesPerNode + 1);
                for(int i = 0; i < tripleNumber; i++) {
                    Triple triple1 = generateTripleWithStartNode(node, nodeIds, edgeIds);
                    Triple triple2 = generateTripleWithStartNode(triple1.object, nodeIds, edgeIds);
                    if(
                            ( triple1.subject.equals(targetNode) && !positives.contains(triple2.object) )
                                    || (  triple2.object.equals(targetNode) && !positives.contains(triple1.subject) )
                    ){
                        i--;
                    } else {
                        writer.write(triple1.subject + " " + triple1.predicate + " " + triple1.object + " . \n");
                        writer.write(triple2.subject + " " + triple2.predicate + " " + triple2.object + " . \n");
                        graph.addObjectTriple(triple1);
                        graph.addObjectTriple(triple2);
                    }
                }

            }

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }
}
