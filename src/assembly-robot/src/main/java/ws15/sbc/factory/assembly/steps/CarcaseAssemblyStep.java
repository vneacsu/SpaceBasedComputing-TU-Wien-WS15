package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class CarcaseAssemblyStep extends TransactionalAssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(CarcaseAssemblyStep.class);

    @Override
    protected void performStepWithinTransaction() {
        log.info("Performing carcase assembly step");

    }
}
