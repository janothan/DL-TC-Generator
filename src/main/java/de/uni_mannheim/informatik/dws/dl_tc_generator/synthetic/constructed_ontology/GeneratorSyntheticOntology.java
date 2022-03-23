package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class GeneratorSyntheticOntology extends GeneratorSynthetic {


    public static final String TC_GROUP_NAME = "synthetic_ontology";

    public GeneratorSyntheticOntology(File directoryToGenerate) {
        super(directoryToGenerate);
        generatorSet = new HashSet<>();

        // TC01
        generatorSet.add(new Tc01GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc01", getTcGroupName()).toFile()
        ));

        // TC02
        generatorSet.add(new Tc02GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc02", getTcGroupName()).toFile()
        ));

        // TC03
        generatorSet.add(new Tc03GeneratorSyntheticOntology(
           Paths.get(getGeneratedDirectory().getAbsolutePath(), "tc03", getTcGroupName()).toFile()
        ));

        // TC04
        generatorSet.add(new Tc04GeneratorSyntheticOntology(
                Paths.get(getGeneratedDirectory().getAbsolutePath(), "tc04", getTcGroupName()).toFile()
        ));

        // TC05
        generatorSet.add(new Tc05GeneratorSyntheticOntology(
                Paths.get(getGeneratedDirectory().getAbsolutePath(), "tc05", getTcGroupName()).toFile()
        ));

        // TC06
        generatorSet.add((new Tc06GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc06", getTcGroupName()).toFile()
        )));

        // TC07
        generatorSet.add((new Tc07GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc07", getTcGroupName()).toFile()
        )));

        // TC08
        generatorSet.add((new Tc08GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc08", getTcGroupName()).toFile()
        )));

        // TC09
        generatorSet.add((new Tc09GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc09", getTcGroupName()).toFile()
        )));

        // TC10
        generatorSet.add((new Tc10GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc10", getTcGroupName()).toFile()
        )));

        // TC11
        generatorSet.add((new Tc11GeneratorSyntheticOntology(
                Paths.get(generatedDirectory.getAbsolutePath(), "tc11", getTcGroupName()).toFile()
        )));

        generatorSet.forEach(x -> x.setSizes(sizes));
        generatorSet.forEach(x -> x.setSeparator(separator));
    }

    public GeneratorSyntheticOntology(String directoryToGeneratePath) {
        this(new File(directoryToGeneratePath));
    }

    @Override
    public String getTcGroupName() {
        return TC_GROUP_NAME;
    }
}
