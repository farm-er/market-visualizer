package com.oussama.marketvisualizer.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class Footer extends HBox {

    private final Color backgroundColor = Color.web("#1a1a1a");
    private final Color accentColor = Color.web("#ff9900");
    private final Color textColor = Color.web("#ffffff");
    
    public Footer() {
        this.setPadding(new Insets(10, 20, 10, 20));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #111111;");
        this.setAlignment(Pos.CENTER_LEFT);
        
        // Left side - status information
        HBox leftSide = new HBox();
        leftSide.setSpacing(15);
        leftSide.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Connected");
        statusLabel.setTextFill(Color.web("#00cc00"));
        
        leftSide.getChildren().add(statusLabel);
        
        // Right side - additional metrics
        HBox rightSide = new HBox();
        rightSide.setSpacing(15);
        rightSide.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        
        Label gasPrice = new Label("Gas: 35 Gwei");
        gasPrice.setTextFill(textColor);
        
        Label ethPrice = new Label("ETH: $2,381.50");
        ethPrice.setTextFill(textColor);
        
        rightSide.getChildren().addAll(gasPrice, ethPrice);
        
        // Assemble the this
        this.getChildren().addAll(leftSide, rightSide);
    }

}
