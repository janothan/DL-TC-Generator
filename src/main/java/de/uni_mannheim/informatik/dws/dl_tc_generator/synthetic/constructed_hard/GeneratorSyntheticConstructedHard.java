package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_hard;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;

public class GeneratorSyntheticConstructedHard extends GeneratorSynthetic {


    public static final String TC_GROUP_NAME = "synthetic_constructed_hard";

    public GeneratorSyntheticConstructedHard(File directoryToGenerate) {
        super(directoryToGenerate);
        generatorSet = new HashSet<>();

        // TC 01 hard/non-hard is identical
        generatorSet.add(new Tc01GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", TC_GROUP_NAME).toFile()
        ));

        // TC 02 hard/non-hard is identical
        generatorSet.add(new Tc02GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", TC_GROUP_NAME).toFile()
        ));

        // TC 03 hard/non-hard is identical
        generatorSet.add(new Tc03GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc03", TC_GROUP_NAME).toFile()
        ));

        // TC 04 hard/non-hard is identical
        generatorSet.add(new Tc04GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc04", TC_GROUP_NAME).toFile()
        ));

        // TC05 hard/non-hard is identical
        generatorSet.add(new Tc05GeneratorSyntheticConstructed(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc05", TC_GROUP_NAME).toFile()
        ));

        // TC06 can be made hard
        generatorSet.add(new Tc06GeneratorSyntheticConstructedHard(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06h", TC_GROUP_NAME).toFile()
        ));


        generatorSet.add(new Tc07GeneratorSyntheticConstructedHard(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07h", TC_GROUP_NAME).toFile()
        ));

        generatorSet.add(new Tc08GeneratorSyntheticConstructedHard(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08h", TC_GROUP_NAME).toFile()
        ));

        /*
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

         */

        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSyntheticConstructedHard(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    public String getTcGroupName(){
        return TC_GROUP_NAME;
    }
}
