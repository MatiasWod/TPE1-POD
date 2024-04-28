package ar.edu.itba.pod.client.events;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.events.EventsResponse;
import ar.edu.itba.pod.events.EventsServiceGrpc;
import ar.edu.itba.pod.events.RegistrationRequest;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;

public class RegisterAction extends Action {

    public RegisterAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel){
        EventsServiceGrpc.EventsServiceBlockingStub stub = EventsServiceGrpc.newBlockingStub(channel);

        RegistrationRequest registerRequest = RegistrationRequest.newBuilder().setAirlineName(System.getProperty("airline")).build();

        try{
            Iterator<EventsResponse> responseIterator = stub.register(registerRequest);

            while (responseIterator.hasNext()) {
                EventsResponse response = responseIterator.next();

                switch (response.getStatus()) {
                    case REGISTER_SUCCESS:
                        System.out.printf("%s registered successfully for check-in events\n", System.getProperty("airline"));
                        break;
                    case COUNTER_ASSIGNMENT_SUCCESS:
                        System.out.printf("%d counters (%d-%d) in Sector %s are now checking in passengers from %s ",
                                response.getCounterCount(), response.getFirstCounter(), response.getLastCounter(), response.getSector(), System.getProperty("airline"));
                        if(!response.getFlightsList().isEmpty()){
                            printFlights(response.getFlightsList());
                            System.out.print(" flights.\n");
                        }
                        break;
                    case PASSENGER_QUEUE_SUCCESS:
                        System.out.printf("Booking %s for flight %s from %s is now waiting to check-in on counters (%d-%d) in Sector %s with %d people in line\n",
                                response.getBookingCode(), response.getFlights(0), System.getProperty("airline"), response.getFirstCounter(), response.getLastCounter(),
                                response.getSector(), response.getPendingAhead());
                        break;
                    case CHECKIN_SUCCESS:
                        System.out.printf("Check-in successful of XYZ345 for flight AA123 at counter 3 in Sector C\n");
                        break;
                    case COUNTER_FREED:
                        System.out.printf("Ended check-in for flights ");
                        if(!response.getFlightsList().isEmpty()){
                            printFlights(response.getFlightsList());
                        }
                        System.out.printf(" on counters (%d-%d) from Sector %s.\n", response.getFirstCounter(), response.getLastCounter(), response.getSector());
                        break;
                    case COUNTER_ASSIGNMENT_PENDING:
                    case COUNTER_ASSIGNMENT_PENDING_CHANGE:
                        System.out.printf("%d counters in Sector %s for flights ", response.getCounterCount(), response.getSector());
                        if(!response.getFlightsList().isEmpty()){
                            printFlights(response.getFlightsList());
                        }
                        System.out.printf("is pending with %d other pendings ahead.", response.getPendingAhead());
                        break;
                    case DESTROYED:
                       return;
                }
            }

        }

        /*
        Iterator<NotificationResponse> responseIterator = stub.followBooking(request);
            while (responseIterator.hasNext()) {
                NotificationResponse response = responseIterator.next();
                System.out.println(NotificationStatus.valueOf(response.getStatus().name()).consumeNotification(response));
            }
         */
        catch (StatusRuntimeException exception){
            if (exception.getStatus() == Status.INVALID_ARGUMENT){
                throw new IllegalArgumentException();
            } else if (exception.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments();
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./eventsClient
                    -DserverAddress=XX.XX.XX.XX:YYYY 
                    -Daction=register 
                    -Dairline= $airlineName
                """;
    }

    public void printFlights(List<String> flights){
        for(String flight : flights){
            System.out.printf("%s|", flight);
        }
    }
}
