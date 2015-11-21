package ws15.sbc.factory.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.ComponentRepositoryProducer;
import ws15.sbc.factory.dto.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ComponentsController implements Initializable {

    private final ComponentRepository componentRepository;

    @FXML private ListView<Component> listView;

    public ComponentsController() {
        componentRepository = ComponentRepositoryProducer.getSingleton();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Component> components = componentRepository.readAll();

        ObservableList<Component> observableComponents = FXCollections.observableArrayList(components);
        listView.setItems(observableComponents);

        componentRepository.onComponent(observableComponents::add);
    }
}

