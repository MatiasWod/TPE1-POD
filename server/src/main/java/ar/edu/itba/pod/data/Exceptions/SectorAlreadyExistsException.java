package ar.edu.itba.pod.data.Exceptions;

public class SectorAlreadyExistsException extends RuntimeException {
    public SectorAlreadyExistsException(String sector) {
        super(String.format("El sector %s ya existe", sector));
    }
}
