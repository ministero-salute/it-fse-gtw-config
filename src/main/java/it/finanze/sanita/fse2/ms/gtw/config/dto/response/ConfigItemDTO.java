package it.finanze.sanita.fse2.ms.gtw.config.dto.response;

import java.util.List;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object that organize the response.
 * 
 * @author Simone Lungarella
 */
@Getter
@Setter
@NoArgsConstructor
public class ConfigItemDTO extends ResponseDTO {

    @Size(min = 0, max = 10000)
    @Schema(description = "Elementi di configurazione")
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
