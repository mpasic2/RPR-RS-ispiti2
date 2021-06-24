package ba.unsa.etf.rs;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.vratiBazuNaDefault();

        // Postavljamo Veliku Britaniju za pomorsku državu u bazi kako bismo mogli testirati
        Drzava vbr = dao.nadjiDrzavu("Velika Britanija");
        vbr.setPomorska(true);
        dao.izmijeniDrzavu(vbr);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(null, dao.drzave());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
        theStage = stage;
    }

    @Test
    public void testPoljaPostoje(FxRobot robot) {
        CheckBox cbLuka = robot.lookup("#cbLuka").queryAs(CheckBox.class);
        assertFalse(cbLuka.isSelected());
    }

    @Test
    public void testVracanjeGrada(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Belfast");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("280211");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Velika Britanija");

        // Jeste luka
        robot.clickOn("#cbLuka");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Grad belfast = ctrl.getGrad();
        assertEquals("Belfast", belfast.getNaziv());
        assertEquals(280211, belfast.getBrojStanovnika());
        assertEquals("Velika Britanija", belfast.getDrzava().getNaziv());
        assertTrue(belfast.isLuka());
    }

    @Test
    public void testNijePomorska(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Beč");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("1899055");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");

        // Jeste luka
        robot.clickOn("#cbLuka");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        // Čekamo da dijalog postane vidljiv
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Provjera teksta
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane.lookupAll("Država Austrija nije pomorska država"));

        // Klik na dugme Ok
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Označavamo da ipak nije luka
        robot.clickOn("#cbLuka");
        robot.clickOn("#btnOk");

        // Provjera da nije luka
        Grad bech = ctrl.getGrad();
        assertEquals("Beč", bech.getNaziv());
        assertEquals(1899055, bech.getBrojStanovnika());
        assertEquals("Austrija", bech.getDrzava().getNaziv());
        assertFalse(bech.isLuka());
    }
}