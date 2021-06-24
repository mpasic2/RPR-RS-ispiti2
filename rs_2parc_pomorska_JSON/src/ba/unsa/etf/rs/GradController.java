package ba.unsa.etf.rs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.ArrayList;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    private Grad grad;
    public CheckBox cbLuka;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            for (Drzava drzava : listDrzave) {
                if (drzava.getId() == grad.getDrzava().getId()) {
                    choiceDrzava.getSelectionModel().select(drzava);
                }
            }
            //cbLuka.setSelected(grad.isLuka());
            if(grad.isLuka()) cbLuka.setSelected(true);

//                else throw new NotMaritimeCountryException(choiceDrzava.getValue().getNaziv());

        } else {
            choiceDrzava.getSelectionModel().selectFirst();
            cbLuka.setSelected(false);
        }

    }


    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;
        boolean pomorska = true;

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }


        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }

        if(cbLuka.isSelected() && !choiceDrzava.getValue().isPomorska()) {sveOk=false; pomorska=false;}

        if(!pomorska){
            DialogPane dialog = new DialogPane();
            dialog.setContentText("Država " + choiceDrzava.getValue() + " nije pomorska država");
            System.out.println("evo doso do ovog");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Država " + choiceDrzava.getValue() + " nije pomorska država");
            alert.setContentText("Država " + choiceDrzava.getValue() + " nije pomorska država");

            alert.showAndWait();

        }

        if (!sveOk) return;

        if (grad == null) grad = new Grad();
        grad.setNaziv(fieldNaziv.getText());
        grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        grad.setDrzava(choiceDrzava.getValue());
        grad.setLuka(cbLuka.isSelected());
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }
}
