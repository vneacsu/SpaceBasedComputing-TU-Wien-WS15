package ws15.sbc.factory.assembly;

import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.ContainerUtil;
import ws15.sbc.factory.dto.Case;

import java.net.URI;

public class SimpleSupplier {

    private static final Logger log = LoggerFactory.getLogger(SimpleSupplier.class);

    private static final URI SPACE = URI.create("xvsm://localhost:4242");

    public static void main(String[] argv) throws MzsCoreException {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        ContainerReference cref = ContainerUtil.getOrCreateNamedContainer(SPACE, "components", capi);

        Case c = new Case();
        log.debug("Writing {}", c.toString());
        capi.write(cref, new Entry(c));

        core.shutdown(true);
    }
}
