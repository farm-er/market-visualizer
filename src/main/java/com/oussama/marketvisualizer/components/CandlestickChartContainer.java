package com.oussama.marketvisualizer.components;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CandlestickChartContainer extends Application {

    private final String DARK_BACKGROUND = "#161625";
    private final String GRID_LINE_COLOR = "#2A2A3A";
    private final String TEXT_COLOR = "#FFFFFF";
    private final String UP_COLOR = "#26A69A";
    private final String DOWN_COLOR = "#EF5350";
    
    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");

        // Create time period selectors 
        HBox timeSelectors = createTimeSelectors();
        
        // Create candlestick chart
        VBox chartContainer = createCandlestickChart();
        
        // Combine all components
        VBox content = new VBox(10, chartContainer, timeSelectors);
        content.setPadding(new Insets(15));
        
        root.setCenter(content);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Crypto Candlestick Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTimeSelectors() {
        HBox timeSelectors = new HBox(10);
        timeSelectors.setAlignment(Pos.CENTER_LEFT);
        
        ToggleGroup group = new ToggleGroup();
        
        String[] periods = {"24H", "48H", "7 Days", "1 Month", "6 Months", "1 Year"};
        for (String period : periods) {
            ToggleButton button = new ToggleButton(period);
            button.setToggleGroup(group);
            button.setStyle("-fx-background-color: transparent; " +
                           "-fx-text-fill: " + TEXT_COLOR + ";" +
                           "-fx-border-color: transparent;");
            
            // Add hover effects
            button.setOnMouseEntered(e -> 
                button.setStyle("-fx-background-color: #2A2A3A; " +
                               "-fx-text-fill: " + TEXT_COLOR + ";" +
                               "-fx-border-color: transparent;"));
            
            button.setOnMouseExited(e -> {
                if (!button.isSelected()) {
                    button.setStyle("-fx-background-color: transparent; " +
                                   "-fx-text-fill: " + TEXT_COLOR + ";" +
                                   "-fx-border-color: transparent;");
                }
            });
            
            button.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    button.setStyle("-fx-background-color: #2A2A3A; " +
                                   "-fx-text-fill: " + TEXT_COLOR + ";" +
                                   "-fx-border-color: transparent;");
                } else {
                    button.setStyle("-fx-background-color: transparent; " +
                                   "-fx-text-fill: " + TEXT_COLOR + ";" +
                                   "-fx-border-color: transparent;");
                }
            });
            
            timeSelectors.getChildren().add(button);
        }
        
        // Select first period by default
        ((ToggleButton)timeSelectors.getChildren().get(0)).setSelected(true);
        
        return timeSelectors;
    }

    private VBox createCandlestickChart() {
        // Create axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel("");
        yAxis.setLabel("");
        
        // Style axes
        xAxis.setTickLabelFill(Color.web(TEXT_COLOR));
        yAxis.setTickLabelFill(Color.web(TEXT_COLOR));
        
        // Set style for grid lines
        xAxis.setStyle("-fx-tick-label-fill: " + TEXT_COLOR + ";" +
                      "-fx-tick-mark-visible: false;");
        
        yAxis.setStyle("-fx-tick-label-fill: " + TEXT_COLOR + ";" +
                      "-fx-tick-mark-visible: false;" + 
                      "-fx-minor-tick-visible: false;");
        
        // Custom chart implementation
        CandlestickChart chart = new CandlestickChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(true);
        chart.setVerticalGridLinesVisible(true);
        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setAnimated(false);
        
        // Set chart background color
        chart.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";" +
                     "-fx-border-color: " + GRID_LINE_COLOR + ";" +
                     "-fx-border-width: 1;");
        
        // Add sample data
        addSampleCandlestickData(chart);
        
        VBox chartContainer = new VBox(chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        
        return chartContainer;
    }
    
    private void addSampleCandlestickData(CandlestickChart chart) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Generate sample data similar to Bitcoin prices around $7800-$8000
        double basePrice = 7800.0;
        List<CandleData> candleDataList = new ArrayList<>();
        
        for (int i = 0; i < 48; i++) {
            double open = basePrice + random.nextDouble() * 200 - 100;
            double close = open + random.nextDouble() * 80 - 40;
            double high = Math.max(open, close) + random.nextDouble() * 50;
            double low = Math.min(open, close) - random.nextDouble() * 50;
            
            String timeLabel = String.format("%02d:00", (i % 24));
            
            candleDataList.add(new CandleData(timeLabel, open, high, low, close));
            
            // Simulate some trend
            basePrice = close;
        }
        
        chart.setData(candleDataList);
    }

    /**
     * Custom Candlestick Chart implementation
     */
    public class CandlestickChart extends javafx.scene.chart.XYChart<String, Number> {
        
        public CandlestickChart(CategoryAxis xAxis, NumberAxis yAxis) {
            super(xAxis, yAxis);
        }
        
        public void setData(List<CandleData> candleDataList) {
            ObservableList<Series<String, Number>> data = FXCollections.observableArrayList();
            Series<String, Number> series = new Series<>();
            
            for (CandleData cd : candleDataList) {
                Data<String, Number> item = new Data<>(cd.getTimeLabel(), cd.getClose());
                item.setExtraValue(cd);
                series.getData().add(item);
            }
            
            data.add(series);
            setData(data);
        }
        
        @Override
        protected void layoutPlotChildren() {
            for (Series<String, Number> series : getData()) {
                for (Data<String, Number> item : series.getData()) {
                    CandleData cd = (CandleData) item.getExtraValue();
                    
                    double x = getXAxis().getDisplayPosition(item.getXValue());
                    double y = getYAxis().getDisplayPosition(item.getYValue());
                    
                    double openY = getYAxis().getDisplayPosition(cd.getOpen());
                    double closeY = getYAxis().getDisplayPosition(cd.getClose());
                    double highY = getYAxis().getDisplayPosition(cd.getHigh());
                    double lowY = getYAxis().getDisplayPosition(cd.getLow());
                    
                    // Calculate width
                    double width = 7.0;
                    
                    // Draw candle body (rectangle)
                    double candleHeight = Math.abs(closeY - openY);
                    double candleY = Math.min(openY, closeY);
                    
                    // Determine if it's an up or down candle
                    String fillColor = cd.getClose() >= cd.getOpen() ? UP_COLOR : DOWN_COLOR;
                    
                    // Create rectangle for candle body
                    javafx.scene.shape.Rectangle body = new javafx.scene.shape.Rectangle(
                            x - width / 2, candleY, width, Math.max(candleHeight, 1)
                    );
                    body.setFill(Color.web(fillColor));
                    body.setStroke(Color.web(fillColor));
                    
                    // Create line for high/low wicks
                    javafx.scene.shape.Line highLowLine = new javafx.scene.shape.Line(
                            x, highY, x, lowY
                    );
                    highLowLine.setStroke(Color.web(fillColor));
                    
                    // Add nodes to chart
                    getPlotChildren().addAll(highLowLine, body);
                }
            }
        }
        
        @Override
        protected void dataItemChanged(Data<String, Number> item) {}
        
        @Override
        protected void dataItemAdded(Series<String, Number> series, int itemIndex, Data<String, Number> item) {}
        
        @Override
        protected void dataItemRemoved(Data<String, Number> item, Series<String, Number> series) {}
        
        @Override
        protected void seriesAdded(Series<String, Number> series, int seriesIndex) {}
        
        @Override
        protected void seriesRemoved(Series<String, Number> series) {}
    }
    
    /**
     * Class to hold candlestick data
     */
    public static class CandleData {
        private final String timeLabel;
        private final double open;
        private final double high;
        private final double low;
        private final double close;
        
        public CandleData(String timeLabel, double open, double high, double low, double close) {
            this.timeLabel = timeLabel;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }
        
        public String getTimeLabel() { return timeLabel; }
        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}