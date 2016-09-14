package org.narcotek.cputemp.systemui.io;

import org.narcotek.cputemp.systemui.root.RootNotGrantedException;
import org.narcotek.cputemp.systemui.root.RootShell;

import java.io.File;
import java.io.IOException;

/**
 * A temperature reader using Java's Process API to gain root access and read a file using the
 * "cat" command
 */
public final class RootTempReader implements TempReader {

    private RootShell shell;

    /**
     * Default constructor creating a root shell object and asking for root access immediately
     *
     * @throws IOException             If the commands "su" or "id" could not be executed
     * @throws RootNotGrantedException If root was not granted
     */
    public RootTempReader() throws IOException, RootNotGrantedException {
        shell = new RootShell();
    }

    /**
     * Reads the given temperature file and returns the temperature as a double
     *
     * @param tempFile Temperature file
     * @return Temperature as double
     * @throws IOException If the "cat" command could not be executed
     */
    @Override
    public double readTemperature(File tempFile) throws IOException {
        String temp = shell.run("cat " + tempFile.getPath());

        double out = Float.parseFloat(temp);

        return out;
    }

    @Override
    public String toString() {
        return RootTempReader.class.getSimpleName();
    }

}
