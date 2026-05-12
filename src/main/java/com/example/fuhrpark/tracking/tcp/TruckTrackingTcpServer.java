package com.example.fuhrpark.tracking.tcp;

import com.example.fuhrpark.tracking.dto.TruckLocationMessage;
import com.example.fuhrpark.tracking.service.TruckLocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class TruckTrackingTcpServer {
    private final TruckLocationService truckLocationService;
    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final int port;
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    private ServerSocket serverSocket;
    private Thread acceptThread;

    public TruckTrackingTcpServer(
            TruckLocationService truckLocationService,
            @Value("${tracking.tcp.enabled:true}") boolean enabled,
            @Value("${tracking.tcp.port:9090}") int port) {
        this.truckLocationService = truckLocationService;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.enabled = enabled;
        this.port = port;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        if (!enabled) {
            log.info("TCP truck tracking server disabled");
            return;
        }

        acceptThread = new Thread(this::runServer, "truck-tracking-tcp-acceptor");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void runServer() {
        try (ServerSocket socket = new ServerSocket(port)) {
            serverSocket = socket;
            log.info("TCP truck tracking server listening on port {}", port);

            while (!socket.isClosed()) {
                Socket clientSocket = socket.accept();
                clientExecutor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException exception) {
            if (serverSocket != null && serverSocket.isClosed()) {
                log.info("TCP truck tracking server stopped");
            } else {
                log.error("TCP truck tracking server failed", exception);
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                handleLine(line);
            }
        } catch (IOException exception) {
            log.warn("TCP tracking client connection failed: {}", exception.getMessage());
        }
    }

    private void handleLine(String line) {
        if (line == null || line.isBlank()) {
            return;
        }

        try {
            TruckLocationMessage message = objectMapper.readValue(line, TruckLocationMessage.class);
            truckLocationService.updateLocation(message);
        } catch (Exception exception) {
            log.warn("Invalid TCP tracking message: {}", exception.getMessage());
        }
    }

    @PreDestroy
    public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException exception) {
                log.warn("Failed to close TCP truck tracking server socket: {}", exception.getMessage());
            }
        }

        clientExecutor.shutdownNow();

        if (acceptThread != null) {
            acceptThread.interrupt();
        }

        log.info("TCP truck tracking server shutdown completed");
    }
}
