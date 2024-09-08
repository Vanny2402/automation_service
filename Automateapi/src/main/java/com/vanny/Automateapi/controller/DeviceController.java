package com.vanny.Automateapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vanny.Automateapi.model.CapabilityModel;
import com.vanny.Automateapi.service.CapabalityService;

@RestController
public class DeviceController {

    @Autowired
    private CapabalityService capabilityService;

    @GetMapping("/adbDevice")
    public List<CapabilityModel> getDeviceCapabilities() {
        return capabilityService.getConnectedDevices();
    }
}
