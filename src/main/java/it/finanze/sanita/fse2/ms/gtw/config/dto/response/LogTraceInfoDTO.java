/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import it.finanze.sanita.fse2.ms.gtw.config.dto.AbstractDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *	Trace info.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LogTraceInfoDTO extends AbstractDTO {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 2740206900821421907L;

	/**
	 * Span.
	 */
	private final String spanID;
	
	/**
	 * Trace.
	 */
	private final String traceID;

}
