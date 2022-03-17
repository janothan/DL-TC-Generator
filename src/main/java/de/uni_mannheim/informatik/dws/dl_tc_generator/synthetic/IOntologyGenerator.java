package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;

import java.util.Set;

public interface IOntologyGenerator {


    String getRandomInstanceId();

    String getRandomPredicateId();

    String getRandomClassId();

    String getRandomPredicateForInstance(String instanceId);

    Triple getRandomPredicateObjectForInstance(String nodeId);

    String getRandomObjectNodeForInstance(String edgeId);

    String getRandomSubjectNodeForPredicate(String edgeId);

    Set<String> getClasses();

    Set<String> getProperties();

    Set<String> getInstances();

    String getRange(String propertyId);

    String getDomain(String propertyId);

    Tree getClassTree();

    String getInstanceType(String instance);

}
