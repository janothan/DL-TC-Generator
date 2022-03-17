package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.Tc01GeneratorSyntheticConstructed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Tc01GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc01GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc01GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc01GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc01GeneratorSyntheticOntology.class);

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // determine target edge
            String targetEdge = ontologyGenerator.getRandomPredicateId();
            ontologyGenerator.ensureSubjectNumberForPredicate(targetEdge, nodesOfInterest);

            String targetClass = ontologyGenerator.getDomain(targetEdge);

            // let's generate positives
            while (positives.size() < nodesOfInterest) {
                for(String targetNode : ontologyGenerator.getInstancesOfTypeTransitive(targetClass)){
                    graph.addObjectTriple(targetNode, targetEdge, ontologyGenerator.getRandomObjectNodeForInstance(targetEdge));
                }
            }

            // let's generate negatives
            while(negatives.size() < nodesOfInterest){
                for(String targetNode : ontologyGenerator.getInstancesOfTypeTransitive(targetClass)){
                    if(positives.contains(targetNode)){
                        continue;
                    }
                    negatives.add(targetNode);
                }
            }

            // let's generate random connections
            // TODO

        } catch (
                IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

    @Override
    public String getTcId() {
        return null;
    }
}
