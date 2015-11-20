package ws15.sbc.factory.supply;

import org.mozartspaces.core.*;
import ws15.sbc.factory.common.ContainerUtil;

import java.net.URI;

public class Main {

    public static void main(String[] argv) throws MzsCoreException, InterruptedException {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);
        ContainerReference cref = ContainerUtil.getOrCreateNamedContainer(URI.create("xvsm://localhost:4242"), "components", capi);

        //noinspection InfiniteLoopStatement
        for (;;) {
            Entry entry = new Entry("Take this component!");
            capi.write(entry, cref);
            System.out.println("Entry written");

            Thread.sleep(2000);
        }

    }
}
