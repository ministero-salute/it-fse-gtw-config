/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import java.util.List;

import javax.validation.constraints.Size;

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

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 8557935099846761267L;

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
