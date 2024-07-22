package unsw.blackout;

import java.util.List;

import unsw.blackout.Equipment.Equipment;

public class FileTransfer {
    private File file;
    private Equipment sender;
    private Equipment receiver;
    private int progress;

    public FileTransfer(File file, Equipment sender, Equipment receiver) {
        this.file = file;
        this.sender = sender;
        this.receiver = receiver;
        this.progress = 0;
        receiver.addEmptyFile(file.getName(), file.getContent());
    }

    public boolean transfer(int newPosition, List<String> communicableEntitiesInRange) {
        if (!communicableEntitiesInRange.contains(getReceiver().getId())) {
            sender.senderOutOfRange(file, receiver);
            receiver.receiverOutOfRange(file, sender);
            return false;
        }

        int endIndex = Math.min(newPosition, file.getContent().length());
        receiver.appendFile(file.getName(), file.getContent().substring(getProgress(), endIndex));

        setProgress(newPosition);

        if (newPosition >= file.getFileSize()) {
            receiver.updateFileComplete(file.getName(), true);
            return false;
        }
        return true;
    }

    public List<String> communicableEntitiesInRange(EquipmentManager equipments) {
        return getSender().findInRange(equipments);
    }


    public Equipment getSender() {
        return sender;
    }

    public Equipment getReceiver() {
        return receiver;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public File getFile() {
        return file;
    }
}
