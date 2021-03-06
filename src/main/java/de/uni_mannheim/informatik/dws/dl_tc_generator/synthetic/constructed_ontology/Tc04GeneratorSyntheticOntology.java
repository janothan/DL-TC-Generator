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
 * <p>
 * {@code
 * SELECT DISTINCT ?x WHERE
 * {
 * {
 * ?x a dbo:City .
 * ?x ?y dbr:United_States .
 * }
 * UNION
 * {
 * ?x a dbo:City .
 * dbr:United_States ?z  ?x .
 * }
 * }
 * }
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

    public Tc04GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                          int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                          int[] sizes) {
        super(directory, numberOfClasses, numberOfEdges, totalNodesFactor, maxTriplesPerNode, branchingFactor, sizes);
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

            // we need to ensure that there are enough instances to be put as domain/range
            // (to avoid infinity loops)
            for (String someRangeProperty : ontologyGenerator.getPropertiesWhereInstanceIsRange(targetInstance)) {
                ontologyGenerator.ensureEnoughInstancesOfType(ontologyGenerator.getDomain(someRangeProperty),
                        nodesOfInterest / 2 + 1);
            }

            for (String someDomainProperty : ontologyGenerator.getPropertiesWhereInstanceIsDomain(targetInstance)) {
                ontologyGenerator.ensureEnoughInstancesOfType(ontologyGenerator.getRange(someDomainProperty),
                        nodesOfInterest / 2 + 1);
            }

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target instance: ").append(targetInstance).append("\n");

            // let's generate positives part 1: target is subject
            LOGGER.info("Generating positives (part 1 of 2).");
            while (positives.size() < nodesOfInterest / 2 + 1) {
                String propertyId = ontologyGenerator.getRandomPropertyWhereInstanceIsDomain(targetInstance);
                String positive = ontologyGenerator.getRandomObjectForProperty(propertyId);
                if (positives.contains(positive)) {
                    continue;
                }

                writer.write(targetInstance + " " + propertyId + " " + positive + " .\n");
                graph.addObjectTriple(new Triple(targetInstance, propertyId, positive));
                positives.add(positive);
            }

            // let's generate positives part 2: target is object
            LOGGER.info("Generating positives (part 2 of 2).");
            while (positives.size() < nodesOfInterest) {
                String propertyId = ontologyGenerator.getRandomPropertyWhereInstanceIsRange(targetInstance);
                String positive = ontologyGenerator.getRandomSubjectForProperty(propertyId);
                if (positives.contains(positive)) {
                    continue;
                }
                writer.write(positive + " " + propertyId + " " + targetInstance + " .\n");
                graph.addObjectTriple(new Triple(positive, propertyId, targetInstance));
                positives.add(positive);
            }

            LOGGER.info("Generating random connections.");
            for (String instanceId : ontologyGenerator.getInstances()) {

                if (instanceId.equals(targetInstance)) {
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
            LOGGER.info("Determining negatives.");
            Set<String> graphNodes = ontologyGenerator.getInstances();
            negatives = new HashSet<>(graphNodes);
            negatives.removeAll(positives);

        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
        // serialize the ontology
        ontologyGenerator.serializeOntology(new File(fileToBeWritten.getParentFile(), "ontology.nt"));
    }
}
