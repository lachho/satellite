package unsw.blackout.Equipment.Device;

import unsw.utils.Angle;
import unsw.utils.Constants;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, String type, Angle position, boolean isMoving) {
        super(deviceId, type, position);
        super.setRange(Constants.HANDHELD_RANGE);
        if (isMoving) {
            setSpeed(Constants.HANDHELD_SPEED);
        }
    }
}
