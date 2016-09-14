package org.narcotek.cputemp.systemui.io;

import java.io.File;

/**
 * An interface for a temperature reader; this interface allows different ways of retrieving the
 * temperature from a temperature file.
 */
public interface TempReader {

    double readTemperature(File tempFile) throws Exception;

}
