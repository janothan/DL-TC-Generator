package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;

import java.util.Set;

public interface IOntologyGenerator {


    String getRandomInstanceId();

    String getRandomPropertyId();

    /**
     * {@code predicate.domain = predicate.range}
     * @return Some randomly drawn predicate ID where the domain is equal to the range.
     */
    String getRandomPropertyIdWhereDomainIsRange();

    String getRandomClassId();

    String getRandomPropertyForInstance(String instanceId);

    Triple getRandomPropertyObjectForInstance(String nodeId);

    String getRandomObjectForProperty(String edgeId);

    String getRandomSubjectForProperty(String edgeId);

    Set<String> getClasses();

    Set<String> getProperties();

    Set<String> getInstances();

    String getRange(String propertyId);

    String getDomain(String propertyId);

    Tree getClassTree();

    String getInstanceType(String instance);

    /**
     * Ensure that there are at least {@code subjectNumber} different subjects for the provided {@code predicateId}.
     * @param predicateId Predicate ID.
     * @param subjectNumber Number of desired unique subjects.
     */
    void ensureSubjectNumberForProperty(String predicateId, int subjectNumber);

    /**
     * Ensure that there are at least {@code objectNumber} different objects for the provided {@code predicateId}.
     * @param predicateId Predicate ID.
     * @param objectNumber Number of desired unique objects.
     */
    void ensureObjectNumberForProperty(String predicateId, int objectNumber);

    void ensureEnoughInstancesOfType(String classId, int desiredNumber);

    Set<String> getInstancesOfTypeTransitive(String classId);

    ITreeGenerator getTreeGenerator();

    Set<String> getPropertyRangeInstances(String property);

    void ensurePropertyWithRangeInstance(String instanceId, int desiredNumber);

    void ensurePropertyWithDomainInstance(String instanceId, int desiredNumber);

    Set<String> getPropertiesWhereInstanceIsDomain(String instanceId);

    Set<String> getPropertiesWhereInstanceIsRange(String instanceId);

    String getRandomPropertyWhereInstanceIsDomain(String instanceId);

    String getRandomPropertyWhereInstanceIsRange(String instanceId);
}
