import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Example tests for RGBFileFormat. The private helper methods assert that the
 * load and save methods work correctly. Create additional test files (and add
 * corresponding lines to the public methods) to get 100% test coverage.
 * 
 * @author CS159 Faculty
 * @version Spring 2022
 */
public class RGBFileFormatTest {

    /**
     * Tests RGB files that should load without error.
     */
    @Test
    public void testLoad() {
        testLoad("3x4.png", "3x4.txt");
        testLoad("6x5.png", "6x5.txt");
    }

    /**
     * Tests loading a valid RGB file.
     * 
     * @param picfile path to the jpg/png file
     * @param rgbfile path to the rgb/txt file
     */
    private void testLoad(String picfile, String rgbfile) {
        try {
            Picture expect = new Picture(picfile);
            Picture actual = RGBFileFormat.load(rgbfile);
            comparePictures(expect, actual, rgbfile);
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Compares two pictures, pixel by pixel. If the picture contents don't
     * match, displays an error with the location of the first difference.
     * 
     * @param expect the expected picture object
     * @param actual the actual picture object
     * @param name filename for error messages
     */
    private void comparePictures(Picture expect, Picture actual, String name) {
        assertEquals(expect.width(), actual.width(), name + " width");
        assertEquals(expect.height(), actual.height(), name + " height");
        for (int x = 0; x < expect.width(); x++) {
            for (int y = 0; y < expect.height(); y++) {
                if (expect.getRGB(x, y) != actual.getRGB(x, y)) {
                    fail(String.format("%s differs at (%d, %d)", name, x, y));
                }
            }
        }
    }

    /**
     * Tests files that should throw RGBException.
     */
    @Test
    public void testError() {
        testError("3x4.bad", "commas", 1, 1);
        testError("6x5.bad", "parens", 2, 1);
        testError("empty.txt", "empty file", 0, 0);
        testError("blank.txt", "blank line", 0, 2);
        testError("range.txt", "range", 0, 2);
        testError("number.txt", "number", 1, 2);
        testError("ragged.txt", "ragged", 0, 1);
    }

    /**
     * Tests loading an invalid RGB file.
     * 
     * @param rgbfile path to the rgb/txt file
     * @param msg expected message of the RGBException
     * @param x expected col index of the RGBException
     * @param y expected row index of the RGBException
     */
    private void testError(String rgbfile, String msg, int x, int y) {
        String expect = String.format("%s (x=%d, y=%d)", msg, x, y);
        try {
            RGBFileFormat.load(rgbfile);
            fail("RGBException not thrown: " + expect);
        } catch (FileNotFoundException e) {
            fail(e);
        } catch (RGBException e) {
            assertEquals(expect, e.getMessage());
        }
    }

    /**
     * Tests saving existing pictures in RGB format.
     */
    @Test
    public void testSave() {
        testSave("3x4.png", "3x4.txt");
        testSave("6x5.png", "6x5.txt");
    }

    /**
     * Tests saving a valid RGB file.
     * 
     * @param picfile path to the jpg/png file
     * @param rgbfile path to the rgb/txt file
     */
    private void testSave(String picfile, String rgbfile) {
        String outfile = rgbfile.substring(0, rgbfile.length() - 3) + "out";
        try {
            Picture pic = new Picture(picfile);
            RGBFileFormat.save(outfile, pic);
            compareFiles(rgbfile, outfile);
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Compares two files, line by line. If the file contents don't match,
     * displays an error with the line number of the first difference.
     * 
     * @param expect path to expected output file
     * @param actual path to actual output file
     */
    private void compareFiles(String expect, String actual) {
        // open the two files for comparison
        Scanner exp = null;
        Scanner act = null;
        try {
            exp = new Scanner(new File(expect));
            act = new Scanner(new File(actual));
        } catch (FileNotFoundException e) {
            fail(e);
        }

        // read expected and actual output, line by line
        int lineno = 1;
        while (exp.hasNextLine() && act.hasNextLine()) {
            String eLine = exp.nextLine();
            String aLine = act.nextLine();
            String message = actual + " incorrect output on line " + lineno;
            assertEquals(eLine, aLine, message);
            lineno++;
        }

        // check for missing/extra output
        if (exp.hasNextLine()) {
            fail(actual + " missing output on line " + lineno);
        }
        if (act.hasNextLine()) {
            fail(actual + " extra output on line " + lineno);
        }
        exp.close();
        act.close();
    }

}
