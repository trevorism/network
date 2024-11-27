package com.trevorism.service

import com.trevorism.model.NetworkNode

interface NodeService {

    List<NetworkNode> getNodes(String networkId)
    NetworkNode getNode(String id)
    NetworkNode deleteNode(String id)
    NetworkNode createNode(Node node)
}