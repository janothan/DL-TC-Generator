package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class GeneratorSyntheticConstructed extends GeneratorSynthetic {


    public static final String TC_GROUP_NAME = "synthetic_constructed";

    public GeneratorSyntheticConstructed(File directoryToGenerate) {
        super(directoryToGenerate);
        generatorSet = new HashSet<>();
        generatorSet.add(new Tc01GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc02GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc03GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc03", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc04GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc04", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc05GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc05", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc06GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc07GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc08GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc09GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc09", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc10GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc10", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc11GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc11", TC_GROUP_NAME).toFile()
        ));
        generatorSet.add(new Tc12GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc12", TC_GROUP_NAME).toFile()
        ));

        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSyntheticConstructed(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    public String getTcGroupName(){
        return TC_GROUP_NAME;
    }
}
