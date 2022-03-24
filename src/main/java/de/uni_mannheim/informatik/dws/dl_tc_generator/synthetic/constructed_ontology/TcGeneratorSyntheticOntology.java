package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.TcGeneratorSyntheticConstructed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public abstract class TcGeneratorSyntheticOntology extends TcGeneratorSyntheticConstructed {


    public TcGeneratorSyntheticOntology(File directory, int[] sizes) {
        super(directory, sizes);
        setDefaultOntologyGenerator();
    }

    public TcGeneratorSyntheticOntology(File directory) {
        super(directory);
        setDefaultOntologyGenerator();
    }

    public TcGeneratorSyntheticOntology(String directory) {
        super(directory);
        setDefaultOntologyGenerator();
    }

    public TcGeneratorSyntheticOntology(File directory,
                                        int numberOfClasses,
                                        int numberOfEdges,
                                        int totalNodesFactor,
                                        int maxTriplesPerNode,
                                        int branchingFactor,
                                        int[] sizes){
        super(directory);
        setSizes(sizes);
        setNumberOfEdges(numberOfEdges);
        this.totalNodesFactor = totalNodesFactor;
        this.numberOfClasses = numberOfClasses;
        this.numberOfEdges = numberOfEdges;
        this.treeGenerator = new ConstantSplitTreeGenerator(branchingFactor);
        setMaxTriplesPerNode(maxTriplesPerNode);
        setOntologyGenerator(new OntologyGenerator(
                numberOfClasses, numberOfEdges, getNumberOfInstances(),
                getTcId(), new ConstantSplitTreeGenerator(branchingFactor)
        ));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TcGeneratorSyntheticOntology.class);

    int numberOfClasses = Defaults.NUMBER_OF_CLASSES;
    int numberOfEdges = Defaults.NUMBER_OF_EDGES;
    ConstantSplitTreeGenerator treeGenerator = new ConstantSplitTreeGenerator(Defaults.BRANCHING_FACTOR);

    private void setDefaultOntologyGenerator(){
        OntologyGenerator og = new OntologyGenerator(
                Defaults.NUMBER_OF_CLASSES,
                Defaults.NUMBER_OF_EDGES,
                getNumberOfInstances(),
                getTcId(),
                treeGenerator
        );
        setOntologyGenerator(og);
    }

    OntologyGenerator ontologyGenerator;

    /**
     * Set the total nodes factor.
     * Note: While this method works, it is more efficient to use
     * {@link TcGeneratorSyntheticOntology#setOntologyGenerator(OntologyGenerator)} because for each member variable
     * change, the graph has to be re-generated.
     * @param totalNodesFactor The total nodes factor (must be greater than 2).
     */
    @Override
    public void setTotalNodesFactor(int totalNodesFactor) {
        if(totalNodesFactor < 2){
            LOGGER.error("The totalNodesFactor must be >= 2. Doing nothing.");
            return;
        }
        this.totalNodesFactor = totalNodesFactor;
        this.ontologyGenerator = new OntologyGenerator(
                numberOfClasses,
                numberOfEdges,
                getNumberOfInstances(),
                getTcId(),
                treeGenerator
        );
    }

    /**
     * Set the size classes.
     * Note: While this method works, it is more efficient to use
     * {@link TcGeneratorSyntheticOntology#setOntologyGenerator(OntologyGenerator)} because for each member variable
     * change, the graph has to be re-generated.
     * @param sizes The desired size classes.
     */
    @Override
    public void setSizes(int[] sizes) {
        if(sizes == null || sizes.length == 0){
            LOGGER.error("Invalid size parameter.");
        }
        this.sizes = sizes;
        if(this.ontologyGenerator != null) {
            this.ontologyGenerator = new OntologyGenerator(
                    numberOfClasses,
                    numberOfEdges,
                    getNumberOfInstances(),
                    getTcId(),
                    treeGenerator
            );
        }
    }

    public int getNumberOfInstances(){
        return Arrays.stream(sizes).sum() * totalNodesFactor;
    }

    public OntologyGenerator getOntologyGenerator() {
        return ontologyGenerator;
    }

    public void setOntologyGenerator(OntologyGenerator ontologyGenerator) {
        this.ontologyGenerator = ontologyGenerator;
    }

    /**
     * The set of negatives (in the hard case, those are tailored rather than generated).
     */
    Set<String> negatives = new HashSet<>();

    public final Random random = new Random(42);

    /**
     * Get the negatives.
     *
     * @return Set of negatives.
     */
    @Override
    public Set<String> getNegatives() {
        return negatives;
    }

    public void writeConfigToNewLog(File fileToBeWritten, int totalNodes, int nodesOfInterest, int totalEdges, int maxTriplesPerNode){
        configLog = new StringBuilder();
        configLog.append("File to be written: ").append(fileToBeWritten.getAbsolutePath()).append("\n");
        configLog.append("# of Instances : ").append(ontologyGenerator.getInstances().size()).append("\n");
        configLog.append("# of Classes : ").append(ontologyGenerator.getClasses().size()).append("\n");
        configLog.append("# of Properties: ").append(ontologyGenerator.getProperties().size()).append("\n");
        configLog.append("Max triples per node: ").append(maxTriplesPerNode).append("\n");
        configLog.append("Nodes of interest: ").append(nodesOfInterest).append("\n");
        Class<? extends ITreeGenerator> c = ontologyGenerator.getTreeGenerator().getClass();
        configLog.append("Tree generator class: ").append(c.getSimpleName()).append("\n");
        int avgBranchingFactor =
                ((ConstantSplitTreeGenerator) ontologyGenerator.getTreeGenerator()).getAvgBranchingFactor();
        configLog.append("AVG Branching factor: ").append(avgBranchingFactor).append("\n");
        if(c.getSimpleName().equals(ConstantSplitTreeGenerator.class.getSimpleName())){
            ConstantSplitTreeGenerator cstg = (ConstantSplitTreeGenerator) ontologyGenerator.getTreeGenerator();
            configLog.append("Split number of ConstantSplitTreeGenerator: ").append(cstg.avgBranchingFactor)
                    .append("\n");
        }
    }

    public int getBranchingFactor() {
        return this.treeGenerator.avgBranchingFactor;
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    @Override
    public int getNumberOfEdges() {
        return numberOfEdges;
    }
}
