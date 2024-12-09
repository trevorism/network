package com.trevorism.controller

import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/api/node")
class NodeController {

    private static final Logger log = LoggerFactory.getLogger(NodeController)

    @Tag(name = "Sample Node Operations")
    @Operation(summary = "Receive a network message")
    @Post(value = "/{id}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    boolean handleMessage(String id, @Body Map message) {
        try {
            log.info("Node ${id} -- Received message: ${message}")
            return true
        } catch (Exception ignored) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create network")
        }
    }

    @Tag(name = "Sample Node Operations")
    @Operation(summary = "Receive a network message **Secure")
    @Post(value = "/secure/{id}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    boolean handleSecureMessage(String id, @Body Map message) {
        try {
            log.info("Node ${id} -- Received secure message: ${message}")
            return true
        } catch (Exception ignored) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create network")
        }
    }
}
