package com.trevorism.service

import com.trevorism.data.FastDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.data.model.filtering.SimpleFilter
import com.trevorism.event.ChannelClient
import com.trevorism.event.DefaultChannelClient
import com.trevorism.event.model.EventSubscription
import com.trevorism.https.SecureHttpClient
import com.trevorism.model.Network
import com.trevorism.model.NetworkNode

class DefaultNetworkService implements NetworkService {

    private Repository<Network> repository
    private Repository<NetworkNode> nodeRepository
    private ChannelClient channelClient

    private static final String NETWORK_PREFIX = "network-"

    DefaultNetworkService(SecureHttpClient httpClient) {
        this.repository = new FastDatastoreRepository<>(Network, httpClient)
        this.nodeRepository = new FastDatastoreRepository<>(NetworkNode, httpClient)
        this.channelClient = new DefaultChannelClient(httpClient)
    }

    @Override
    List<Network> getNetworks() {
        repository.list()
    }

    @Override
    Network getNetwork(String id) {
        repository.get(id)
    }

    @Override
    Network deleteNetwork(String id) {
        List<NetworkNode> nodes = getNetworkNodes(id)
        nodes.each {
            deleteNetworkNode(id, it.id)
        }
        Network deleted = repository.delete(id)
        String topicName = "${NETWORK_PREFIX}${deleted.id}"
        channelClient.deleteTopic(topicName)

        return deleted
    }

    @Override
    Network createNetwork(Network network) {
        List<Network> existing = getNetworks()
        if (existing.find { it.name == network.name }) {
            throw new RuntimeException("Network with name ${network.name} already exists")
        }
        String topicName = "${NETWORK_PREFIX}${network.name}"
        channelClient.createTopic(topicName)

        network.dateCreated = new Date()
        network.topicName = topicName
        return repository.create(network)
    }

    @Override
    NetworkNode join(String networkId, NetworkNode networkNode) {
        Network network = getNetwork(networkId)
        if (!network) {
            throw new RuntimeException("Network with id ${networkId} does not exist")
        }
        networkNode.networkId = networkId
        NetworkNode created = nodeRepository.create(networkNode)
        channelClient.createSubscription(new EventSubscription(name: "${NETWORK_PREFIX}${networkNode.name}", topic: "${NETWORK_PREFIX}${network.name}", url: networkNode.listenUrl))
        return created
    }

    @Override
    List<NetworkNode> getNetworkNodes(String networkId) {
        Network network = getNetwork(networkId)
        if (!network) {
            throw new RuntimeException("Network with id ${networkId} does not exist")
        }
        return nodeRepository.filter(new SimpleFilter("networkId", "=", networkId))
    }

    @Override
    NetworkNode getNetworkNode(String networkId, String id) {
        Network network = getNetwork(networkId)
        if (!network) {
            throw new RuntimeException("Network with id ${networkId} does not exist")
        }
        nodeRepository.get(id)
    }

    @Override
    NetworkNode deleteNetworkNode(String networkId, String id) {
        NetworkNode toBeDeleted = getNetworkNode(networkId, id)
        channelClient.deleteSubscription("${NETWORK_PREFIX}${toBeDeleted.name}")
        nodeRepository.delete(toBeDeleted.id)
    }
}
