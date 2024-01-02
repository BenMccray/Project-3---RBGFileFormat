/**
 * Indicates that an RGB file's format is incorrect.
 * 
 * @author CS159 Faculty
 * @version Spring 2022
 */
public class RGBException extends Exception {

    /**
     * Constructs an RGBException given the location of the first error.
     * 
     * @param msg error message
     * @param x column index
     * @param y row index
     */
    public RGBException(String msg, int x, int y) {
        super(String.format("%s (x=%d, y=%d)", msg, x, y));
    }

}
