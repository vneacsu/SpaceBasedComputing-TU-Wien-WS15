package ws15.sbc.factory.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Casing;
import ws15.sbc.factory.common.dto.Contract;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import static ws15.sbc.factory.common.dto.Casing.Color.GRAY;
import static ws15.sbc.factory.common.dto.Casing.Type.NORMAL;

public class ContractsTabController implements Initializable {

    private final static Logger log = LoggerFactory.getLogger(ContractsTabController.class);

    @FXML
    private ListView<Contract> contractsListView;

    @FXML
    private TextField nDronesTextField;
    @FXML
    private ComboBox<Casing.Type> casingTypeCombo;
    @FXML
    private ComboBox<Casing.Color> casingColorCombo;

    private ObservableList<Contract> contracts = observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contractsListView.setItems(contracts);

        nDronesTextField.setText("3");

        casingTypeCombo.setItems(observableArrayList(Casing.Type.values()));
        casingTypeCombo.setValue(NORMAL);

        casingColorCombo.setItems(observableArrayList(Casing.Color.values()));
        casingColorCombo.setValue(GRAY);
    }

    @FXML
    private void onAddContract(MouseEvent event) {
        int nDrones = Integer.parseInt(nDronesTextField.getText());
        Casing.Type type = casingTypeCombo.getValue();
        Casing.Color color = casingColorCombo.getValue();

        Contract contract = new Contract(nDrones, type, color);
        log.info("Adding new contract {}", contract);

        contracts.add(contract);
    }
}
