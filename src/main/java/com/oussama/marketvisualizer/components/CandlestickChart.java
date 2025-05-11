package com.oussama.marketvisualizer.components;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
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

public class CandlestickChart extends javafx.scene.chart.XYChart<String, Number> {
      
    
    /*
     * 
     * PUBLIC FUNCTION
     * 
     * I WIILL CALL IT THE API OF THIS CLASS 
     * 
     */

    public void setTimeRange( String T) {
        timeRange = T;
    }


    /*
     * 
     * CONSTRUCTOR
     * 
     */

    public CandlestickChart( String T) {

        super(new CategoryAxis(), new NumberAxis());

        timeRange = T;

        Axis<String> xAxis = getXAxis();
        Axis<Number> yAxis = getYAxis();

        xAxis.setLabel("");
        xAxis.setTickMarkVisible(false);

        yAxis.setLabel("");
        yAxis.setTickMarkVisible(false);
        yAxis.setSide(javafx.geometry.Side.RIGHT);

        addSampleCandlestickData(this);
        layoutPlotChildren();
        setLegendVisible(false);
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
    
    @Override
    protected void layoutPlotChildren() {

        // find the max and min to help setting the bounds after
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;

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
                if (cd.getHigh()>max) {
                    max = cd.getHigh();
                }
                if (cd.getLow()<min) {
                    min = cd.getLow();
                }
            }
        }

        // i will set the bounds here 
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setAutoRanging(false); 
        yAxis.setLowerBound(min-100);     
        yAxis.setUpperBound(max+100);
        yAxis.setTickUnit(100);
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


    /* 
     * 
     * PRIVATE FIELDS
     * 
     */

    private final String DARK_BACKGROUND = "#161625";
    private final String GRID_LINE_COLOR = "#ff0000";
    private final String TEXT_COLOR = "#FFFFFF";
    private final String UP_COLOR = "#26A69A";
    private final String DOWN_COLOR = "#EF5350";

    private final Random random = new Random();

    private String timeRange;

}
