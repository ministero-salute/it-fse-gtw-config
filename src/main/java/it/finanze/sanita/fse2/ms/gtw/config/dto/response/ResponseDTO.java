/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import javax.validation.constraints.Size;

import it.finanze.sanita.fse2.ms.gtw.config.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *	Base response.
 */
@Getter
@Setter
public class ResponseDTO extends AbstractDTO {

	/**
	 * Trace id log.
	 */
	@Size(min = 0, max = 100)
	private String traceID;
	
	/**
	 * Span id log.
	 */
	@Size(min = 0, max = 100)
	private String spanID;
	
	/**
	 * Error.
	 */
	private ErrorResponseDTO error;

	/**
	 * Instantiates a new response DTO.
	 */
	public ResponseDTO() {
	}

	/**
	 * Instantiates a new response DTO.
	 *
	 * @param traceInfo the trace info
	 */
	public ResponseDTO(final LogTraceInfoDTO traceInfo) {
		traceID = traceInfo.getTraceID();
		spanID = traceInfo.getSpanID(); 
	}
	
	/**
	 * Instantiates a new response DTO.
	 *
	 * @param traceInfo the trace info
	 * @param errorCode the error code
	 * @param errorMsg the error msg
	 */
	public ResponseDTO(final LogTraceInfoDTO traceInfo, final Integer errorCode, final String errorMsg) {
		traceID = traceInfo.getTraceID();
		spanID = traceInfo.getSpanID();
		error = new ErrorResponseDTO(errorCode, errorMsg); 
	}
	
}
