/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.enums;


import lombok.Getter;

public enum OperationLogEnum implements ILogEnum {

	ADD_CONFIG_PROPERTIES("ADD-PROPS", "Adding Configuration properties to the gtw-config-db"),
	REMOVE_CONFIG_PROPERTIES("REMOVE-PROPS", "Removing Configuration properties from the gtw-config-db"),
	UPDATE_CONFIG_PROPERTIES("UPDATE-PROPS", "Updating Configuration properties from the gtw-config-db");

	@Getter
	private String code;

	@Getter
	private String description;

	private OperationLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

}
