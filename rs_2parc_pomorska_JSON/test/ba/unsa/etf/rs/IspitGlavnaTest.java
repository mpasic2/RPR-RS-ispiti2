package ba.unsa.etf.rs;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @Start
    public void start (Stage stage) throws Exception {
        dao.vratiBazuNaDefault();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Gradovi svijeta");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @AfterEach
    public void zatvoriGrad(FxRobot robot) {
        if (robot.lookup("#btnCancel").tryQuery().isPresent())
            robot.clickOn("#btnCancel");
    }

    @Test
    public void testDodajPomorskuDrzavu(FxRobot robot) {
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Bosna i Hercegovina");

        // Glavni grad će biti automatski izabran kao prvi

        robot.clickOn("#cbPomorska");
        robot.clickOn("#btnOk");

        // Da li je BiH dodana u bazu?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        assertEquals(4, dao.drzave().size());

        Drzava bih = null;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Bosna i Hercegovina")) {
                bih = drzava;
                break;
            }
        assertNotNull(bih);
        assertEquals(true, bih.isPomorska());
    }

    @Test
    public void testDodajLuku(FxRobot robot) {
        // Najprije dodajemo pomorsku državu
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Bosna i Hercegovina");

        // Glavni grad će biti automatski izabran kao prvi

        robot.clickOn("#cbPomorska");
        robot.clickOn("#btnOk");

        // Zatim dodajemo pomorski grad
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#fieldNaziv");
        robot.write("Neum");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("3000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Bosna i Hercegovina");
        robot.clickOn("#cbLuka");
        robot.clickOn("#btnOk");


        // Da li se grad dodao u bazu?
        Grad neum = dao.nadjiGrad("Neum");
        assertTrue(neum.isLuka());

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Klikamo na Neum
        robot.clickOn("Neum");
        robot.clickOn("#btnIzmijeniGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Checkbox je označen
        CheckBox cbLuka = robot.lookup("#cbLuka").queryAs(CheckBox.class);
        assertTrue(cbLuka.isSelected());
    }

    @Test
    public void testDodajLukuNijePomorska(FxRobot robot) {
        // Probaćemo dodati luku u državu koja nije pomorska
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#fieldNaziv");
        robot.write("Innsbruck");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("130000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("#cbLuka");
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

        // Da li se grad dodao u bazu?
        Grad innsbruck = dao.nadjiGrad("Innsbruck");
        assertFalse(innsbruck.isLuka());

        // Vraćamo glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Klikamo na Neum
        robot.clickOn("Innsbruck");
        robot.clickOn("#btnIzmijeniGrad");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Checkbox je označen
        CheckBox cbLuka = robot.lookup("#cbLuka").queryAs(CheckBox.class);
        assertFalse(cbLuka.isSelected());
    }
}
