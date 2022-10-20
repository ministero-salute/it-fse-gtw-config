package it.finanze.sanita.fse2.ms.gtw.config.controller.impl;

import javax.servlet.http.HttpServletRequest;

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
 * 
 * @author Simone Lungarella
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
