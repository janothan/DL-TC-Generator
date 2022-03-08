package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.TcGeneratorSyntheticConstructed;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


public abstract class TcGeneratorSyntheticConstructedHard extends TcGeneratorSyntheticConstructed {


    public TcGeneratorSyntheticConstructedHard(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public TcGeneratorSyntheticConstructedHard(File directory) {
        super(directory);
    }

    public TcGeneratorSyntheticConstructedHard(String directory) {
        super(directory);
    }

    /**
     * The set of negatives (in the hard case, those are tailored rather than generated).
     */
    Set<String> negatives = new HashSet<>();

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
