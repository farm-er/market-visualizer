package com.oussama.marketvisualizer.models;

public class CandleData {
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
