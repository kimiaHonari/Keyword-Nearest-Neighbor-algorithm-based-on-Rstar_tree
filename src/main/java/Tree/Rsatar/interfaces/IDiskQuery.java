package Tree.Rsatar.interfaces;


import Tree.Rsatar.dto.PointDTO;
import Tree.Rsatar.dto.TreeDTO;
import Tree.Rsatar.nodes.RStarNode;

import java.io.File;
import java.io.FileNotFoundException;

public interface IDiskQuery {
    void saveNode(RStarNode node);

    RStarNode loadNode(long nodeId) throws FileNotFoundException;

    long savePoint(PointDTO pointDTO);

    PointDTO loadPoint(long pointer);

    int saveTree(TreeDTO tree, File saveFile);

    TreeDTO loadTree(File saveFile);
}
