package com.trevorism.service

import com.trevorism.model.Network

interface NetworkService {

    List<Network> getNetworks()
    Network getNetwork(String id)
    Network deleteNetwork(String id)
    Network createNetwork(Network network)

}
