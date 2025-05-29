package com.oussama.marketvisualizer.components;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONObject;

import com.oussama.Main;
import com.oussama.marketvisualizer.models.CandleData;

public class CandlestickChart extends javafx.scene.chart.XYChart<String, Number> {
    
    /*
     * 
     * PUBLIC FUNCTION
     * 
     * I WILL CALL IT THE API OF THIS CLASS 
     * 
     */

    public void setTimeRange( String t) {
        timeRange = t;
        setData();
    }

    public void setSymbol( String s) {
        symbol = s;
        setData();
    }

    public void setCandleTimeRange( String t) {
        candleTimeRange = t;
        setData();
    }

    /*
     * 
     * CONSTRUCTOR
     * 
    */

    public CandlestickChart( String tr, String ctr, String symb) {

        super(new CategoryAxis(), new NumberAxis());

        setLegendVisible(false);
        this.timeRange = tr;
        this.candleTimeRange = ctr;
        this.symbol = symb;

        CategoryAxis xAxis = (CategoryAxis) getXAxis();
        NumberAxis yAxis = (NumberAxis) getYAxis();

        xAxis.setLabel("");
        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelRotation( 0);

        yAxis.setLabel("");
        yAxis.setTickMarkVisible(false);
        yAxis.setSide(javafx.geometry.Side.RIGHT);


        // LAUNCH FETCHING FOR ALL KINDS OF DATA ( DAILY, WEEKLY, MONTHLY)

        // GETTING DATA AS JSON OJBJECT


        // TODO: store data in cache return file name
        // TODO: create an entry for the data in the database
        getNewData();
        setData();
    }


    private void initializeEmptyChart() {
        ObservableList<Series<String, Number>> emptyData = FXCollections.observableArrayList();
        Series<String, Number> emptySeries = new Series<>();
        emptySeries.setName("Loading " + symbol + "...");
        emptyData.add(emptySeries);
        this.setData(emptyData);
    }


    /*
     * 
     * 
     * PRIVATE METHODS
     * 
     */


