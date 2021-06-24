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

// Test funkcionalnosti dugmeta Izmijeni državu na glavnom prozoru

@ExtendWith(ApplicationExtension.class)
public class IspitIzmjenaDrzaveTest {
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
    public void testPostojiDugme(FxRobot robot) {
        Button izmijeniDugme = robot.lookup("#btnIzmijeniDrzavu").queryAs(Button.class);
        assertNotNull(izmijeniDugme);
    }

    @Test
    public void testIzmjenaDrzaveNaziv(FxRobot robot) {
        // Selektujemo grad Manchester
        robot.clickOn("Manchester");
        // Klikamo na dugme
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Klikamo na polje za ime
        robot.clickOn("#fieldNaziv");
        // Dopisujemo aaaa na kraj imena države Velika Britanija
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write("aaaa");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Sada selektujemo grad London i editujemo državu
        robot.clickOn("London");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Uzimamo sadržaj polja "Naziv"
        TextField naziv = robot.lookup("#fieldNaziv").queryAs(TextField.class);
        assertEquals("Velika Britanijaaaaa", naziv.getText());

        // Dodajemo par slova b
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write("bbbb");

        // Cancel dugme ne bi trebalo izmijeniti ime države
        robot.clickOn("Cancel");

        // Ponovo klikamo na Manchester
        robot.clickOn("Manchester");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Država je i dalje "Velika Britanijaaaaa"
        naziv = robot.lookup("#fieldNaziv").queryAs(TextField.class);
        assertEquals("Velika Britanijaaaaa", naziv.getText());

        // Vraćamo kako je bilo
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("Velika Britanija");

        robot.clickOn("Ok");
    }


    @Test
    public void testIzmjenaDrzaveNazivDAO(FxRobot robot) {
        // Selektujemo grad Manchester
        robot.clickOn("Manchester");
        // Klikamo na dugme
        robot.clickOn("Izmijeni državu");
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Klikamo na polje za ime
        robot.clickOn("#fieldNaziv");
        // Dopisujemo aaaa na kraj imena države Velika Britanija
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write("aaaa");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Sada selektujemo grad London i editujemo državu
        robot.clickOn("London");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Dodajemo par slova b
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write("bbbb");

        // Cancel dugme ne bi trebalo izmijeniti ime države
        robot.clickOn("Cancel");

        // Provjeravamo ime države u DAO klasi
        GeografijaDAO dao = GeografijaDAO.getInstance();
        boolean imaNova=false, imaStara=false, imaNajnovija=false;
        for(Drzava drzava : dao.drzave()) {
            if (drzava.getNaziv().equals("Velika Britanija"))
                imaStara = true;
            if (drzava.getNaziv().equals("Velika Britanijaaaaa"))
                imaNova = true;
            if (drzava.getNaziv().equals("Velika Britanijaaaaabbbb"))
                imaNajnovija = true;
        }
        assertFalse(imaStara);
        assertTrue(imaNova);
        assertFalse(imaNajnovija);

        // Vraćamo kako je bilo
        robot.clickOn("Manchester");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("Velika Britanija");

        robot.clickOn("Ok");
    }

    @Test
    public void testIzmjenaDrzaveGlavniGrad(FxRobot robot) {
        // Selektujemo grad Beč
        robot.clickOn("Beč");
        // Klikamo na dugme
        robot.clickOn("Izmijeni državu");
        // Sakrivamo glavni prozor da ne ometa testove
        Platform.runLater(() -> theStage.hide());
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Klikamo na polje za glavni grad i biramo Graz
        robot.clickOn("#choiceGrad");
        robot.clickOn("Graz");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Čekamo da se pojavi glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sada selektujemo grad Graz i editujemo državu
        robot.clickOn("Graz");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        Platform.runLater(() -> theStage.hide());

        // Uzimamo sadržaj polja "choiceGrad"
        ChoiceBox<Grad> choiceGrad = robot.lookup("#choiceGrad").queryAs(ChoiceBox.class);
        Grad glavniGrad = choiceGrad.getSelectionModel().getSelectedItem();

        assertEquals("Graz", glavniGrad.getNaziv());

        // Biramo opet Beč
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        // Cancel dugme ne bi trebalo izmijeniti glavni grad
        robot.clickOn("Cancel");

        // Čekamo da se pojavi glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Da li je glavni grad i dalje Graz?
        robot.clickOn("Beč");
        robot.clickOn("Izmijeni državu");
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        Platform.runLater(() -> theStage.hide());

        // Uzimamo sadržaj polja "choiceGrad"
        choiceGrad = robot.lookup("#choiceGrad").queryAs(ChoiceBox.class);
        glavniGrad = choiceGrad.getSelectionModel().getSelectedItem();

        assertEquals("Graz", glavniGrad.getNaziv());

        // Vraćamo kako je bilo
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        // Potvrda izmjene
        robot.clickOn("Ok");
        Platform.runLater(() -> theStage.show());
    }


    @Test
    public void testIzmjenaDrzaveGlavniGradDAO(FxRobot robot) {
        // Selektujemo grad Beč
        robot.clickOn("Beč");
        // Klikamo na dugme
        robot.clickOn("Izmijeni državu");
        // Sakrivamo glavni prozor da ne ometa testove
        Platform.runLater(() -> theStage.hide());
        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();
        // Klikamo na polje za glavni grad i biramo Graz
        robot.clickOn("#choiceGrad");
        robot.clickOn("Graz");
        // Potvrda izmjene
        robot.clickOn("Ok");

        // Čekamo da se pojavi glavni prozor
        Platform.runLater(() -> theStage.show());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Provjeravamo glavni grad u DAO klasi
        GeografijaDAO dao = GeografijaDAO.getInstance();
        boolean ispravanGlavniGrad = false;
        for(Drzava drzava : dao.drzave()) {
            if (drzava.getNaziv().equals("Austrija") && drzava.getGlavniGrad().getNaziv().equals("Graz"))
                ispravanGlavniGrad = true;
        }
        assertTrue(ispravanGlavniGrad);
    }
}
