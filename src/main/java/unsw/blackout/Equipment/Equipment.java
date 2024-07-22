package unsw.blackout.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import unsw.blackout.EquipmentManager;
import unsw.blackout.File;
import unsw.blackout.FileTransferException;
import unsw.blackout.Moveable;
import unsw.blackout.Surface;
import unsw.blackout.Transferrable;
import unsw.blackout.Equipment.Satellite.TeleportingSatellite;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.Constants;
import unsw.utils.MathsHelper;

public abstract class Equipment implements Moveable, Transferrable {
    private String id;
    private String type;
    private Angle position;
    private double height;
    private double range;
    private ArrayList<File> files;
    private double capacity;
    private double receive;
    private double send;
    private double speed;
    private int direction;

    public Equipment(String id, String type, Angle position) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.height = MathsHelper.RADIUS_OF_JUPITER;
        this.range = (double) 0;
        this.files = new ArrayList<File>();
        this.capacity = Constants.INFINITY;
        this.receive = Constants.INFINITY;
        this.send = Constants.INFINITY;
        this.speed  = (double) 0;
        this.direction = MathsHelper.CLOCKWISE;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getReceive() {
        return receive;
    }

    public void setReceive(double receive) {
        this.receive = receive;
    }

    public double getSend() {
        return send;
    }

    public void setSend(double send) {
        this.send = send;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addFile(String filename, String content) {
        File file = new File(filename, content, content.length(), true);
        files.add(file);
    }

    public void addEmptyFile(String filename, String content) {
        File file = new File(filename, "", content.length(), false);
        files.add(file);
    }

    public File getFile(String filename) {
        return files.stream().filter(file -> file.getName().equals(filename)).findFirst().orElse(null);
    }

    public void removeFile(String filename) {
        File file = getFile(filename);
        if (file != null) {
            removeFile(file);
        }
    }

    public void appendFile(String filename, String content) {
        getFile(filename).appendFile(content);
    };

    public void updateFileComplete(String filename, boolean isFileComplete) {
        getFile(filename).updateFileComplete(isFileComplete);
    }

    public void removeFile(File file) {
        files.remove(file);
    }

    public void updateFileForTeleport(String filename) {
        getFile(filename).updateFileForTeleport();
    }

    public int getFileCount() {
        return getFiles().size();
    }

    public int getStorageCount() {
        return getFiles().stream().mapToInt(File::getFileSize).sum();
    }

    public String getFileContent(String filename) {
        return getFile(filename).getContent();
    }

    public EntityInfoResponse getInfo() {
        Map<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();
        for (File file : getFiles()) {
            files.put(file.getName(), file.getInfo());
        }

        return new EntityInfoResponse(getId(), getPosition(), getHeight(), getType(), files);
    }

    public boolean isTransferable(Equipment equipment) {
        return MathsHelper.isVisible(height, position, equipment.getHeight(), equipment.getPosition())
            && MathsHelper.getDistance(height, position, equipment.getHeight(), equipment.getPosition()) <= range;
    }

    public HashSet<String> findInRangeHelper(HashSet<String> inRange, EquipmentManager equipments) {
        for (Equipment equipment : equipments.getAll()) {
            if (isTransferable(equipment)) {
                if (!inRange.contains(equipment.getId()) && equipment.getType() == Constants.RELAY) {
                    inRange.add(equipment.getId());
                    inRange = equipment.findInRangeHelper(inRange, equipments);
                } else {
                    inRange.add(equipment.getId());
                }
            }
        }
        return inRange;
    }

    @Override
    public List<String> findInRange(EquipmentManager equipments) {
        HashSet<String> inRange = new HashSet<String>();
        inRange = findInRangeHelper(inRange, equipments);

        List<String> resultList = new ArrayList<>(inRange);
        resultList.remove(id);
        return resultList;
    }

    @Override
    public void sendFileErrorCheck(File file) throws FileTransferException {
        if (getFile(file.getName()) != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(file.getName());
        }
    }

    public void senderOutOfRange(File file, Equipment receiver) {
        return;
    }

    public void receiverOutOfRange(File file, Equipment sender) {
        if (!(sender.getClass().equals(TeleportingSatellite.class))) {
            removeFile(file.getName());
        }
    }

    public Angle newPosition() {
        double newAngle = position.toRadians() + (direction * speed / height * 1);
        newAngle = newAngle % Constants.REVOLUTION;

        if (newAngle < 0) {
            newAngle += Constants.REVOLUTION;
        }

        return Angle.fromRadians(newAngle);
    }

    public boolean crossThreshold(Angle threshold) {
        return (position.compareTo(threshold) * newPosition().compareTo(threshold) <= 0);
    }

    public void move(Surface surface) {
        move();
    }

    @Override
    public void move() {
        setPosition(newPosition());
    }

    public boolean isSatellite() {
        return false;
    }

    public boolean isDevice() {
        return false;
    }
}
