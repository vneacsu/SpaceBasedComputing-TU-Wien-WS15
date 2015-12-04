package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class FactoryDashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FactoryDashboardController.class);

    @Inject private RawComponentRepository rawComponentRepository;
    @Inject private ProcessedComponentRepository processedComponentRepository;
    @Inject private DroneRepository droneRepository;
    @Inject private CalibratedDroneRepository calibratedDroneRepository;
    @Inject private GoodDroneRepository goodDroneRepository;
    @Inject private BadDroneRepository badDroneRepository;

    @FXML
    private FactoryDashboardModel model;

    @FXML
    private ListView<Drone> assembledDronesListView;
    @FXML
    private ListView<Drone> calibratedDronesListView;
    @FXML
    private ListView<Drone> goodDronesListView;
    @FXML
    private ListView<Drone> badDronesListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing factory dashboard controller");

        registerInventoryChangeListeners();

        initializeDroneListViewsListeners();
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

    private void initializeDroneListViewsListeners() {
        Arrays.asList(assembledDronesListView, calibratedDronesListView, goodDronesListView, badDronesListView).stream()
                .forEach(it -> it.setOnMouseClicked(e -> model.selectedDroneProperty().setValue(it.getSelectionModel().getSelectedItem())));
    }
}
