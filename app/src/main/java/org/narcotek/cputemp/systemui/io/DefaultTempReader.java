package org.narcotek.cputemp.systemui.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Default temperature reader using Java's IO API
 */
public final class DefaultTempReader implements TempReader {

    /**
     * Reads the given temperature file and returns the temperature as a double
     *
     * @param tempFile Temperature file
     * @return Temperature as double
     * @throws FileNotFoundException If the file could not be found
     */
    @Override
    public double readTemperature(File tempFile) throws FileNotFoundException {
        double out;

        Scanner scanner = new Scanner(tempFile);

        out = scanner.nextFloat();

        scanner.close();

        return out;
    }

    @Override
    public String toString() {
        return DefaultTempReader.class.getSimpleName();
    }

}
