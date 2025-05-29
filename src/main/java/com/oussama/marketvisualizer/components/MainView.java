package com.oussama.marketvisualizer.components;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oussama.marketvisualizer.layout.Dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView extends VBox {

    // MAIN VIEW SECTIONS
    private final HBox topBar;
    private final CandlestickChart chart;
    private final HBox bottomBar;
    
    // SWITCHED FROM LISTS TO MAPS TO DIRECTLY PASS THE CORRECT VALUE TO USE IN THE API URL
    // BUT HAD TO MOVE BACK TO LISTS BECAUSE WE NEED ORDERED ENTRIES
    private static final List<String> TIME_RANGES = List.of( "1M", "3M", "6M", "1Y");
    private static final List<String> CANDLE_TIME_RANGES = List.of("Daily", "Weekly", "Monthly");
    private static final Map<String, String> TIME_RANGES_MAP = Map.of( "1M", "1M", "3M", "3M", "6M", "6M", "1Y", "12M");
    private static final Map<String, String> CANDLE_TIME_RANGES_MAP = Map.of("Daily", "DAILY", "Weekly", "WEEKLY", "Monthly", "MONTHLY");
    private String selectedTimeRange = TIME_RANGES.get(0);
    private String selectedCandleTimeRange = CANDLE_TIME_RANGES.get(0);

    // STYLES 
    private final String mainStyle = "-fx-padding: 10 0 10 0;";
    private final String bottomBarStyle = "-fx-padding: 0 20 0 20;";

    public void setSelectedTimeRange( String tr) {
        if (TIME_RANGES.contains(tr)) {
            for (javafx.scene.Node node : ((HBox) bottomBar.getChildren().get(0)).getChildren()) {
                if (node instanceof Button) {
                    Button b = (Button) node;
                    if (b.getText().equals(selectedTimeRange)) {
                        b.setStyle( Dashboard.unSelectedButtonStyle);
                        break;
                    }
                }
            }
            selectedTimeRange = tr;
            // GIVE THE CHART THE CORRESPONDING TIME RANGE TO USE DIRECTLY IN THE API QUERY
            chart.setTimeRange( TIME_RANGES_MAP.get( selectedTimeRange));
        }
    }

    public void setSelectedCandleTimeRange( String ctr) {
        if (CANDLE_TIME_RANGES.contains(ctr)) {
            for (javafx.scene.Node node : ((HBox) bottomBar.getChildren().get(2)).getChildren()) {
                if (node instanceof Button) {
                    Button b = (Button) node;
                    if (b.getText().equals(selectedCandleTimeRange)) {
                        b.setStyle( Dashboard.unSelectedButtonStyle);
                        break;
                    }
                }
            }
            selectedCandleTimeRange = ctr;
            // GIVE THE CHART THE CORRESPONDING TIME RANGE TO USE DIRECTLY IN THE API QUERY
            chart.setCandleTimeRange( CANDLE_TIME_RANGES_MAP.get( selectedCandleTimeRange));
        }
    }

    public MainView() {

        this.setSpacing(0);
        this.setStyle(mainStyle);
        this.setAlignment(Pos.TOP_CENTER);

        // TOP BAR THAT WILL CONTAIN INFORMATION ABOUT THE ASSET BEING VIEWED
        topBar = new HBox();
        topBar.setSpacing(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // THE MAIN CHART PLACEMENT
        System.out.println(TIME_RANGES_MAP.get( selectedTimeRange) + CANDLE_TIME_RANGES_MAP.get( selectedCandleTimeRange));
        chart = new CandlestickChart( TIME_RANGES_MAP.get( selectedTimeRange), CANDLE_TIME_RANGES_MAP.get( selectedCandleTimeRange), "IBM");

        
        // THE BOTTOM BAR THAT WILL CONTAIN MAINLY THE BUTTONS TO SWITCH TIME CONSTRAINT ON THE CHART
        bottomBar = new HBox();
        bottomBar.setSpacing(15);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        bottomBar.setStyle( bottomBarStyle);

        HBox timeArray = new HBox();
        timeArray.setSpacing(15);
        timeArray.setAlignment(Pos.CENTER_LEFT);

        for (String time : TIME_RANGES) {
            timeArray.getChildren().add(createTimeButton(time));
        }

        HBox candleTimeArray = new HBox();
        candleTimeArray.setSpacing(15);
        candleTimeArray.setAlignment(Pos.CENTER_RIGHT);

        for (String time : CANDLE_TIME_RANGES) {
            candleTimeArray.getChildren().add(createCandleTimeButton(time));
        }
        
        bottomBar.getChildren().addAll( timeArray, spacer, candleTimeArray);

        VBox.setVgrow(chart, Priority.ALWAYS);

        // ADD THE MAIN PARTS TO THE MAIN VIEW`
        this.getChildren().addAll( topBar, chart, bottomBar);

    }

    private Button createTimeButton( String label) {
        Button b = new Button(label);
        if (label == selectedTimeRange) {
            b.setStyle( Dashboard.selectedButtonStyle);
        } else {
            b.setStyle( Dashboard.unSelectedButtonStyle);
        }
        
        b.setOnMouseEntered(
            e -> {
                if (!label.equals( selectedTimeRange)) {
                    b.setStyle( Dashboard.selectedButtonStyle);
                }
            }
        );

        b.setOnMouseExited(
            e -> {
                if (!label.equals( selectedTimeRange)) {
                    b.setStyle( Dashboard.unSelectedButtonStyle);
                }
            }
        );

        b.setOnMouseClicked(
            e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // select if not selected
                    if (label != selectedTimeRange) {
                        b.setStyle( Dashboard.selectedButtonStyle);
                        setSelectedTimeRange(label);
                    }
                }
            }
        );

        return b;
    }


    private Button createCandleTimeButton( String label) {
        Button b = new Button(label);
        if (label == selectedCandleTimeRange) {
            b.setStyle( Dashboard.selectedButtonStyle);
        } else {
            b.setStyle( Dashboard.unSelectedButtonStyle);
        }
        
        b.setOnMouseEntered(
            e -> {
                if (!label.equals( selectedCandleTimeRange)) {
                    b.setStyle( Dashboard.selectedButtonStyle);
                }
            }
        );

        b.setOnMouseExited(
            e -> {
                if (!label.equals( selectedCandleTimeRange)) {
                    b.setStyle( Dashboard.unSelectedButtonStyle);
                }
            }
        );

        b.setOnMouseClicked(
            e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    // select if not selected
                    if (label != selectedCandleTimeRange) {
                        b.setStyle( Dashboard.selectedButtonStyle);
                        setSelectedCandleTimeRange(label);
                    }
                }
            }
        );

        return b;
    }


}
