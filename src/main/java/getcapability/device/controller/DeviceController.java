package getcapability.device.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import getcapability.device.model.CapabilityModel;
import getcapability.device.service.CapabalityService;

@RestController
public class DeviceController {

    @Autowired
    private CapabalityService capabilityService;

    @GetMapping("/adbDevice")
    public List<CapabilityModel> getDeviceCapabilities() {
        return capabilityService.getConnectedDevices();
    }
}
