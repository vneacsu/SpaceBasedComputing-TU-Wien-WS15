package ws15.sbc.factory.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.dto.ProcessedComponent;
import ws15.sbc.factory.common.dto.RawComponent;

public class FactoryDashboardModel {

    private final ObservableList<RawComponent> rawComponents = FXCollections.observableArrayList();
    private final ObservableList<ProcessedComponent> processedComponents = FXCollections.observableArrayList();
    private final ObservableList<Drone> drones = FXCollections.observableArrayList();
    private final ObservableList<Drone> calibratedDrones = FXCollections.observableArrayList();
    private final ObservableList<Drone> goodDrones = FXCollections.observableArrayList();
    private final ObservableList<Drone> badDrones = FXCollections.observableArrayList();

    public ObservableList<RawComponent> getRawComponents() {
        return rawComponents;
    }

    public ObservableList<ProcessedComponent> getProcessedComponents() {
        return processedComponents;
    }

    public ObservableList<Drone> getDrones() {
        return drones;
    }

    public ObservableList<Drone> getCalibratedDrones() {
        return calibratedDrones;
    }

    public ObservableList<Drone> getGoodDrones() {
        return goodDrones;
    }

    public ObservableList<Drone> getBadDrones() {
        return badDrones;
    }
}
