package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;

import java.util.List;

public interface IOntologyGenerator {


    String getRandomNodeId();

    String getRandomEdgeId();

    String getRandomEdgeForNode();

    Triple getRandomPredicateObjectForNode(String nodeId);

    String getRandomObjectNodeForEdge(String edgeId);

    String getRandomSubjectNodeForEdge(String edgeId);

    List<String> getClasses();

    List<String> getEdges();

    List<String> getInstances();
}
