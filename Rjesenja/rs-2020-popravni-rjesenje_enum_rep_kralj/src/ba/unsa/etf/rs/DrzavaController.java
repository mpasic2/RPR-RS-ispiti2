package ba.unsa.etf.rs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DrzavaController {
    public TextField fieldNaziv;
    public ChoiceBox<Grad> choiceGrad;
    private Drzava drzava;
    final private ObservableList<Grad> listGradovi;
    public ChoiceBox<Drzava.Uredjenje> choiceUredjenje;
    final private ObservableList<Drzava.Uredjenje> listUredjenje;

    public DrzavaController(Drzava drzava, ArrayList<Grad> gradovi) {
        this.drzava = drzava;
        listGradovi = FXCollections.observableArrayList(gradovi);
        listUredjenje = FXCollections.observableArrayList(Drzava.Uredjenje.values());
    }

    @FXML
    public void initialize() {
        choiceGrad.setItems(listGradovi);
        choiceUredjenje.setItems(listUredjenje);
        if (drzava != null) {
            fieldNaziv.setText(drzava.getNaziv());
            //choiceGrad.getSelectionModel().select(drzava.getGlavniGrad());
            for (Grad grad : listGradovi)
                if (grad.getId() == drzava.getGlavniGrad().getId())
                    choiceGrad.getSelectionModel().select(grad);
            choiceUredjenje.setValue(drzava.getUredjenje());
        } else {
            choiceGrad.getSelectionModel().selectFirst();
            choiceUredjenje.getSelectionModel().selectFirst();
        }
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }

        if (!sveOk) return;

        if (drzava == null) drzava = new Drzava();
        drzava.setNaziv(fieldNaziv.getText());
        drzava.setGlavniGrad(choiceGrad.getSelectionModel().getSelectedItem());
        drzava.setUredjenje(choiceUredjenje.getValue());
        closeWindow();
    }

    public void clickCancel(ActionEvent actionEvent) {
        drzava = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }
}