     /**
      * this function fetch the data and process the json to candle list and updates the raw data attributs and then it sorts them
      */
    private void getNewData() {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {

            CompletableFuture<List<CandleData>> dailyFuture = CompletableFuture.supplyAsync( () -> {
                    JSONObject dailyData = fetchData("json", "all", "DAILY");
                    return processJSON( dailyData);
                } 
            );
            CompletableFuture<List<CandleData>> weeklyFuture = CompletableFuture.supplyAsync( () -> {
                    JSONObject weeklyData = fetchData("json", "all", "WEEKLY");
                    return processJSON( weeklyData);
                } 
            );
            CompletableFuture<List<CandleData>> monthlyFuture = CompletableFuture.supplyAsync( () -> { 
                    JSONObject monthlyData = fetchData("json", "all", "MONTHLY");
                    return processJSON( monthlyData);
                }
            );

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                dailyFuture, weeklyFuture, monthlyFuture);
            allFutures.get();

            rawDailyData = dailyFuture.get();
            rawWeeklyData = weeklyFuture.get();
            rawMonthlyData = monthlyFuture.get();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            rawDailyData.sort(Comparator.comparing(c -> LocalDate.parse(c.getTimeLabel(), formatter)));
            rawWeeklyData.sort(Comparator.comparing(c -> LocalDate.parse(c.getTimeLabel(), formatter)));
            rawMonthlyData.sort(Comparator.comparing(c -> LocalDate.parse(c.getTimeLabel(), formatter)));
        
        } catch ( InterruptedException | ExecutionException e) {
            // TODO: handle errors
            System.out.println(e);
        } finally {
            // TODO: I might add some post operations
        }

    }


    /**
      * this function updates the data that will be drawn to the chart based on the selected parameters ( time range, candle time range)
      */
    private void setData() {

        if (timeRange == null || timeRange.isEmpty()) {
            System.out.println("Time range is null or empty, skipping data load.");
            return;
        }

        List<CandleData> candleDataList;

        System.out.println("selected time range is "+candleTimeRange);
        // POPULATE DATA BASED ON THE TIME RANGE
        switch (candleTimeRange) {
            case "DAILY":
                candleDataList = rawDailyData;
                break;
            case "WEEKLY":
                candleDataList = rawWeeklyData;
                break;
            case "MONTHLY":
                candleDataList = rawMonthlyData;
                break;
            default:
                candleDataList = new ArrayList<>();
                break;
        }

        System.out.println("populated data with size "+ candleDataList.size());

        // TODO: need to limit the data the requested time range 

        LocalDate start = LocalDate.now().minusYears(1).minusMonths( Integer.parseInt( timeRange.replace( "M", "")));
        LocalDate end = LocalDate.now().minusYears(1);

        ObservableList<Series<String, Number>> data = FXCollections.observableArrayList();
        Series<String, Number> series = new Series<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (CandleData cd : candleDataList) {
            LocalDate candleDate = LocalDate.parse(cd.getTimeLabel(), formatter);

            if (candleDate.isAfter(start) && candleDate.isBefore(end)) {
                Data<String, Number> item = new Data<>(cd.getTimeLabel(), cd.getClose());
                item.setExtraValue(cd);
                series.getData().add(item);
            }

        }
        
        System.out.println("adding data to series ");

        data.add(series);
        setData(data);

    }
    
    public static String readResourceFile(String filename) {
        ClassLoader classLoader = Main.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            if (is == null) {
                throw new IllegalArgumentException("File not found: " + filename);
            }
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
            return scanner.useDelimiter("\\A").next();  // Read entire content
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject fetchData( String dataType, String option, String requestedTimeRange) {

        if (requestedTimeRange.equals("DAILY")) {
            String json = readResourceFile("ibm_daily.json");
            return new JSONObject(json);
        }
        if (requestedTimeRange.equals("WEEKLY")) {
            String json = readResourceFile("ibm_weekly.json");
            return new JSONObject(json);
        }
        if (requestedTimeRange.equals("MONTHLY")) {
            String json = readResourceFile("ibm_monthly.json");
            return new JSONObject(json);
        }


        String apiKey = "SNATUCE87YJU1E7S";
        List<CandleData> candleDataList = new ArrayList<>();

        String apiUrl;

        if ( option == "all") {
            apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_" + requestedTimeRange +"&symbol=" + symbol + "&outputsize=full&datatype=" + dataType +"&apikey=" + apiKey;
            System.out.println( apiUrl);
        } else {
            apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_" + candleTimeRange +"&symbol=" + symbol + "&datatype=" + dataType +"&apikey=" + apiKey;
        }

        // HANDLE ALL EXCEPTIONS
        // url
        // io
        try {

            System.out.println( apiUrl);
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                // TODO: check for errors
                System.out.println(jsonResponse.toString());

                return jsonResponse;
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
                double width=7;
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
            ObservableList<Axis.TickMark<String>> tickMarks = getXAxis().getTickMarks();
            for (int i = 0; i < tickMarks.size(); i++) {
                tickMarks.get(i).setTextVisible(i % 5 == 0);
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
    

    private List<CandleData> processJSON( JSONObject jsonData) {

        List<CandleData> candleDataList = new ArrayList<>();
        for ( Object k: jsonData.keySet()) {
            if ( !k.toString().equals("Meta Data")) {
                
                JSONObject data = jsonData.getJSONObject( k.toString());

                for (Object timestamp : data.keySet()) {
            
                    JSONObject candleData = data.getJSONObject(timestamp.toString());
                    
                    // Extract OHLCV data
                    double open = Double.parseDouble(candleData.getString("1. open"));
                    double high = Double.parseDouble(candleData.getString("2. high"));
                    double low = Double.parseDouble(candleData.getString("3. low"));
                    double close = Double.parseDouble(candleData.getString("4. close"));
                    long volume = Long.parseLong(candleData.getString("5. volume"));
                    
                    // Format the timestamp for display
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dateTime = LocalDate.parse(timestamp.toString(), inputFormatter);
                    String timeLabel = dateTime.format(inputFormatter);
                    
                    // Create and add CandleData
                    candleDataList.add(new CandleData(timeLabel, open, high, low, close, volume));
                }
            }
        }
        return candleDataList;
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

    private String timeRange;
    private String  candleTimeRange;
    private String symbol;

    private List<CandleData> rawDailyData;
    private List<CandleData> rawWeeklyData;
    private List<CandleData> rawMonthlyData;

}


