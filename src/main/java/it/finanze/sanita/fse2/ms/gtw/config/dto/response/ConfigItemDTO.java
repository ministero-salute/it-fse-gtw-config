/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import java.util.List;

import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object that organize the response.
 */
@Getter
@Setter
@NoArgsConstructor
public class ConfigItemDTO extends ResponseDTO {

	@ArraySchema(minItems = 0, maxItems = 10000, schema = @Schema(implementation = ConfigItemETY.class))
    List<ConfigItemETY> configurationItems;

    @Size(min = 0, max = 10000)
    @Schema(description = "Numero di elementi di configurazione")
    Integer size;

    public ConfigItemDTO(final LogTraceInfoDTO traceInfo, final List<ConfigItemETY> inConfigurationItems, final Integer inSize) {
        super(traceInfo);
        configurationItems = inConfigurationItems;
        size = inSize;
    }
}
