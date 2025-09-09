package org.example.javafxui.genealogy;

import org.example.javafxui.VillagerData;
import java.util.List;
import java.util.stream.Collectors;

public class TreeNode {
    VillagerData data;
    List<TreeNode> children;

    double nodeW, nodeH;
    double subtreeW;
    double x, y;

    public TreeNode(VillagerData d, List<TreeNode> children) {
        this.data = d;
        this.children = children;
    }

    public static TreeNode build(VillagerData d) {
        return new TreeNode(d,
                d.getChildren().stream().map(TreeNode::build).collect(Collectors.toList()));
    }
    public double getMaxY() {
        double max = y + nodeH; // нижняя граница текущей ноды
        for (TreeNode c : children) {
            max = Math.max(max, c.getMaxY());
        }
        return max;
    }

}
