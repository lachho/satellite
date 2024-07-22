package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.blackout.Equipment.Equipment;

public class FileTransferManager {
    private ArrayList<FileTransfer> transfers;

    public FileTransferManager() {
        this.transfers = new ArrayList<FileTransfer>();
    }

    public void add(FileTransfer transfer) {
        transfers.add(transfer);
    }

    public ArrayList<FileTransfer> getTransfers() {
        return transfers;
    }

    public void remove(FileTransfer transfer) {
        transfers.remove(transfer);
    }

    public int calculateBandwith(double bandwith, int transferCount) {
        return (int) Math.floor(bandwith / transferCount);
    }

    public int calculateReceive(Equipment equipment) {
        return calculateBandwith(equipment.getReceive(), countReceives(equipment));
    }

    public int calculateSend(Equipment equipment) {
        return calculateBandwith(equipment.getSend(), countReceives(equipment));
    }

    public int countSends(Equipment equipment) {
        return (int) transfers.stream().filter(transfer -> transfer.getSender().equals(equipment)).count();
    }

    public int countReceives(Equipment equipment) {
        return (int) transfers.stream().filter(transfer -> transfer.getReceiver().equals(equipment)).count();
    }

    public void sendFileErrorCheck(File file, Equipment sender, Equipment receiver)
        throws FileTransferException {
        if (!file.isFileComplete()) {
            throw new FileTransferException.VirtualFileNotFoundException(file.getName());
        } else if (countSends(sender) >= sender.getSend()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(receiver.getId());
        } else if (countReceives(receiver) >= receiver.getReceive()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(sender.getId());
        }

        receiver.sendFileErrorCheck(file);
    }

    public boolean simulate(FileTransfer transfer, List<String> communicableEntitiesInRange) {
        int newPosition = transfer.getProgress() + Math.min(calculateSend(transfer.getSender()),
            calculateReceive(transfer.getReceiver()));

        return transfer.transfer(newPosition, communicableEntitiesInRange);
    }
}
