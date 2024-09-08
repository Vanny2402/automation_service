package com.vanny.Automateapi.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vanny.Automateapi.model.CapabilityModel;

@Service
public class CapabalityService{

	public List<CapabilityModel> getConnectedDevices() {
		List<CapabilityModel> devices = new ArrayList<>();
		try {
			// Command to list connected devices
			Process process = Runtime.getRuntime().exec("adb devices -l");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("model:")) {
					// Parse the device information
					CapabilityModel device = parseDeviceInfo(line);
					devices.add(device);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return devices;
	}

	private CapabilityModel parseDeviceInfo(String adbOutput) {
		CapabilityModel device = new CapabilityModel();
		String[] details = adbOutput.split("\\s+");

		for (String detail : details) {
			if (detail.startsWith("device:")) {
				device.setDeviceName(detail.split(":")[1]);
			} else if (detail.startsWith("model:")) {
				device.setModel(detail.split(":")[1]);
			} else if (detail.startsWith("brand:")) {
				device.setBrand(detail.split(":")[1]);
			} else if (detail.startsWith("device")) {
				device.setDeviceSerial(details[0]); 
			}
		}
		// Fetch Android version
		try {
			Process process = Runtime.getRuntime().exec("adb -s " + device.getDeviceSerial() + " shell getprop ro.build.version.release");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String androidVersion = reader.readLine();
			device.setAndroidVersion(androidVersion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return device;
	}
}
