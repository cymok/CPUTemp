package org.narcotek.cputemp.systemui.root;

/**
 * Thrown by the constructor of the root shell class if root access was denied
 */
public final class RootNotGrantedException extends Exception {

    public RootNotGrantedException(String message) {
        super(message);
    }

}
