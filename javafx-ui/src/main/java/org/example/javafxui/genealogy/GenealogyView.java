package org.example.javafxui.genealogy;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import org.example.javafxui.VillagerData;

public class GenealogyView extends ScrollPane {

    private Pane contentPane;
    private GenealogyTree tree;
    private TreeRenderer renderer;
    private double offsetX;

    public GenealogyView() {
        contentPane = new Pane();
        setContent(contentPane);
        setFitToWidth(false);  // чтобы скролл реально работал
        setFitToHeight(false);

        renderer = new TreeRenderer();

        // слушаем изменения размеров окна — нужно для пересчёта масштаба
        widthProperty().addListener((obs, oldVal, newVal) -> fitTree());
        heightProperty().addListener((obs, oldVal, newVal) -> fitTree());
    }

    public void update(VillagerData root) {
        if (root == null) return;

        contentPane.getChildren().clear();
        tree = new GenealogyTree(root);
        tree.layout();

        if (tree.getNodeCount() < 10) {
            double centerX = getWidth() / 2.0;
            double treeWidth = tree.getContentWidth();
            offsetX = centerX - (treeWidth / 2.0);
        } else {
            offsetX = 0;
        }
        renderer.setOffsetX(offsetX);

        renderer.render(contentPane, tree.getRootNode());

        contentPane.setPrefSize(tree.getContentWidth(), tree.getContentHeight());
    }

    private void fitTree() {
        if (tree == null) return;

        double margin = 50; // запас по краям

        double treeWidth = tree.getContentWidth();
        double treeHeight = tree.getContentHeight();

        double availableWidth = getViewportBounds().getWidth() - margin * 2;
        double availableHeight = getViewportBounds().getHeight() - margin * 2;

        if (availableWidth <= 0 || availableHeight <= 0) return;

        double scale = Math.min(
                availableWidth / treeWidth,
                availableHeight / treeHeight
        );
        scale = Math.min(scale, 1.0);

        contentPane.setScaleX(scale);
        contentPane.setScaleY(scale);

        double scaledWidth = treeWidth * scale;
        double scaledHeight = treeHeight * scale;

        double offsetX = (getViewportBounds().getWidth() - scaledWidth) / 2;
        double offsetY = (getViewportBounds().getHeight() - scaledHeight) / 2;

        contentPane.setLayoutX(offsetX);
        contentPane.setLayoutY(offsetY);
    }

}
