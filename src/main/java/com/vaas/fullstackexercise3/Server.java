package com.vaas.fullstackexercise3;

import java.io.*;
import java.net.*;

import javafx.collections.ObservableList;
import org.apache.hc.client5.http.fluent.Request;

public class Server {
    private final Peers knownPeersObj;
    private static Server server;
    private static int port;

    private Server() {
        knownPeersObj = new Peers();
    }

    public static Server getServerObject() {
        if (server == null) server = new Server();
        return server;
    }

    public void startServer(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + (Server.port = serverSocket.getLocalPort()));
            knownPeersObj.loadKnownPeers();

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            StringBuilder builder = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).isBlank() && reader.ready()) {
                builder.append(line).append(System.lineSeparator());
            }
            String request = builder.toString();
            System.out.println(request);

            InetSocketAddress senderAddr = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
            System.out.println(senderAddr);
            System.out.println(senderAddr.getClass());

            String[] parts = request.split(" ");
            String endpoint = parts[1];

            switch (endpoint) {
                case "/known_peers":
                    sendKnownPeers(writer, senderAddr);
                    break;
                case "/new_peer":
                    addNewKnownPeer(writer, senderAddr);
                    break;
                case "/new_message":
                    // Handle new message
                    break;
                default:
                    System.out.println("Unknown command: " + endpoint);
                    break;
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendKnownPeers(PrintWriter writer, InetSocketAddress ipFrom) throws IOException {
        writer.println("HTTP/1.1 200 OK");
        writer.println();
        for (InetSocketAddress knownPeer : knownPeersObj.getKnownPeers(ipFrom)) {
            writer.println(knownPeer.toString().substring(1));
        }
        knownPeersObj.broadcastNewPeer(ipFrom);
    }

    private void addNewKnownPeer(PrintWriter writer, InetSocketAddress peerInfo) throws IOException {
        knownPeersObj.addNewPeer(peerInfo);
        System.out.println("New known peer added: " + peerInfo.getHostString() + ":" + peerInfo.getPort());
        writer.println("HTTP/1.1 200 OK");
        writer.println();
    }

    public ObservableList<InetSocketAddress> getKnownPeers() {
        return knownPeersObj.knownPeers;
    }

    public static boolean sendPostRequest(InetSocketAddress addrTo, String endpoint, String message) {
        try {
            if (Request.post(addrTo + "/" + endpoint).addHeader("Port", String.valueOf(port)).addHeader("Message", message).execute().returnResponse().getCode() != 200) {
                throw new IOException();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
