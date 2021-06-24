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
        CheckBox cb = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertNotNull(cb);
        assertEquals("Potrebna viza", cb.getText());
        // Po defaultu nije aktivan
        assertFalse(cb.isSelected());
    }

    @Test
    public void testDefault(FxRobot robot) {
        // Kreiramo državu bez klikanja na checkbox
        robot.clickOn("#fieldNaziv");
        robot.write("Mađarska");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnOk");

        Drzava madjarska = ctrl.getDrzava();
        assertEquals("Mađarska", madjarska.getNaziv());
        assertEquals("Pariz", madjarska.getGlavniGrad().getNaziv());
        assertFalse(madjarska.isViza());
    }

    @Test
    public void testPromjena(FxRobot robot) {
        // Kreiramo državu za koju treba viza
        robot.clickOn("#fieldNaziv");
        robot.write("Argentina");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#cbViza");
        robot.clickOn("#btnOk");

        Drzava argentina = ctrl.getDrzava();
        assertEquals("Argentina", argentina.getNaziv());
        assertEquals("Pariz", argentina.getGlavniGrad().getNaziv());
        assertTrue(argentina.isViza());
    }
}