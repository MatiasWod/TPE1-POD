package ar.edu.itba.pod.data;

import java.util.List;

public class Counter {
    private final int counterId;
    
    public Counter(int counterId){
        this.counterId = counterId;
    }

    public int getCounterId() {
        return counterId;
    }
}
