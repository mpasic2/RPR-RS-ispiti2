package ba.unsa.etf.rpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// Provjera da li se letovi korektno evidentiraju u bazi

public class IspitLetoviDAOTest {
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @Test
    void testDodajLet() {
        // Dodajem gradu London let za Beč
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        assertEquals(0, london.getLetovi().size());
        london.getLetovi().add(bech);
        dao.izmijeniGrad(london);

        // Uzimam drugu verziju za London
        Grad london2 = dao.nadjiGrad("London");
        assertEquals(1, london2.getLetovi().size());
        boolean found = false;
        for(Grad grad : london2.getLetovi())
            if (grad.getNaziv().equals("Beč")) found = true;
        assertTrue(found);
    }

    @Test
    void testUzajamnoLet() {
        // Ako dodamo let London-Beč, da li smo dodali i let Beč-London?
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        assertEquals(0, bech.getLetovi().size());
        london.getLetovi().add(bech);
        dao.izmijeniGrad(london);

        // Uzimam drugu verziju za Beč
        Grad bech2 = dao.nadjiGrad("Beč");
        assertEquals(1, bech2.getLetovi().size());
        boolean found = false;
        for(Grad grad : bech2.getLetovi())
            if (grad.getNaziv().equals("London")) found = true;
        assertTrue(found);
    }

    @Test
    void testDodajGrad() {
        // Kreiramo novi grad sa letovima
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        Grad sarajevo = new Grad(0, "Sarajevo", 350000, dao.nadjiDrzavu("Francuska"));
        sarajevo.getLetovi().add(bech);
        sarajevo.getLetovi().add(london);
        dao.dodajGrad(sarajevo);

        // Pronalazim Sarajevo u bazi
        Grad sarajevo2 = dao.nadjiGrad("Sarajevo");
        assertEquals(2, sarajevo2.getLetovi().size());
        boolean found = false;
        for(Grad grad : sarajevo2.getLetovi())
            if (grad.getNaziv().equals("London")) found = true;
        assertTrue(found);
        found = false;
        for(Grad grad : sarajevo2.getLetovi())
            if (grad.getNaziv().equals("Beč")) found = true;
        assertTrue(found);
    }

    @Test
    void testObrisiGrad() {
        // Dodajem gradu London let za Beč
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        assertEquals(0, london.getLetovi().size());
        london.getLetovi().add(bech);
        dao.izmijeniGrad(london);

        // Uzimam drugu verziju za London i brišem
        Grad london2 = dao.nadjiGrad("London");
        assertEquals(1, london2.getLetovi().size());
        dao.obrisiGrad(london2);

        // Uzimam drugu verziju za Beč
        Grad bech2 = dao.nadjiGrad("Beč");

        // Obrisan je i let Beč-London
        assertEquals(0, bech2.getLetovi().size());
    }
}
