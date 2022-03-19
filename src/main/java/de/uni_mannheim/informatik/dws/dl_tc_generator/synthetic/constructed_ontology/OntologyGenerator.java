package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import de.uni_mannheim.informatik.dws.dl_tc_generator.Util;
import de.uni_mannheim.informatik.dws.jrdf2vec.walk_generation.data_structures.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyGenerator implements IOntologyGenerator {


    public OntologyGenerator(int numberOfClasses, int numberOfProperties, int numberOfInstances, String tcId,
                             ITreeGenerator treeGenerator) {
        this.tcId = tcId;

        // generate ids
        classIds = new HashSet<>(generateIds(numberOfClasses, "C_" + tcId));
        propertyIds = new HashSet<>(generateIds(numberOfProperties, "P_" + tcId));
        instanceIds = new HashSet<>(generateIds(numberOfInstances, "I_" + tcId));

        // build class tree
        this.treeGenerator = treeGenerator;
        classTree = treeGenerator.generateTree(classIds, classIds.iterator().next());

        // type instances
        instanceTypes = new HashMap<>();
        classInstancesNonTransitive = new HashMap<>();
        for(String instance : instanceIds){
            String classId = Util.randomDrawFromSet(classIds);
            addInstance(instance, classId);
        }

        // build property domain and range
        propertyRanges = new HashMap<>();
        propertyRangeInstances = new HashMap<>();
        propertyDomains = new HashMap<>();
        propertyDomainInstances = new HashMap<>();
        instanceSubjectProperties = new HashMap<>();

        boolean isFirstProperty = true;
        for(String propertyId : propertyIds) {
            if(isFirstProperty){
                addPropertyDomain(propertyId, classTree.getRoot());
                addPropertyRange(propertyId, classTree.getRoot());
                isFirstProperty = false;
            } else {
                String randomDomain = Util.randomDrawFromSet(classIds);
                addPropertyDomain(propertyId, randomDomain);
                String randomRange = Util.randomDrawFromSet(classIds);
                addPropertyRange(propertyId, randomRange);
            }
        }
    }

    private void addInstance(String instance, String classId){
        instanceIds.add(instance);
        instanceTypes.put(instance, classId);

        Set<String> types = classTree.getAllChildrenOfNode(classId);
        types.add(classId);

        if(classInstancesNonTransitive.containsKey(classId)){
            classInstancesNonTransitive.get(classId).add(instance);
        } else {
            Set<String> instanceSet = new HashSet<>();
            instanceSet.add(instance);
            classInstancesNonTransitive.put(classId, instanceSet);
        }

        if(propertyDomainInstances != null) {
            // we need to update:
            // - propertyDomainInstances
            // - instanceSubjectProperties
            for (Map.Entry<String, String> entry : propertyDomains.entrySet()) {
                if (types.contains(entry.getValue())) {
                    updatePropertyDomain(entry.getKey());
                }
            }
        }

        if(propertyRangeInstances != null){
            for(Map.Entry<String, String> entry : propertyRanges.entrySet()){
                if(types.contains(entry.getValue())){
                    updatePropertyRange(entry.getKey());
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyGenerator.class);

    private Set<String> generateIds(int numberOfIds, String idComponent){
        Set<String> result = new HashSet<>();
        for(int i = 0; i < numberOfIds; i++){
            result.add("<" + idComponent + "_" + i + ">");
        }
        return result;
    }

    private void addPropertyDomain(String property, String domain){
        propertyDomains.put(property, domain);
        updatePropertyDomain(property);
    }

    /**
     * Update indices for the provided property.
     * @param property The property that is to be updated.
     */
    private void updatePropertyDomain(String property) {
        final String domain = getDomain(property);
        Set<String> instances = getClassInstancesTransitive(domain);
        propertyDomainInstances.put(property, instances);
        for(String instance : instances) {
            if(instanceSubjectProperties.containsKey(instance)) {
                instanceSubjectProperties.get(instance).add(property);
            } else {
                Set<String> properties = new HashSet<>();
                properties.add(property);
                instanceSubjectProperties.put(instance, properties);
            }
        }
    }

    /**
     * Update the property indices for domain and range.
     * @param propertyId Identifier of the property.
     */
    private void updateProperty(String propertyId){
        updatePropertyRange(propertyId);
        updatePropertyDomain(propertyId);
    }

    private void addPropertyRange(String property, String range){
        propertyRanges.put(property, range);
        updatePropertyRange(property);
    }

    private void updatePropertyRange(String property){
        final String range = getRange(property);
        Set<String> instances = getClassInstancesTransitive(range);
        propertyRangeInstances.put(property, instances);
    }

    public Set<String> getClassInstancesTransitive(String classId){
        Set<String> allClasses = new HashSet<>();
        allClasses.add(classId);
        allClasses.addAll(classTree.getAllChildrenOfNode(classId));
        Set<String> result = new HashSet<>();
        for(String classIdLocal : allClasses) {
            Set<String> instances = classInstancesNonTransitive.get(classIdLocal);
            if(instances == null){
                LOGGER.warn("No class instances found for class '" + classIdLocal + "'. " +
                        "This may happen in situations where there are many classes and few instances.");
            }
            result.addAll(instances);
        }
        return result;
    }

    private final String tcId;
    private final ITreeGenerator treeGenerator;
    Set<String> classIds;
    Set<String> propertyIds;
    Set<String> instanceIds;
    Tree classTree;

    Map<String, String> propertyRanges;
    Map<String, String> propertyDomains;

    /**
     * {@code instance -> <all possible property ids where domain(property)=instance >}
     */
    Map<String, Set<String>> instanceSubjectProperties;

    /**
     * {@code property -> <all possible range INSTANCE ids>}
     */
    Map<String, Set<String>> propertyRangeInstances;

    /**
     * {@code property -> <all possible domain INSTANCE ids>}
     */
    Map<String, Set<String>> propertyDomainInstances;

    /**
     * {@code class -> all instances (non transitive)}
     */
    Map<String, Set<String>> classInstancesNonTransitive;

    /**
     * {@code instance -> class id}
     */
    Map<String, String> instanceTypes;

    @Override
    public String getRandomInstanceId() {
        return Util.randomDrawFromSet(instanceIds);
    }

    @Override
    public String getRandomPredicateId() {
        return Util.randomDrawFromSet(propertyIds);
    }

    @Override
    public String getRandomClassId() {
        return Util.randomDrawFromSet(classIds);
    }

    @Override
    public String getRandomPredicateForInstance(String instanceId) {
        if(instanceSubjectProperties.get(instanceId) == null){
            LOGGER.error("Did not find instance " + instanceId + " in instanceSubjectProperties.\n" +
                    "Program will fail.");
        }
        return Util.randomDrawFromSet(instanceSubjectProperties.get(instanceId));
    }

    @Override
    public Triple getRandomPredicateObjectForInstance(String nodeId) {
        String predicate = getRandomPredicateForInstance(nodeId);
        Set<String> instances = propertyRangeInstances.get(predicate);
        if(instances == null){
            LOGGER.error("No property for instance id: " + nodeId);
            return null;
        }
        String object = Util.randomDrawFromSet(instances);
        return new Triple(nodeId, predicate, object);
    }

    /**
     * Obtain a random range instance of the specified property.
     * @param edgeId Property id.
     * @return Random instance that fulfills range criterion.
     */
    @Override
    public String getRandomObjectNodeForInstance(String edgeId) {
        return Util.randomDrawFromSet(propertyRangeInstances.get(edgeId));
    }

    @Override
    public String getRandomSubjectNodeForPredicate(String edgeId) {
        return Util.randomDrawFromSet(propertyDomainInstances.get(edgeId));
    }

    @Override
    public Set<String> getClasses() {
        return this.classIds;
    }

    @Override
    public Set<String> getProperties() {
        return this.propertyIds;
    }

    @Override
    public Set<String> getInstances() {
        return this.instanceIds;
    }

    @Override
    public String getRange(String propertyId) {
        return propertyRanges.get(propertyId);
    }

    @Override
    public String getDomain(String propertyId) {
        return propertyDomains.get(propertyId);
    }

    @Override
    public Tree getClassTree() {
        return this.classTree;
    }

    @Override
    public String getInstanceType(String instance) {
        return instanceTypes.get(instance);
    }

    @Override
    public void ensureSubjectNumberForPredicate(String predicateId, int subjectNumber) {
        int target = subjectNumber - propertyDomainInstances.get(predicateId).size();
        if (target <= 0){
            LOGGER.info("Enough subjects for " + predicateId + ". Nothing to generate.");
        } else {
            LOGGER.warn("Not enough subjects for " + predicateId + ". Generating " + target + " additional nodes.");
            String targetClass = propertyDomains.get(predicateId);
            Set<String> desiredClasses = new HashSet<>(classTree.getAllChildrenOfNode(targetClass));
            desiredClasses.add(targetClass);
            for (int i = 0; i < target; i++){
                addInstance("<EXTRA_I_FOR_" + Util.removeTags(predicateId) + "_" + i + ">",
                        Util.randomDrawFromSet(desiredClasses));
            }
        }
    }

    @Override
    public void ensureEnoughInstancesOfType(String classId, int desiredNumber) {
        int target = desiredNumber - getInstancesOfTypeTransitive(classId).size();
        if(target <= 0){
            LOGGER.info("Enough instances for class " + classId + ". Nothing to generate.");
        } else {
            for (int i = 0; i < target; i++) {
                addInstance("<EXTRA_I_FOR_CLASS_" + Util.removeTags(classId) + "_" + i + ">",
                        classId);
            }
        }
    }

    @Override
    public Set<String> getInstancesOfTypeTransitive(String classId) {
        Set<String> targetClasses = new HashSet<>(classTree.getAllChildrenOfNode(classId));
        targetClasses.add(classId);
        Set<String> result = new HashSet<>();
        for(Map.Entry<String, Set<String>> entry : classInstancesNonTransitive.entrySet()){
            if (targetClasses.contains(entry.getKey())){
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    public String getTcId() {
        return tcId;
    }

    public ITreeGenerator getTreeGenerator() {
        return treeGenerator;
    }

    public Set<String> getClassInstancesNonTransitive(String classId) {
        return classInstancesNonTransitive.get(classId);
    }

    public Set<String> getPropertyRangeInstances(String property){
        return propertyRangeInstances.get(property);
    }
}
