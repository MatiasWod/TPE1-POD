package ar.edu.itba.pod.server;

import ar.edu.itba.pod.servants.*;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");
        GlobalExceptionHandlerInterceptor exceptionHandlerInterceptor = new GlobalExceptionHandlerInterceptor();

        int port = 50051;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(
                                new AdminServant(),
                                exceptionHandlerInterceptor))
                .addService(ServerInterceptors.intercept(
                        new CheckInService(),
                        exceptionHandlerInterceptor))
                .addService(ServerInterceptors.intercept(
                        new CounterReservationService(),
                        exceptionHandlerInterceptor))
                .addService(ServerInterceptors.intercept(
                        new QueryServant(),
                        exceptionHandlerInterceptor))
                .build();
        server.start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }}
