package com.vaas.fullstackexercise3;

import com.sun.net.httpserver.HttpServer;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    private Peers knownPeersObj;
    private static Server server;
    private static int port;
    private ObservableList<String> messages;

    private Server() {
        knownPeersObj = new Peers();
        messages = FXCollections.observableArrayList();
    }

    public ObservableList<String> getMessagesObject() {
        return messages;
    }

    public static Server getServerObject() {
        if (server == null) server = new Server();
        return server;
    }

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/known_peers", exchange -> { // This endpoint will be called only when a new client connects
                if (!exchange.getRequestMethod().equals("GET")) return;
                InetSocketAddress address;
                try {
                    address = new InetSocketAddress(exchange.getRemoteAddress().getAddress(), Integer.parseInt(exchange.getRequestHeaders().getFirst("Port")));
                } catch (NumberFormatException e) { // Either invalid port number or no port given in header
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                    return;
                } catch (IllegalArgumentException e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                    return;
                }
                System.out.println(exchange.getRequestHeaders().getFirst("Port"));
                StringBuilder builder = new StringBuilder();
                for (InetSocketAddress knownPeer : knownPeersObj.getKnownPeers(address)) {
                    builder.append(knownPeer.getAddress().getHostAddress()).append(":").append(knownPeer.getPort()).append(System.lineSeparator());
                }
                System.out.println(builder.toString().getBytes().length + ", " + builder.length());
                exchange.sendResponseHeaders(200, builder.length());
                OutputStream out = exchange.getResponseBody();
                out.write(builder.toString().getBytes());
                out.flush();
                exchange.close();
                // TODO: broadcast new peer
            });
            server.createContext("/new_peer", exchange -> { // This endpoint will be called when a new peer is broadcasted
                if (!exchange.getRequestMethod().equals("POST")) return;
                InetSocketAddress newPeerAddress;
                try {
                    InetSocketAddress ignored = new InetSocketAddress(exchange.getRemoteAddress().getAddress(), Integer.parseInt(exchange.getRequestHeaders().getFirst("Port")));
                    String[] body = new String(exchange.getRequestBody().readAllBytes()).split(":");
                    newPeerAddress = new InetSocketAddress(body[0], Integer.parseInt(body[1]));
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                    return;
                }

                knownPeersObj.addNewPeer(newPeerAddress);
                System.out.println("New Peer added: " + newPeerAddress.getAddress().getHostAddress() + ":" + newPeerAddress.getPort());
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            });
            server.createContext("/new_message", exchange -> { // This endpoint will be called when a new peer is broadcasted
                if (!exchange.getRequestMethod().equals("POST")) return;
                String body;
                InetSocketAddress address;
                try {
                    address = new InetSocketAddress(exchange.getRemoteAddress().getAddress(), Integer.parseInt(exchange.getRequestHeaders().getFirst("Port")));
                    body = new String(exchange.getRequestBody().readAllBytes());
                } catch (Exception e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                    return;
                }

                messages.add(address.getHostString() + ":" + address.getPort() + " - " + body);
                System.out.println("New Message received: " + body);
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            });
            server.start();
            port = server.getAddress().getPort();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<InetSocketAddress> getKnownPeers() {
        return knownPeersObj.knownPeers;
    }

    public boolean requestPeersFrom(String seedField) {
        try {
            String[] address = seedField.split(":");
            InetSocketAddress peerToConnectTo = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            HttpClient newClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().setHeader("Port", String.valueOf(port)).uri(new URI("http://" + peerToConnectTo.getHostString() + ":" + peerToConnectTo.getPort() + "/known_peers")).version(HttpClient.Version.HTTP_1_1).build();
            HttpResponse<String> response = newClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return false;
            knownPeersObj.knownPeers.clear();
            List<InetSocketAddress> peers = Arrays.stream(response.body().split(System.lineSeparator())).map(o -> new InetSocketAddress(o.split(":")[0], Integer.parseInt(o.split(":")[1]))).toList();
            knownPeersObj.knownPeers.addAll(peers);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
