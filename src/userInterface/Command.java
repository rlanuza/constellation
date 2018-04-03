/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import orbitEngine.routes.Route;

public class Command extends LineConvert {

    public static double MIN_SPEED;
    public static double MAX_SPEED;
    public static double STEP_SPEED;
    public static String NAME;
    public static double MASS;
    public static double RADIUS;
    public static long MIN_LAUNCH_TIME;
    public static long MAX_LAUNCH_TIME;
    public static long STEP_LAUNCH_TIME;
    public static Route route;
    public static String ORIGIN;
    public static String DESTINATION;

    static void loadCommand(String commandFile) {
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(commandFile)));
        } catch (IOException ex) {
            System.out.printf("Error [%s]  reading: %s\n", ex.toString(), commandFile);
            System.exit(1);
        }

        // Load all command from file
        String[] lines = contents.split("\\r?\\n|\\r");;
        for (String lineRaw : lines) {
            String line = lineRaw.trim();
            if ((line.length() == 0) || line.startsWith("#")) {
                // This is a comment
            } else if (line.startsWith("MIN_SPEED:")) {
                Command.MIN_SPEED = getDouble(line, 1.0);
            } else if (line.startsWith("MAX_SPEED:")) {
                Command.MAX_SPEED = getDouble(line, 1000.0);
            } else if (line.startsWith("STEP_SPEED:")) {
                Command.STEP_SPEED = getDouble(line, 5.0);
            } else if (line.startsWith("NAME:")) {
                Command.NAME = getString(line, "Rocket");
            } else if (line.startsWith("MASS:")) {
                Command.MASS = getDouble(line, 1000.0);
            } else if (line.startsWith("RADIUS:")) {
                Command.RADIUS = getDouble(line, 5.0);
            } else if (line.startsWith("MIN_LAUNCH_TIME:")) {
                Command.MIN_LAUNCH_TIME = getLong(line, 1520294400); //Epoc of 2018-Mar-06 00:00:00.0000 TDB)
            } else if (line.startsWith("MAX_LAUNCH_TIME:")) {
                Command.MAX_LAUNCH_TIME = getLong(line, 1551830400);
            } else if (line.startsWith("STEP_LAUNCH_TIME:")) {
                Command.STEP_LAUNCH_TIME = getLong(line, 3600);
            } else if (line.startsWith("ORIGIN:")) {
                Command.ORIGIN = getString(line, "Earth");
            } else if (line.startsWith("DESTINATION:")) {
                Command.NAME = getString(line, "Mars");
            } else {
                System.out.println("Line not processed: " + line);
            }
        }
        //@Todo create a new route to test route = new Route()
    }
}
