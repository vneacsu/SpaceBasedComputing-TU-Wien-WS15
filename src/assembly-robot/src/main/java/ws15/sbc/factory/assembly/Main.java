package ws15.sbc.factory.assembly;

import org.mozartspaces.core.MzsCoreException;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.ComponentRepositoryProducer;

public class Main {

    public static void main(String[] argv) throws MzsCoreException {
        String id = "dummy";

        ComponentRepository componentRepository = ComponentRepositoryProducer.getSingleton();

        new AssemblyRobot(id, componentRepository).run();

        componentRepository.close();
    }
}
