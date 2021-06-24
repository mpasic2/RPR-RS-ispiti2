package ba.unsa.etf.rpr;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        dao.vratiBazuNaDefault();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(null, dao.drzave(), dao.gradovi());
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
        ListView lvLetovi = robot.lookup("#lvLetovi").queryAs(ListView.class);
        assertNotNull(lvLetovi);
        ChoiceBox choiceGrad = robot.lookup("#choiceGrad").queryAs(ChoiceBox.class);
        assertNotNull(choiceGrad);

        // Ujedno testiramo da li će se program krahirati ako ništa nije izabrano
        robot.clickOn("#btnDodajLet");
        robot.clickOn("#btnOk");
    }

    @Test
    public void testVracanjeGrada(FxRobot robot) {
        // Upisujemo grad
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

        Grad sarajevo = ctrl.getGrad();
        assertEquals(3, sarajevo.getLetovi().size());
        int pronadjeniGradovi = 0;
        for(Grad grad : sarajevo.getLetovi()) {
            if (grad.getNaziv().equals("Beč")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("Pariz")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("London")) pronadjeniGradovi++;
        }
        assertEquals(3, pronadjeniGradovi);
    }

    @Test
    public void testViseIstih(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        // Više puta ćemo dodati isti let, nema veze ako se svi vide kao zasebni na listi
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

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Grad sarajevo = ctrl.getGrad();
        assertEquals(2, sarajevo.getLetovi().size());
        int pronadjeniGradovi = 0;
        for(Grad grad : sarajevo.getLetovi()) {
            if (grad.getNaziv().equals("Beč")) pronadjeniGradovi++;
            if (grad.getNaziv().equals("London")) pronadjeniGradovi++;
        }
        assertEquals(2, pronadjeniGradovi);
    }
}