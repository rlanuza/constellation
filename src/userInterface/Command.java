package userInterface;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Command extends LineConvert {

    public String NAME = "Rocket";
    public double MASS = 1000;
    public double RADIUS = 5.0;
    public Color COLOR = Color.ORANGE;
    public String ORIGIN = "Earth";
    public String TARGET = "Mars";
    public double MIN_SPEED = 1;
    public double MAX_SPEED = 1000;
    public double STEP_SPEED = 5;
    public double UNDER_STEP_SPEED_FACTOR = 10.0;
    public double OVER_STEP_SPEED_FACTOR = 10.0;
    public long MIN_LAUNCH_TIME = 1520294400;
    public long MAX_LAUNCH_TIME = 1551830400;
    public long STEP_LAUNCH_TIME = 3600;
    public double OVER_STEP_TIME_FACTOR = 10.0;
    public int STEPS_LIMIT_ON_CANDIDATE = 200;
    public double LAUNCH_CORRECTION_FACTOR = 3.0;
    public double LAUNCH_ELEVATION = 1;
    public double OVERTAKE_TOLERANCE_RADIUS = 40.0;
    public double MAX_OVERTAKE_RADIUS = 1000.0;
    public String CSV_DELIMITER = ",";
    public String CSV_DECIMAL_POINT = ".";

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
                NAME = getString(line, NAME);
            } else if (line.startsWith("MASS:")) {
                MASS = getDouble(line, MASS);
            } else if (line.startsWith("RADIUS:")) {
                RADIUS = getDouble(line, RADIUS);
            } else if (line.startsWith("COLOR:")) {
                COLOR = getColor(line, COLOR);
            } else if (line.startsWith("ORIGIN:")) {
                ORIGIN = getString(line, ORIGIN);
            } else if (line.startsWith("TARGET:")) {
                TARGET = getString(line, TARGET);
            } else if (line.startsWith("MIN_SPEED:")) {
                MIN_SPEED = getDouble(line, MIN_SPEED);
            } else if (line.startsWith("MAX_SPEED:")) {
                MAX_SPEED = getDouble(line, MAX_SPEED);
            } else if (line.startsWith("STEP_SPEED:")) {
                STEP_SPEED = getDouble(line, STEP_SPEED);
            } else if (line.startsWith("UNDER_STEP_SPEED_FACTOR:")) {
                UNDER_STEP_SPEED_FACTOR = getDouble(line, UNDER_STEP_SPEED_FACTOR);
            } else if (line.startsWith("OVER_STEP_SPEED_FACTOR:")) {
                OVER_STEP_SPEED_FACTOR = getDouble(line, OVER_STEP_SPEED_FACTOR);
            } else if (line.startsWith("MIN_LAUNCH_TIME:")) {
                MIN_LAUNCH_TIME = getLong(line, MIN_LAUNCH_TIME); //Epoc of 2018-Mar-06 00:00:00.0000 TDB)
            } else if (line.startsWith("MAX_LAUNCH_TIME:")) {
                MAX_LAUNCH_TIME = getLong(line, MAX_LAUNCH_TIME);
            } else if (line.startsWith("STEP_LAUNCH_TIME:")) {
                STEP_LAUNCH_TIME = getLong(line, STEP_LAUNCH_TIME);
            } else if (line.startsWith("OVER_STEP_TIME_FACTOR:")) {
                OVER_STEP_TIME_FACTOR = getDouble(line, OVER_STEP_TIME_FACTOR);
            } else if (line.startsWith("STEPS_LIMIT_ON_CANDIDATE:")) {
                STEPS_LIMIT_ON_CANDIDATE = (int) getLong(line, STEPS_LIMIT_ON_CANDIDATE);
            } else if (line.startsWith("LAUNCH_CORRECTION_FACTOR:")) {
                LAUNCH_CORRECTION_FACTOR = getDouble(line, LAUNCH_CORRECTION_FACTOR);
            } else if (line.startsWith("LAUNCH_ELEVATION:")) {
                LAUNCH_ELEVATION = getDouble(line, LAUNCH_ELEVATION);
            } else if (line.startsWith("OVERTAKE_TOLERANCE_RADIUS:")) {
                OVERTAKE_TOLERANCE_RADIUS = getDouble(line, OVERTAKE_TOLERANCE_RADIUS);
            } else if (line.startsWith("MAX_OVERTAKE_RADIUS:")) {
                MAX_OVERTAKE_RADIUS = getDouble(line, MAX_OVERTAKE_RADIUS);
            } else if (line.startsWith("CSV_DELIMITER:")) {
                CSV_DELIMITER = getStringWithComma(line, CSV_DELIMITER);
            } else if (line.startsWith("CSV_DECIMAL_POINT:")) {
                CSV_DECIMAL_POINT = getStringWithComma(line, CSV_DECIMAL_POINT);
            } else {
                System.out.println("Line not processed: " + line);
            }
        }
    }
}
