/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.enums;

import lombok.Getter;

/**
 * Enum of configuration items categories.
 */
public enum ConfigItemTypeEnum {
    
    GENERIC("GENERIC"),
    GARBAGE("GARBAGE"),
    STATUS_MANAGER("STATUS_MANAGER");

    @Getter
    private String name;

    ConfigItemTypeEnum(final String inName) {
        name = inName;
    }
}
