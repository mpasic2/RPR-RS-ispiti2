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

// Test funkcionalnosti uređenja sa glavnog ekrana

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
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
        // Uređenje: federacija
        robot.clickOn("#choiceUredjenje");
        robot.clickOn("Federacija");
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
        assertEquals(Drzava.Uredjenje.FEDERACIJA, argentina.getUredjenje());

        Grad buenosAires = dao.nadjiGrad("Buenos Aires");
        assertNotNull(buenosAires);
        assertEquals("Buenos Aires", buenosAires.getNaziv());
        assertEquals(15600000, buenosAires.getBrojStanovnika());
        assertEquals("Argentina", buenosAires.getDrzava().getNaziv());
        assertEquals(Drzava.Uredjenje.FEDERACIJA, buenosAires.getDrzava().getUredjenje());

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
        robot.clickOn("#choiceUredjenje");
        robot.clickOn("Federacija");
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

        // Država u kojoj je Graz je republika
        robot.clickOn("Graz");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        ChoiceBox choiceUredjenje = robot.lookup("#choiceUredjenje").queryAs(ChoiceBox.class);
        Drzava.Uredjenje uredjenje = (Drzava.Uredjenje)choiceUredjenje.getValue();
        assertEquals(Drzava.Uredjenje.REPUBLIKA, uredjenje);
        robot.clickOn("Cancel");

        // Čekamo da se zatvori prozor da ne bi smetao
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Država u kojoj je Buenos Aires je federacija
        robot.clickOn("Buenos Aires");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        choiceUredjenje = robot.lookup("#choiceUredjenje").queryAs(ChoiceBox.class);
        uredjenje = (Drzava.Uredjenje)choiceUredjenje.getValue();
        assertEquals(Drzava.Uredjenje.FEDERACIJA, uredjenje);
        robot.clickOn("Cancel");

        // Brišemo državu kroz DAO da ne bi padao sljedeći test
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.obrisiGrad(dao.nadjiGrad("Buenos Aires"));
        dao.obrisiDrzavu("Argentina");
    }

    @Test
    public void testIzmjenaDrzave(FxRobot robot) {
        // Od sada je Velika Britanija diktatura
        robot.clickOn("Manchester");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        robot.clickOn("#choiceUredjenje");
        robot.clickOn("Diktatura");
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
        // Da li je država diktatura?
        ChoiceBox choiceUredjenje = robot.lookup("#choiceUredjenje").queryAs(ChoiceBox.class);
        Drzava.Uredjenje uredjenje = (Drzava.Uredjenje)choiceUredjenje.getValue();
        assertEquals(Drzava.Uredjenje.DIKTATURA, uredjenje);
        robot.clickOn("Cancel");

        // Da li je država u kojoj je Graz republika?
        robot.clickOn("Graz");
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        choiceUredjenje = robot.lookup("#choiceUredjenje").queryAs(ChoiceBox.class);
        uredjenje = (Drzava.Uredjenje)choiceUredjenje.getValue();
        assertEquals(Drzava.Uredjenje.REPUBLIKA, uredjenje);
        robot.clickOn("Cancel");

        // Provjeravamo i kroz DAO
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava velikaBritanija = dao.nadjiDrzavu("Velika Britanija");
        assertEquals(Drzava.Uredjenje.DIKTATURA, velikaBritanija.getUredjenje());
    }
}
