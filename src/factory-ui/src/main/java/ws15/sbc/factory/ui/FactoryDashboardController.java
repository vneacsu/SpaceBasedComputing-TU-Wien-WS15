package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.dto.ProcessedComponent;
import ws15.sbc.factory.dto.RawComponent;

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
    private TxManager txManager;

    @FXML
    private ListView<RawComponent> rawComponentsListView;

    @FXML
    private ListView<ProcessedComponent> processedComponentsListView;

    private ObservableList<RawComponent> observableRawComponents = FXCollections.observableArrayList();

    private ObservableList<ProcessedComponent> observableProcessedComponents = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing factory dashboard controller");

        initializeListViews();

        synchronizeCurrentInventoryState();

        registerInventoryChangeListeners();
    }

    private void initializeListViews() {
        rawComponentsListView.setItems(observableRawComponents);
        processedComponentsListView.setItems(observableProcessedComponents);
    }

    private void synchronizeCurrentInventoryState() {
        txManager.beginTransaction();

        try {
            observableRawComponents.addAll(rawComponentRepository.readAll());
            observableProcessedComponents.addAll(processedComponentRepository.readAll());
        } catch (Exception e) {
            txManager.rollback();
            throw e;
        } finally {
            txManager.commit();
        }
    }

    private void registerInventoryChangeListeners() {
        rawComponentRepository.onEntityStored(component -> Platform.runLater(() -> observableRawComponents.add(component)));
        processedComponentRepository.onEntityStored(component -> Platform.runLater(() -> observableProcessedComponents.add(component)));

        rawComponentRepository.onEntityTaken(component -> Platform.runLater(() -> observableRawComponents.remove(component)));
        processedComponentRepository.onEntityTaken(component -> Platform.runLater(() -> observableProcessedComponents.remove(component)));
    }
}
