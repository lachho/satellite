package blackout;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class MyTests {
    @Test
    public void testDelete() {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices and deletes them
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        controller.removeDevice("DeviceA");
        controller.removeDevice("DeviceB");
        controller.removeDevice("DeviceC");
        controller.removeSatellite("Satellite1");

        // check it actually got deleted
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.listDeviceIds());

    }

    @Test
    public void testMovingFullRevolutions() {
        // tests functionality if crossing the 0 radians point (for angle to stay in range)
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(1));

        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(358.95), 100 + RADIUS_OF_JUPITER,
            "StandardSatellite"), controller.getInfo("Satellite1"));

        // simulate 1 full revolution
        controller.simulate(176);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(358.87), 100 + RADIUS_OF_JUPITER,
            "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testRelayStartOutOfRange() {
        // tests functionality if crossing the 0 radians point (for angle to stay in range)
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(345));
        controller.simulate(127);
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(140)) > 0);
        // out of bounds at 190
        controller.simulate(40);
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(190)) > 0);
        // should turn back
        controller.simulate();
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(190)) < 0);
    }

    @Test
    public void testTeleportingStartOutOfRange() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 100 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(359));

        controller.simulate(3);
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(0)) > 0);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(219);
        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(0)) == 0);
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);

        // Verify changed direction
        controller.simulate();
        assertTrue(controller.getInfo("Satellite1").getPosition().compareTo(Angle.fromDegrees(350)) > 0);
    }

    @Test
    public void testDesktopNotFound() {
        // testing that standard and desktop cannot see each other
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(315));

        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(320));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "DeviceB"),
            controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "Satellite1"),
            controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite1"),
            controller.communicableEntitiesInRange("DeviceB"));
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceC"));
    }

    @Test
    public void testRelayFinding() {
        // testing that standard and desktop cannot see each other
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(302));
        controller.createSatellite("Satellite2", "RelaySatellite", 20000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(349));
        controller.createSatellite("Satellite3", "RelaySatellite", 20000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(34));

        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(14));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(333));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(65));
        controller.createDevice("DeviceE", "DesktopDevice", Angle.fromDegrees(165));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3", "DeviceB", "DeviceD"),
                controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("DeviceC"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3", "Satellite1"),
                controller.communicableEntitiesInRange("DeviceB"));
    }

    @Test
    public void testExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(330));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(315));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        controller.addFileToDevice("DeviceC", "File2", "tot");

        // file does not exist
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
            () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));

        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate();

        // file transfer in progress
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
            () -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));

        // bandwith full for receive
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
            () -> controller.sendFile("File2", "DeviceC", "Satellite1"));

        controller.simulate(msg.length());

        // file already exist on sender
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
            () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));

        controller.addFileToDevice("DeviceC", "File3", "80characters".repeat(7));
        // no storage space
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
            () -> controller.sendFile("File3", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceC", "Satellite1"));
        controller.simulate(3);
        controller.addFileToDevice("DeviceC", "File4", "womp");
        assertDoesNotThrow(() -> controller.sendFile("File4", "DeviceC", "Satellite1"));
        controller.simulate(5);
        controller.addFileToDevice("DeviceC", "File5", "hehe");

        // too many files
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
            () -> controller.sendFile("File5", "DeviceC", "Satellite1"));
        }

    @Test
    public void testMultipleFileSends() {

        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(300));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Heyhi";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        controller.addFileToDevice("DeviceC", "File3", "80characters".repeat(7));
        controller.addFileToDevice("DeviceC", "File4", "wompe");
        controller.addFileToDevice("DeviceC", "File5", "hehee");

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite2"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
            controller.getInfo("Satellite2").getFiles().get("FileAlpha"));

        controller.simulate();

        // send one file really fast
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
            controller.getInfo("Satellite2").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("File3", "DeviceC", "Satellite2"));
        assertDoesNotThrow(() -> controller.sendFile("File4", "DeviceC", "Satellite2"));
        assertDoesNotThrow(() -> controller.sendFile("File5", "DeviceC", "Satellite2"));
        assertEquals(new FileInfoResponse("File3", "", 84, false),
            controller.getInfo("Satellite2").getFiles().get("File3"));

        controller.simulate();
        // check slowing of bandwith
        assertEquals(new FileInfoResponse("File3", "80cha", 84, false),
            controller.getInfo("Satellite2").getFiles().get("File3"));

        controller.simulate();

        // bandwidth increases for 2nd minute since others r done
        assertEquals(new FileInfoResponse("File3", "80characters80charac", 84, false),
            controller.getInfo("Satellite2").getFiles().get("File3"));

        assertDoesNotThrow(() -> controller.sendFile("File5", "Satellite2", "Satellite1"));
        controller.simulate();

        // check bottleneck occurs as 1 byte due to standard satellite
        assertEquals(new FileInfoResponse("File5", "h", 5, false),
            controller.getInfo("Satellite1").getFiles().get("File5"));
    }

    @Test
    public void testDeviceOutOfRange() {

        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(290));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.addFileToDevice("DeviceB", "File3", "80characters".repeat(6));

        assertDoesNotThrow(() -> controller.sendFile("File3", "DeviceB", "Satellite1"));
        assertEquals(new FileInfoResponse("File3", "", 72, false),
            controller.getInfo("Satellite1").getFiles().get("File3"));

        controller.simulate();
        assertEquals(new FileInfoResponse("File3", "8", 72, false),
            controller.getInfo("Satellite1").getFiles().get("File3"));

        controller.simulate(6);
        // should be out of range now

        assertEquals(0, controller.getInfo("Satellite1").getFiles().size());

        controller.simulate();
    }

    @Test
    public void testTeleportTransfer() {

        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(190));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(176));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.addFileToDevice("DeviceB", "File3", "trytentyrt".repeat(15));
        controller.addFileToDevice("DeviceB", "File2", "portywortet");

        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceB", "Satellite2"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File2", "portywortet", 11, true),
            controller.getInfo("Satellite2").getFiles().get("File2"));

        assertDoesNotThrow(() -> controller.sendFile("File2", "Satellite2", "Satellite1"));
        assertDoesNotThrow(() -> controller.sendFile("File3", "DeviceB", "Satellite2"));

        controller.simulate(5);
        assertEquals(new FileInfoResponse("File2", "portywore", 9, true),
            controller.getInfo("Satellite1").getFiles().get("File2"));
        assertEquals(new FileInfoResponse("File3", "ryenyr".repeat(15), 150, true),
            controller.getInfo("DeviceB").getFiles().get("File3"));
    }

    @Test
    public void testMovingDevice() {
        // tests functionality if crossing the 0 radians point (for angle to stay in range)
        BlackoutController controller = new BlackoutController();

        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(359), true);
        controller.createDevice("Device2", "HandheldDevice", Angle.fromDegrees(359), false);
        controller.simulate();

        // device1 some movement
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(359.04), RADIUS_OF_JUPITER,
            "HandheldDevice"), controller.getInfo("Device1"));

        // simulate crossing the 0 degree mark
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(0.02), RADIUS_OF_JUPITER,
            "HandheldDevice"), controller.getInfo("Device1"));
        // device2 no movement
        assertEquals(new EntityInfoResponse("Device2", Angle.fromDegrees(359), RADIUS_OF_JUPITER,
            "HandheldDevice"), controller.getInfo("Device2"));
    }

    @Test
    public void testSlope() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(358), true);
        controller.createSlope(358, 7, 1);
        controller.createSlope(7, 20, -3);
        controller.simulate(25);

        // device1 some movement
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(359.0244), RADIUS_OF_JUPITER + 1.0244,
            "HandheldDevice"), controller.getInfo("Device1"));

        // device should be near top of peak now
        controller.simulate(180);
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(6.39992), RADIUS_OF_JUPITER + 8.39992,
            "HandheldDevice"), controller.getInfo("Device1"));

        // check 2nd slope starts up high, and height should be lower than previous check
        controller.simulate(25);
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(7.4242), RADIUS_OF_JUPITER + 7.7273,
            "HandheldDevice"), controller.getInfo("Device1"));

        // after slope returns to radius, height should not go below radius
        controller.simulate(100);
        assertEquals(new EntityInfoResponse("Device1", Angle.fromDegrees(11.5219), RADIUS_OF_JUPITER,
            "HandheldDevice"), controller.getInfo("Device1"));
    }

}
