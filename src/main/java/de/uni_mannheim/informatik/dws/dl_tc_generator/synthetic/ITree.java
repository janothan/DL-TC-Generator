package de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic;

import java.util.List;
import java.util.Set;

public interface ITree {


    Set<String> getChildrenOfNode(String nodeId);
    Set<String> getParentsOfNode(String nodeId);

    String getRoot();

    Set<String> getAllNodes();

    void addLeaf(String parent, String child);
}
