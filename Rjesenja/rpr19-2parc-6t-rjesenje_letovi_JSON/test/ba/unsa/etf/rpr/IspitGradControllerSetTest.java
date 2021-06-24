package ba.unsa.etf.rpr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerSetTest {
    Stage theStage;
    GradController ctrl;
    Grad globalniGrad;

    @Start
    public void start(Stage stage) throws Exception {
        // Kreiramo formu sa popunjenim gradom
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.vratiBazuNaDefault();

        Grad marseille = new Grad(100, "Marseille", 861635, dao.nadjiDrzavu("Francuska"));
        Grad london = dao.nadjiGrad("London");
        marseille.getLetovi().add(london);
        Grad bech = dao.nadjiGrad("Beč");
        marseille.getLetovi().add(bech);
        globalniGrad = marseille;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(marseille, dao.drzave(), dao.gradovi());
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
    public void testIspravneVrijednosti(FxRobot robot) {
        // Da li su popunjene odgovarajuće vrijednosti
        TextField fieldNaziv = robot.lookup("#fieldNaziv").queryAs(TextField.class);
        assertEquals("Marseille", fieldNaziv.getText());

        ListView<Grad> lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);

        // Marseille ima letove za London i Beč
        assertEquals(2, lvLetovi.getItems().size());
        if (lvLetovi.getItems().get(0).getNaziv().equals("London")) {
            assertEquals("Beč", lvLetovi.getItems().get(1).getNaziv());
        } else {
            assertEquals("Beč", lvLetovi.getItems().get(0).getNaziv());
            assertEquals("London", lvLetovi.getItems().get(1).getNaziv());
        }
    }

    @Test
    public void testDodajLet(FxRobot robot) {
        // Dodajemo tri leta od čega je samo Pariz novi
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

        Grad marseille = ctrl.getGrad();
        assertEquals(3, marseille.getLetovi().size());
        int pronadjeniGradovi = 0;
        for(Grad grad : marseille.getLetovi()) {
            if (grad.getNaziv().equals("Beč")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("Pariz")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("London")) pronadjeniGradovi++;
        }
        assertEquals(3, pronadjeniGradovi);
    }

    @Test
    public void testCancel(FxRobot robot) {
        // Klik na dugme Cancel ne bi smio dodati nove letove
        robot.clickOn("#choiceGrad");
        robot.clickOn("Graz");
        robot.clickOn("#btnDodajLet");

        // Klik na dugme Cancel
        robot.clickOn("#btnCancel");

        // Globalna verzija grada i dalje ima 2 leta
        assertEquals(2, globalniGrad.getLetovi().size());;

        // Da li za grad Graz postoji evidentiran let?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad graz = dao.nadjiGrad("Graz");
        assertEquals(0, graz.getLetovi().size());
    }

}
