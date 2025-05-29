package com.oussama.marketvisualizer.layout;

import com.oussama.marketvisualizer.components.CandlestickChart;
import com.oussama.marketvisualizer.components.Footer;
import com.oussama.marketvisualizer.components.Header;
import com.oussama.marketvisualizer.components.MainView;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.CacheHint;
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
    
    // STYLES
    public static final String unSelectedButtonStyle = "-fx-background-color: transparent;-fx-text-fill: white;-fx-font-weight: bold;-fx-padding: 5 10 5 10;";
    public static final String selectedButtonStyle = "-fx-background-color: #333333;-fx-text-fill: #ff9900;-fx-font-weight: bold;-fx-padding: 5 10 5 10;";

    /**
     * Constructs the dashboard and initializes all layout components
     */
    public Dashboard() {
        // Initialize the root layout
        root = new BorderPane();
        root.setStyle("-fx-background-color: #000;");
        root.setOnMousePressed(event -> {
            root.requestFocus(); // Move focus away from TextField
        });


        header = new Header();
        createMainArea();
        footer = new Footer();
        
        // Assemble the complete layout
        root.setTop(header);
        root.setCenter(mainArea);
        root.setBottom(footer);
    }
    

    private void createMainArea() {
    
        final String TEXT_COLOR = "#FFFFFF";

        mainArea = new VBox();
        mainArea.setAlignment(Pos.CENTER);

        VBox mainChart = new MainView();

        VBox.setVgrow(mainChart, Priority.ALWAYS);
    
        

        mainArea.getChildren().add( mainChart);
    }


    public Parent getRoot() {
        return root;
    }
}