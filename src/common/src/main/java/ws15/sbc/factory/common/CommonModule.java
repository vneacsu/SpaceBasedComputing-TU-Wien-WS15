package ws15.sbc.factory.common;

import com.google.inject.PrivateModule;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.app.impl.SpaceBasedAppManager;
import ws15.sbc.factory.common.app.impl.XBasedAppManager;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.SpaceBasedComponentRepository;
import ws15.sbc.factory.common.repository.XBasedComponentRepository;
import ws15.sbc.factory.common.utils.PropertyUtils;

public class CommonModule extends PrivateModule {

    @Override
    protected void configure() {
        expose(AppManager.class);
        expose(ComponentRepository.class);

        MzsCore core = DefaultMzsCore.newInstance();

        bind(MzsCore.class).toInstance(core);
        bind(Capi.class).toInstance(new Capi(core));

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
        bind(ComponentRepository.class).to(SpaceBasedComponentRepository.class);
    }

    private void bindXBased() {
        bind(AppManager.class).to(XBasedAppManager.class);
        bind(ComponentRepository.class).to(XBasedComponentRepository.class);
    }
}
