package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;

public class GeneratorSyntheticConstructed extends GeneratorSynthetic {


    public GeneratorSyntheticConstructed(File directoryToGenerate) {
        super(directoryToGenerate);
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", "synthetic_constructed").toFile()
        ));
        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSyntheticConstructed(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

}
