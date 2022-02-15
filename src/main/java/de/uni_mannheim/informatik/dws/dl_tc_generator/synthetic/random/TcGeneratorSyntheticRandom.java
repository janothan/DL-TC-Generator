package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;

import java.io.File;

public abstract class TcGeneratorSyntheticRandom extends TcGeneratorSynthetic {


    public TcGeneratorSyntheticRandom(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public TcGeneratorSyntheticRandom(File directory) {
        super(directory);
    }

    public TcGeneratorSyntheticRandom(String directory) {
        super(directory);
    }

}

