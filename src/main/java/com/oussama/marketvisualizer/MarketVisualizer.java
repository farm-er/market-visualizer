package com.oussama.marketvisualizer;

import javafx.application.Application;
import javafx.css.converter.PaintConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.jfoenix.controls.JFXButton;
import com.oussama.marketvisualizer.layout.Dashboard;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Market Visualizer Application
 * Based on the design provided in the reference image
 */
public class MarketVisualizer extends Application {
    
    
    @Override
    public void start(Stage primaryStage) {
        Dashboard dashboard = new Dashboard();
        Scene scene = new Scene(dashboard.getRoot(), 1000, 700);

        scene.getStylesheets().add(getClass().getResource("/styles/candlestick.css").toExternalForm());


        primaryStage.setTitle("Market Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}