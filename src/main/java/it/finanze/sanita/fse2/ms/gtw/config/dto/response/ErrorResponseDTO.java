package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.gtw.config.dto.AbstractDTO;
import lombok.Data;


/**
 * The Class ErrorResponseDTO.
 * 
 * 	Error response.
 */
@Data
public class ErrorResponseDTO extends AbstractDTO {

	/**
	 * Codice.
	 */
	@Schema(description = "Codice di errore")
	@Min(value = 100)
	@Max(value = 599)
	private final Integer code;
	
	/**
	 * Messaggio.
	 */
	@Schema(description = "Messaggio di errore")
	@Size(min = 0, max = 1000)
	private final String message;

}
