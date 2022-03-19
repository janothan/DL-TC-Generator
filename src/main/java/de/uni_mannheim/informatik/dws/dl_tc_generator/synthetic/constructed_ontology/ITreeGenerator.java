package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import java.util.Set;

public interface ITreeGenerator {


    Tree generateTree(Set<String> nodes, String rootNode);
}
