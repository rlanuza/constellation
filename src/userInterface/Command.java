package userInterface;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Capture the command file requirements.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Command extends LineConvert {

    /**
     * Rocket name.
     */
    public String NAME = "Rocket";
    /**
     * Rocket mass. Unit Kg.
     */
    public double MASS = 1000;
    /**
     * Rocket radius. Unit m.
     */
    public double RADIUS = 5.0;
    /**
     * Rocket orbit color.
     */
    public Color COLOR = Color.ORANGE;
    /**
     * Rocket planet origin.
     */
    public String ORIGIN = "Earth";
    /**
     * Rocket planet target.
     */
    public String TARGET = "Mars";
    /**
     * Rocket minimum iteration speed. Unit m/s.
     */
    public double MIN_SPEED = 1;
    /**
     * Rocket maximum iteration speed. Unit m/s.
     */
    public double MAX_SPEED = 1000;
    /**
     * Rocket step iteration speed. Unit m/s.
     */
    public double STEP_SPEED = 5;
    /**
     * Under-step fine speed optimization factor used when lasts target land fails are in near distances.
     */
    public double UNDER_STEP_SPEED_FACTOR = 10.0;
    /**
     * Over-step coarse speed optimization factor used when lasts target land fails are in far distances.
     */
    public double OVER_STEP_SPEED_FACTOR = 10.0;
    /**
     * Rocket minimum launch time. Unit epoch seconds.
     */
    public long MIN_LAUNCH_TIME = 1520294400;
    /**
     * Rocket maximum launch time. Unit epoch seconds.
     */
    public long MAX_LAUNCH_TIME = 1551830400;
    /**
     * Rocket iteration speed time. Unit seconds.
     */
    public long STEP_LAUNCH_TIME = 3600;
    /**
     * Over-step coarse time optimization factor used when lasts target land fails with far distances.
     */
    public double OVER_STEP_TIME_FACTOR = 10.0;
    /**
     * Steps accepted to iterate on a candidate route with same initial planet conditions with different launch angle adjust.
     */
    public int STEPS_LIMIT_ON_CANDIDATE = 200;
    /**
     * Launch correction factor to increase the iteration adjust.
     */
    public double LAUNCH_CORRECTION_FACTOR = 3.0;
    /**
     * Launcher elevation or distance to ground of origin to avoid collision detection on launch time.
     */
    public double LAUNCH_ELEVATION = 1;
    /**
     * Tolerance that we accept to continue route iteration calculus when the rocket seems to get distance from a failed target. Unit target
     * planet radius.
     */
    public double OVERTAKE_TOLERANCE_RADIUS = 40.0;
    /**
     * Max overtake distance. When it is exceeded the iterations with different launch angle are finished. Unit target planet radius.
     */
    public double MAX_OVERTAKE_RADIUS = 1000.0;
    /**
     * CSV output files field delimiter.
     */
    public String CSV_DELIMITER = ",";
    /**
     * CSV output files float point.
     */
    public String CSV_DECIMAL_POINT = ".";

    /**
     * Load a command set-up from the given file
     *
     * @param commandFile command file name.
     */
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
                MIN_LAUNCH_TIME = getLong(line, MIN_LAUNCH_TIME);
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
