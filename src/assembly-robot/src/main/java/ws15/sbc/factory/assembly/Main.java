package ws15.sbc.factory.assembly;

import org.mozartspaces.core.MzsCoreException;

import java.net.URI;

public class Main {

    private static final URI SPACE = URI.create("xvsm://localhost:4242");

    public static void main(String[] argv) throws MzsCoreException {
        String id = "dummy";

        new AssemblyRobot(id, SPACE).run();
    }
}
