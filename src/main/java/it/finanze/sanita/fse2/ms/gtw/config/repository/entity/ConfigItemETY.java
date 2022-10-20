/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.repository.entity;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity that handles configuration items.
 * 
 * @author Simone Lungarella
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("#{@configDataBean}")
public class ConfigItemETY {

    @Field("key")
	@Schema(minLength = 0, maxLength = 1000, description = "Categoria dei configuration items")
    private String key;

    @Field("config_items")
	@Schema(minLength = 0, maxLength = 10000, description = "Mappa chiave-valore dei configuration items")
    private Map<String, String> items;
}
