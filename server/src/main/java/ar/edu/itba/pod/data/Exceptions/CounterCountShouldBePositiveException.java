package ar.edu.itba.pod.data.Exceptions;

import com.google.protobuf.Message;

public class CounterCountShouldBePositiveException extends RuntimeException{
    public static final String MESSAGE = "Counter count must be positive";

    public CounterCountShouldBePositiveException() {
        super(MESSAGE);
    }
}
