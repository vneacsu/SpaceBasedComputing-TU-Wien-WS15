package ws15.sbc.factory.common;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;

public class ContainerUtil {

    private static final Logger log = LoggerFactory.getLogger(ContainerUtil.class);

    public static ContainerReference getOrCreateNamedContainer(final URI space,
                                                                final String containerName,
                                                                final Capi capi) throws MzsCoreException {

        ContainerReference cref;
        try {
            // Get the Container
            log.debug("Lookup container");
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
            log.debug("Container found");
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            log.debug("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<>();
            obligatoryCoords.add(new FifoCoordinator());
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
            log.debug("Container created");
        }
        return cref;
    }

}
