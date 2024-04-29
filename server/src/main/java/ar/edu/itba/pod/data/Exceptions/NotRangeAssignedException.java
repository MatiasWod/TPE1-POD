package ar.edu.itba.pod.data.Exceptions;

public class NotRangeAssignedException extends RuntimeException {
    public NotRangeAssignedException(int counterId) {
        super(String.format("There is not a counter range assigned starting at counter %d", counterId));
    }
}
