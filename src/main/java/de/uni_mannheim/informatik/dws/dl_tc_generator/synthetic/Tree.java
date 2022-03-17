package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A very simple tree datastructure.
 */
public class Tree implements ITree {


    public Tree(String rootNode){
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


    @Override
    public Set<String> getChildrenOfNode(String nodeId) {
        if(nodeId == null || !nodeIds.contains(nodeId)){
            LOGGER.error("nodeId does not exist. Returning null.");
            return null;
        }
        return nodeToChildren.get(nodeId);
    }

    @Override
    public Set<String> getParentsOfNode(String nodeId) {
        if(nodeId == null || !nodeIds.contains(nodeId)){
            LOGGER.error("nodeId does not exist. Returning null.");
            return null;
        }
        return nodeToParents.get(nodeId);
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

        if(this.nodeToChildren.containsKey(parent)){
            this.nodeToChildren.get(parent).add(child);
        } else {
            Set<String> childSet = new HashSet<>();
            childSet.add(child);
            this.nodeToChildren.put(parent, childSet);
        }

        if(this.nodeToParents.containsKey(child)){
            this.nodeToParents.get(child).add(parent);
        } else {
            Set<String> parentSet = new HashSet<>();
            parentSet.add(parent);
            this.nodeToParents.put(child, parentSet);
        }
    }

}
