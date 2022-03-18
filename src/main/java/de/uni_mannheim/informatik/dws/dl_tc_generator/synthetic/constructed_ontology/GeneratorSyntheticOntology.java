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
