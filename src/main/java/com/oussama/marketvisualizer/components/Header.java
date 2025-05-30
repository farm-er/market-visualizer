package com.oussama.marketvisualizer.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Header extends HBox {

    private final Color backgroundColor = Color.web("#1a1a1a");
    private final Color accentColor = Color.web("#ff9900");
    private final Color textColor = Color.web("#ffffff");


    // STYLES
    private final String mainStyle = "-fx-height: 10;-fx-background-color: transparent;-fx-border-width: 0 0 2 0;-fx-border-color: #555;-fx-border-style: solid;";

    public Header() {

        this.setPadding(new Insets(0, 20, 0, 20));
        this.setSpacing(50);
        this.setStyle( mainStyle);
        this.setAlignment(Pos.CENTER);
        
        // Logo from resources
        StackPane logoContainer = new StackPane();
        logoContainer.setPadding(new Insets(0, 10, 0, 0));

        
        try {
            // Load logo from resources
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo-transparent.png"), 
                                      0, 0, true, false); // Use original size and preserve ratio with smooth scaling
            ImageView logoView = new ImageView(logoImage);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true); // disables smoothing to avoid blur
            logoView.setCache(true);
            logoView.setCacheHint(CacheHint.SPEED); // or QUALITY depending on performance needs

            logoView.setFitHeight(30);

            logoContainer.getChildren().add(logoView);
        } catch (Exception e) {
            // Fallback if logo loading fails
            Label logoLabel = new Label("MARKET VISUALIZER");
            logoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            logoLabel.setTextFill(accentColor);
            logoContainer.getChildren().add(logoLabel);
            System.err.println("Could not load logo: " + e.getMessage());
        }
        
        
        // Controls on the right (time, watchlist dropdown)
        HBox controls = new HBox();
        controls.setSpacing(20);
        controls.setAlignment(Pos.CENTER_RIGHT);
        
        // Current time display with automatic updates
        Label timeLabel = new Label();
        timeLabel.setTextFill(textColor);
        timeLabel.setFont(Font.font("Verdana", 12));
        
        // Update time every second
        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                java.util.Date date = new java.util.Date();
                String timeString = new java.text.SimpleDateFormat("HH:mm:ss").format(date);
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    timeLabel.setText(timeString);
                });
            }
        }, 0, 1000);
        
        
        // Add components to controls
        controls.getChildren().addAll(timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        this.getChildren().addAll( logoContainer, spacer, controls);
    }
 
    /**
     * Creates a navigation button with consistent styling
     */
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #333333; -fx-text-fill: #ff9900; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;"));
        return button;
    }
    

}
