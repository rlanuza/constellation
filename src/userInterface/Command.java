/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Command extends LineConvert {

    public String NAME;
    public double MASS;
    public double RADIUS;
    public Color COLOR;
    public String ORIGIN;
    public String TARGET;
    public double MIN_SPEED;
    public double MAX_SPEED;
    public double STEP_SPEED;
    public boolean ITERATE_SPEED_FIRST;
    public long MIN_LAUNCH_TIME;
    public long MAX_LAUNCH_TIME;
    public long STEP_LAUNCH_TIME;

    Command(String commandFile) {
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
            } else if (line.startsWith("NAME:")) {
                this.NAME = getString(line, "Rocket");
            } else if (line.startsWith("MASS:")) {
                this.MASS = getDouble(line, 1000.0);
            } else if (line.startsWith("RADIUS:")) {
                this.RADIUS = getDouble(line, 5.0);
            } else if (line.startsWith("COLOR:")) {
                this.COLOR = getColor(line, Color.ORANGE);
            } else if (line.startsWith("ORIGIN:")) {
                this.ORIGIN = getString(line, "Earth");
            } else if (line.startsWith("TARGET:")) {
                this.TARGET = getString(line, "Mars");
            } else if (line.startsWith("MIN_SPEED:")) {
                this.MIN_SPEED = getDouble(line, 1.0);
            } else if (line.startsWith("MAX_SPEED:")) {
                this.MAX_SPEED = getDouble(line, 1000.0);
            } else if (line.startsWith("STEP_SPEED:")) {
                this.STEP_SPEED = getDouble(line, 5.0);
            } else if (line.startsWith("MIN_LAUNCH_TIME:")) {
                this.MIN_LAUNCH_TIME = getLong(line, 1520294400); //Epoc of 2018-Mar-06 00:00:00.0000 TDB)
            } else if (line.startsWith("MAX_LAUNCH_TIME:")) {
                this.MAX_LAUNCH_TIME = getLong(line, 1551830400);
            } else if (line.startsWith("STEP_LAUNCH_TIME:")) {
                this.STEP_LAUNCH_TIME = getLong(line, 3600);
            } else if (line.startsWith("ITERATE_SPEED_FIRST:")) {
                this.ITERATE_SPEED_FIRST = getBoolean(line, true);
            } else {
                System.out.println("Line not processed: " + line);
            }
        }
    }
}
