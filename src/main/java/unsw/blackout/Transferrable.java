package unsw.blackout;

import java.util.List;

public interface Transferrable {
    public List<String> findInRange(EquipmentManager equipments);

    public void sendFileErrorCheck(File file) throws FileTransferException;
}
