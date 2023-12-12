package org.zhvtsv.models;

import java.util.Arrays;

public class ExtentRequest {
    private String id;
    private double [] extent;

    public double[] getExtent() {
        return extent;
    }

    public void setExtent(double[] extent) {
        this.extent = extent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ExtentRequest{" +
                "id='" + id + '\'' +
                ", extent=" + Arrays.toString(extent) +
                '}';
    }
}
