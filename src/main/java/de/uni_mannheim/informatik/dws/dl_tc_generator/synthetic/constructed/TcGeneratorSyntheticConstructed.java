package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.TcGeneratorSynthetic;

import java.io.File;

public abstract class TcGeneratorSyntheticConstructed extends TcGeneratorSynthetic {
    public TcGeneratorSyntheticConstructed(File directory, int[] sizes) {
        super(directory, sizes);
    }

    public TcGeneratorSyntheticConstructed(File directory) {
        super(directory);
    }

    public TcGeneratorSyntheticConstructed(String directory) {
        super(directory);
    }
}
