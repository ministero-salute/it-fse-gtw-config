/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.WhoIsResponseDTO;

/**
 * Controller interface that defines all accessible api to handle generic configuration of a gateway.
 */
@RequestMapping(path = "/v1")
@Tag(name = "Gateway Configuration service", description = "Gateway Configuration service")
public interface IGatewayCTL {
    
    @GetMapping(value = "/whois")
	@Operation(summary = "Restituisce il nome del gateway", description = "Restituisce il nome del gateway")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigItemDTO.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Gateway name retrieved correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WhoIsResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<WhoIsResponseDTO> getGatewayName(HttpServletRequest request);

}
