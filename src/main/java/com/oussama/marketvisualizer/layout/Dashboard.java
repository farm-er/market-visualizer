package com.oussama.marketvisualizer.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Main Dashboard class responsible for assembling all UI components
 * like charts, buttons, and other visual elements.
 */
public class Dashboard {
    private BorderPane root;
    private HBox header;
    private VBox mainArea;
    private HBox footer;
    
    // Asset selection controls
    private ComboBox<String> assetSelector;
    
    // Theme support
    private final Color backgroundColor = Color.web("#1a1a1a");
    private final Color accentColor = Color.web("#ff9900");
    private final Color textColor = Color.web("#ffffff");
    
    /**
     * Constructs the dashboard and initializes all layout components
     */
    public Dashboard() {
        // Initialize the root layout
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a1a;");
        
        // Create and configure all sections
        createHeader();
        createMainArea();
        createFooter();
        
        // Assemble the complete layout
        root.setTop(header);
        root.setCenter(mainArea);
        root.setBottom(footer);
    }
    
    /**
     * Creates the header section with logo and navigation
     */
    private void createHeader() {
        header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setSpacing(20);
        header.setStyle("-fx-background-color: #111111;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Logo from resources
        StackPane logoContainer = new StackPane();
        logoContainer.setPadding(new Insets(0, 10, 0, 0));
        
        try {
            // Load logo from resources
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo-transparent.png"), 
                                      0, 0, true, true); // Use original size and preserve ratio with smooth scaling
            ImageView logoView = new ImageView(logoImage);
            logoView.setPreserveRatio(true);
            logoView.setFitHeight(30);
            
            // Improve rendering quality
            logoView.setSmooth(true);
            logoView.setCache(true);
            logoView.setCacheHint(javafx.scene.CacheHint.QUALITY);
            
            // If using a high-DPI display, adjust for that
            double scaleFactor = javafx.stage.Screen.getPrimary().getOutputScaleX();
            if (scaleFactor > 1.0) {
                // For high DPI displays, use slightly larger size
                logoView.setFitHeight(30 * scaleFactor);
            }
            
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
        
        navButtons.getChildren().addAll(dashboardBtn, settingsBtn);
        
        // Search bar
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.setStyle("-fx-background-color: #333333; -fx-background-radius: 5;");
        searchContainer.setPadding(new Insets(5, 10, 5, 10));
        
        // Search icon (using a text label as placeholder, could be replaced with an actual icon)
        Label searchIcon = new Label("🔍");
        searchIcon.setTextFill(Color.LIGHTGRAY);
        
        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-prompt-text-fill: #999999;");
        searchField.setPromptText("Search tokens and pools");
        
        searchContainer.getChildren().addAll(searchIcon, searchField);
        
        // Controls on the right (time, watchlist dropdown)
        HBox controls = new HBox();
        controls.setSpacing(20);
        controls.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(controls, Priority.ALWAYS);
        
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
        
        // Assemble the header
        header.getChildren().addAll(logoContainer, navButtons, searchContainer, controls);
    }
    
    /**
     * Creates the main content area for charts and data visualization
     */
    private void createMainArea() {
        mainArea = new VBox();
        mainArea.setPadding(new Insets(20));
        mainArea.setSpacing(20);
        mainArea.setAlignment(Pos.TOP_CENTER);
        
        // Charts section
        HBox chartsSection = new HBox();
        chartsSection.setSpacing(15);
        chartsSection.setAlignment(Pos.CENTER);
        
        // Total Value Locked chart placeholder
        VBox tvlChartContainer = createChartContainer("TOTAL VALUE LOCKED (TVL)", "$24.7M");
        
        // Volume chart placeholder
        VBox volumeChartContainer = createChartContainer("VOLUME", "$24.7M");
        
        chartsSection.getChildren().addAll(tvlChartContainer, volumeChartContainer);
        
        // Liquidity pools section
        VBox poolsSection = new VBox();
        poolsSection.setSpacing(15);
        poolsSection.setPadding(new Insets(15, 0, 0, 0));
        
        // Tab-like navigation for pools
        HBox poolTabs = new HBox();
        poolTabs.setSpacing(5);
        
        Button allPoolsBtn = createTabButton("ALL POOLS", true);
        Button farmBtn = createTabButton("FARM", false);
        Button optionsBtn = createTabButton("OPTIONS", false);
        Button collateralBtn = createTabButton("COLLATERAL", false);
        Button vaultBtn = createTabButton("VAULT", false);
        
        poolTabs.getChildren().addAll(allPoolsBtn, farmBtn, optionsBtn, collateralBtn, vaultBtn);
        
        // Pool listing area
        HBox poolListings = new HBox();
        poolListings.setSpacing(15);
        poolListings.setPadding(new Insets(15, 0, 0, 0));
        
        // Example pool listings
        VBox pool1 = createPoolItem("sETH to ETH", "APR: 10%");
        VBox pool2 = createPoolItem("ETH to DAI", "APR: 8%");
        VBox pool3 = createPoolItem("ETH to SOCKS", "APR: 12%");
        VBox pool4 = createPoolItem("SUSHI to WETH", "APR: 15%");
        
        poolListings.getChildren().addAll(pool1, pool2, pool3, pool4);
        
        poolsSection.getChildren().addAll(poolTabs, poolListings);
        
        // Add components to main area
        mainArea.getChildren().addAll(chartsSection, poolsSection);
    }
    
    /**
     * Creates the footer section with additional information and controls
     */
    private void createFooter() {
        footer = new HBox();
        footer.setPadding(new Insets(10, 20, 10, 20));
        footer.setSpacing(20);
        footer.setStyle("-fx-background-color: #111111;");
        footer.setAlignment(Pos.CENTER_LEFT);
        
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
        
        // Assemble the footer
        footer.getChildren().addAll(leftSide, rightSide);
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
    
    /**
     * Creates a tab-like button for the pools section
     */
    private Button createTabButton(String text, boolean selected) {
        Button button = new Button(text);
        if (selected) {
            button.setStyle("-fx-background-color: #333333; -fx-text-fill: #ff9900; -fx-font-weight: bold;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        }
        button.setOnMouseEntered(e -> {
            if (!selected) {
                button.setStyle("-fx-background-color: #222222; -fx-text-fill: #ff9900; -fx-font-weight: bold;");
            }
        });
        button.setOnMouseExited(e -> {
            if (!selected) {
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        });
        return button;
    }
    
    /**
     * Creates a chart container with title and value
     */
    private VBox createChartContainer(String title, String value) {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #222222; -fx-background-radius: 5;");
        container.setPadding(new Insets(15));
        container.setMinWidth(400);
        container.setMinHeight(200);
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#999999"));
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        
        header.getChildren().add(titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setTextFill(textColor);
        valueLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        
        // Chart placeholder - to be replaced with actual chart component
        StackPane chartPlaceholder = new StackPane();
        chartPlaceholder.setMinHeight(150);
        chartPlaceholder.setStyle("-fx-background-color: #222222;");
        
        container.getChildren().addAll(header, valueLabel, chartPlaceholder);
        return container;
    }
    
    /**
     * Creates a pool item card
     */
    private VBox createPoolItem(String pairName, String aprText) {
        VBox poolItem = new VBox();
        poolItem.setStyle("-fx-background-color: #222222; -fx-background-radius: 5;");
        poolItem.setPadding(new Insets(15));
        poolItem.setSpacing(5);
        poolItem.setMinWidth(200);
        
        Label pairLabel = new Label(pairName);
        pairLabel.setTextFill(textColor);
        pairLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        
        Label aprLabel = new Label(aprText);
        aprLabel.setTextFill(accentColor);
        aprLabel.setFont(Font.font("Verdana", 12));
        
        Label descLabel = new Label("Become a liquidity provider to allow instantaneous swaps between tokens");
        descLabel.setTextFill(Color.web("#999999"));
        descLabel.setFont(Font.font("Verdana", 10));
        descLabel.setWrapText(true);
        
        Button checkGuideBtn = new Button("CHECK GUIDE");
        checkGuideBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ff9900; -fx-text-fill: #ff9900; -fx-font-weight: bold;");
        
        poolItem.getChildren().addAll(pairLabel, aprLabel, descLabel, checkGuideBtn);
        
        return poolItem;
    }
    
    /**
     * Updates the dashboard with new data
     * @param data The data to update the dashboard with
     */
    public void updateData(Object data) {
        // This method will be implemented to update charts and other components
        // when actual data handling is implemented
    }
    
    /**
     * Adds a component to the main content area
     * @param node The component to add
     */
    public void addToMainArea(Node node) {
        mainArea.getChildren().add(node);
    }
    
    /**
     * Returns the root node of the dashboard
     * @return The root node
     */
    public Parent getRoot() {
        return root;
    }
}