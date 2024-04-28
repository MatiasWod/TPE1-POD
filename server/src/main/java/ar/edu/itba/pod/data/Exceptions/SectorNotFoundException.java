package ar.edu.itba.pod.data.Exceptions;

public class SectorNotFoundException extends RuntimeException{

    public SectorNotFoundException(String sector) {
        super(String.format("Sector %s not found", sector));
    }
}
