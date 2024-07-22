package unsw.blackout.Equipment.Satellite;

import unsw.utils.Angle;
import unsw.utils.Constants;
import unsw.utils.MathsHelper;

public class RelaySatellite extends Satellite {
    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setRange(Constants.RELAY_RANGE);
        setSpeed(Constants.RELAY_SPEED);
        setCapacity(Constants.RELAY_BYTES);
    }

    @Override
    public void move() {
        if (
            getPosition().compareTo(Constants.RELAY_THRESHOLD) < 0
            && getPosition().compareTo(Constants.RELAY_TOP) >= 0
        ) {
            setDirection(MathsHelper.CLOCKWISE);
        } else if (
            getPosition().compareTo(Constants.RELAY_THRESHOLD) >= 0
            || getPosition().compareTo(Constants.RELAY_BOTTOM) <= 0
        ) {
            setDirection(MathsHelper.ANTI_CLOCKWISE);
        }

        super.move();
    }
}
