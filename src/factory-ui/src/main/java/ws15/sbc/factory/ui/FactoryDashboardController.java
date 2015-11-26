package ws15.sbc.factory.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.dto.RawComponent;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FactoryDashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FactoryDashboardController.class);

    @Inject
    private RawComponentRepository rawComponentRepository;

    @Inject
    private TxManager txManager;

    @FXML
    private ListView<RawComponent> rawComponentsListView;

    private ObservableList<RawComponent> observableRawComponents = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing factory dashboard controller");

        rawComponentsListView.setItems(observableRawComponents);

        txManager.beginTransaction();
        try {
            observableRawComponents.addAll(rawComponentRepository.readAll());
        } catch (Exception e) {
            txManager.rollback();
            throw e;
        } finally {
            txManager.commit();
        }

        rawComponentRepository.onComponent(rawComponent -> Platform.runLater(() -> observableRawComponents.add(rawComponent)));
    }
}
