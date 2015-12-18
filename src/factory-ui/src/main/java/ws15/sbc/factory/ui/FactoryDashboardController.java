package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Carcase;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.dto.EngineRotorPair;
import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;

import javax.inject.Inject;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static ws15.sbc.factory.common.dto.Drone.*;

public class FactoryDashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FactoryDashboardController.class);

    @Inject
    private Repository repository;

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
        repository.onEntityStored(EntityMatcher.of(RawComponent.class), it -> Platform.runLater(() -> model.getRawComponents().add(it)));
        repository.onEntityTaken(EntityMatcher.of(RawComponent.class), it -> Platform.runLater(() -> model.getRawComponents().remove(it)));

        repository.onEntityStored(EntityMatcher.of(EngineRotorPair.class), it -> Platform.runLater(() -> model.getProcessedComponents().add(it)));
        repository.onEntityTaken(EntityMatcher.of(EngineRotorPair.class), it -> Platform.runLater(() -> model.getProcessedComponents().remove(it)));

        repository.onEntityStored(EntityMatcher.of(Carcase.class), it -> Platform.runLater(() -> model.getProcessedComponents().add(it)));
        repository.onEntityTaken(EntityMatcher.of(Carcase.class), it -> Platform.runLater(() -> model.getProcessedComponents().remove(it)));

        repository.onEntityStored(EntityMatcher.of(Drone.class).withFieldEqualTo(IS_CALIBRATED_FIELD, false), it -> Platform.runLater(() -> model.getDrones().add(it)));
        repository.onEntityTaken(EntityMatcher.of(Drone.class).withFieldEqualTo(IS_CALIBRATED_FIELD, false), it -> Platform.runLater(() -> model.getDrones().remove(it)));

        repository.onEntityStored(EntityMatcher.of(Drone.class).withFieldEqualTo(IS_CALIBRATED_FIELD, true).withNullField(TESTED_BY_FIELD), it -> Platform.runLater(() -> model.getCalibratedDrones().add(it)));
        repository.onEntityTaken(EntityMatcher.of(Drone.class).withFieldEqualTo(IS_CALIBRATED_FIELD, true).withNullField(TESTED_BY_FIELD), it -> Platform.runLater(() -> model.getCalibratedDrones().remove(it)));

        repository.onEntityStored(EntityMatcher.of(Drone.class).withNotNullField(TESTED_BY_FIELD).withFieldEqualTo(IS_GOOD_DRONE_FIELD, true), it -> Platform.runLater(() -> model.getGoodDrones().add(it)));
        repository.onEntityTaken(EntityMatcher.of(Drone.class).withNotNullField(TESTED_BY_FIELD).withFieldEqualTo(IS_GOOD_DRONE_FIELD, true), it -> Platform.runLater(() -> model.getGoodDrones().remove(it)));

        repository.onEntityStored(EntityMatcher.of(Drone.class).withNotNullField(TESTED_BY_FIELD).withFieldEqualTo(IS_GOOD_DRONE_FIELD, false), it -> Platform.runLater(() -> model.getBadDrones().add(it)));
        repository.onEntityTaken(EntityMatcher.of(Drone.class).withNotNullField(TESTED_BY_FIELD).withFieldEqualTo(IS_GOOD_DRONE_FIELD, false), it -> Platform.runLater(() -> model.getBadDrones().remove(it)));
    }

    private void initializeDroneListViewsListeners() {
        Arrays.asList(assembledDronesListView, calibratedDronesListView, goodDronesListView, badDronesListView).stream()
                .forEach(it -> it.setOnMouseClicked(e -> model.selectedDroneProperty().setValue(it.getSelectionModel().getSelectedItem())));
    }
}
