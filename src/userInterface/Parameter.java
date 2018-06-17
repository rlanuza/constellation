package userInterface;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import orbitEngine.Body;

public class Parameter extends LineConvert {

    private static final int ASTRO_STRING_FIELDS = 12;
    public static int CALCULUS_METHOD = 1;
    public static boolean ONLY_2D_DATA = false;
    public static long SIMULATION_STEPS = 1440 * 10000;
    public static long STEPS_PER_PLOT = 1440;
    public static long START_EPOCH_TIME = 1520294400; //Epoc of 2018-Mar-06 00:00:00.0000 TDB)
    public static double STEP_TIME = 60.0;
    public static double METERS_PER_PIXEL = 1.3e-10;
    public static long MAX_ORBIT_POINTS = 1000;
    public static int SCREEN_PERCENT = 50;
    public static ArrayList<Body> bodyList = new ArrayList<Body>();
    public static boolean SCREEN_RESIZABLE = false;
    public static Color COLOR_SCREEN = Color.BLACK;
    public static Color COLOR_DATE = Color.RED;
    public static Color COLOR_SCALE = Color.ORANGE;
    public static Color COLOR_ANGLE = Color.YELLOW;

    Parameter(String constellationFile, String calculusMethod) {
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(constellationFile)));
        } catch (IOException ex) {
            System.out.printf("Error [%s]  reading: %s\n", ex.toString(), constellationFile);
            System.exit(1);
        }
        // Load all constellation data from file
        String[] lines = contents.split("\\r?\\n|\\r");;
        for (String lineRaw : lines) {
            String line = lineRaw.trim();
            if ((line.length() == 0) || line.startsWith("#")) {
                // This is a comment
            } else if (line.startsWith("STEP_TIME:")) {
                STEP_TIME = getDouble(line, STEP_TIME);
            } else if (line.startsWith("SIMULATION_STEPS:")) {
                SIMULATION_STEPS = getLong(line, SIMULATION_STEPS);
            } else if (line.startsWith("STEPS_PER_PLOT:")) {
                STEPS_PER_PLOT = getLong(line, STEPS_PER_PLOT);
            } else if (line.startsWith("START_EPOCH_TIME:")) {
                START_EPOCH_TIME = getLong(line, START_EPOCH_TIME);
            } else if (line.startsWith("CALCULUS_METHOD:")) {
                CALCULUS_METHOD = (int) getLong(line, CALCULUS_METHOD);
            } else if (line.startsWith("METERS_PER_PIXEL:")) {
                METERS_PER_PIXEL = getDouble(line, METERS_PER_PIXEL);
            } else if (line.startsWith("MAX_ORBIT_POINTS:")) {
                MAX_ORBIT_POINTS = getLong(line, MAX_ORBIT_POINTS);
            } else if (line.startsWith("SCREEN_PERCENT:")) {
                SCREEN_PERCENT = (int) getLong(line, SCREEN_PERCENT);
            } else if (line.startsWith("SCREEN_RESIZABLE:")) {
                SCREEN_RESIZABLE = getBoolean(line, SCREEN_RESIZABLE);
            } else if (line.startsWith("ONLY_2D_DATA:")) {
                ONLY_2D_DATA = getBoolean(line, ONLY_2D_DATA);
            } else if (line.startsWith("COLOR_SCREEN:")) {
                COLOR_SCREEN = getColor(line, COLOR_SCREEN);
            } else if (line.startsWith("COLOR_DATE:")) {
                COLOR_DATE = getColor(line, COLOR_DATE);
            } else if (line.startsWith("COLOR_SCALE:")) {
                COLOR_SCALE = getColor(line, COLOR_SCALE);
            } else if (line.startsWith("COLOR_ANGLE:")) {
                COLOR_ANGLE = getColor(line, COLOR_ANGLE);
            } else {
                String[] datas = line.split(",");
                if (datas.length == ASTRO_STRING_FIELDS) {
                    String name = datas[0].trim();
                    double mass = Double.valueOf(datas[1].trim());
                    double radius = Double.valueOf(datas[2].trim());
                    double x = Double.valueOf(datas[3].trim()) * 1000;
                    double y = Double.valueOf(datas[4].trim()) * 1000;
                    double z = ONLY_2D_DATA ? 0.0 : Double.valueOf(datas[5].trim()) * 1000;
                    double vx = Double.valueOf(datas[6].trim()) * 1000;
                    double vy = Double.valueOf(datas[7].trim()) * 1000;
                    double vz = ONLY_2D_DATA ? 0.0 : Double.valueOf(datas[8].trim()) * 1000;
                    Color astroColor = getColor(":" + datas[9] + "," + datas[10] + "," + datas[11], Color.YELLOW);
                    Body new_body = new Body(name, mass, radius, x, y, z, vx, vy, vz, astroColor);
                    bodyList.add(new_body);
                } else {
                    System.out.println("Line not processed: " + line);
                }
            }
        }
        if (calculusMethod != null && !calculusMethod.isEmpty()) {
            CALCULUS_METHOD = Integer.valueOf(calculusMethod);
        }

        if ((CALCULUS_METHOD < 0) || (3 < CALCULUS_METHOD)) {
            System.out.println("Error, Invalid CALCULUS_METHOD");
            System.exit(1);
        }
    }
}
