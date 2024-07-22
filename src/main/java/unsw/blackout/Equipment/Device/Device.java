package unsw.blackout.Equipment.Device;

import java.util.List;
import java.util.stream.Collectors;

import unsw.blackout.EquipmentManager;
import unsw.blackout.Surface;
import unsw.blackout.Equipment.Equipment;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Device extends Equipment {

    public Device(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
        setDirection(MathsHelper.ANTI_CLOCKWISE);
    }

    public List<String> findInRange(EquipmentManager equipments) {
        List<String> inRange = super.findInRange(equipments);
        List<String> deviceIds = equipments.getAll().stream()
        .filter(equipment -> equipment.isDevice())
            .map(Device.class::cast)
            .map(Device::getId)
            .collect(Collectors.toList());

        inRange.removeAll(deviceIds);

        return inRange;
    }

    @Override
    public void move(Surface surface) {
        super.move();
        double newHeight = surface.getHeight(getPosition().toDegrees());
        setHeight(newHeight);
    }

    @Override
    public boolean isDevice() {
        return true;
    }
}
