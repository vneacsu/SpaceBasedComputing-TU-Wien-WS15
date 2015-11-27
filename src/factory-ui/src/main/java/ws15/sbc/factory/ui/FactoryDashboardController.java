package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.DroneRepository;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FactoryDashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FactoryDashboardController.class);

    @Inject
    private RawComponentRepository rawComponentRepository;
    @Inject
    private ProcessedComponentRepository processedComponentRepository;
    @Inject
    private DroneRepository droneRepository;

    @Inject
    private TxManager txManager;

    @FXML
    private FactoryDashboardModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing factory dashboard controller");

        synchronizeCurrentInventoryState();

        registerInventoryChangeListeners();
    }

    private void synchronizeCurrentInventoryState() {
        txManager.beginTransaction();

        try {
            model.getRawComponents().addAll(rawComponentRepository.readAll());
            model.getProcessedComponents().addAll(processedComponentRepository.readAll());
            model.getDrones().addAll(droneRepository.readAll());
        } catch (Exception e) {
            txManager.rollback();
            throw e;
        } finally {
            txManager.commit();
        }
    }

    private void registerInventoryChangeListeners() {
        rawComponentRepository.onEntityStored(component -> Platform.runLater(() -> model.getRawComponents().add(component)));
        rawComponentRepository.onEntityTaken(component -> Platform.runLater(() -> model.getRawComponents().remove(component)));

        processedComponentRepository.onEntityStored(component -> Platform.runLater(() -> model.getProcessedComponents().add(component)));
        processedComponentRepository.onEntityTaken(component -> Platform.runLater(() -> model.getProcessedComponents().remove(component)));

        droneRepository.onEntityStored(drone -> Platform.runLater((() -> model.getDrones().add(drone))));
        droneRepository.onEntityTaken(drone -> Platform.runLater((() -> model.getDrones().remove(drone))));
    }
}
