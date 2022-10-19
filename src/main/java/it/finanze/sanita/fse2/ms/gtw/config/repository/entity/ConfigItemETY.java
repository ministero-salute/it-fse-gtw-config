/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.repository.entity;

import java.util.Map;

import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	@Size(min = 0, max = 1000)
    private String key;

    @Field("config_items")
	@Size(min = 0, max = 1000)
    private Map<String, String> items;
}
