package ba.unsa.etf.rs;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;

public class NotMaritimeCountryException extends Throwable {
    public NotMaritimeCountryException(String choiceDrzava) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Država " + choiceDrzava + " nije pomorska država");
        alert.setResizable(true);
    }
}
