package com.vaas.fullstackexercise3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.InetSocketAddress;

public class Peers {
    public ObservableList<InetSocketAddress> knownPeers;

    public Peers() {
        knownPeers = FXCollections.observableArrayList();
    }

    public ObservableList<InetSocketAddress> getKnownPeers(InetSocketAddress addressRequestedFrom) {
        addNewPeer(addressRequestedFrom);
        return knownPeers;
    }

    public void addNewPeer(InetSocketAddress peer) {
        if (peer.getAddress().isLoopbackAddress()) return;
        knownPeers.add(peer);
    }
}
