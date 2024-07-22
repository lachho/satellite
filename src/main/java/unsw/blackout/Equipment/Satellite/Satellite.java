package unsw.blackout.Equipment.Satellite;

import unsw.blackout.File;
import unsw.blackout.FileTransferException;
import unsw.blackout.Equipment.Equipment;
import unsw.utils.Angle;

public abstract class Satellite extends Equipment {

    public Satellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, position);
        super.setHeight(height);
    }

    @Override
    public void sendFileErrorCheck(File file) throws FileTransferException {
        super.sendFileErrorCheck(file);
        if (getStorageCount() + file.getFileSize() >= getCapacity()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    @Override
    public boolean isSatellite() {
        return true;
    }
}
