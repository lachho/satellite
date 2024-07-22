package unsw.utils;

public final class Constants {
    public static final String HANDHELD = "HandheldDevice";
    public static final String LAPTOP = "LaptopDevice";
    public static final String DESKTOP = "DesktopDevice";

    public static final double HANDHELD_RANGE = 50_000;
    public static final double LAPTOP_RANGE = 100_000;
    public static final double DESKTOP_RANGE = 200_000;
    public static final double HANDHELD_SPEED = 50;
    public static final double LAPTOP_SPEED = 30;
    public static final double DESKTOP_SPEED = 20;
    public static final double INFINITY = Double.POSITIVE_INFINITY;

    public static final String STANDARD = "StandardSatellite";
    public static final String TELEPORTING = "TeleportingSatellite";
    public static final String RELAY = "RelaySatellite";

    public static final double STANDARD_RANGE = 150_000;
    public static final double STANDARD_SPEED = 2_500;
    public static final double STANDARD_FILES = 3;
    public static final double STANDARD_BYTES = 80;
    public static final double STANDARD_RECEIVE = 1;
    public static final double STANDARD_SEND = 1;

    public static final double TELEPORTING_RANGE = 200_000;
    public static final double TELEPORTING_SPEED = 1_000;
    public static final double TELEPORTING_BYTES = 200;
    public static final double TELEPORTING_RECEIVE = 15;
    public static final double TELEPORTING_SEND = 10;
    public static final Angle TELEPORTING_START = Angle.fromDegrees(0);
    public static final Angle TELEPORTING_END = Angle.fromDegrees(180);

    public static final double RELAY_RANGE = 200_000;
    public static final double RELAY_SPEED = 1_500;
    public static final double RELAY_BYTES = 0;
    public static final double RELAY_RECEIVE = Double.POSITIVE_INFINITY;
    public static final double RELAY_SEND = Double.POSITIVE_INFINITY;
    public static final Angle RELAY_THRESHOLD = Angle.fromDegrees(345);
    public static final Angle RELAY_TOP = Angle.fromDegrees(190);
    public static final Angle RELAY_BOTTOM = Angle.fromDegrees(140);

    public static final double REVOLUTION = 2 * Math.PI;

}
