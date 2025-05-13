package com.oussama.marketvisualizer.components;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class ConnectionChecker extends Label {

    private final ScheduledExecutorService scheduler;

    public ConnectionChecker() {

        setText("Checking connection...");
        setTextFill(Color.web("#00cc00"));

        this.setTextFill(Color.web("#00cc00"));

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            checkPing();
        }, 0, 10, TimeUnit.SECONDS);
        
    }

    /**
     * Checks ping to Google and updates the label with the result
     * @return The ping time in milliseconds, or -1 if connection failed
     */
    private long checkPing() {
        long startTime = System.currentTimeMillis();
        try {
            
            URL url = new URL("https://www.google.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setRequestMethod("HEAD");
            conn.connect();
            
            int responseCode = conn.getResponseCode();
            long latency = System.currentTimeMillis() - startTime;
            
            if (responseCode >= 200 && responseCode < 400) {
                Platform.runLater(() -> {
                    setText(latency + " ms");
                    
                    if (latency < 100) {
                        setTextFill(Color.web("#00cc00"));
                    } else if (latency < 300) {
                        setTextFill(Color.web("#ffcc00"));
                    } else {
                        setTextFill(Color.web("#ff6600"));
                    }
                });
                return latency;
            } else {
                Platform.runLater(() -> {
                    setText("Connection Error: " + responseCode);
                    setTextFill(Color.web("#ff0000"));
                });
                return -1;
            }
            
        } catch (IOException e) {
            Platform.runLater(() -> {
                setText("Connection Failed");
                setTextFill(Color.web("#ff0000"));
            });
            return -1;
        }
    }

    /**
     * Stops the connection checker and releases resources.
     * Should be called when the application is shutting down.
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

}
