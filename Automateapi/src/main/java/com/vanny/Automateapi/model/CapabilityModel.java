package com.vanny.Automateapi.model;

import lombok.Data;

@Data
public class CapabilityModel {
	private Long id;
    private String deviceName;
    private String model;
    private String brand;
    private String androidVersion;
    private String deviceSerial;
}
