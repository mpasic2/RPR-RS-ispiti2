package ba.unsa.etf.rpr;

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

// Test zadatka 2 sa glavnog ekrana

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
    public void testDodajLet(FxRobot robot) {
        // Dodajemo let Pariz - Beč
        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Dodajem let za beč
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodajLet");

        robot.clickOn("#btnOk");

        // Da li se let dodao u dao?
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(1, pariz.getLetovi().size());
        boolean found = false;
        for (Grad g : pariz.getLetovi())
            if (g.getNaziv().equals("Beč")) found=true;
        assertTrue(found);

        // Ponovo klikamo na Pariz
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(1, lvLetovi.getItems().size());
        assertEquals("Beč", lvLetovi.getItems().get(0).getNaziv());
    }

    @Test
    void testUzajamnoLet(FxRobot robot) {
        // Ako dodamo let London-Beč, da li smo dodali i let Beč-London?
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Dodajem let za Beč
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodajLet");

        robot.clickOn("#btnOk");

        // Da li se let dodao u dao?
        Grad bech = dao.nadjiGrad("Beč");
        assertEquals(1, bech.getLetovi().size());
        boolean found = false;
        for (Grad g : bech.getLetovi())
            if (g.getNaziv().equals("London")) found=true;
        assertTrue(found);

        // Sada klikamo na Beč
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Beč");
        robot.clickOn("#btnIzmijeniGrad");

        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(1, lvLetovi.getItems().size());
        assertEquals("London", lvLetovi.getItems().get(0).getNaziv());
    }

    @Test
    void testVisestrukih(FxRobot robot) {
        // Dodajemo nekoliko letova
        robot.clickOn("Manchester");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Dodajemo letove
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Graz");
        robot.clickOn("#btnDodajLet");

        robot.clickOn("#btnOk");

        // Dodali smo 3 leta
        Grad manchester = dao.nadjiGrad("Manchester");
        assertEquals(3, manchester.getLetovi().size());

        // Ponovo klikamo na Manchester
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Manchester");
        robot.clickOn("#btnIzmijeniGrad");

        // Dodali smo 3 leta
        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(3, lvLetovi.getItems().size());

        robot.clickOn("#btnCancel");

        // Sada klikamo na London
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Imamo let za Manchester
        lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(1, lvLetovi.getItems().size());
        assertEquals("Manchester", lvLetovi.getItems().get(0).getNaziv());

        robot.clickOn("#btnCancel");

        // Sada klikamo na Pariz
        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Imamo nula letova
        lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(0, lvLetovi.getItems().size());

        robot.clickOn("#btnCancel");

        // Isto provjeravamo kroz dao
        Grad london = dao.nadjiGrad("London");
        assertEquals(1, london.getLetovi().size());
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(0, pariz.getLetovi().size());
    }

    @Test
    void testCancel(FxRobot robot) {
        // Ako kliknemo na Cancel, let ne bi trebao biti dodan
        robot.clickOn("London");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        // Dodajem let za Pariz
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodajLet");

        robot.clickOn("#btnCancel");

        // Let se nije dodao u bazu
        Grad london = dao.nadjiGrad("London");
        assertEquals(0, london.getLetovi().size());
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(0, pariz.getLetovi().size());

        // Sada klikamo na Pariz
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Let se nije dodao u listview
        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(0, lvLetovi.getItems().size());

        robot.clickOn("#btnCancel");
    }

    @Test
    void testDodavanjeGrada(FxRobot robot) {
        // Dodajemo grad sa letovima
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        // Dodajemo tri leta
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodajLet");

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        // Grad se dodao u bazu i ima letove
        Grad sarajevo = dao.nadjiGrad("Sarajevo");
        assertEquals(3, sarajevo.getLetovi().size());
        int pronadjeniGradovi = 0;
        for(Grad grad : sarajevo.getLetovi()) {
            if (grad.getNaziv().equals("Beč")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("Pariz")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("London")) pronadjeniGradovi++;
        }
        assertEquals(3, pronadjeniGradovi);

        // Let se dodao i za druge gradove
        Grad london = dao.nadjiGrad("London");
        assertEquals(1, london.getLetovi().size());
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(1, pariz.getLetovi().size());
        boolean found = false;
        for (Grad g : london.getLetovi())
            if (g.getNaziv().equals("Sarajevo")) found=true;
        assertTrue(found);

        // Sada provjeravamo kroz GUI
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Let se dodao u listview
        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(1, lvLetovi.getItems().size());
        assertEquals("Sarajevo", lvLetovi.getItems().get(0).getNaziv());

        robot.clickOn("#btnCancel");

        robot.clickOn("Sarajevo");
        robot.clickOn("#btnIzmijeniGrad");

        // Let se nije dodao u listview
        lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(3, lvLetovi.getItems().size());

        robot.clickOn("#btnCancel");
    }

    @Test
    void testDodavanjeCancel(FxRobot robot) {
        // Dodajemo grad sa letovima
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Sakrivam glavni prozor da nam ne smeta
        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        // Dodajemo tri leta
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodajLet");

        // Klik na dugme ok
        robot.clickOn("#btnCancel");

        // Let se nije dodao u bazu za druge gradove
        Grad london = dao.nadjiGrad("London");
        assertEquals(0, london.getLetovi().size());
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(0, pariz.getLetovi().size());

        // Sada provjeravamo kroz GUI
        Platform.runLater(() -> theStage.show());
        // Čekamo da se prozor pokaže
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Pariz");
        robot.clickOn("#btnIzmijeniGrad");

        // Let se nije dodao u listview
        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertEquals(0, lvLetovi.getItems().size());
        robot.clickOn("#btnCancel");
    }
}
