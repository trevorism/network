package com.trevorism.service

import com.trevorism.model.Network
import com.trevorism.model.NetworkNode

interface NetworkService {

    List<Network> getNetworks()
    Network getNetwork(String id)
    Network deleteNetwork(String id)
    Network createNetwork(Network network)

    NetworkNode join(String networkId, NetworkNode networkNode)
    List<NetworkNode> getNetworkNodes(String networkId)
    NetworkNode getNetworkNode(String networkId, String id)
    NetworkNode deleteNetworkNode(String networkId, String id)

}
