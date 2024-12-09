package com.trevorism.controller

import com.trevorism.model.Network
import com.trevorism.service.NetworkService
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

class NetworkControllerTest {

    @Test
    void testCreateNetwork() {
        NetworkController controller = new NetworkController()
        Network network = new Network(name: "test", dateCreated: new Date())
        controller.networkService = { Network nw ->
            nw.id = "1"
            nw.topicName = "network-${nw.name}"
            return nw
        } as NetworkService

        Network created = controller.createNetwork(network)
        assert created
        assert created.name == "test"
        assert created.id == "1"
        assert created.topicName == "network-test"
    }

    @Test
    void testCreateInvalidNetwork() {
        NetworkController controller = new NetworkController()
        Network network = new Network(name: "test", dateCreated: new Date())
        controller.networkService = { throw new RuntimeException("error") } as NetworkService
        assertThrows(HttpStatusException.class, () -> controller.createNetwork(network))
    }

    @Test
    void testListNetworks() {
        NetworkController controller = new NetworkController()
        Network network = new Network(name: "test", dateCreated: new Date())
        controller.networkService = { -> [network] } as NetworkService
        def result = controller.listNetworks()
        assert result
    }

    @Test
    void testGetNetwork() {
        NetworkController controller = new NetworkController()
        Network network = new Network(name: "test", dateCreated: new Date())
        controller.networkService = { String id -> id == "1" ? network : null } as NetworkService
        assert controller.getNetwork("1")
        assert !controller.getNetwork("2")
    }

    @Test
    void testDelete() {
        NetworkController controller = new NetworkController()
        Network network = new Network(name: "test", dateCreated: new Date())
        controller.networkService = { String id -> id == "1" ? network : null } as NetworkService
        assert controller.delete("1")
        assert !controller.delete("2")
    }
}
