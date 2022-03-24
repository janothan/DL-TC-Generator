package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class GeneratorSyntheticOntology extends GeneratorSynthetic {


    public static final String TC_GROUP_NAME = "synthetic_ontology";

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorSyntheticOntology.class);

    public GeneratorSyntheticOntology(File directoryToGenerate) {
        this(directoryToGenerate,
                Defaults.NUMBER_OF_CLASSES,
                Defaults.NUMBER_OF_EDGES,
                Defaults.NODE_FACTOR,
                Defaults.MAX_TRIPLES_PER_NODE,
                Defaults.BRANCHING_FACTOR,
                Defaults.SIZES);
    }

    public GeneratorSyntheticOntology(File directory, int numberOfClasses, int numberOfEdges,
                                      int totalNodesFactor, int maxTriplesPerNode, int branchingFactor,
                                      int[] sizeClasses) {
        super(directory);
        generatorSet = new HashSet<>();
        setNumberOfEdges(numberOfEdges); // since the set is empty, this is not performance critical
        this.nodesFactor = totalNodesFactor;
        setSizes(sizeClasses);

        // TC01
        LOGGER.info("Instantiating Tc01GeneratorSyntheticOntology");
        generatorSet.add(new Tc01GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC02
        LOGGER.info("Instantiating Tc02GeneratorSyntheticOntology");
        generatorSet.add(new Tc02GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC03
        LOGGER.info("Instantiating Tc03GeneratorSyntheticOntology");
        generatorSet.add(new Tc03GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc03", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC04
        LOGGER.info("Instantiating Tc04GeneratorSyntheticOntology");
        generatorSet.add(new Tc04GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc04", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC05
        LOGGER.info("Instantiating Tc05GeneratorSyntheticOntology");
        generatorSet.add(new Tc05GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc05", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC06
        LOGGER.info("Instantiating Tc06GeneratorSyntheticOntology");
        generatorSet.add(new Tc06GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC07
        LOGGER.info("Instantiating Tc07GeneratorSyntheticOntology");
        generatorSet.add(new Tc07GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC08
        LOGGER.info("Instantiating Tc08GeneratorSyntheticOntology");
        generatorSet.add(new Tc08GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC09
        LOGGER.info("Instantiating Tc09GeneratorSyntheticOntology");
        generatorSet.add(new Tc09GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc09", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC10
        LOGGER.info("Instantiating Tc10GeneratorSyntheticOntology");
        generatorSet.add(new Tc10GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc10", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC11
        LOGGER.info("Instantiating Tc11GeneratorSyntheticOntology");
        generatorSet.add(new Tc11GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc11", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));

        // TC12
        LOGGER.info("Instantiating Tc12GeneratorSyntheticOntology");
        generatorSet.add(new Tc12GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc12", getTcGroupName()).toFile(),
                numberOfClasses,
                numberOfEdges,
                totalNodesFactor,
                maxTriplesPerNode,
                branchingFactor,
                sizeClasses
        ));
    }

    public GeneratorSyntheticOntology(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    public void setNumberOfEdges(int numberOfEdges){
        this.numberOfEdges = numberOfEdges;
        generatorSet.forEach(x -> x.setNumberOfEdges(numberOfEdges) );
    }

    @Override
    public String getTcGroupName() {
        return TC_GROUP_NAME;
    }
}
