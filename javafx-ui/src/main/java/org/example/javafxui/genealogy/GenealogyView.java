package org.example.javafxui.genealogy;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import org.example.javafxui.VillagerData;

public class GenealogyView extends ScrollPane {

    private Pane contentPane;
    private GenealogyTree tree;
    private TreeRenderer renderer;

    public GenealogyView() {
        contentPane = new Pane();
        setContent(contentPane);
        setFitToWidth(false);  // чтобы скролл реально работал
        setFitToHeight(false);

        renderer = new TreeRenderer();
    }

    public void update(VillagerData root) {
        if (root == null) return;

        contentPane.getChildren().clear();
        tree = new GenealogyTree(root);
        tree.layout();

        renderer.render(contentPane, tree.getRootNode());

        contentPane.setPrefSize(tree.getContentWidth(), tree.getContentHeight());
    }
}
