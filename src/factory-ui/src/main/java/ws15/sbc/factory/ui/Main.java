package ws15.sbc.factory.ui;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import ws15.sbc.factory.common.ContainerUtil;

import java.net.URI;
import java.util.ArrayList;

public class Main {

    private static final URI space = URI.create("xvsm://localhost:4242");

    public static void main(String[] argv) throws MzsCoreException, InterruptedException {

        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        ContainerReference cref = ContainerUtil.getOrCreateNamedContainer(space, "components", capi);

        //noinspection InfiniteLoopStatement
        for (;;) {
            TransactionReference tx = capi.createTransaction(10000, URI.create("xvsm://localhost:4242"));
            ArrayList<String> entries = capi.take(cref, FifoCoordinator.newSelector(), MzsConstants.RequestTimeout.INFINITE, tx);

            String message = entries.get(0);

            // output
            System.out.println(message);

            capi.commitTransaction(tx);
            Thread.sleep(1000);
        }

    }
}