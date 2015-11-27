package ws15.sbc.factory.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ws15.sbc.factory.dto.Drone;
import ws15.sbc.factory.dto.ProcessedComponent;
import ws15.sbc.factory.dto.RawComponent;

public class FactoryDashboardModel {

    private final ObservableList<RawComponent> rawComponents = FXCollections.observableArrayList();
    private final ObservableList<ProcessedComponent> processedComponents = FXCollections.observableArrayList();
    private final ObservableList<Drone> drones = FXCollections.observableArrayList();

    public ObservableList<RawComponent> getRawComponents() {
        return rawComponents;
    }

    public ObservableList<ProcessedComponent> getProcessedComponents() {
        return processedComponents;
    }

    public ObservableList<Drone> getDrones() {
        return drones;
    }
}
