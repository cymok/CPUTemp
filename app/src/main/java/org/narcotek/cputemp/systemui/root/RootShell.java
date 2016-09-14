package org.narcotek.cputemp.systemui.root;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Small helper class representing a root shell; allows running shell commands and getting their
 * results
 */
public final class RootShell {

    private Process process;
    private BufferedReader input;
    private BufferedWriter output;

    /**
     * Default constructor
     *
     * @throws IOException             If the commands "su" or "id" could not be executed
     * @throws RootNotGrantedException If root access was not granted
     */
    public RootShell() throws IOException, RootNotGrantedException {
        process = Runtime.getRuntime().exec("su -c /system/bin/sh");

        input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        String id = run("id");
        if (!id.contains("uid=0")) {
            throw new RootNotGrantedException("Root not granted!");
        }
    }

    /**
     * Runs the given command and returns the output.
     *
     * @param cmd Command to run
     * @return Command output
     * @throws IOException If the given command could not be executed
     */
    public String run(String cmd) throws IOException {
        output.write(cmd + "\n");
        output.flush();

        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            stringBuilder.append(input.readLine());
            if (!input.ready()) {
                break;
            }
        }

        return stringBuilder.toString();
    }

}
