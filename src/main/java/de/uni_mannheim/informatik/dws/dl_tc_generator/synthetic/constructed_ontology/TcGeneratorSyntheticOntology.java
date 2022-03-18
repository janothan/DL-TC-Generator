package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Defaults;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.TcGeneratorSyntheticConstructed;

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

    private void setDefaultOntologyGenerator(){
        int instances = Arrays.stream(sizes).sum() * Defaults.NODE_FACTOR;
        OntologyGenerator og = new OntologyGenerator(
            Defaults.NUMBER_OF_CLASSES, Defaults.NUMBER_OF_EDGES, instances, getTcId(),
                new ConstantSplitTreeGenerator(Defaults.CLASS_SPLITS)
        );
        setOntologyGenerator(og);
    }

    IOntologyGenerator ontologyGenerator;

    public IOntologyGenerator getOntologyGenerator() {
        return ontologyGenerator;
    }

    public void setOntologyGenerator(IOntologyGenerator ontologyGenerator) {
        this.ontologyGenerator = ontologyGenerator;
    }

    /**
     * The set of negatives (in the hard case, those are tailored rather than generated).
     */
    Set<String> negatives = new HashSet<>();

    public final Random random = new Random();

    /**
     * Get the negatives.
     *
     * @return Set of negatives.
     */
    @Override
    public Set<String> getNegatives() {
        return negatives;
    }
}
