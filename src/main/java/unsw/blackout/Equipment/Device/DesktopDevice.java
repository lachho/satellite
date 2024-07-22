package unsw.blackout.Equipment.Device;

import java.util.List;

import unsw.blackout.EquipmentManager;
import unsw.blackout.Equipment.Equipment;
import unsw.utils.Angle;
import unsw.utils.Constants;

public class DesktopDevice extends Device {
    public DesktopDevice(String deviceId, String type, Angle position, boolean isMoving) {
        super(deviceId, type, position);
        setRange(Constants.DESKTOP_RANGE);
        if (isMoving) {
            setSpeed(Constants.DESKTOP_SPEED);
        }
    }

    @Override
    public List<String> findInRange(EquipmentManager equipments) {
        List<String> inRange = super.findInRange(equipments);

        for (Equipment equipment : equipments.getAll()) {
            if (equipment.getType().equals(Constants.STANDARD)) {
                inRange.remove(equipment.getId());
            }
        }

        return inRange;
    }
}
