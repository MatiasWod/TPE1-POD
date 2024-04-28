package ar.edu.itba.pod.data;

import java.util.ArrayList;
import java.util.List;

public class CounterRange {
    private Counter firstCounter;
    private int lastCounterId;

    public CounterRange(Counter firstCounter, int lastCounterId) {
        this.firstCounter = firstCounter;
        this.lastCounterId = lastCounterId;
    }

    public void setCounterId(int counterId) {
        this.lastCounterId = counterId;
    }


    public Counter getFirstCounter() {
        return firstCounter;
    }

    public int getLastCounterId() {
        return lastCounterId;
    }

    public String getAirline() {
        return firstCounter.getAirline();
    }
}
