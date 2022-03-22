package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Query pattern:
 *
 * {@code
 * SELECT DISTINCT ?x WHERE
 * {
 *     {
 *         ?x a dbo:City .
 *         ?x ?y dbr:United_States .
 *     }
 *     UNION
 *     {
 *         ?x a dbo:City .
 *         dbr:United_States ?z  ?x .
 *     }
 * }
 * }
 *
 */
public class Tc04GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc04GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc04GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc04GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc04GeneratorSyntheticOntology.class);

    @Override
    public String getTcId() {
        return "tc04";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            // determine target instance
            final String targetInstance = ontologyGenerator.getRandomInstanceId();

            // ensure that there is at least a property with domain=targetInstance
            // and one property with range=instance
            ontologyGenerator.ensurePropertyWithDomainInstance(targetInstance, 1);
            ontologyGenerator.ensurePropertyWithRangeInstance(targetInstance, 1);

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target instance: ").append(targetInstance).append("\n");

            // let's generate positives: target is subject
            while (positives.size() < nodesOfInterest/2+1) {
                String propertyId = ontologyGenerator.getRandomPropertyWhereInstanceIsDomain(targetInstance);
                String positive = ontologyGenerator.getRandomObjectForProperty(propertyId);
                writer.write(targetInstance + " " + propertyId + " " + positive + " .\n");
                graph.addObjectTriple(new Triple(targetInstance, propertyId, positive));
                positives.add(positive);
            }

            // let's generate positives: target is object
            while (positives.size() < nodesOfInterest) {
                String propertyId = ontologyGenerator.getRandomPropertyWhereInstanceIsRange(targetInstance);
                String positive = ontologyGenerator.getRandomSubjectForProperty(propertyId);
                writer.write( positive + " " + propertyId + " " + targetInstance + " .\n");
                graph.addObjectTriple(new Triple(positive, propertyId, targetInstance));
                positives.add(positive);
            }

            for (String instanceId : ontologyGenerator.getInstances()) {

                if(instanceId.equals(targetInstance)){
                    // we have enough triples
                    continue;
                }

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {

                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if (triple.subject.equals(targetInstance) || triple.object.equals(targetInstance)) {
                        i--;
                    } else {
                        writer.write(triple.subject + " " + triple.predicate + " " + triple.object + " . \n");
                        graph.addObjectTriple(triple);
                    }
                }
            }

            // set negatives
            Set<String> graphNodes = ontologyGenerator.getInstances();
            negatives = new HashSet<>(graphNodes);
            negatives.removeAll(positives);

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }
}
