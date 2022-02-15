package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;

import java.io.File;

public class GeneratorSyntheticConstructed extends GeneratorSynthetic {


    public GeneratorSyntheticConstructed(File directoryToGenerate) {
        super(directoryToGenerate);

    }

    public GeneratorSyntheticConstructed(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }
}
