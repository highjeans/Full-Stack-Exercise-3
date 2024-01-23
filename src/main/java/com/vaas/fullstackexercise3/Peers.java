package com.vaas.fullstackexercise3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Peers {
    private ArrayList<InetSocketAddress> knownPeers;

    public Peers() {
        knownPeers = new ArrayList<>();
    }

    public ArrayList<InetSocketAddress> getKnownPeers(InetSocketAddress addressRequestedFrom) throws IOException {
        knownPeers.add(addressRequestedFrom);
        saveKnownPeers();
        broadcastNewPeer(addressRequestedFrom);
        return knownPeers;
    }

    private void broadcastNewPeer(InetSocketAddress address) {
        // TODO: Broadcast using server.java
    }

    public void saveKnownPeers() throws IOException {
        File knownPeersFile = new File("known_peers.txt");
        knownPeersFile.createNewFile();
        FileWriter writer = new FileWriter(knownPeersFile);
        for (InetSocketAddress knownPeer : knownPeers) {
            writer.write(knownPeer.toString());
            writer.write(System.lineSeparator());
        }
        writer.flush();
        writer.close();
    }

    public void loadKnownPeers() throws FileNotFoundException {
        File knownPeersFile = new File("known_peers.txt");
        Scanner scanner = new Scanner(knownPeersFile);
        while (scanner.hasNextLine()) {
            String[] newLine = scanner.nextLine().split(":");
            knownPeers.add(new InetSocketAddress(newLine[0], Integer.parseInt(newLine[1])));
        }
        scanner.close();
    }
}
