package com.oussama;

import javafx.application.Application;
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
public class App extends Application {
    
    // Market data model class
    private static class MarketData {
        private LocalDate date;
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;
        
        public MarketData(LocalDate date, double open, double high, double low, double close, long volume) {
            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }
        
        public LocalDate getDate() { return date; }
        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }
        public long getVolume() { return volume; }
    }
    
    // Exchange pair class
    private static class ExchangePair {
        private String fromCurrency;
        private String toCurrency;
        private double exchangeRate;
        private String description;
        
        public ExchangePair(String fromCurrency, String toCurrency, double exchangeRate, String description) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.exchangeRate = exchangeRate;
            this.description = description;
        }
        
        public String getFromCurrency() { return fromCurrency; }
        public String getToCurrency() { return toCurrency; }
        public double getExchangeRate() { return exchangeRate; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return fromCurrency + " to " + toCurrency;
        }
    }
    
    private List<MarketData> marketData;
    private List<ExchangePair> exchangePairs;
    
    @Override
    public void start(Stage primaryStage) {
        // Generate dummy data
        generateDummyData();
        
        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-pane");
        
        // Create the header
        HBox header = createHeader();
        mainLayout.setTop(header);
        
        // Create the main content
        TabPane tabPane = createMainContent();
        mainLayout.setCenter(tabPane);
        
        // Create the scene with dark theme
        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        
        // Set up the stage
        primaryStage.setTitle("Market Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(10));
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Logo/Brand
        Label logo = new Label("🔶");
        logo.getStyleClass().add("logo");
        
        Label brand = new Label("FUNDS");
        brand.getStyleClass().add("brand");
        
        // Navigation links
        HBox navLinks = new HBox(10);
        navLinks.setAlignment(Pos.CENTER_LEFT);
        
        String[] links = {"PORTFOLIO", "LIQUIDITY POOLS", "..."};
        for (String link : links) {
            Label navLink = new Label(link);
            navLink.getStyleClass().add("nav-link");
            if (link.equals("PORTFOLIO")) {
                navLink.getStyleClass().add("active-link");
            }
            navLinks.getChildren().add(navLink);
        }
        
        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User account section
        HBox account = new HBox(10);
        account.setAlignment(Pos.CENTER_RIGHT);
        
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("icon");
        
        Label accountIcon = new Label("👤");
        accountIcon.getStyleClass().add("icon");
        
        Label accountName = new Label("ANONYM... WHALE");
        accountName.getStyleClass().add("account-name");
        
        account.getChildren().addAll(searchIcon, accountIcon, accountName);
        
        header.getChildren().addAll(logo, brand, navLinks, spacer, account);
        
        return header;
    }
    
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");
        
        // Create main dashboard tab
        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setClosable(false);
        
        BorderPane dashboardContent = new BorderPane();
        dashboardContent.getStyleClass().add("dashboard-content");
        
        // Top section with charts
        GridPane chartsSection = createChartsSection();
        dashboardContent.setTop(chartsSection);
        
        // Bottom section with pools
        GridPane poolsSection = createPoolsSection();
        dashboardContent.setCenter(poolsSection);
        
        dashboardTab.setContent(dashboardContent);
        tabPane.getTabs().add(dashboardTab);
        
        return tabPane;
    }
    
    private GridPane createChartsSection() {
        GridPane chartsGrid = new GridPane();
        chartsGrid.getStyleClass().add("charts-grid");
        chartsGrid.setPadding(new Insets(15));
        chartsGrid.setHgap(15);
        chartsGrid.setVgap(15);
        
        // Value Line Chart Section
        VBox valueChartBox = new VBox(5);
        valueChartBox.getStyleClass().add("chart-box");
        
        Label valueTitle = new Label("TOTAL VALUE LOCKED (TVL)");
        valueTitle.getStyleClass().add("chart-title");
        
        Label valueAmount = new Label("$24.7M");
        valueAmount.getStyleClass().add("amount-label");
        
        Label valueDate = new Label("29 Aug 2023");
        valueDate.getStyleClass().add("date-label");
        
        // Line chart for value
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        LineChart<String, Number> valueLineChart = new LineChart<>(xAxis, yAxis);
        valueLineChart.setLegendVisible(false);
        valueLineChart.setCreateSymbols(false);
        valueLineChart.getStyleClass().add("line-chart");
        
        XYChart.Series<String, Number> valueSeries = new XYChart.Series<>();
        // Add dummy data to chart
        for (int i = 0; i < marketData.size(); i++) {
            MarketData data = marketData.get(i);
            valueSeries.getData().add(new XYChart.Data<>(data.getDate().toString(), data.getClose()));
        }
        valueLineChart.getData().add(valueSeries);
        
        // Time filter buttons
        HBox timeFilterButtons = new HBox(5);
        timeFilterButtons.getStyleClass().add("time-filter");
        
        String[] timeFilters = {"1D", "1W", "1M", "3M", "6M", "1Y", "ALL"};
        for (String filter : timeFilters) {
            Button filterBtn = new Button(filter);
            filterBtn.getStyleClass().add("filter-button");
            if (filter.equals("1M")) {
                filterBtn.getStyleClass().add("active-filter");
            }
            timeFilterButtons.getChildren().add(filterBtn);
        }
        
        valueChartBox.getChildren().addAll(valueTitle, valueAmount, valueDate, valueLineChart, timeFilterButtons);
        
        // Volume Chart Section
        VBox volumeChartBox = new VBox(5);
        volumeChartBox.getStyleClass().add("chart-box");
        
        Label volumeTitle = new Label("VOLUME");
        volumeTitle.getStyleClass().add("chart-title");
        
        Label volumeAmount = new Label("$24.7M");
        volumeAmount.getStyleClass().add("amount-label");
        
        Label volumeDate = new Label("29 Aug 2023");
        volumeDate.getStyleClass().add("date-label");
        
        // Bar chart for volume
        CategoryAxis volXAxis = new CategoryAxis();
        NumberAxis volYAxis = new NumberAxis();
        volXAxis.setTickLabelsVisible(false);
        volYAxis.setTickLabelsVisible(false);
        
        BarChart<String, Number> volumeBarChart = new BarChart<>(volXAxis, volYAxis);
        volumeBarChart.setLegendVisible(false);
        volumeBarChart.getStyleClass().add("bar-chart");
        
        XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
        // Add dummy data to chart
        for (int i = 0; i < marketData.size(); i++) {
            MarketData data = marketData.get(i);
            volumeSeries.getData().add(new XYChart.Data<>(data.getDate().toString(), data.getVolume() / 100000));
        }
        volumeBarChart.getData().add(volumeSeries);
        
        // Time filter buttons for volume (same as above)
        HBox volumeTimeFilterButtons = new HBox(5);
        volumeTimeFilterButtons.getStyleClass().add("time-filter");
        
        for (String filter : timeFilters) {
            Button filterBtn = new Button(filter);
            filterBtn.getStyleClass().add("filter-button");
            if (filter.equals("1M")) {
                filterBtn.getStyleClass().add("active-filter");
            }
            volumeTimeFilterButtons.getChildren().add(filterBtn);
        }
        
        volumeChartBox.getChildren().addAll(volumeTitle, volumeAmount, volumeDate, volumeBarChart, volumeTimeFilterButtons);
        
        // Add charts to the grid
        chartsGrid.add(valueChartBox, 0, 0);
        chartsGrid.add(volumeChartBox, 1, 0);
        
        return chartsGrid;
    }
    
    private GridPane createPoolsSection() {
        GridPane poolsGrid = new GridPane();
        poolsGrid.getStyleClass().add("pools-grid");
        poolsGrid.setPadding(new Insets(0, 15, 15, 15));
        poolsGrid.setHgap(15);
        poolsGrid.setVgap(15);
        
        // Create tabs for the pools section
        HBox poolTabs = new HBox(10);
        poolTabs.getStyleClass().add("pool-tabs");
        
        String[] tabs = {"ALL POOLS", "FARM", "OPTIONS", "COLLECTION", "VAULT"};
        for (String tabName : tabs) {
            Button tab = new Button(tabName);
            tab.getStyleClass().add("pool-tab");
            if (tabName.equals("ALL POOLS")) {
                tab.getStyleClass().add("active-tab");
            }
            poolTabs.getChildren().add(tab);
        }
        
        poolsGrid.add(poolTabs, 0, 0, 2, 1);
        
        // Create farm pool cards
        for (int i = 0; i < 4; i++) {
            ExchangePair pair = exchangePairs.get(i);
            VBox poolCard = createPoolCard(pair);
            poolsGrid.add(poolCard, i % 2, i / 2 + 1);
        }
        
        // Right side panel with currency selection and stats
        VBox statsPanel = createStatsPanel();
        poolsGrid.add(statsPanel, 2, 0, 1, 3);
        
        return poolsGrid;
    }
    
    private VBox createPoolCard(ExchangePair pair) {
        VBox card = new VBox(10);
        card.getStyleClass().add("pool-card");
        card.setPadding(new Insets(15));
        
        Label farmPool = new Label("Farm Pool");
        farmPool.getStyleClass().add("card-subtitle");
        
        HBox pairBox = new HBox(5);
        pairBox.setAlignment(Pos.CENTER_LEFT);
        
        Label fromCurrency = new Label(pair.getFromCurrency());
        fromCurrency.getStyleClass().add("currency-label");
        
        Label arrow = new Label("to");
        arrow.getStyleClass().add("arrow-label");
        
        Label toCurrency = new Label(pair.getToCurrency());
        toCurrency.getStyleClass().add("currency-label");
        
        Label apy = new Label("APY " + (new Random().nextInt(20) + 10) + "%");
        apy.getStyleClass().add("apy-label");
        
        pairBox.getChildren().addAll(fromCurrency, arrow, toCurrency, apy);
        
        Label description = new Label(pair.getDescription());
        description.getStyleClass().add("description-label");
        description.setWrapText(true);
        
        JFXButton checkButton = new JFXButton("CHECK MORE");
        checkButton.getStyleClass().add("check-button");
        
        card.getChildren().addAll(farmPool, pairBox, description, checkButton);
        
        return card;
    }
    
    private VBox createStatsPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("stats-panel");
        panel.setPadding(new Insets(15));
        
        // Currency selection section
        HBox currencySelection = new HBox(15);
        currencySelection.setAlignment(Pos.CENTER);
        
        MFXComboBox<String> fromCurrency = new MFXComboBox<>();
        fromCurrency.getItems().addAll("ETH", "BTC", "USDC");
        fromCurrency.setValue("ETH");
        fromCurrency.getStyleClass().add("currency-combo");
        
        Label arrow = new Label("➝");
        arrow.getStyleClass().add("arrow-icon");
        
        MFXComboBox<String> toCurrency = new MFXComboBox<>();
        toCurrency.getItems().addAll("USDC", "ETH", "DAI");
        toCurrency.setValue("USDC");
        toCurrency.getStyleClass().add("currency-combo");
        
        currencySelection.getChildren().addAll(fromCurrency, arrow, toCurrency);
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.getStyleClass().add("stats-grid");
        statsGrid.setHgap(10);
        statsGrid.setVgap(15);
        
        // Row 1
        Label exchangeRateLabel = new Label("EXCHANGE RATE");
        statsGrid.add(exchangeRateLabel, 0, 0);
        
        Label exchangeRateValue = new Label("0.034");
        exchangeRateValue.getStyleClass().add("value-label");
        statsGrid.add(exchangeRateValue, 1, 0);
        
        // Row 2
        Label feeLabel = new Label("FEE");
        statsGrid.add(feeLabel, 0, 1);
        
        Label feeValue = new Label("0.3%");
        feeValue.getStyleClass().add("value-label");
        statsGrid.add(feeValue, 1, 1);
        
        // Row 3
        Label volumeLabel = new Label("24H VOLUME");
        statsGrid.add(volumeLabel, 0, 2);
        
        Label volumeValue = new Label("$4.2M");
        volumeValue.getStyleClass().add("value-label");
        statsGrid.add(volumeValue, 1, 2);
        
        // Add liquidity button
        JFXButton addLiquidityBtn = new JFXButton("ADD LIQUIDITY");
        addLiquidityBtn.getStyleClass().add("add-liquidity-button");
        
        panel.getChildren().addAll(currencySelection, statsGrid, addLiquidityBtn);
        
        return panel;
    }
    
    private void generateDummyData() {
        // Generate market data for the last 30 days
        marketData = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        double price = 1800.0; // Starting price
        Random random = new Random();
        
        for (int i = 30; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // Generate random price movement
            double change = (random.nextDouble() - 0.5) * 50.0;
            price += change;
            
            double open = price;
            double close = price + (random.nextDouble() - 0.5) * 20.0;
            double high = Math.max(open, close) + random.nextDouble() * 15.0;
            double low = Math.min(open, close) - random.nextDouble() * 15.0;
            
            // Generate random volume
            long volume = 1000000 + random.nextInt(2000000);
            
            marketData.add(new MarketData(date, open, high, low, close, volume));
        }
        
        // Generate exchange pairs
        exchangePairs = new ArrayList<>();
        exchangePairs.add(new ExchangePair("ETH", "ETH", 1.0, "Enables a liquidity provider to allow instantaneous swaps between native ETH and Wrapped Ether."));
        exchangePairs.add(new ExchangePair("ETH", "DAI", 0.0005, "Enables a liquidity provider to allow instantaneous swaps between ETH and Stablecoin DAI."));
        exchangePairs.add(new ExchangePair("ETH", "SOCKS", 0.00003, "Enables a liquidity provider to allow instantaneous swaps between ETH and SOCKS Standard tokens."));
        exchangePairs.add(new ExchangePair("SUSHI", "WETH", 0.004, "Enables a liquidity provider to allow instantaneous swaps between SUSHI and Wrapped Ether."));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
