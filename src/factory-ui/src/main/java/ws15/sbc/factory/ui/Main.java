package ws15.sbc.factory.ui;

import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import ws15.sbc.factory.common.ContainerUtil;
import ws15.sbc.factory.dto.Case;

import java.net.URI;

public class Main {

    private static final URI space = URI.create("xvsm://localhost:4242");

    public static void main(String[] argv) throws MzsCoreException, InterruptedException {

        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        ContainerReference cref = ContainerUtil.getOrCreateNamedContainer(space, "components", capi);

        NotificationManager notifManager = new NotificationManager(core);

        NotificationListener notifListener = (source, operation, entries) -> {
            Entry entry = (Entry) CapiUtil.getSingleEntry(entries);
            Case caseEntry = (Case) entry.getValue();
        };
        notifManager.createNotification(cref, notifListener, Operation.WRITE);

    }
}