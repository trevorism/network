package com.trevorism.service

import com.trevorism.data.FastDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.event.ChannelClient
import com.trevorism.event.DefaultChannelClient
import com.trevorism.https.SecureHttpClient
import com.trevorism.model.Network

class DefaultNetworkService implements NetworkService{

    private Repository<Network> repository
    private ChannelClient channelClient

    DefaultNetworkService(SecureHttpClient httpClient) {
        this.repository = new FastDatastoreRepository<>(Network, httpClient)
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
        Network deleted = repository.delete(id)
        String topicName = "network-${deleted.id}"
        channelClient.deleteTopic(topicName)
        return deleted
    }

    @Override
    Network createNetwork(Network network) {
        List<Network> existing = getNetworks()
        if(existing.find { it.name == network.name }){
            throw new RuntimeException("Network with name ${network.name} already exists")
        }
        network.dateCreated = new Date()
        Network created = repository.create(network)
        String topicName = "network-${created.id}"
        channelClient.createTopic(topicName)
        return created
    }
}
