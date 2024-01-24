package com.vaas.fullstackexercise3;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private final Peers knownPeersObj;
    private static Server server;

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
            System.out.println("Server started on port " + serverSocket.getLocalPort());

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
                    addNewKnownPeer(senderAddr);
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

    private void addNewKnownPeer(InetSocketAddress peerInfo) throws IOException {
        knownPeersObj.addNewPeer(peerInfo);
        System.out.println("New known peer added: " + peerInfo.getHostString() + ":" + peerInfo.getPort());
    }

    public List<InetSocketAddress> getKnownPeers() {
        return knownPeersObj.knownPeers;
    }

}
