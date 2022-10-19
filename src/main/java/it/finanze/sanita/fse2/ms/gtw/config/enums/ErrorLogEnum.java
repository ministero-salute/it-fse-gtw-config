/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.enums;


import lombok.Getter;

public enum ErrorLogEnum implements ILogEnum {

	KO_GENERIC("KO-GENERIC", "Errore generico"); 

	@Getter
	private String code;

	@Getter
	private String description;

	private ErrorLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

}
