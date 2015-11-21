package ws15.sbc.factory.supply;

import org.mozartspaces.core.*;
import ws15.sbc.factory.common.ContainerUtil;
import ws15.sbc.factory.dto.Case;

import java.net.URI;

public class Main {

    public static void main(String[] argv) throws MzsCoreException, InterruptedException {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        ContainerReference cref = ContainerUtil.getOrCreateNamedContainer(URI.create("xvsm://localhost:4242"), "components", capi);

        Case c = new Case();

        //noinspection InfiniteLoopStatement
        for (;;) {
            capi.write(new Entry(c), cref);

            System.out.println("Entry written");
            Thread.sleep(2000);
        }

    }
}
