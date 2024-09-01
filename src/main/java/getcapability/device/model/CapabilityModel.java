package getcapability.device.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CapabilityModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String deviceName;
    private String model;
    private String brand;
    private String androidVersion;
    private String deviceSerial;
}
