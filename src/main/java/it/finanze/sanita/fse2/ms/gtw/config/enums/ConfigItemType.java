/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.enums;

import lombok.Getter;

/**
 * Enum of configuration items categories.
 * 
 * @author Simone Lungarella
 */
public enum ConfigItemType {
    
    GENERIC("GENERIC"),
    GARBAGE("GARBAGE");

    @Getter
    private String name;

    ConfigItemType(final String inName) {
        name = inName;
    }
}
