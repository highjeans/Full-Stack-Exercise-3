package com.vaas.fullstackexercise3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.InetSocketAddress;

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
            }
        }
    }

    public void addNewPeer(InetSocketAddress peer) throws IOException {
        if (peer.getAddress().isLoopbackAddress()) return;
        knownPeers.add(peer);
    }
}
