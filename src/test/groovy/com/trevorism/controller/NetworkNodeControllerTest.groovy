package com.trevorism.controller

import com.trevorism.model.NetworkNode
import com.trevorism.service.NetworkService
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

class NetworkNodeControllerTest {

    @Test
    void testCreateNetworkNode() {
        NetworkNodeController controller = new NetworkNodeController()
        NetworkNode node = new NetworkNode(name: "testNode")
        controller.networkService = { String networkId, NetworkNode nd ->
            nd.id = "1"
            return nd
        } as NetworkService

        NetworkNode created = controller.createNetworkNode("network1", node)
        assert created
        assert created.name == "testNode"
        assert created.id == "1"
    }

    @Test
    void testCreateInvalidNetworkNode() {
        NetworkNodeController controller = new NetworkNodeController()
        NetworkNode node = new NetworkNode(name: "testNode")
        controller.networkService = { throw new RuntimeException("error") } as NetworkService
        assertThrows(HttpStatusException.class, () -> controller.createNetworkNode("network1", node))
    }

    @Test
    void testListNetworkNodes() {
        NetworkNodeController controller = new NetworkNodeController()
        NetworkNode node = new NetworkNode(name: "testNode")
        controller.networkService = { String networkId -> [node] } as NetworkService
        def result = controller.listNetworkNodes("network1")
        assert result
    }

    @Test
    void testGetNetworkNode() {
        NetworkNodeController controller = new NetworkNodeController()
        NetworkNode node = new NetworkNode(name: "testNode")
        controller.networkService = { String networkId, String id -> id == "1" ? node : null } as NetworkService
        assert controller.getNetworkNode("network1", "1")
        assert !controller.getNetworkNode("network1", "2")
    }

    @Test
    void testDelete() {
        NetworkNodeController controller = new NetworkNodeController()
        NetworkNode node = new NetworkNode(name: "testNode")
        controller.networkService = { String networkId, String id -> id == "1" ? node : null } as NetworkService
        assert controller.delete("network1", "1")
        assert !controller.delete("network1", "2")
    }
}