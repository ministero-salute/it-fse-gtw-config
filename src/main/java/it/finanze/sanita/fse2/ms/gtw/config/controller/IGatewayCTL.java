/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.config.controller;

import jakarta.servlet.http.HttpServletRequest;

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
