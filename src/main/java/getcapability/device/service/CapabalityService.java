package getcapability.device.service;

import getcapability.device.model.CapabilityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CapabalityService {

    @Autowired
    private RestTemplate restTemplate;

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

    public void sendDeviceCapabilitiesToServer() {
        List<CapabilityModel> devices = getConnectedDevices();

        if (!devices.isEmpty()) {
        	//String serverUrl = "http://3.104.78.45/adbDevice";
        	 String serverUrl = "http://ec2-3-104-78-45.ap-southeast-2.compute.amazonaws.com/adbDevice";
            try {
                restTemplate.postForObject(serverUrl, devices, String.class);
                System.out.println("Device capabilities sent successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to send device capabilities: " + e.getMessage());
            }
        } else {
            System.out.println("No devices connected.");
        }
    }
}
