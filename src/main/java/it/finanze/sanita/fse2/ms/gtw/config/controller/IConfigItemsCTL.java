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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;

/**
 * Controller interface that defines all accessible api to handle configuration items.
 */
@RequestMapping(path = "/v1")
@Tag(name = "Gateway Configuration Items service", description = "Gateway Configuration Items service")
public interface IConfigItemsCTL {

    @GetMapping(value = "/config-items")
	@Operation(summary = "Recupero elementi di configurazione", description = "Restituisce tutti gli elementi di configurazione")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigItemDTO.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Item retrieved correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigItemDTO.class))),
			@ApiResponse(responseCode = "404", description = "Configuration items not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<ConfigItemDTO> getConfigurationItems(@RequestParam(required = true) ConfigItemTypeEnum type, HttpServletRequest request);

    @PostMapping(value = "/config-items",  consumes = { MediaType.APPLICATION_JSON_VALUE})
	@Operation(summary = "Persistenza elementi di configurazione", description = "Persiste gli elementi di configurazione")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Configuration items persisted correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<ResponseDTO> saveConfigurationItems(@RequestParam(required = true) ConfigItemTypeEnum type, @RequestBody(required = true) @Size(min = 0, max = 1000) Map<String, String> configItems,  HttpServletRequest request);

	@DeleteMapping(value = "/config-items")
	@Operation(summary = "Eliminazione elementi di configurazione", description = "Elimina uno o più elementi di configurazione")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Configuration items deleted correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Configuration items not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<ResponseDTO> deleteConfigurationItems(@RequestParam(required = true) ConfigItemTypeEnum type, @RequestParam(required = false) @Size(min = 0, max = 1000) String itemKey,  HttpServletRequest request);

	@PutMapping(value = "/config-items")
	@Operation(summary = "Aggiornamento elementi di configurazione", description = "Aggiorna un elemento di configurazione")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Configuration item updated correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Configuration item not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<ResponseDTO> updateConfigurationItems(@RequestParam(required = true) ConfigItemTypeEnum type, @RequestParam(required = true) @Size(min = 0, max = 1000) String itemKey, @RequestParam(required = true) @Size(min = 0, max = 1000) String value, HttpServletRequest request);
	
	@GetMapping(value = "/config-items/props")
	@Operation(summary = "Recupero elementi di configurazione", description = "Restituisce tutti gli elementi di configurazione")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigItemDTO.class)))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Item retrieved correctly", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigItemDTO.class))),
			@ApiResponse(responseCode = "404", description = "Configuration items not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))})
	ResponseEntity<String> getValueOfSpecificConfigurationItems(@RequestParam(required = true) ConfigItemTypeEnum type,
			@RequestParam(required = true) String props,HttpServletRequest request);

}
