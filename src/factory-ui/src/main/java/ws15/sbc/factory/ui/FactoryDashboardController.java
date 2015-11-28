package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FactoryDashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FactoryDashboardController.class);

    @Inject private RawComponentRepository rawComponentRepository;
    @Inject private ProcessedComponentRepository processedComponentRepository;
    @Inject private DroneRepository droneRepository;
    @Inject private CalibratedDroneRepository calibratedDroneRepository;
    @Inject private GoodDroneRepository goodDroneRepository;
    @Inject private BadDroneRepository badDroneRepository;

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
            model.getCalibratedDrones().addAll(calibratedDroneRepository.readAll());
            model.getGoodDrones().addAll(goodDroneRepository.readAll());
            model.getBadDrones().addAll(badDroneRepository.readAll());
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

        calibratedDroneRepository.onEntityStored(drone -> Platform.runLater((() -> model.getCalibratedDrones().add(drone))));
        calibratedDroneRepository.onEntityTaken(drone -> Platform.runLater((() -> model.getCalibratedDrones().remove(drone))));

        goodDroneRepository.onEntityStored(drone -> Platform.runLater((() -> model.getGoodDrones().add(drone))));
        goodDroneRepository.onEntityTaken(drone -> Platform.runLater((() -> model.getGoodDrones().remove(drone))));

        badDroneRepository.onEntityStored(drone -> Platform.runLater((() -> model.getBadDrones().add(drone))));
        badDroneRepository.onEntityTaken(drone -> Platform.runLater((() -> model.getBadDrones().remove(drone))));
    }
}
