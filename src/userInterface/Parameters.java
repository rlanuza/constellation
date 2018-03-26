package userInterface;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import orbitEngine.Body;
import static orbitEngine.Constellation.ASTRO_STRING_FIELDS;

public class Parameters {

    public static int CALCULUS_METHOD;
    public static long SIMULATION_STEPS;
    public static long STEPS_PER_PLOT;
    public static long START_EPOCH_TIME;
    public static double STEP_TIME;
    public static double METERS_PER_PIXEL;
    public static long MAX_ORBIT_POINTS;
    public static int SCREEN_PERCENT;
    public static ArrayList<Body> bodyList = new ArrayList<Body>();
    public static boolean SCREEN_RESIZABLE;
    public static Color COLOR_SCREEN;
    public static Color COLOR_DATE;
    public static Color COLOR_SCALE;
    public static Color COLOR_ANGLE;

    private static Boolean getBoolean(String line, boolean defaultValue) {
        String fields[] = line.split(":|#|,|;");
        if (fields.length > 1) {
            return Boolean.valueOf(fields[1].trim());
        } else {
            return defaultValue;
        }
    }

    private static long getLong(String line, long defaultValue) {
        String fields[] = line.split(":|#|,|;");
        if (fields.length > 1) {
            return Long.valueOf(fields[1].trim());
        } else {
            return defaultValue;
        }
    }

    private static double getDouble(String line, double defaultValue) {
        String fields[] = line.split(":|#|,|;");
        if (fields.length > 1) {
            return Double.valueOf(fields[1].trim());
        } else {
            return defaultValue;
        }
    }

    private static Color getColor(String line, Color defaultColor) {
        String fields[] = line.split(":|#|,|;");
        if (fields.length > 3) {
            int r = Integer.parseInt(fields[1].trim()) & 255;
            int g = Integer.parseInt(fields[2].trim()) & 255;
            int b = Integer.parseInt(fields[3].trim()) & 255;
            return new Color(r, g, b);
        } else {
            return defaultColor;
        }
    }

    static void loadParameters(String constellationFile) {
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(constellationFile)));
        } catch (IOException ex) {
            System.out.println("Error reading: " + constellationFile);
            System.exit(1);
        }
        // Load all constellation data from file
        String[] lines = contents.split("\\r?\\n|\\r");;
        for (String lineRaw : lines) {
            String line = lineRaw.trim();
            if ((line.length() == 0) || line.startsWith("#")) {
                // This is a comment
            } else if (line.startsWith("STEP_TIME:")) {
                Parameters.STEP_TIME = getDouble(line, 60.0);
            } else if (line.startsWith("SIMULATION_STEPS:")) {
                Parameters.SIMULATION_STEPS = getLong(line, 1440 * 10000);
            } else if (line.startsWith("STEPS_PER_PLOT:")) {
                Parameters.STEPS_PER_PLOT = getLong(line, 1440);
            } else if (line.startsWith("START_EPOCH_TIME:")) {
                Parameters.START_EPOCH_TIME = getLong(line, 1520294400); //Epoc of 2018-Mar-06 00:00:00.0000 TDB)
            } else if (line.startsWith("CALCULUS_METHOD:")) {
                Parameters.CALCULUS_METHOD = (int) getLong(line, 0);
            } else if (line.startsWith("METERS_PER_PIXEL:")) {
                Parameters.METERS_PER_PIXEL = getDouble(line, 1.3e-10);
            } else if (line.startsWith("MAX_ORBIT_POINTS:")) {
                Parameters.MAX_ORBIT_POINTS = getLong(line, 1000);
            } else if (line.startsWith("SCREEN_PERCENT:")) {
                Parameters.SCREEN_PERCENT = (int) getLong(line, 1000);
            } else if (line.startsWith("SCREEN_RESIZABLE:")) {
                Parameters.SCREEN_RESIZABLE = (boolean) getBoolean(line, false);
            } else if (line.startsWith("COLOR_SCREEN:")) {
                Parameters.COLOR_SCREEN = getColor(line, Color.BLACK);
            } else if (line.startsWith("COLOR_DATE:")) {
                Parameters.COLOR_DATE = getColor(line, Color.RED);
            } else if (line.startsWith("COLOR_SCALE:")) {
                Parameters.COLOR_SCALE = getColor(line, Color.ORANGE);
            } else if (line.startsWith("COLOR_ANGLE:")) {
                Parameters.COLOR_ANGLE = getColor(line, Color.YELLOW);
            } else {
                String[] datas = line.split(",");
                if (datas.length == ASTRO_STRING_FIELDS) {
                    String name = datas[0].trim();
                    double mass = Double.valueOf(datas[1].trim());
                    double radius = Double.valueOf(datas[2].trim());
                    double x = Double.valueOf(datas[3].trim()) * 1000;
                    double y = Double.valueOf(datas[4].trim()) * 1000;
                    double z = Double.valueOf(datas[5].trim()) * 1000;
                    double vx = Double.valueOf(datas[6].trim()) * 1000;
                    double vy = Double.valueOf(datas[7].trim()) * 1000;
                    double vz = Double.valueOf(datas[8].trim()) * 1000;
                    Color astroColor = getColor("," + datas[9] + "," + datas[10] + "," + datas[11], Color.YELLOW);
                    Body new_body = new Body(name, mass, radius, x, y, z, vx, vy, vz, astroColor);
                    bodyList.add(new_body);
                } else {
                    System.out.println("Line not processed: " + line);
                }
            }
        }

    }
}