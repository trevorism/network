package com.trevorism.service

import com.trevorism.data.Repository
import com.trevorism.data.model.filtering.SimpleFilter
import com.trevorism.event.ChannelClient
import com.trevorism.event.model.EventSubscription
import com.trevorism.https.SecureHttpClient
import com.trevorism.model.Network
import com.trevorism.model.NetworkNode
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

class DefaultNetworkServiceTest {

    @Test
    void testGetNetworks() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [list: { -> [new Network(name: "test")] }] as Repository

        def result = service.getNetworks()
        assert result.size() == 1
        assert result[0].name == "test"
    }

    @Test
    void testGetNetwork() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [get: { String id -> new Network(name: "test") }] as Repository

        def result = service.getNetwork("1")
        assert result
        assert result.name == "test"
    }

    @Test
    void testDeleteNetwork() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [
                get: { String id -> new Network(id: "1", name: "test") },
                delete: { String id -> new Network(id: "1", name: "test") }
        ] as Repository
        service.nodeRepository = [filter: { SimpleFilter sf -> [] }] as Repository
        service.channelClient = [deleteTopic: { String topicName -> }] as ChannelClient

        def result = service.deleteNetwork("1")
        assert result
        assert result.id == "1"
    }

    @Test
    void testCreateNetwork() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [
                list: { -> [] },
                create: { Network network -> new Network(id: "1", name: "test", topicName: "network-test") }
        ] as Repository
        service.channelClient = [createTopic: { String topicName -> }] as ChannelClient

        Network network = new Network(name: "test")
        def result = service.createNetwork(network)
        assert result
        assert result.id == "1"
        assert result.name == "test"
        assert result.topicName == "network-test"
    }

    @Test
    void testJoin() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [get: { String id -> new Network(id: "1", name: "test") }] as Repository
        service.nodeRepository = [create: { NetworkNode node -> new NetworkNode(id: "1", name: "node1", networkId: "1") }] as Repository
        service.channelClient = [createSubscription: { EventSubscription subscription -> return subscription }] as ChannelClient

        NetworkNode node = new NetworkNode(name: "node1", listenUrl: "http://localhost")
        def result = service.join("1", node)
        assert result
        assert result.id == "1"
        assert result.name == "node1"
        assert result.networkId == "1"
    }

    @Test
    void testGetNetworkNodes() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [get: { String id -> new Network(id: "1", name: "test") }] as Repository
        service.nodeRepository = [filter: { x -> [new NetworkNode(name: "node1")] }] as Repository

        def result = service.getNetworkNodes("1")
        assert result.size() == 1
        assert result[0].name == "node1"
    }

    @Test
    void testGetNetworkNode() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [get: { String id -> new Network(id: "1", name: "test") }] as Repository
        service.nodeRepository = [get: { String id -> new NetworkNode(name: "node1") }] as Repository

        def result = service.getNetworkNode("1", "1")
        assert result
        assert result.name == "node1"
    }

    @Test
    void testDeleteNetworkNode() {
        SecureHttpClient httpClient = [get: { -> null }] as SecureHttpClient
        DefaultNetworkService service = new DefaultNetworkService(httpClient)
        service.repository = [get: { String id -> new Network(id: "1", name: "test") }] as Repository
        service.nodeRepository = [
                get: { String id -> new NetworkNode(id: "1", name: "node1") },
                delete: { String id -> new NetworkNode(id: "1", name: "node1") }
        ] as Repository
        service.channelClient = [deleteSubscription: { String subscriptionName -> }] as ChannelClient

        def result = service.deleteNetworkNode("1", "1")
        assert result
        assert result.id == "1"
        assert result.name == "node1"
    }
}