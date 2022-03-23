package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.constructed_ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A very simple tree datastructure.
 */
public class Tree implements ITree {


    public Tree(String rootNode) {
        this.root = rootNode;
        nodeIds = new HashSet<>();
        nodeIds.add(rootNode);
        nodeToChildren = new HashMap<>();
        nodeToParents = new HashMap<>();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Tree.class);

    /**
     * The root node.
     */
    private final String root;

    private final Set<String> nodeIds;

    /**
     * Map from {@code nodeId -> childIds}.
     */
    private final Map<String, Set<String>> nodeToChildren;

    /**
     * Map from {@code nodeId -> parentIds}.
     */
    private final Map<String, Set<String>> nodeToParents;


    /**
     * Non transitive
     * @param nodeId The node id.
     * @return
     */
    @Override
    public Set<String> getChildrenOfNode(String nodeId) {
        if (nodeId == null || !nodeIds.contains(nodeId)) {
            LOGGER.error("nodeId does not exist. Returning null.");
            return new HashSet<>();
        }
        Set<String> result = nodeToChildren.get(nodeId);
        if (result == null) {
            return new HashSet<>();
        } else return result;
    }

    @Override
    public Set<String> getAllChildrenOfNode(String nodeId) {
        Set<String> result = new HashSet<>();
        for (String child : this.getChildrenOfNode(nodeId)) {
            result.add(child);
            result.addAll(getAllChildrenOfNode(child));
        }
        return result;
    }


    @Override
    public Set<String> getParentsOfNode(String nodeId) {
        if (nodeId == null || !nodeIds.contains(nodeId)) {
            LOGGER.error("nodeId does not exist. Returning null.");
            return new HashSet<>();
        }
        Set<String> result = nodeToParents.get(nodeId);
        if (result == null) {
            return new HashSet<>();
        } else return result;
    }

    @Override
    public Set<String> getAllParentsOfNode(String nodeId) {
        Set<String> result = new HashSet<>();
        if (nodeId.equals(getRoot())) {
            result.add(nodeId);
        }
        for (String parent : getParentsOfNode(nodeId)) {
            result.add(parent);
            result.addAll(getAllParentsOfNode(parent));
        }
        return result;
    }

    public String getRoot() {
        return root;
    }

    @Override
    public Set<String> getAllNodes() {
        return this.nodeIds;
    }

    @Override
    public void addLeaf(String parent, String child) {
        this.nodeIds.add(parent);
        this.nodeIds.add(child);

        if (this.nodeToChildren.containsKey(parent)) {
            this.nodeToChildren.get(parent).add(child);
        } else {
            Set<String> childSet = new HashSet<>();
            childSet.add(child);
            this.nodeToChildren.put(parent, childSet);
        }

        if (this.nodeToParents.containsKey(child)) {
            this.nodeToParents.get(child).add(parent);
        } else {
            Set<String> parentSet = new HashSet<>();
            parentSet.add(parent);
            this.nodeToParents.put(child, parentSet);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Nodes:\n");
        for (String node : this.nodeIds) {
            builder.append("- ").append(node).append("\n");
        }
        builder.append("\nConnections:\n");
        for (Map.Entry<String, Set<String>> entry : nodeToChildren.entrySet()) {
            for (String child : entry.getValue()) {
                builder.append("- ").append(entry.getKey()).append(" -> ").append(child).append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Checks if aID is a bID.
     * @param aId a id
     * @param isBId b id
     * @return true if a is a b else false.
     */
    public boolean isA(String aId, String isBId){
        Set<String> bChildren = getAllChildrenOfNode(isBId);
        bChildren.add(isBId);
        return bChildren.contains(aId);
    }

}
