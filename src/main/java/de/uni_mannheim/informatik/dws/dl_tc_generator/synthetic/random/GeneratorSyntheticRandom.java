package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.random;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class GeneratorSyntheticRandom extends GeneratorSynthetic {


    public static final String TC_GROUP_NAME = "synthetic_random";


    public GeneratorSyntheticRandom(File directoryToGenerate) {
        super(directoryToGenerate);
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc02GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc03GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc03", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc04GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc04", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc05GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc05", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc06GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc07GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc08GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc09GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc09", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc10GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc10", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc11GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc11", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc12GeneratorSyntheticRandom(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc12", TC_GROUP_NAME).toFile()
        ));

        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSyntheticRandom(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    public String getTcGroupName(){
        return TC_GROUP_NAME;
    }
}
