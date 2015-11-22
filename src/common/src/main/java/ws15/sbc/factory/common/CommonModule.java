package ws15.sbc.factory.common;

import com.google.inject.PrivateModule;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.app.impl.SpaceBasedAppManager;
import ws15.sbc.factory.common.app.impl.XBasedAppManager;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedRawRawComponentRepository;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedTxManager;
import ws15.sbc.factory.common.repository.xbased.XBasedRawComponentRepository;
import ws15.sbc.factory.common.repository.xbased.XBasedTxManager;
import ws15.sbc.factory.common.utils.PropertyUtils;

import java.net.URI;

public class CommonModule extends PrivateModule {

    @Override
    protected void configure() {
        expose(AppManager.class);
        expose(RawComponentRepository.class);
        expose(TxManager.class);

        MzsCore core = DefaultMzsCore.newInstance();

        bind(MzsCore.class).toInstance(core);
        bind(Capi.class).toInstance(new Capi(core));

        bind(URI.class).toInstance(URI.create("xvsm://localhost:4242"));

        final String repoStrategy = PropertyUtils.getProperty("repoStrategy").orElse("spaceBased");

        switch (repoStrategy) {
            case "spaceBased":
                bindSpaceBased();
                break;
            case "xBased":
                bindXBased();
                break;
            default:
                throw new IllegalArgumentException("Invalid repoStrategy");
        }
    }

    private void bindSpaceBased() {
        bind(AppManager.class).to(SpaceBasedAppManager.class);
        bind(RawComponentRepository.class).to(SpaceBasedRawRawComponentRepository.class);
        bind(TxManager.class).to(SpaceBasedTxManager.class);
    }

    private void bindXBased() {
        bind(AppManager.class).to(XBasedAppManager.class);
        bind(RawComponentRepository.class).to(XBasedRawComponentRepository.class);
        bind(TxManager.class).to(XBasedTxManager.class);
    }
}
