/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.controller.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.LogTraceInfoDTO;

/**
 * 
 * @author CPIERASC
 *
 *	Abstract controller.
 */
public abstract class AbstractCTL implements Serializable {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 6251845150968743934L;
	
	@Autowired
	private Tracer tracer;

	protected LogTraceInfoDTO getLogTraceInfo() {
		LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
		if (tracer.currentSpan() != null) {
			out = new LogTraceInfoDTO(
					tracer.currentSpan().context().spanIdString(), 
					tracer.currentSpan().context().traceIdString());
		}
		return out;
	}

}
