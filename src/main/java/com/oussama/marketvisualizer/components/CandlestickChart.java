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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

import com.oussama.marketvisualizer.models.CandleData;

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

    public void setCandleTimeRange( String t) {
        candleTimeRange = t;
    }

    /*
     * 
     * CONSTRUCTOR
     * 
    */

    public CandlestickChart( String tr, String ctr, String symb) {

        super(new CategoryAxis(), new NumberAxis());

        setLegendVisible(false);
        setTimeRange( tr);
        setCandleTimeRange( ctr);
        setSymbol(symb);

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
        String apiKey = "2G59I82BETK10186";


        switch (timeRange) {
            case "1M":
                candleDataList = fetchMonthData(apiKey, "2009", "01");
                break;

            // case "1M":
            //     candleDataList = fetchData("1month");
            //     break;
        
            // case "3M":
            //     candleDataList = fetchData("30min");         
            //     break;
            
            // case "6M":
            //     candleDataList = fetchData("60min");
            //     break;
            
            // case "1Y":
            //     candleDataList = fetchData("1day");
            //     break;

            default:
                break;
        }

        if (candleDataList != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            candleDataList.sort(Comparator.comparing(c -> LocalDate.parse(c.getTimeLabel(), formatter)));
            this.setData(candleDataList);
        } else {
            this.setData(new ArrayList<>());
        }

    }
    
    private List<CandleData> fetchMonthData(String apiKey, String year, String month) {

        try {

            List<CandleData> candleDataList = new ArrayList<>();

            String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + apiKey;
            System.out.println( apiUrl);
            
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

                JSONObject timeSeries = jsonResponse.getJSONObject("Time Series (Daily)");

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
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dateTime = LocalDate.parse(timestamp, inputFormatter);
                    String timeLabel = dateTime.format(inputFormatter);
                    
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

        getPlotChildren().clear();

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
                double width=getWidth()/30-2;
                // double width=getWidth()/1440;

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

        if (max - min < 10) {
            yAxis.setLowerBound(min - 1);     
            yAxis.setUpperBound(max + 1);
            yAxis.setTickUnit(1);
        } else if (max < 100 ) {
            yAxis.setLowerBound(Math.floor((min - 5) / 10.0) * 10);     
            yAxis.setUpperBound(Math.ceil((max + 5) / 10.0) * 10);
            yAxis.setTickUnit(1);
        }else {
            yAxis.setLowerBound(Math.floor((min - 5) / 100.0) * 100);     
            yAxis.setUpperBound(Math.ceil((max + 5) / 100.0) * 100);
            yAxis.setTickUnit(100);
        }
        
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
    private String  candleTimeRange;
    private String symbol;

}
