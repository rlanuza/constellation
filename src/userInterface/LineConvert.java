package userInterface;

import java.awt.Color;

public class LineConvert {

    private static String[] getdata(String line) {
        String[] fields = line.split(":");
        if (fields.length > 1) {
            return fields[1].split("#")[0].split(",|;");
        } else {
            return null;
        }
    }

    protected static boolean getBoolean(String line, boolean defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Boolean.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    protected static long getLong(String line, long defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Long.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    protected static double getDouble(String line, double defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Double.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    protected static String getString(String line, String defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return fields[0].trim();
        } else {
            return defaultValue;
        }
    }

    protected static String getStringWithComma(String line, String defaultValue) {
        String[] fields = line.split(":")[1].split("#");
        if (fields != null) {
            return fields[0].trim();
        } else {
            return defaultValue;
        }
    }

    protected static Color getColor(String line, Color defaultColor) {
        String[] fields = getdata(line);
        if ((fields != null) && (fields.length == 3)) {
            int r = Integer.parseInt(fields[0].trim()) & 255;
            int g = Integer.parseInt(fields[1].trim()) & 255;
            int b = Integer.parseInt(fields[2].trim()) & 255;
            return new Color(r, g, b);
        } else {
            return defaultColor;
        }
    }

}
