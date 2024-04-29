package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.data.Exceptions.*;
import com.google.protobuf.Any;
import com.google.rpc.Code;
import io.grpc.*;
import io.grpc.protobuf.StatusProto;

import java.util.Map;


public class GlobalExceptionHandlerInterceptor implements ServerInterceptor {

    @Override
    public <T, R> ServerCall.Listener<T> interceptCall(
            ServerCall<T, R> serverCall, Metadata headers, ServerCallHandler<T, R> serverCallHandler) {
        ServerCall.Listener<T> delegate = serverCallHandler.startCall(serverCall, headers);
        return new ExceptionHandler<>(delegate, serverCall, headers);
    }

    private static class ExceptionHandler<T, R> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<T> {
        private final ServerCall<T, R> delegate;
        private final Metadata headers;

        ExceptionHandler(ServerCall.Listener<T> listener, ServerCall<T, R> serverCall, Metadata headers) {
            super(listener);
            this.delegate = serverCall;
            this.headers = headers;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, delegate, headers);
            }
        }

        private final Map<Class<? extends Throwable>, Code> errorCodesByException = Map.ofEntries(
            Map.entry(SectorAlreadyExistsException.class, Code.ALREADY_EXISTS),
            Map.entry(SectorNotFoundException.class, Code.NOT_FOUND),
            Map.entry(CounterCountShouldBePositiveException.class, Code.FAILED_PRECONDITION),
            Map.entry(FlightCodeAlreadyExistsInOtherAirlineException.class, Code.ALREADY_EXISTS),
            Map.entry(PassengerAlreadyExistsException.class, Code.FAILED_PRECONDITION),
            Map.entry(SectorsIsEmptyException.class, Code.NOT_FOUND),
            Map.entry(AirlineNotMatchesCounterRangeException.class, Code.FAILED_PRECONDITION),
            Map.entry(AirlineNotFoundException.class, Code.NOT_FOUND),
            Map.entry(AirlineNotMatchesFlight.class, Code.FAILED_PRECONDITION),
            Map.entry(NoPassengersInFlightException.class, Code.FAILED_PRECONDITION),
            Map.entry(BadCounterRangeException.class, Code.FAILED_PRECONDITION),
            Map.entry(CounterRangeAlreadyAssignedToflight.class, Code.ALREADY_EXISTS),
            Map.entry(FlightCodeAlreadyInCounterRangeQueue.class, Code.ALREADY_EXISTS),
            Map.entry(StillPassengersInLineException.class, Code.FAILED_PRECONDITION),
            Map.entry(AirlineAlreadyRegisteredForEventsException.class, Code.ALREADY_EXISTS),
            Map.entry(BookingNotFoundException.class, Code.NOT_FOUND),
            Map.entry(NotRangeAssignedException.class, Code.ALREADY_EXISTS),
            Map.entry(FlightNotMatchesCounterException.class, Code.FAILED_PRECONDITION),
            Map.entry(PassengerAlreadyCheckedIn.class, Code.ALREADY_EXISTS),
            Map.entry(PassengerAlreadyInQueue.class, Code.FAILED_PRECONDITION)
        );

        private void handleException(RuntimeException exception, ServerCall<T, R> serverCall, Metadata headers) {
            Throwable error = exception;
            if (!errorCodesByException.containsKey(error.getClass())) {
                // Si la excepción vino "wrappeada" entonces necesitamos preguntar por la causa.
                error = error.getCause();
                if (error == null || !errorCodesByException.containsKey(error.getClass())) {
                    // Una excepción NO esperada.
                    serverCall.close(Status.UNKNOWN, headers);
                    return;
                }
            }
            // Una excepción esperada.
            com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                    .setCode(errorCodesByException.get(error.getClass()).getNumber())
                    .setMessage(error.getMessage())
                    .build();
            StatusRuntimeException statusRuntimeException = StatusProto.toStatusRuntimeException(rpcStatus);
            Status newStatus = Status.fromThrowable(statusRuntimeException);
            serverCall.close(newStatus, headers);
        }
    }

}