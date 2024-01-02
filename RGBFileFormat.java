import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Utility class for reading/writing files in RGB format.
 * 
 * @author Ben McCray
 * @version 2/23/22
 */
public class RGBFileFormat {

    /**
     * Find width of picture.
     * 
     * @param file file
     * @return width of picture
     */
    private static int findWidth(File file) {
        int width = 0;
        int charCount = 0;
        String str = "";
        Scanner s = null;
        try {
            s = new Scanner(file).useDelimiter("[^0-9\n]+");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // loop through file, scanner only picks up numbers. If all three number
        // values are found, add 1 to width and reset charCount
        while (s.hasNext()) {
            str = s.next();
            charCount++;
            if (charCount == 3) {
                charCount = 0;
                width++;
            }
            if (str.equals("\n")) {
                break;
            }
        }
        return width;
    }

    /**
     * Find width of a specific line, used for ragged.
     * 
     * @param line specific line
     * @return width of line
     */
    private static int findSpecificWidth(String line) {
        int width = 0;
        int charCount = 0;
        String str = "";

        Scanner scanner = new Scanner(line).useDelimiter("[^0-9\n]+");
        while (scanner.hasNext()) {
            str = scanner.next();
            charCount++;
            if (charCount == 3) {
                charCount = 0;
                width++;
            }
            if (str.equals("\n")) {
                break;
            }
        }
        return width;
    }

    /**
     * Find height of picture.
     * 
     * @param file file
     * @return height picture height
     */
    private static int findHeight(File file) {
        int height = 0;
        Scanner s = null;
        try {
            s = new Scanner(file).useDelimiter("[^0-9\n]+");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Loop through again with a reset scanner to find height based on
        // number of lines
        try {
            s = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (s.hasNextLine()) {
            height++;
            s.nextLine();
        }
        s.close();
        return height;
    }

    /**
     * Create a picture object based on a file.
     * 
     * @param path the path to the file
     * @return picture object
     */
    private static Picture createPicture(String path) {
        File file = new File(path);
        int width = findWidth(file);
        int height = findHeight(file);

        Picture picture = new Picture(width, height);
        return picture;
    }

    /**
     * If the file is empty, throw an RGBException with the message "empty
     * file".
     * 
     * @param file file
     * @throws RGBException empty file
     */
    private static void findEmptyFile(File file) throws RGBException {
        if (file.length() == 0) {
            throw new RGBException("empty file", 0, 0);
        }
    }

    /**
     * If any line is blank, throw an RGBException with the message "blank
     * line".
     * 
     * @param file file
     * @throws RGBException blank line
     * @throws FileNotFoundException if file doesn't exist
     */
    private static void findBlankLine(File file)
            throws RGBException, FileNotFoundException {
        Scanner scanner = new Scanner(file);

        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().equals("")) {
                throw new RGBException("blank line", 0, lineNumber);
            }
            lineNumber++;
        }
    }

    /**
     * If a number is not in the range 0 to 255, throw an RGBException with the
     * message "range".
     * 
     * @param file file
     * @throws RGBException range
     * @throws FileNotFoundException if file doesn't exist
     */
    private static void findRangeException(File file)
            throws RGBException, FileNotFoundException {
        Scanner scanner = new Scanner(file).useDelimiter("[^0-9-]+");
        int width = findWidth(file);
        int x = 0;
        int index = 0;
        int y = 0;
        int test = 0;
        try {
            while (scanner.hasNext()) {
                test = scanner.nextInt();
                if (index < 3) {
                    if (test > 255 || test < 0) {
                        throw new RGBException("range", x, y);
                    }
                }
                index++;
                if (index == 3) {
                    index = 0;
                    x++;
                }

                if (x == width) {
                    x = 0;
                    y++;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RGBException("range", x, y);
        }

    }

    /**
     * If the file has an inconsistent number of columns, throw an RGBException
     * with the message "ragged".
     * 
     * @param file file
     * @throws RGBException ragged
     * @throws FileNotFoundException if file doesn't exist
     */
    private static void findRagged(File file)
            throws RGBException, FileNotFoundException {
        Scanner scanner = new Scanner(file);

        int width = findWidth(file);
        int height = findHeight(file);

        for (int y = 0; y < height; y++) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int otherWidth = findSpecificWidth(line);
                if (otherWidth != width) {
                    throw new RGBException("ragged", 0, y);
                }
            }
        }
    }

    /**
     * If a pixel does not begin with '(' or end with ')', throw an RGBException
     * with the message "parens".
     * 
     * @param file file
     * @throws FileNotFoundException if file doesn't exist
     * @throws RGBException if parens don't match
     */
    private static void findParensException(File file)
            throws FileNotFoundException, RGBException {
        int parens1 = 0;
        int parens2 = 0;
        int col = 0;
        int row = 0;
        String pixel = "";
        Scanner scanner = new Scanner(file).useDelimiter("\\t");
        while (scanner.hasNextLine()) {
            pixel = scanner.next();

            for (int i = 0; i < pixel.length(); i++) {
                if (pixel.charAt(i) == '(') {
                    parens1++;
                }
                if (pixel.charAt(i) == ')') {
                    parens2++;
                }
                if (pixel.charAt(i) == '\n') {
                    if (parens1 == parens2) {
                        col = 0;
                    } else if (parens1 != parens2) {
                        throw new RGBException("parens", col, row);
                    }
                    row++;
                }
            }
            if (parens1 == parens2) {
                col++;
            } else if (parens1 != parens2) {
                throw new RGBException("parens", col, row);
            }
        }
    }

    /**
     * If a pixel does not have exactly two commas, throw an RGBException with
     * the message "commas".
     * 
     * @param file file
     * @throws FileNotFoundException if file doesn't exist
     * @throws RGBException if commas don't match
     */
    private static void findCommasException(File file)
            throws FileNotFoundException, RGBException {
        int commas = 0;
        int col = 0;
        int row = 0;
        String pixel = "";
        Scanner scanner = new Scanner(file).useDelimiter("\t");
        while (scanner.hasNextLine()) {
            pixel = scanner.next();
            for (int i = 0; i < pixel.length(); i++) {
                if (pixel.charAt(i) == ',') {
                    commas++;
                }
                if (pixel.charAt(i) == '\n' && i == pixel.length() - 1
                        && commas == 2) {
                    break;
                }
                if (pixel.charAt(i) == '\n' && commas == 2) {
                    row++;
                    col = 0;
                    commas = 0;
                }

            }
            if (commas == 2) {
                col++;
                commas = 0;
            } else if (commas != 2) {
                throw new RGBException("commas", col, row);
            }

        }
    }

    /**
     * If a number does not parse as an integer, throw an RGBException with the
     * message "number".
     * 
     * @param file file
     * @throws FileNotFoundException if file doesn't exist
     * @throws RGBException if integer can't parse
     */
    private static void findNumberException(File file)
            throws FileNotFoundException, RGBException {
        String parse = "";
        int x = 0;
        int y = 0;
        int index = 0;
        Scanner scanner = new Scanner(file).useDelimiter("[(,)\t( ]+");
        while (scanner.hasNext()) {
            parse = scanner.next();
            try {
                if (parse.equals("\n")) {
                    y++;
                    x = 0;
                    index = 0;
                } else {
                    int check = Integer.parseInt(parse);
                }
            } catch (NumberFormatException e) {
                throw new RGBException("number", x, y);
            }
            index++;
            if (index == 3) {
                x++;
                index = 0;
            }
        }
    }

    /**
     * Helper method for finding and throwing every possible exception for
     * load().
     * 
     * @param path path to file
     * @throws RGBException for any error in the txt file
     * @throws FileNotFoundException if file doesn't exist
     */
    private static void findException(String path)
            throws RGBException, FileNotFoundException {
        File file = new File(path);

        findCommasException(file);
        findParensException(file);
        findEmptyFile(file);
        findBlankLine(file);
        findRangeException(file);
        findNumberException(file);
        findRagged(file);

    }

    /**
     * Assign each pixel color to picture object.
     * 
     * @param path path to file
     * @return filled in picture
     * @throws FileNotFoundException if file not found
     */
    private static Picture assignPicture(String path)
            throws FileNotFoundException {
        Picture picture = createPicture(path);
        File file = new File(path);
        Scanner scanner = new Scanner(file).useDelimiter("[^0-9]+");

        int r = 0;
        int g = 0;
        int b = 0;

        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                if (scanner.hasNext()) {
                    r = scanner.nextInt();
                    g = scanner.nextInt();
                    b = scanner.nextInt();

                    Color color = new Color(r, g, b);
                    picture.set(x, y, color);
                }
            }
        }
        return picture;
    }

    /**
     * Loads a picture from an RGB file.
     * 
     * @param path the path to the file
     * @return the corresponding picture
     * @throws FileNotFoundException if path is not found
     * @throws RGBException if file format is incorrect
     */
    public static Picture load(String path)
            throws FileNotFoundException, RGBException {
        findException(path);
        Picture picture = assignPicture(path);
        return picture;
    }

    /**
     * Saves a picture to an RGB file.
     * 
     * @param path the path to the file
     * @param picture the picture to save
     * @throws FileNotFoundException if path is not found
     */
    public static void save(String path, Picture picture)
            throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(path);
        int width = picture.width();
        int height = picture.height();
        String[][] rgbValues = new String[height][width];

        // get the rgb value for every pixel in picture.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixel = picture.get(x, y);
                int red = pixel.getRed();
                int green = pixel.getGreen();
                int blue = pixel.getBlue();
                String rgb = String.format("(%3d, %3d, %3d)", red, green, blue);
                rgbValues[y][x] = rgb;
            }
        }
        // assign each string rgb to a txt file.
        for (int i = 0; i < rgbValues.length; i++) {
            for (int j = 0; j < rgbValues[i].length; j++) {
                if (j < rgbValues[i].length - 1) {
                    pw.print(rgbValues[i][j] + "\t");
                } else {
                    pw.println(rgbValues[i][j]);
                }

            }
            // pw.println();
        }
        pw.close();
    }

}
