package com.vaas.fullstackexercise3;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<InetSocketAddress> knownPeers;

    public Server() {
        knownPeers = new ArrayList<>();
    }

    public void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = reader.readLine();

            if (request != null) {
                String[] parts = request.split(" ");
                String command = parts[0];

                switch (command) {
                    case "GET_KNOWN_PEERS":
                        sendKnownPeers(writer);
                        break;
                    case "NEW_PEER":
                        addNewKnownPeer(parts[1]);
                        break;
                    case "NEW_MESSAGE":
                        // Handle new message
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendKnownPeers(PrintWriter writer) {
        for (InetSocketAddress knownPeer : knownPeers) {
            writer.println("KNOWN_PEER " + knownPeer.getHostString() + " " + knownPeer.getPort());
        }
    }

    private void addNewKnownPeer(String peerInfo) {
        String[] parts = peerInfo.split(":");
        InetSocketAddress newPeer = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
        knownPeers.add(newPeer);
        System.out.println("New known peer added: " + newPeer.getHostString() + ":" + newPeer.getPort());
    }

    public List<InetSocketAddress> getKnownPeers() {
        return knownPeers;
    }

}
