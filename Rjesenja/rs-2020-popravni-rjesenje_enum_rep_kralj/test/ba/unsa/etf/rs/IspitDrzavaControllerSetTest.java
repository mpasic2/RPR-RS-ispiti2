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
public class IspitDrzavaControllerSetTest {
    Stage theStage;
    DrzavaController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        // Kreiramo formu sa popunjenom državom za koju treba viza
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad bech = dao.nadjiGrad("Beč");
        Drzava sjevernaKoreja = new Drzava(12345, "Sjeverna Koreja", bech, Drzava.Uredjenje.DIKTATURA);

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
        // Da li je selektovana odgovarajuća vrijednost u ChoiceBoxu
        ChoiceBox choiceUredjenje = robot.lookup("#choiceUredjenje").queryAs(ChoiceBox.class);
        Drzava.Uredjenje uredjenje = (Drzava.Uredjenje)choiceUredjenje.getValue();
        assertEquals(Drzava.Uredjenje.DIKTATURA, uredjenje);

        // Kada se klikne na ok, da li je i dalje diktatura?
        robot.clickOn("#btnOk");
        Drzava sjevernaKoreja = ctrl.getDrzava();
        assertEquals("Sjeverna Koreja", sjevernaKoreja.getNaziv());
        assertEquals("Beč", sjevernaKoreja.getGlavniGrad().getNaziv());
        assertEquals(Drzava.Uredjenje.DIKTATURA, sjevernaKoreja.getUredjenje());
    }

    @Test
    public void testIzmjenaDržave(FxRobot robot) {
        // Klikamo na checkbox
        robot.clickOn("#choiceUredjenje");
        robot.clickOn("Republika");

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
        assertEquals(Drzava.Uredjenje.REPUBLIKA, sjevernaKoreja.getUredjenje());
    }
}
