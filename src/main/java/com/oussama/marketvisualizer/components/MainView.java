package com.oussama.marketvisualizer.components;

import java.util.List;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainView extends VBox {

    private final HBox topBar;
    private final CandlestickChart chart;
    private final HBox bottomBar;
    
    private static final List<String> TIME_RANGES = List.of("1D", "5D", "2W", "3M", "6M");
    private String selectedTimeRange = "1D";

    public void setSelectedTimeRange( String tr) {
        if (TIME_RANGES.contains(tr)) {
            for (javafx.scene.Node node : ((HBox) bottomBar.getChildren().get(0)).getChildren()) {
                if (node instanceof Button) {
                    Button b = (Button) node;
                    if (b.getText().equals(selectedTimeRange)) {
                        // Reset the style for all buttons
                        b.setStyle(
                            "-fx-background-color: transparent;"+
                            "-fx-text-fill: white;" + 
                            "-fx-font-weight: bold;"+
                            "-fx-padding: 0 10 0 10;"
                        );
                    }
                }
            }
            selectedTimeRange = tr;
            // change the state for the chart
            chart.setTimeRange(selectedTimeRange);
        }
    }

    public MainView() {

        this.setSpacing(0);
        this.setStyle(
            "-fx-padding: 10 0 10 0;"
        );
        this.setAlignment(Pos.TOP_CENTER);

        // TOP BAR THAT WILL CONTAIN INFORMATION ABOUT THE ASSET BEING VIEWED

        topBar = new HBox();
        topBar.setSpacing(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // THE MAIN CHART PLACEMENT

        chart = new CandlestickChart(selectedTimeRange);

        // THE BOTTOM BAR THAT WILL CONTAIN MAINLY THE BUTTONS TO SWITCH TIME CONSTRAINT ON THE CHART

        

        bottomBar = new HBox();
        bottomBar.setSpacing(15);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setStyle("-fx-padding: 0 20 0 20;");

        HBox timeArray = new HBox();
        timeArray.setSpacing(15);
        timeArray.setAlignment(Pos.CENTER_LEFT);

        for (String time : TIME_RANGES) {
            timeArray.getChildren().add(createTimeButton(time, chart));
        }
        
        bottomBar.getChildren().addAll( timeArray);

        VBox.setVgrow(chart, Priority.ALWAYS);

        // ADD THE MAIN PARTS TO THE MAIN VIEW`
        this.getChildren().addAll( topBar, chart, bottomBar);

    }


    private Button createTimeButton( String label, CandlestickChart c) {
        Button b = new Button(label);
        if (label == selectedTimeRange) {
            b.setStyle(
                "-fx-background-color: #333333;"+
                "-fx-text-fill: #ff9900;" + 
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 10 5 10;"
            );
        } else {
            b.setStyle(
                "-fx-background-color: transparent;"+
                "-fx-text-fill: white;" + 
                "-fx-font-weight: bold;"+
                "-fx-padding: 5 10 5 10;"
            );
        }
        
        b.setOnMouseEntered(
            e -> {
                if (!label.equals( selectedTimeRange)) {
                    b.setStyle(
                        "-fx-background-color: #333333;"+
                        "-fx-text-fill: #ff9900;" + 
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10 5 10;"
                    );
                }
            }
        );

        b.setOnMouseExited(
            e -> {
                if (!label.equals( selectedTimeRange)) {
                    b.setStyle(
                        "-fx-background-color: transparent;"+
                        "-fx-text-fill: white;" + 
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10 5 10;"
                    );
                }
            }
        );

        b.setOnMouseClicked(
            e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // select if not selected
                    if (label != selectedTimeRange) {
                        System.out.println("changing time range");
                        b.setStyle(
                            "-fx-background-color: #333333;"+
                            "-fx-text-fill: #ff9900;" + 
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 5 10 5 10;"
                        );
                        setSelectedTimeRange(label);
                    }
                }
            }
        );

        return b;
    }


}
