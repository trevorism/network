package com.trevorism.controller

import com.trevorism.model.NetworkNode
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.NetworkService
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import io.micronaut.http.exceptions.HttpStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/api/network")
class NetworkNodeController {

    private static final Logger log = LoggerFactory.getLogger(NetworkNodeController)

    @Inject
    NetworkService networkService

    @Tag(name = "Network Node Operations")
    @Operation(summary = "Join a network **Secure")
    @Status(HttpStatus.CREATED)
    @Post(value = "/{networkId}/node", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    NetworkNode createNetworkNode(String networkId, @Body NetworkNode node) {
        try {
            return networkService.join(networkId, node)
        } catch (Exception ignored) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create network")
        }
    }

    @Tag(name = "Network Node Operations")
    @Operation(summary = "Get all network nodes **Secure")
    @Get(value = "/{networkId}/node", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    List<NetworkNode> listNetworkNodes(String networkId) {
        try {
            return networkService.getNetworkNodes(networkId)
        } catch (Exception e) {
            log.error("Unable to get network nodes", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to get network nodes: ${e.message}")
        }
    }

    @Tag(name = "Network Node Operations")
    @Operation(summary = "Get node details **Secure")
    @Get(value = "/{networkId}/node/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    NetworkNode getNetworkNode(String networkId, String id) {
        try {
            return networkService.getNetworkNode(networkId, id)
        } catch (Exception e) {
            log.warn("Unable to find network node ${id}", e)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Unable to find network node ${id}")
        }
    }

    @Tag(name = "Network Node Operations")
    @Operation(summary = "Remove node from network **Secure")
    @Delete(value = "/{networkId}/node/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    NetworkNode delete(String networkId, String id) {
        try {
            return networkService.deleteNetworkNode(networkId, id)
        } catch (Exception ignored) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Unable to delete network node ${id}")
        }
    }
}
