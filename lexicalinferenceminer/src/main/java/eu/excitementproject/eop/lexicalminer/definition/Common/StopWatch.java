package eu.excitementproject.eop.lexicalminer.definition.Common;

public class StopWatch {
    
    private long startTime = 0;
    private long stopTime = 0;
    private long totalSoFar;
    private boolean running = false;

    
    public void start() {

    	this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    
    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    	
    	this.totalSoFar += stopTime - startTime;
    	this.stopTime=0;
    	this.startTime=0;
    }

    
    //elaspsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
             elapsed = totalSoFar + (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed =  totalSoFar;
        }
        return elapsed;
    }
    
    
    //elapsed time in seconds
    public long getTotalTimeSeconds() {
        long elapsed;
        if (running) {
            elapsed = (( totalSoFar+ System.currentTimeMillis() - startTime) / 1000);
        }
        else {
            elapsed = ((totalSoFar) / 1000);
        }
        return elapsed;
    }

    
    
    
    //sample usage
    public static void main(String[] args) {
        StopWatch s = new StopWatch();
        s.start();
        //code you want to time goes here
        s.stop();
        System.out.println("elapsed time in milliseconds: " + s.getElapsedTime());
    }
}