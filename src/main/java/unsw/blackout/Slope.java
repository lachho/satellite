package unsw.blackout;

public class Slope {
    private int start;
    private int end;
    private int gradient;
    private double startHeight;

    public Slope(int start, int end, int gradient, double startHeight) {
        this.start = start;
        this.end = end;
        this.gradient = gradient;
        this.startHeight = startHeight;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getGradient() {
        return gradient;
    }

    public double getStartHeight() {
        return startHeight;
    }

    public double getDistance(double position) {
        double dist = position - start;
        if (dist < 0) {
            dist += 360;
        }

        return dist;
    }

    public double getHeight(double position) {
        return startHeight + gradient * getDistance(position);
    }

    public boolean liesBetween(double position) {
        if (start > end) {
            return start <= position || end >= position;
        } else {
            return start <= position && end >= position;
        }
    }

    public double getEndHeight() {
        return getHeight(end);
    }
}
