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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Header extends HBox {

    private final Color backgroundColor = Color.web("#1a1a1a");
    private final Color accentColor = Color.web("#ff9900");
    private final Color textColor = Color.web("#ffffff");

    public Header() {

        this.setPadding(new Insets(0, 20, 0, 20));
        this.setSpacing(50);
        this.setStyle(
            "-fx-height: 10;"+
            "-fx-background-color: transparent;"+
            "-fx-border-width: 0 0 2 0;"+
            "-fx-border-color: #555;"+
            "-fx-border-style: solid;"
        );
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

            // // If using a high-DPI display, adjust for that
            // double scaleFactor = javafx.stage.Screen.getPrimary().getOutputScaleX();
            // if (scaleFactor > 1.0) {
            //     // For high DPI displays, use slightly larger size
            //     logoView.setFitHeight(30 * scaleFactor);
            // }
            logoContainer.getChildren().add(logoView);
        } catch (Exception e) {
            // Fallback if logo loading fails
            Label logoLabel = new Label("MARKET VISUALIZER");
            logoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            logoLabel.setTextFill(accentColor);
            logoContainer.getChildren().add(logoLabel);
            System.err.println("Could not load logo: " + e.getMessage());
        }
        
        // Navigation buttons
        HBox navButtons = new HBox();
        navButtons.setSpacing(15);
        navButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button dashboardBtn = createNavButton("DASHBOARD");
        Button settingsBtn = createNavButton("SETTINGS");
        
        navButtons.getChildren().addAll( logoContainer, dashboardBtn, settingsBtn);
        
        // Search bar
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.setStyle(
            "-fx-background-color: transparent;"+
            "-fx-padding: 5 0 5 0;"
        );
        
        // Search icon (using a text label as placeholder, could be replaced with an actual icon)
        Label searchIcon = new Label("🔍");
        searchIcon.setTextFill(Color.WHITE);
        
        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPrefWidth(200);
        searchField.setPromptText("Search assets");
        searchField.setStyle(
            "-fx-background-color: #000;"+
            "-fx-padding: 5 20 5 20;" +
            "-fx-color: #555;"
        );
        searchField.setOnMouseEntered(e -> searchField.setStyle(
            searchField.getStyle()+
            "-fx-padding: 4 20 4 20;" +
            "-fx-border-width: 1;"+
            "-fx-border-radius: 5;"+
            "-fx-border-style: solid;"+
            "-fx-border-color: #555;"
        ));

        searchField.setOnMouseExited(e -> searchField.setStyle(
            "-fx-background-color: #000;"+
            "-fx-padding: 5 20 5 20;" +
            "-fx-color: #555;"
        ));
        
        
        searchContainer.getChildren().addAll(searchIcon, searchField);
        
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
        
        // Watchlist dropdown
        ComboBox<String> watchlistSelector = new ComboBox<>();
        watchlistSelector.getItems().addAll(
            "Default Watchlist",
            "DeFi Tokens",
            "NFT Projects",
            "Layer 2 Solutions",
            "New Listings"
        );
        watchlistSelector.setValue("Default Watchlist");
        watchlistSelector.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        
        // Add components to controls
        controls.getChildren().addAll(timeLabel, watchlistSelector);
        
        HBox.setHgrow(navButtons, Priority.ALWAYS);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);
        HBox.setHgrow(controls, Priority.ALWAYS);

        this.getChildren().addAll(navButtons, searchContainer, controls);
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
