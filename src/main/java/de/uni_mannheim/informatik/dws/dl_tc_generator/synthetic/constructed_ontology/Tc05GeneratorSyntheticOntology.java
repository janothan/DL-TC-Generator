package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 *
 *
 * {@code
 * SELECT DISTINCT ?x WHERE
 * {
 *     {
 *         ?x a dbo:City .
 *         ?x ?r1 ?z .
 *         ?z ?r2 dbr:New_York_City .
 *     }
 *     UNION
 *     {
 *         ?x a dbo:City .
 *         ?z ?y1 ?x .
 *         dbr:New_York_City ?y2 ?z .
 *     }
 * }
 * }
 */
public class Tc05GeneratorSyntheticOntology extends TcGeneratorSyntheticOntology {


    public Tc05GeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public Tc05GeneratorSyntheticOntology(File directory) {
        super(directory);
    }

    public Tc05GeneratorSyntheticOntology(String directory) {
        super(directory);
    }

    public Tc05GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                          int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                          int[] sizes) {
        super(directory, numberOfClasses, numberOfEdges, totalNodesFactor, maxTriplesPerNode, branchingFactor, sizes);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Tc05GeneratorSyntheticOntology.class);

    @Override
    public String getTcId() {
        return "tc05";
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

            String z = ontologyGenerator.getRandomTripleWithObject(targetInstance).subject;
            String x = ontologyGenerator.getRandomTripleWithObject(z).subject;

            // determine target type
            String targetType = ontologyGenerator.getInstanceType(x);

            // ensure we have enough instances of the target type
            ontologyGenerator.ensureEnoughInstancesOfType(targetType, 2*nodesOfInterest);

            Set<String> typeInstances = ontologyGenerator.getInstancesOfTypeTransitive(targetType);

            writeConfigToNewLog(fileToBeWritten, totalNodes, nodesOfInterest, totalEdges, maxTriplesPerNode);
            configLog.append("Target instance: ").append(targetInstance).append("\n");
            configLog.append("Target type: ").append(targetType).append("\n");

            // forward positives x -> r1 -> z -> r2 -> targetInstance
            while(positives.size() < nodesOfInterest/2){
                Triple t1 = ontologyGenerator.getRandomTripleWithObject(targetInstance);
                Triple t2 = ontologyGenerator.getRandomTripleWithObjectWhereSubjectOfType(t1.subject, targetType);
                positives.add(t1.subject);
                graph.addObjectTriple(t1);
                graph.addObjectTriple(t2);
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " .\n");
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " .\n");
            }

            // backward positives x -> r1 -> z -> r2 -> targetInstance
            while(positives.size() < nodesOfInterest){
                Triple t1 = ontologyGenerator.getRandomTripleWithSubject(targetInstance);
                Triple t2 = ontologyGenerator.getRandomTripleWithSubjectWhereObjectOfType(t1.object, targetType);
                positives.add(t2.object);
                graph.addObjectTriple(t1);
                graph.addObjectTriple(t2);
                writer.write(t1.subject + " " + t1.predicate + " " + t1.object + " .\n");
                writer.write(t2.subject + " " + t2.predicate + " " + t2.object + " .\n");
            }

            typeInstances.removeAll(positives);
            negatives = typeInstances;


            // let's add random triples / write negatives
            for(String node : ontologyGenerator.getInstances()) {

                // randomly generate triples
                // draw number of triples
                int tripleNumber = random.nextInt(maxTriplesPerNode + 1);
                for (int i = 0; i < tripleNumber; i++) {
                    Triple t = ontologyGenerator.getRandomTripleWithSubject(node);

                    // case 1: object is target instance
                    if(t.object.equals(targetInstance)){
                        i--;
                        continue;
                    }

                    // case 2: object is connected to targetInstance
                    for(Triple checkTriple : graph.getObjectTriplesInvolvingObject(targetInstance)) {
                        if(checkTriple.subject.equals(t.object)){
                            i--;
                            continue;
                        }
                    }

                    // case 3: targetInstance is connected to subject
                    for(Triple checkTriple : graph.getObjectTriplesInvolvingSubject(targetInstance)){
                        if(checkTriple.object.equals(t.subject)){
                            i++;
                            continue;
                        }
                    }

                    graph.addObjectTriple(t);
                    writer.write(t.subject + " " + t.predicate + " " + t.object + " .\n");
                }
            }


        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the file.", e);
        }
    }

}
