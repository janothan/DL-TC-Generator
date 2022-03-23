package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Positive Query
 * {@code
* SELECT DISTINCT ?x WHERE
* {
*     ?x a dbo:Film .
*     ?y dbo:notableWork ?x .
*     ?y a dbo:Writer .
* } LIMIT <number>
* }
 */
public class Tc08GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc08GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc08GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc08GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc01GeneratorSyntheticOntology.class);

    @Override
    public String getTcId() {
        return "tc09";
    }

    @Override
    protected void writeGraphAndSetPositives(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode) {
        if (fileToBeWritten.exists()) {
            LOGGER.error("The file to be written exists already. Aborting generation.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeWritten), StandardCharsets.UTF_8))) {

            final String targetProperty = ontologyGenerator.getRandomPropertyWhereDomainHasMoreThanTwoSubtypes();
            final String targetClass = ontologyGenerator.getRange(targetProperty);
            ontologyGenerator.ensureEnoughInstancesOfType(targetClass, 2 * nodesOfInterest);
            Set<String> targetObjectInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetClass);

            Iterator<String> domainTypeIterator =
                  ontologyGenerator.classTree.getChildrenOfNode(ontologyGenerator.getDomain(targetProperty)).iterator();

            String positiveUpperSubjectClass = domainTypeIterator.next();
            String negativeUpperSubjectClass = domainTypeIterator.next();

            Iterator<String> objectIterator = targetObjectInstances.iterator();

            // let's generate positives
            while(positives.size() < nodesOfInterest){
                String object = objectIterator.next();
                positives.add(object);
                String subject = ontologyGenerator.getRandomInstanceOfTypeTransitive(positiveUpperSubjectClass);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate negatives
            while (negatives.size() < nodesOfInterest) {
                String object = objectIterator.next();
                negatives.add(object);
                String subject = ontologyGenerator.getRandomInstanceOfTypeTransitive(negativeUpperSubjectClass);
                graph.addObjectTriple(new Triple(subject, targetProperty, object));
                writer.write(subject + " " + targetProperty + " " + object + " .\n");
            }

            // let's generate random connections
            for (String instanceId : ontologyGenerator.getInstances()) {

                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);

                for (int i = 0; i < tripleNumber; i++) {
                    Triple triple = ontologyGenerator.getRandomTripleWithSubject(instanceId);
                    if ( triple.predicate.equals(targetProperty) ) {
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
