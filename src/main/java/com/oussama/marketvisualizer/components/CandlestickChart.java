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
import javafx.scene.text.Font;
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
import java.time.DayOfWeek;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.oussama.Main;
import com.oussama.marketvisualizer.models.CandleData;

public class CandlestickChart extends javafx.scene.chart.XYChart<Number, Number> {
    
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
        getNewData();
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

        super(new NumberAxis(), new NumberAxis());

        setLegendVisible(false);
        setAnimated( false);
        setPadding( new Insets( 0, 0, 0, 10));
        this.timeRange = tr;
        this.candleTimeRange = ctr;
        this.symbol = symb;

        NumberAxis xAxis = (NumberAxis) getXAxis();
        NumberAxis yAxis = (NumberAxis) getYAxis();

        xAxis.setLabel("");
        xAxis.setTickMarkVisible(false);

        xAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number value) {


                int index = value.intValue();
        
                ObservableList<Series<Number, Number>> chartData = getData();
                if (chartData.isEmpty() || chartData.get(0).getData().isEmpty()) {
                    return "";
                }

                Series<Number, Number> series = chartData.get(0);
        
                if (index < 0 || index >= series.getData().size()) {
                    return "";
                }
                
                Data<Number, Number> dataPoint = series.getData().get(index);
                CandleData candle = (CandleData) dataPoint.getExtraValue();
                
                if (candle == null) {
                    return "";
                }

                return formatLabel(candle.getTimeLabel());
            }
        
            @Override
            public Number fromString(String string) {
                return 0;
            }

            private String formatLabel(String timeLabel) {

                try {
                    LocalDate date = LocalDate.parse(timeLabel, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    
                    switch (candleTimeRange) {
                        case "DAILY":
                            // For daily data, show based on time range
                            int months = Integer.parseInt(timeRange.replace("M", ""));
                            
                            if (months <= 3) {
                                // 3 months or less - show every few days
                                return date.format(DateTimeFormatter.ofPattern("MM/dd"));
                            } else if (months <= 12) {
                                // System.out.println(date.getDayOfWeek() == DayOfWeek.MONDAY);
                                // Up to 1 year - show only Mondays
                                if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    return date.format(DateTimeFormatter.ofPattern("MM/dd"));
                                }
                                return "";
                            } else {
                                // More than 1 year - show first day of month
                                if (date.getDayOfMonth() == 1) {
                                    return date.format(DateTimeFormatter.ofPattern("MMM yy"));
                                }
                                return "";
                            }
                            
                        case "WEEKLY":
                            // For weekly data - show month/day
                            return date.format(DateTimeFormatter.ofPattern("MM/dd"));
                            
                        case "MONTHLY":
                            // For monthly data - show month/year
                            return date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                            
                        default:
                            return date.format(DateTimeFormatter.ofPattern("MM/dd"));
                    }
                    
                } catch (Exception e) {
                    return timeLabel; // fallback to original if parsing fails
                }
            }
        });

        yAxis.setLabel("");
        yAxis.setTickMarkVisible(false);
        yAxis.setSide(javafx.geometry.Side.RIGHT);

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

        // LAUNCH FETCHING FOR ALL KINDS OF DATA ( DAILY, WEEKLY, MONTHLY)

        // GETTING DATA AS JSON OJBJECT


        // TODO: store data in cache return file name
        // TODO: create an entry for the data in the database
        getNewData();
        setData();
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

        LocalDate start = LocalDate.now().minusYears(1).minusMonths( Integer.parseInt( timeRange.replace( "M", "")));
        LocalDate end = LocalDate.now().minusYears(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<CandleData> candleDataList;

        // POPULATE DATA BASED ON THE TIME RANGE
        switch (candleTimeRange) {
            case "DAILY":
                candleDataList = rawDailyData.stream()
                    .filter(c -> LocalDate.parse( c.getTimeLabel(), formatter).isAfter(start) && LocalDate.parse( c.getTimeLabel(), formatter).isBefore(end))
                    .collect(Collectors.toList());
                break;
            case "WEEKLY":
                candleDataList = rawWeeklyData.stream()
                    .filter(c -> LocalDate.parse( c.getTimeLabel(), formatter).isAfter(start) && LocalDate.parse( c.getTimeLabel(), formatter).isBefore(end))
                    .collect(Collectors.toList());
                break;
            case "MONTHLY":
                candleDataList = rawMonthlyData.stream()
                    .filter(c -> LocalDate.parse( c.getTimeLabel(), formatter).isAfter(start) && LocalDate.parse( c.getTimeLabel(), formatter).isBefore(end))
                    .collect(Collectors.toList());
                break;
            default:
                candleDataList = new ArrayList<>();
                break;
        }

        

        ObservableList<Series<Number, Number>> data = FXCollections.observableArrayList();
        Series<Number, Number> series = new Series<>();
        for (int i=0; i<candleDataList.size(); i++) {
            CandleData cd = candleDataList.get(i);
            Data<Number, Number> item = new Data<>( i, cd.getClose());
            item.setExtraValue(cd);
            series.getData().add(item);
        }

        data.add(series);
        super.setData(data);
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
        } else {
            apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_" + candleTimeRange +"&symbol=" + symbol + "&datatype=" + dataType +"&apikey=" + apiKey;
        }

        // HANDLE ALL EXCEPTIONS
        // url
        // io
        try {

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

        System.out.println("rendering");

        // CLEANING THE PLOT BEFORE RENDERING NEW DATA
        getPlotChildren().clear();

        // SAFE GARDING EMPTY DATA AND AVOID RENDERING NOTHING
        if (getData().isEmpty() || getData().get(0).getData().isEmpty()) {
            return;
        }
        
        // UPDATING CHART CONSTRAINTS LIKE MAX VALUES FOR AXIS
        updateChartConstraints();

        // Y AXIS CONFIGURATION
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setAutoRanging(false);
        if (maxY - minY < 10) {
            yAxis.setLowerBound(minY - 5);     
            yAxis.setUpperBound(maxY + 5);
            yAxis.setTickUnit(1);
        } else if (maxY < 100 ) {
            yAxis.setLowerBound((Math.floor((minY - 5) / 10.0) * 10)-5);     
            yAxis.setUpperBound((Math.ceil((maxY + 5) / 10.0) * 10)+5);
            yAxis.setTickUnit(10);
        }else {
            yAxis.setLowerBound((Math.floor((minY - 5) / 100.0) * 100)-5);     
            yAxis.setUpperBound((Math.ceil((maxY + 5) / 100.0) * 100)+5);
            yAxis.setTickUnit(10);
        }



        // X AXIS CONFIGURATION
        NumberAxis xAxis = (NumberAxis) getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound( -1);     
        xAxis.setUpperBound( getData().get(0).getData().size());
        

        double plotWidth = getWidth() - yAxis.getWidth() - 20; // Account for axis width and padding
        int dataCount = getData().get(0).getData().size();
        double candleWidth = Math.max(2, Math.min(20, plotWidth / dataCount * 0.8)); // 80% of available space

        for (Series<Number, Number> series : getData()) {
            for (Data<Number, Number> item : series.getData()) {

                CandleData cd = (CandleData) item.getExtraValue();
                
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());

                double openY = getYAxis().getDisplayPosition(cd.getOpen());
                double closeY = getYAxis().getDisplayPosition(cd.getClose());
                double highY = getYAxis().getDisplayPosition(cd.getHigh());
                double lowY = getYAxis().getDisplayPosition(cd.getLow());

                // Draw candle body (rectangle)
                double candleHeight = Math.abs(closeY - openY);
                double candleY = Math.min(openY, closeY);
                
                // Determine if it's an up or down candle
                String fillColor = cd.getClose() >= cd.getOpen() ? UP_COLOR : DOWN_COLOR;
                
                // Create rectangle for candle body
                javafx.scene.shape.Rectangle body = new javafx.scene.shape.Rectangle(
                        x - candleWidth / 2, candleY, candleWidth, candleHeight
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


        xAxis.requestAxisLayout();
        yAxis.requestAxisLayout();
        layout();

        // TICK MARKS VISIBILITY

        ObservableList<Axis.TickMark<Number>> yTickMarks = yAxis.getTickMarks();
        yTickMarks.get(0).setTextVisible(false);

        int dataSize = getData().get(0).getData().size();
        double xTickUnit = Math.max(1, dataSize / 10); // Show ~10 ticks
        xAxis.setTickUnit(xTickUnit);

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

    private void updateChartConstraints() {
        maxY = Double.NEGATIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        for (Series<Number, Number> series : getData()) {
            for (Data<Number, Number> item : series.getData()) {
                CandleData cd = (CandleData) item.getExtraValue();
                if (cd.getHigh()>maxY) {
                    maxY = cd.getHigh();
                }
                if (cd.getLow()<minY) {
                    minY = cd.getLow();
                }
            }
        }
    }

    @Override
    protected void dataItemChanged(Data<Number, Number> item) {}
    
    @Override
    protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
    
    @Override
    protected void dataItemRemoved(Data<Number, Number> item, Series<Number, Number> series) {}
    
    @Override
    protected void seriesAdded(Series<Number, Number> series, int seriesIndex) {}
    
    @Override
    protected void seriesRemoved(Series<Number, Number> series) {}

    /* 
     * 
     * PRIVATE FIELDS
     * 
     */

    // CANDLE COLORS 
    private final String UP_COLOR = "#26A69A";
    private final String DOWN_COLOR = "#EF5350";

    // CONGIGURATION VARIABLES
    private String timeRange;
    private String  candleTimeRange;
    private String symbol;

    // RAW DATA FOR THE CURRENT SYMBOLE
    private List<CandleData> rawDailyData;
    private List<CandleData> rawWeeklyData;
    private List<CandleData> rawMonthlyData;


    // CHART CONFIGURATION VAARIABLES
    private double maxY; 
    private double minY; 

}


