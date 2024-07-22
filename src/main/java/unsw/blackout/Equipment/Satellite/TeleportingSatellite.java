package unsw.blackout.Equipment.Satellite;

import unsw.blackout.File;
import unsw.blackout.Equipment.Equipment;
import unsw.utils.Angle;
import unsw.utils.Constants;
import unsw.utils.MathsHelper;

public class TeleportingSatellite extends Satellite {
    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        super.setRange(Constants.TELEPORTING_RANGE);
        setSpeed(Constants.TELEPORTING_SPEED);
        setDirection(MathsHelper.ANTI_CLOCKWISE);
        setCapacity(Constants.TELEPORTING_BYTES);
        setReceive(Constants.TELEPORTING_RECEIVE);
        setSend(Constants.TELEPORTING_SEND);
    }

    @Override
    public void move() {
        // check if crossing the 359->0 degree boundary, to not teleport.
        if (getDirection() == MathsHelper.ANTI_CLOCKWISE && newPosition().compareTo(getPosition()) <= 0) {
            super.move();
        } else if (crossThreshold(Constants.TELEPORTING_END) && !crossThreshold(Constants.TELEPORTING_START)) {
            setPosition(Constants.TELEPORTING_START);
            setDirection(getDirection() * -1);
        } else {
            super.move();
        }
    }

    public void senderOutOfRange(File file, Equipment receiver) {
        if (getPosition() == Constants.TELEPORTING_START) {
            removeTAndKeep(file, receiver);
        } else {
            super.senderOutOfRange(file, receiver);
        }
    }

    public void receiverOutOfRange(File file, Equipment sender) {
        if (getPosition() != Constants.TELEPORTING_START) {
            super.receiverOutOfRange(file, sender);
        } else if (sender.isDevice()) {
            removeTAndCancel(file);
            super.receiverOutOfRange(file, sender);
        } else if (sender.isSatellite()) {
            removeTAndKeep(file, this);
        }

    }

    public void removeTAndKeep(File file, Equipment receiver) {
        int endIndex = receiver.getFileContent(file.getName()).length();
        String newContent = file.getContent().substring(endIndex).replace("t", "");
        receiver.appendFile(file.getName(), newContent);
        receiver.updateFileForTeleport(file.getName());
    }

    public void removeTAndCancel(File file) {
        if (getPosition() == Constants.TELEPORTING_START) {
            file.replaceT();
        }
    }
}

