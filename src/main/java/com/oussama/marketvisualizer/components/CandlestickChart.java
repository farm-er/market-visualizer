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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

public class CandlestickChart extends javafx.scene.chart.XYChart<String, Number> {
      
    
    /*
     * 
     * PUBLIC FUNCTION
     * 
     * I WIILL CALL IT THE API OF THIS CLASS 
     * 
     */

    public void setTimeRange( String t) {
        timeRange = t;
    }

    public void setSymbol( String s) {
        symbol = s;
    }


    /*
     * 
     * CONSTRUCTOR
     * 
     */

    public CandlestickChart( String tr, String symb) {

        super(new CategoryAxis(), new NumberAxis());

        setLegendVisible(false);
        timeRange = tr;
        symbol = symb;

        Axis<String> xAxis = getXAxis();
        Axis<Number> yAxis = getYAxis();

        xAxis.setLabel("");
        xAxis.setTickMarkVisible(false);

        yAxis.setLabel("");
        yAxis.setTickMarkVisible(false);
        yAxis.setSide(javafx.geometry.Side.RIGHT);

        storeData();
        layoutPlotChildren();
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

    private void storeData() {

        // XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        if (timeRange == null) {
            System.err.println("Warning: timeRange is null");
            this.setData(new ArrayList<>());
            return;
        }
    

        List<CandleData> candleDataList = new ArrayList<>();

        switch (timeRange) {
            case "1M":
                candleDataList = fetchData("1min");
                break;

            case "5M":
                candleDataList = fetchData("5min");
                break;

            case "15M":
                candleDataList = fetchData("15min");
                break;
        
            case "30M":
                candleDataList = fetchData("30min");         
                break;
            
            case "60M":
                candleDataList = fetchData("60min");
                break;
            
            case "1D":
                candleDataList = fetchData("1day");
                break;

            default:
                break;
        }
        
        this.setData(candleDataList);
    }
    
    private List<CandleData> fetchData(String timeRange) {

        try {

            List<CandleData> candleDataList = new ArrayList<>();


            String apiKey = "2G59I82BETK10186";

            String apiUrl = "https://www.alphavantage.co/query" +
                "?function=TIME_SERIES_INTRADAY" +
                "&symbol=" + symbol +
                "&month=2009-01" +
                "&interval=" + timeRange +
                "&extended_hours=false" +
                "&outputsize=compact" +
                "&apikey=" + apiKey;
            
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds timeout
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("Error Message")) {
                    throw new IOException("API Error: " + jsonResponse.getString("Error Message"));
                }

                System.out.println(jsonResponse);

                JSONObject timeSeries = jsonResponse.getJSONObject("Time Series (" + timeRange + ")");

                for (Object timestampObj : timeSeries.keySet()) {
                    
                    String timestamp = (String) timestampObj;
                    
                    JSONObject candleData = timeSeries.getJSONObject(timestamp);
                    
                    // Extract OHLCV data
                    double open = Double.parseDouble(candleData.getString("1. open"));
                    double high = Double.parseDouble(candleData.getString("2. high"));
                    double low = Double.parseDouble(candleData.getString("3. low"));
                    double close = Double.parseDouble(candleData.getString("4. close"));
                    long volume = Long.parseLong(candleData.getString("5. volume"));
                    
                    // Format the timestamp for display
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
                    String timeLabel = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                    
                    // Create and add CandleData
                    candleDataList.add(new CandleData(timeLabel, open, high, low, close, volume));
                }

                return candleDataList;
            } else {
                throw new IOException("HTTP Error: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

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
        // I ADDED AND SUBSTRUCTED 5 TO GET A SMALL PADDING FROM THE SIDES IN CASE OF MIN OR MAX BEING ALREADY A MULTIPLE OF 100
        yAxis.setLowerBound(Math.floor((min - 5) / 100.0) * 100);     
        yAxis.setUpperBound(Math.ceil((max + 5) / 100.0) * 100);
        yAxis.setTickUnit(100);
        yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                return String.format("%.2f", value.doubleValue());
            }
        
            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        });
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
        private final long volume;
        
        public CandleData(String timeLabel, double open, double high, double low, double close, long volume) {
            this.timeLabel = timeLabel;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }
        
        public String getTimeLabel() { return timeLabel; }
        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }
        public long getVolume() { return volume; }
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
    private String symbol;

}
