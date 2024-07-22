package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unsw.blackout.Equipment.Equipment;
import unsw.blackout.Equipment.Device.Device;
import unsw.blackout.Equipment.Satellite.Satellite;
import unsw.response.models.EntityInfoResponse;

public class EquipmentManager {
    private ArrayList<Equipment> equipments;

    public EquipmentManager() {
        equipments = new ArrayList<Equipment>();
    }

    public Equipment getEquipment(String id) {
        for (Equipment equipment : equipments) {
            if (equipment.getId().equals(id)) {
                return equipment;
            }
        }
        return null;
    }

    public ArrayList<Equipment> getAll() {
        return equipments;
    }

    public void add(Equipment equipment) {
        equipments.add(equipment);
    }

    public void remove(String id) {
        equipments.remove(getEquipment(id));
    }

    public List<String> listDeviceIds() {
        return equipments.stream()
            .filter(equipment -> equipment.isDevice())
            .map(Device.class::cast)
            .map(Device::getId)
            .collect(Collectors.toList());
    }

    public List<String> listSatelliteIds() {
        return equipments.stream()
            .filter(equipment -> equipment.isSatellite())
            .map(Satellite.class::cast)
            .map(Satellite::getId)
            .collect(Collectors.toList());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        getEquipment(deviceId).addFile(filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        return getEquipment(id).getInfo();
    }

    public List<String> communicableEntitiesInRange(String id) {
        return getEquipment(id).findInRange(this);
    }

    public File getFile(String id, String fileName) {
        return getEquipment(id).getFile(fileName);
    }
}
