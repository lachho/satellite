package unsw.blackout.Equipment.Satellite;

import java.util.List;

import unsw.blackout.EquipmentManager;
import unsw.blackout.File;
import unsw.blackout.FileTransferException;
import unsw.blackout.Equipment.Equipment;
import unsw.utils.Angle;
import unsw.utils.Constants;

public class StandardSatellite extends Satellite {
    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setRange(Constants.STANDARD_RANGE);
        setSpeed(Constants.STANDARD_SPEED);
        setCapacity(Constants.STANDARD_BYTES);
        setReceive(Constants.STANDARD_RECEIVE);
        setSend(Constants.STANDARD_SEND);
    }

    @Override
    public List<String> findInRange(EquipmentManager equipments) {
        List<String> inRange = super.findInRange(equipments);

        for (Equipment equipment : equipments.getAll()) {
            if (equipment.getType().equals(Constants.DESKTOP)) {
                inRange.remove(equipment.getId());
            }
        }

        return inRange;
    }

    @Override
    public void sendFileErrorCheck(File file) throws FileTransferException {
        super.sendFileErrorCheck(file);
        if (getFileCount() >= Constants.STANDARD_FILES) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        }
    }
}
