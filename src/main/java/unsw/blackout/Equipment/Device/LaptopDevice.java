package unsw.blackout.Equipment.Device;

import unsw.utils.Angle;
import unsw.utils.Constants;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, String type, Angle position, boolean isMoving) {
        super(deviceId, type, position);
        setRange(Constants.LAPTOP_RANGE);
        if (isMoving) {
            setSpeed(Constants.LAPTOP_SPEED);
        }
    }
}
