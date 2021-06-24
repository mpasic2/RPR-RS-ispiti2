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
public class IspitDrzavaControllerTest {
    Stage theStage;
    DrzavaController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.vratiBazuNaDefault();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"));
        ctrl = new DrzavaController(null, dao.gradovi());
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
    public void testPoljaPostoje(FxRobot robot) {
        CheckBox cbPomorska = robot.lookup("#cbPomorska").queryAs(CheckBox.class);
        assertFalse(cbPomorska.isSelected());
    }

    @Test
    public void testPomorska(FxRobot robot) {
        // Upisujemo državu, očekujemo da je najveći jednak glavnom po defaultu
        robot.clickOn("#fieldNaziv");
        robot.write("Bosna i Hercegovina");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#cbPomorska");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Drzava bih = ctrl.getDrzava();
        assertEquals("Bosna i Hercegovina", bih.getNaziv());
        assertEquals("Pariz", bih.getGlavniGrad().getNaziv());
        assertTrue(bih.isPomorska());
    }

    @Test
    public void testNijePomorska(FxRobot robot) {
        // Upisujemo državu, unosimo različit glavni i najveći grad
        robot.clickOn("#fieldNaziv");
        robot.write("Austrija");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Drzava austrija = ctrl.getDrzava();
        assertEquals("Austrija", austrija.getNaziv());
        assertEquals("Beč", austrija.getGlavniGrad().getNaziv());
        assertFalse(austrija.isPomorska());
    }
}