package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String name;
    private String content;
    private int fileSize;
    private boolean isFileComplete;

    public File(String name, String content, int length, boolean isFileComplete) {
        this.name = name;
        this.content = content;
        this.isFileComplete = isFileComplete;
        this.fileSize = length;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content.toString();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void appendFile(String content) {
        setContent(getContent() + content);
    }

    public int getFileSize() {
        return fileSize;
    }


    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isFileComplete() {
        return isFileComplete;
    }

    public void updateFileForTeleport() {
        updateFileComplete(true);
        setFileSize(content.length());
    }

    public void updateFileComplete(boolean isFileComplete) {
        this.isFileComplete = isFileComplete;
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(getName(), getContent(), getFileSize(), isFileComplete());
    }

    public void replaceT() {
        setContent(getContent().replace("t", ""));
    }

}
