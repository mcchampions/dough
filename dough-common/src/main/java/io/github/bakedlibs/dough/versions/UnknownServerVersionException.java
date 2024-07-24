package io.github.bakedlibs.dough.versions;

import javax.annotation.ParametersAreNonnullByDefault;

public class UnknownServerVersionException extends Exception {

    private static final long serialVersionUID = -5932282005937704971L;

    UnknownServerVersionException(String version, Exception x) {
        super("Could not recognize version string: " + version, x);
    }

}
