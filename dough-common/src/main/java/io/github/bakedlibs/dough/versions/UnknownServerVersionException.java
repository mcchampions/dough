package io.github.bakedlibs.dough.versions;

import java.io.Serial;

public class UnknownServerVersionException extends Exception {

    @Serial
    private static final long serialVersionUID = -5932282005937704971L;

    UnknownServerVersionException(String version, Exception x) {
        super("Could not recognize version string: " + version, x);
    }

}
