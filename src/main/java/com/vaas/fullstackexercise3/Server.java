package com.vaas.fullstackexercise3;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private final Peers knownPeersObj;

    public Server() {
        knownPeersObj = new Peers();
    }

    public void startServer(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

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

            String request = reader.readLine();
            System.out.println(request);

            if (request != null) {
                String[] parts = request.split(" ");
                String command = parts[0];

                switch (command) {
                    case "GET_KNOWN_PEERS":
                        sendKnownPeers(writer, parts[1]);
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

    private void sendKnownPeers(PrintWriter writer, String ipFrom) throws IOException {
        String[] ipAddressStr = ipFrom.split(":");
        InetSocketAddress ipAddress = new InetSocketAddress(ipAddressStr[0], Integer.parseInt(ipAddressStr[1]));
        for (InetSocketAddress knownPeer : knownPeersObj.getKnownPeers(ipAddress)) {
            writer.println("KNOWN_PEER " + knownPeer.getHostString() + " " + knownPeer.getPort());
        }
        knownPeersObj.broadcastNewPeer(ipAddress);
    }

    private void addNewKnownPeer(String peerInfo) throws IOException {
        String[] parts = peerInfo.split(":");
        InetSocketAddress newPeer = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
        knownPeersObj.addNewPeer(newPeer);
        System.out.println("New known peer added: " + newPeer.getHostString() + ":" + newPeer.getPort());
    }

    public List<InetSocketAddress> getKnownPeers() {
        return knownPeersObj.knownPeers;
    }

}
