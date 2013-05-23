package com.biziit.taxi.mapapi;

import java.util.List;

public class GeoResultList {
	private String status;  
    private List<GeoResult> results;  
  
    public String getStatus() {  
        return status;  
    }  
  
    public void setStatus(String status) {  
        this.status = status;  
    }  
  
    public List<GeoResult> getResults() {  
        return results;  
    }  
  
    public void setResults(List<GeoResult> results) {  
        this.results = results;  
    }  
  
    @Override  
    public String toString() {  
        return "GeoResults [results=" + results + ", status=" + status + "]";  
    }  
  
}
