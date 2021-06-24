package ba.unsa.etf.rs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// Provjera da li se letovi korektno evidentiraju u bazi

public class IspitGeografijaDAOTest {
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @Test
    void testDodajPomorskuDrzavu() {
        Grad london = dao.nadjiGrad("London");
        assertFalse(london.isLuka());

        Drzava d = new Drzava(1, "Bosna i Hercegovina", london);
        d.setPomorska(true);
        dao.dodajDrzavu(d);

        // Uzimam drugu verziju za BiH
        Drzava bih = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertTrue(bih.isPomorska());
    }

    @Test
    void testDodajLuku() {
        Drzava francuska = dao.nadjiDrzavu("Francuska");
        assertFalse(francuska.isPomorska());
        francuska.setPomorska(true);
        dao.izmijeniDrzavu(francuska);

        Grad calais = new Grad(0, "Calais", 75961, francuska);
        calais.setLuka(true);
        dao.dodajGrad(calais);

        // Uzimam drugu verziju države i grada
        Drzava francuska2 = dao.nadjiDrzavu("Francuska");
        assertTrue(francuska2.isPomorska());
        Grad calais2 = dao.nadjiGrad("Calais");
        assertTrue(calais2.isLuka());
    }

    @Test
    void testIzmijeniLuku() {
        Drzava vbr = dao.nadjiDrzavu("Velika Britanija");
        assertFalse(vbr.isPomorska());
        vbr.setPomorska(true);
        dao.izmijeniDrzavu(vbr);

        Grad london = dao.nadjiGrad("London");
        assertFalse(london.isLuka());
        london.setLuka(true);
        dao.izmijeniGrad(london);

        // Uzimam drugu verziju države i grada
        Drzava vbr2 = dao.nadjiDrzavu("Velika Britanija");
        assertTrue(vbr2.isPomorska());
        Grad london2 = dao.nadjiGrad("London");
        assertTrue(london2.isLuka());
    }

}
