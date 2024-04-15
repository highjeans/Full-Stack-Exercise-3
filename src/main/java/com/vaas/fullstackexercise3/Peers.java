package com.vaas.fullstackexercise3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Peers {
    public ObservableList<InetSocketAddress> knownPeers;

    public Peers() {
        knownPeers = FXCollections.observableArrayList();
    }

    public ObservableList<InetSocketAddress> getKnownPeers(InetSocketAddress addressRequestedFrom) throws IOException {
        addNewPeer(addressRequestedFrom);
        return knownPeers;
    }

    public void broadcastNewPeer(InetSocketAddress address) throws IOException {
        for (InetSocketAddress knownPeer : knownPeers) {
            if (Server.sendPostRequest(knownPeer, "new_peer", knownPeer.toString())) {
                knownPeers.remove(knownPeer);
                saveKnownPeers();
            }
        }
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

    public void addNewPeer(InetSocketAddress peer) throws IOException {
        if (peer.getAddress().isLoopbackAddress()) return;
        knownPeers.add(peer);
        saveKnownPeers();
    }
}
