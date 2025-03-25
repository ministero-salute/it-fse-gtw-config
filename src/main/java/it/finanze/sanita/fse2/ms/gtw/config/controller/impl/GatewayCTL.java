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
package it.finanze.sanita.fse2.ms.gtw.config.controller.impl;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.config.controller.IGatewayCTL;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.WhoIsResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller implementation that exposes all accessible api to handle configuration of a gateway.
 */
@Slf4j
@RestController
public class GatewayCTL implements IGatewayCTL {

    @Autowired
    private IConfigItemsSRV configItemsSRV;

    @Override
    public ResponseEntity<WhoIsResponseDTO> getGatewayName(HttpServletRequest request) {
        
        log.debug("Who-is request received from {}", request.getRemoteAddr());
        final String gatewayName = configItemsSRV.retrieveGatewayName();
        return new ResponseEntity<>(new WhoIsResponseDTO(gatewayName), HttpStatus.OK);
    }
    
}
