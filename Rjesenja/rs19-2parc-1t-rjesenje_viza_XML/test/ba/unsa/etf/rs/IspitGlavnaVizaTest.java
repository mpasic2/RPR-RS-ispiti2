package ba.unsa.etf.rs;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

// Test funkcionalnosti checkbox "Potrebna viza" sa glavnog ekrana

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaVizaTest {
    Stage theStage;
    GlavnaController ctrl;

    @Start
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
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
    public void testDodavanjeDrzave(FxRobot robot) {

        // Dodajemo novu državu za koju treba viza
        robot.clickOn("Dodaj državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Nova država Argentina (glavni grad default)
        robot.clickOn("#fieldNaziv");
        robot.write("Argentina");
        // Potrebna viza
        robot.clickOn("#cbViza");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Dodajemo novi grad
        robot.clickOn("Dodaj grad");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Novi grad: Buenos Aires
        robot.clickOn("#fieldNaziv");
        robot.write("Buenos Aires");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("15600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Argentina");
        robot.clickOn("Ok");

        // Provjeravamo kroz dao da li je sve ok
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava argentina = dao.nadjiDrzavu("Argentina");
        assertNotNull(argentina);
        assertEquals("Argentina", argentina.getNaziv());
        assertTrue(argentina.isViza());

        Grad buenosAires = dao.nadjiGrad("Buenos Aires");
        assertNotNull(buenosAires);
        assertEquals("Buenos Aires", buenosAires.getNaziv());
        assertEquals(15600000, buenosAires.getBrojStanovnika());
        assertEquals("Argentina", buenosAires.getDrzava().getNaziv());
        assertTrue(buenosAires.getDrzava().isViza());

        // Brišemo državu da ne bi padao sljedeći test
        dao.obrisiDrzavu("Argentina");
    }


    // Sljedeći testovi zahtijevaju da se uradi zadatak 1

    @Test
    public void testDodavanjeIzmjenaDrzave(FxRobot robot) {
        // Dodajemo novu državu za koju treba viza
        robot.clickOn("Dodaj državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Nova država Argentina (glavni grad default)
        robot.clickOn("#fieldNaziv");
        robot.write("Argentina");
        // Potrebna viza
        robot.clickOn("#cbViza");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Dodajemo novi grad
        robot.clickOn("Dodaj grad");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Novi grad: Buenos Aires
        robot.clickOn("#fieldNaziv");
        robot.write("Buenos Aires");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("15600000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Argentina");
        robot.clickOn("Ok");

        // Da li je za Graz potrebna viza?
        robot.clickOn("Graz");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        CheckBox potrebnaViza = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertFalse(potrebnaViza.isSelected());
        robot.clickOn("Cancel");

        // Čekamo da se zatvori prozor da ne bi smetao
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Da li je za Buenos Aires potrebna viza?
        robot.clickOn("Buenos Aires");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        potrebnaViza = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertTrue(potrebnaViza.isSelected());
        robot.clickOn("Cancel");

        // Brišemo državu kroz DAO da ne bi padao sljedeći test
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.obrisiDrzavu("Argentina");
    }

    @Test
    public void testIzmjenaDrzave(FxRobot robot) {
        // Od sada nam treba viza za Veliku Britaniju...
        robot.clickOn("Manchester");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Nova država Argentina (glavni grad default)
        robot.clickOn("#cbViza");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Čekamo da se baza podataka ažurira
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sada uzimamo grad London koji je također u Velikoj Britaniji
        robot.clickOn("London");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Potrebna viza
        CheckBox potrebnaViza = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertTrue(potrebnaViza.isSelected());
        robot.clickOn("Cancel");

        // Da li je za Graz potrebna viza?
        robot.clickOn("Graz");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        potrebnaViza = robot.lookup("#cbViza").queryAs(CheckBox.class);
        assertFalse(potrebnaViza.isSelected());
        robot.clickOn("Cancel");

        // Provjeravamo i kroz DAO
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava velikaBritanija = dao.nadjiDrzavu("Velika Britanija");
        assertTrue(velikaBritanija.isViza());
    }
}
