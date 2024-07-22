package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.blackout.Equipment.Equipment;
import unsw.blackout.Equipment.Device.DesktopDevice;
import unsw.blackout.Equipment.Device.HandheldDevice;
import unsw.blackout.Equipment.Device.LaptopDevice;
import unsw.blackout.Equipment.Satellite.RelaySatellite;
import unsw.blackout.Equipment.Satellite.StandardSatellite;
import unsw.blackout.Equipment.Satellite.TeleportingSatellite;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import unsw.utils.Constants;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private EquipmentManager equipments;
    private FileTransferManager transfers;
    private Surface surface;

    public BlackoutController() {
        equipments = new EquipmentManager();
        transfers = new FileTransferManager();
        surface = new Surface();
    }

    public void createDevice(String deviceId, String type, Angle position) {
        createDevice(deviceId, type, position, false);
    }

    public void removeDevice(String deviceId) {
        equipments.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        if (type.equals(Constants.STANDARD)) {
            equipments.add(new StandardSatellite(satelliteId, type, height, position));
        } else if (type.equals(Constants.TELEPORTING)) {
            equipments.add(new TeleportingSatellite(satelliteId, type, height, position));
        } else if (type.equals(Constants.RELAY)) {
            equipments.add(new RelaySatellite(satelliteId, type, height, position));
        }
    }

    public void removeSatellite(String satelliteId) {
        equipments.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return equipments.listDeviceIds();
    }

    public List<String> listSatelliteIds() {
        return equipments.listSatelliteIds();
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        equipments.addFileToDevice(deviceId, filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        return equipments.getInfo(id);
    }

    public void simulate() {
        for (Equipment equipment : equipments.getAll()) {
            equipment.move(surface);
        }

        List<FileTransfer> transfersCopy = new ArrayList<>(transfers.getTransfers());

        for (FileTransfer transfer : transfersCopy) {
            List<String> communicableEntitiesInRange = communicableEntitiesInRange(transfer.getSender().getId());
            if (!transfers.simulate(transfer, communicableEntitiesInRange)) {
                transfers.remove(transfer);
            }
        }
    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        return equipments.communicableEntitiesInRange(id);
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        File file = equipments.getFile(fromId, fileName);
        Equipment sender = equipments.getEquipment(fromId);
        Equipment receiver = equipments.getEquipment(toId);

        if (file == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        transfers.sendFileErrorCheck(file, sender, receiver);
        transfers.add(new FileTransfer(file, sender, receiver));
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        if (type.equals(Constants.HANDHELD)) {
            equipments.add(new HandheldDevice(deviceId, type, position, isMoving));
        } else if (type.equals(Constants.LAPTOP)) {
            equipments.add(new LaptopDevice(deviceId, type, position, isMoving));
        } else if (type.equals(Constants.DESKTOP)) {
            equipments.add(new DesktopDevice(deviceId, type, position, isMoving));
        }
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        surface.addSlope(startAngle, endAngle, gradient);
    }
}
