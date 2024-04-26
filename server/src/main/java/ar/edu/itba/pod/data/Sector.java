package ar.edu.itba.pod.data;

import java.util.ArrayList;
import java.util.List;

public class Sector {
    private final String name;
    private final List<Counter> counters = new ArrayList<>();

    public Sector(String name){
        this.name = name;
    }

    public void addCounter(int counterId){
        counters.add(new Counter(counterId, this));
    }

    public List<Counter> getCounters(){
        return counters;
    }

    public String getName(){
        return name;
    }
}
