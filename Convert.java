/**
 * Utility class for converting images to/from RBG format.
 *
 * @author Ben Mccray
 * @version 3/1/22
 */
public class Convert {

    /**
     * Check to make sure the command line fits with the methods in
     * RGBFileFormat.
     * 
     * @param args command line args
     */
    private static void validateCommand(String[] args) {

        if (args.length != 2) {
            System.err.print("Usage: java Convert SRC DST\n");
            System.exit(1);
        } else {
            String src = args[0];
            String dst = args[1];

            if (!isTxt(src) && !isTxt(dst)) {
                System.err.print("One of the images must end with .txt\n");
                System.exit(1);
            } else if (isTxt(src) && isTxt(dst)) {
                System.err.print("One of the images must end with .txt\n");
                System.exit(1);
            }
            if (!isImage(src) && !isTxt(src)) {
                System.err.print("Unsupported file format: " + src + "\n");
                System.exit(1);
            } else if (!isImage(dst) && !isTxt(dst)) {
                System.err.print("Unsupported file format: " + dst + "\n");
                System.exit(1);
            }
        }
    }

    /**
     * Check if an input is an image.
     * 
     * @param filename src or dst path
     * @return true if an image
     */
    private static boolean isImage(String filename) {
        if (filename.substring(filename.length() - 4).equals(".png")
                || filename.substring(filename.length() - 4).equals(".jpg")) {
            return true;
        }
        return false;
    }

    /**
     * Check if file is a txt file.
     * 
     * @param filename src or dst path
     * @return true if a txt file
     */
    private static boolean isTxt(String filename) {
        if (filename.substring(filename.length() - 4).equals(".txt")) {
            return true;
        }
        return false;
    }

    /**
     * Converts a png/jpg image to RGB format or vice versa.
     * 
     * @param args command-line arguments (src and dst path)
     * @throws Exception if file not found or incorrect format
     */
    public static void main(String[] args) throws Exception {
        validateCommand(args);
        String src = args[0];
        String dst = args[1];

        if (isImage(src) && isTxt(dst)) {
            RGBFileFormat.save(dst, new Picture(src));
        } else if (isImage(dst) && isTxt(src)) {
            RGBFileFormat.load(src);
        }
    }
}
