package com.trevorism.controller

import com.trevorism.model.Network
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.NetworkService
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/api/network")
class NetworkController {

    private static final Logger log = LoggerFactory.getLogger(NetworkController)

    @Inject
    NetworkService networkService

    @Tag(name = "Network Operations")
    @Operation(summary = "Create a network **Secure")
    @Status(HttpStatus.CREATED)
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    Network createNetwork(@Body Network network) {
        try {
            return networkService.createNetwork(network)
        } catch (Exception e) {
            log.error("Unable to create network", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create network")
        }
    }

    @Tag(name = "Network Operations")
    @Operation(summary = "Get all networks  **Secure")
    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    List<Network> listNetworks() {
        try {
            return networkService.getNetworks()
        } catch (Exception e) {
            log.error("Unable to get networks", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to get networks: ${e.message}")
        }
    }

    @Tag(name = "Network Operations")
    @Operation(summary = "Get network details **Secure")
    @Get(value = "{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    Network getNetwork(String id) {
        try {
            return networkService.getNetwork(id)
        } catch (Exception e) {
            log.warn("Unable to find network ${id}", e)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Unable to find network ${id}")
        }
    }

    @Tag(name = "Network Operations")
    @Operation(summary = "Delete a network **Secure")
    @Delete(value = "{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    Network delete(String id) {
        try {
            return networkService.deleteNetwork(id)
        } catch (Exception ignored) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Unable to delete network")
        }
    }
}
