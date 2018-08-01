package userInterface;

import java.awt.Color;

/**
 * Convert input lines to the required format.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class LineConvert {

    /**
     * Split and convert fields to string array removing comments and line title
     *
     * @param line input line to split and format.
     * @return line fields into an array of strings.
     */
    private static String[] getdata(String line) {
        String[] fields = line.split(":");
        if (fields.length > 1) {
            return fields[1].split("#")[0].split(",|;");
        } else {
            return null;
        }
    }

    /**
     * Read a line with boolean input
     *
     * @param line input line to split and format.
     * @param defaultValue default value to return if no input.
     * @return boolean value.
     */
    protected static boolean getBoolean(String line, boolean defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Boolean.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    /**
     * Read a line with long integer input
     *
     * @param line input line to split and format.
     * @param defaultValue default value to return if no input.
     * @return long integer value.
     */
    protected static long getLong(String line, long defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Long.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    /**
     * Read a line with double float input
     *
     * @param line input line to split and format.
     * @param defaultValue default value to return if no input.
     * @return double float value.
     */
    protected static double getDouble(String line, double defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return Double.valueOf(fields[0].trim());
        } else {
            return defaultValue;
        }
    }

    /**
     * Read a line with string input
     *
     * @param line input line to split and format.
     * @param defaultValue default value to return if no input.
     * @return string value.
     */
    protected static String getString(String line, String defaultValue) {
        String[] fields = getdata(line);
        if (fields != null) {
            return fields[0].trim();
        } else {
            return defaultValue;
        }
    }

    /**
     * Read a line with string with comma data input
     *
     * @param line input line to split and format.
     * @param defaultValue default value to return if no input.
     * @return string with comma data value.
     */
    protected static String getStringWithComma(String line, String defaultValue) {
        String[] fields = line.split(":")[1].split("#");
        if (fields != null) {
            return fields[0].trim();
        } else {
            return defaultValue;
        }
    }

    /**
     * Read a line with color data input
     *
     * @param line input line to split and format.
     * @param defaultColor default color value to return if no input.
     * @return color data value.
     */
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
