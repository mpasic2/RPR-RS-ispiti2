package ba.unsa.etf.rs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitDrzavaControllerSet {
    Stage theStage;
    DrzavaController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        // Kreiramo formu sa popunjenom državom za koju treba viza
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad bech = dao.nadjiGrad("Beč");
        Drzava sjevernaKoreja = new Drzava(12345, "Sjeverna Koreja", bech);
        sjevernaKoreja.setViza(true);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"));
        ctrl = new DrzavaController(sjevernaKoreja, dao.gradovi());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Država");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
        theStage = stage;
    }

    @Test
    public void testIspravneVrijednosti(FxRobot robot) {
        TextField fieldNaziv = robot.lookup("#fieldNaziv").queryAs(TextField.class);
        assertEquals("Sjeverna Koreja", fieldNaziv.getText());

        ChoiceBox<Grad> choiceGrad = robot.lookup("#choiceGrad").queryAs(ChoiceBox.class);
        assertEquals("Beč", choiceGrad.getSelectionModel().getSelectedItem().getNaziv());

        // Da li je selektovan checkbox?
        CheckBox potrebnaViza = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertTrue(potrebnaViza.isSelected());

        // Kada se klikne na ok, da li i dalje treba viza?
        robot.clickOn("#btnOk");
        Drzava sjevernaKoreja = ctrl.getDrzava();
        assertEquals("Sjeverna Koreja", sjevernaKoreja.getNaziv());
        assertEquals("Beč", sjevernaKoreja.getGlavniGrad().getNaziv());
        assertTrue(sjevernaKoreja.isViza());
    }

    @Test
    public void testIzmjenaDržave(FxRobot robot) {
        // Klikamo na checkbox
        robot.clickOn("#cbViza");

        // Mijenjamo još neke podatke
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write(" Južna");
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");

        // Kada se klikne na ok, da li je sve ok?
        robot.clickOn("#btnOk");
        Drzava sjevernaKoreja = ctrl.getDrzava();
        assertEquals("Sjeverna Koreja Južna", sjevernaKoreja.getNaziv());
        assertEquals("London", sjevernaKoreja.getGlavniGrad().getNaziv());
        assertFalse(sjevernaKoreja.isViza());

    }
}
