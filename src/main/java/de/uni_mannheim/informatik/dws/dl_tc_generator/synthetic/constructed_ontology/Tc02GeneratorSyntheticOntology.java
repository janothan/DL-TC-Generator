package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Example (?x is positive):
 * ?x a dbo:City .
 * ?y dbo:namedAfter ?x .
 */
public class Tc02GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc02GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc02GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc02GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    public Tc02GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                          int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                          int[] sizes) {
        super(directory, numberOfClasses, numberOfEdges, totalNodesFactor, maxTriplesPerNode, branchingFactor, sizes);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc02GeneratorSyntheticOntology.class);


    @Override
    public String getTcId() {
        return "TC02";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // determine target edge
            final String targetEdge = ontologyGenerator.getRandomPropertyId();

            // positive type class
            final String positiveTypeClass = ontologyGenerator.getRange(targetEdge);

            // making sure that we have enough positives
            ontologyGenerator.ensureObjectNumberForProperty(targetEdge, nodesOfInterest);

            // making sure that we have enough negatives
            ontologyGenerator.ensureEnoughInstancesOfType(positiveTypeClass, 2 * nodesOfInterest);

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target edge: ").append(targetEdge).append("\n");
            configLog.append("Target class (class of positives): ").append(positiveTypeClass).append("\n");

            Set<String> typeInstances = ontologyGenerator.getInstancesOfTypeTransitive(positiveTypeClass);
            Iterator<String> typeInstanceIterator = typeInstances.iterator();

            // let's generate positives
            LOGGER.info("Generating positives.");
            while (positives.size() < nodesOfInterest) {
                String positiveId = typeInstanceIterator.next();
                String subject = ontologyGenerator.getRandomSubjectForProperty(targetEdge);
                writer.write(subject + " " + targetEdge + " " + positiveId + " .\n");
                graph.addObjectTriple(subject, targetEdge, positiveId);
                positives.add(positiveId);
            }

            typeInstances.removeAll(positives);
            typeInstanceIterator = typeInstances.iterator();

            // let's generate negatives
            LOGGER.info("Generating negatives.");
            while (negatives.size() < nodesOfInterest) {
                String negativeId = typeInstanceIterator.next();
                if (positives.contains(negativeId)) {
                    continue;
                }
                negatives.add(negativeId);
            }

            // let's generate some random connections
            LOGGER.info("Generating random connections.");
            for (String instanceId : ontologyGenerator.getInstances()) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {

                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if (triple.predicate.equals(targetEdge) && !positives.contains(triple.object)) {
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
        // serialize the ontology
        ontologyGenerator.serializeOntology(new File(fileToBeWritten.getParentFile(), "ontology.nt"));
    }
}
