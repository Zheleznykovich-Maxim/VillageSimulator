package org.example.javafxui.genealogy;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class TreeRenderer {

    public void render(Pane pane, TreeNode n) {
        Group g = new Group();
        renderNode(g, n);
        pane.getChildren().add(g);
    }

    private void renderNode(Group g, TreeNode n) {
        Label label = new Label("Житель " + n.data.getId());
        label.setFont(Font.font("System", 12));
        label.setStyle(n.data.getStatus().equals("Alive")
                ? "-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 5;"
                : "-fx-background-color: lightgray; -fx-border-color: black; -fx-padding: 5;");

        label.setLayoutX(n.x);
        label.setLayoutY(n.y);
        label.setPrefSize(n.nodeW, n.nodeH);
        g.getChildren().add(label);

        for (TreeNode c : n.children) {
            renderNode(g, c);
            Line line = new Line(
                    n.x + n.nodeW / 2, n.y + n.nodeH + 5,
                    c.x + c.nodeW / 2, c.y
            );
            line.setStroke(Color.BLACK);
            g.getChildren().add(line);
        }
    }
}
