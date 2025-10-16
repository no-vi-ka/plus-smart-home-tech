package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductDto {
    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;
}