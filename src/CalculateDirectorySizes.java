import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Commands line utility to calculate the size of files in directorys.
 */
public class CalculateDirectorySizes {

    /*
     * Constants for console colors for readability.
     */
    private static final String CONSOLE_COLOR_GREEN = "\033[1;32m";
    private static final String CONSOLE_COLOR_BLUE = "\033[1;34m";
    private static final String CONSOLE_COLOR_RESET = "\033[0m";
    private static final String CONSOLE_COLOR_RED = "\033[1;31m";

    /*
     * A constant for holding the division value for converting bytes.
     */
    private static final BigDecimal BYTE_DIVISON = new BigDecimal(1024);

    /*
     * Array to hold the postfixes for human readable formats.
     */
    private static final String[] postfix = new String[] { "B", "Kb", "Mb", "Gb", "Tb", "Pb" };

    /*
     * Command line flags.
     */
    private static boolean recursion = false;
    private static boolean humanReadable = false;

    private static List<String> directories = new ArrayList<String>();
    private static HashMap<String, Long> directorySizes = new HashMap<String, Long>();

    /**
     * Runs the program, calls methods to calculate the file of directories from
     * command line args and outputs the size to the console
     * 
     * @param args The command line args.
     */
    public static void main(String[] args) {
        setupFlagsDirectories(args);
        processDirectories();
        outputValues();
    }

    /**
     * Setup method to initialize the global variables used to control the flow of
     * program. Stores the list of directories passed in.
     * 
     * @param args The command line arguments.
     */
    private static void setupFlagsDirectories(String[] args) {
        for (String argument : args) {
            if (argument.startsWith("-")) {
                if (argument.equals("-r") || argument.endsWith("recursive")) {
                    recursion = true;

                } else if (argument.equals("-h") || argument.endsWith("human")) {
                    humanReadable = true;
                } // else no action taken

            } else {
                directories.add(argument);
            }
        }
    }

    /**
     * Processes all of the directories previously setup, and subdirectories
     * depending on the flags set.
     * 
     */
    private static void processDirectories() {
        for (String directory : directories) {
            processDirectories(directory);
        }
    }

    /**
     * Process the specific directory path, and sub directories if required.
     * 
     * @param path The absolute path to the directory.
     */
    private static void processDirectories(String path) {
        long currentDirectorySize = 0;
        File currentDirectory = new File(path);
        File[] files = currentDirectory.listFiles();

        if (files == null) {
            System.out.println(CONSOLE_COLOR_RED + "Path is not valid: " + CONSOLE_COLOR_RESET + path);
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                if (recursion) {
                    try {
                        processDirectories(file.getCanonicalPath());
                    } catch (IOException exception) {
                        System.out.println(CONSOLE_COLOR_RED + "Cannot process directory, invalid path: " +
                                CONSOLE_COLOR_RESET + path + " " + exception.getMessage());
                    }

                } // else no action needed
            } else {
                currentDirectorySize += file.length();
            }
        }
        try {
            directorySizes.put(currentDirectory.getCanonicalPath(), currentDirectorySize);
        } catch (IOException exception) {
            System.out.println(CONSOLE_COLOR_RED + "Cannot process directory, invalid path: " +
                    CONSOLE_COLOR_RESET + path + " " + exception.getMessage());
        }
    }

    /**
     * Prints out the values of the directory sizes, formats depending on global
     * flags.
     */
    private static void outputValues() {
        long totalSize = 0l;

        StringBuilder sb = new StringBuilder();
        sb.append(CONSOLE_COLOR_GREEN + "Directories: " + "\n" + CONSOLE_COLOR_RESET);

        // Convert keyset to array list, to make it sortable, allowing for a tree like
        // output
        ArrayList<String> directories = new ArrayList<>(directorySizes.keySet());
        Collections.sort(directories);

        for (String directory : directories) {
            long fileSize = directorySizes.get(directory);
            totalSize += fileSize;

            sb.append(CONSOLE_COLOR_GREEN + "Path: " + CONSOLE_COLOR_RESET + directory);
            sb.append(" | " + CONSOLE_COLOR_BLUE + "Size: " + CONSOLE_COLOR_RESET);

            if (humanReadable) {
                String humanReadableNumber = calculateHumanReadableNumber(fileSize);
                sb.append(humanReadableNumber);
            } else {
                sb.append(fileSize + postfix[0]);
            }
            System.out.println(sb.toString());
            sb.setLength(0); // Clear the buffer on the stringbuilder
        }

        sb.append(CONSOLE_COLOR_GREEN + "Total Size: " + CONSOLE_COLOR_RESET);
        if (humanReadable) {
            sb.append(calculateHumanReadableNumber(totalSize));
        } else {
            sb.append(totalSize + postfix[0]);
        }
        System.out.println(sb.toString());

    }

    /**
     * Calculates the highest possible postfix available. Such as a megabyte from
     * bytes.
     * 
     * @param size The number of bytes.
     * @return String representation of the number with postfix attached.
     */
    private static String calculateHumanReadableNumber(long size) {
        StringBuilder sb = new StringBuilder();
        BigDecimal fileSize = new BigDecimal(size);
        int divisionCount = 0;

        // Divides the number into higher postfixes, while keeping track of division for
        // calculating postfix
        for (int i = 0; i < postfix.length; i++) {
            if (fileSize.compareTo(BYTE_DIVISON) > 0) {
                fileSize = fileSize.divide(BYTE_DIVISON);
                divisionCount++;
            }
        }

        fileSize = fileSize.setScale(2, RoundingMode.HALF_UP); // set output to have 2 digits after decimal
        sb.append(fileSize + postfix[divisionCount]);

        return sb.toString();

    }
}