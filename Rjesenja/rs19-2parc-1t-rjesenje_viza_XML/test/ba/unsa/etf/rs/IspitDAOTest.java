package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Provjera da li je polje Viza očuvano u bazi

public class IspitDAOTest {

    @Test
    void testDefaultViza() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        // Pošto nismo mijenjali državu Austrija, za nju po defaultu ne treba viza
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava austrija = dao.nadjiDrzavu("Austrija");
        assertFalse(austrija.isViza());
    }

    @Test
    void testDodajDrzavu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Uzimamo bilo koji grad kao glavni (nije bitno)
        Grad pariz = dao.nadjiGrad("Pariz");

        // Kreiramo države za koje treba i ne treba viza
        Drzava madjarska = new Drzava(0, "Mađarska", pariz);
        dao.dodajDrzavu(madjarska);
        Drzava argentina = new Drzava(0, "Argentina", pariz);
        argentina.setViza(true);
        dao.dodajDrzavu(argentina);

        // Uzimamo nove verzije država iz baze da bismo osigurali da su korektno upisane u bazu
        Drzava madjarska2 = dao.nadjiDrzavu("Mađarska");
        Drzava argentina2 = dao.nadjiDrzavu("Argentina");
        assertNotNull(madjarska2);
        assertNotNull(argentina2);
        assertFalse(madjarska2.isViza());
        assertTrue(argentina2.isViza());

        // Dodajemo grad Buenos Aires
        Grad buenosAires = new Grad(0, "Buenos Aires", 15600000, argentina2);
        dao.dodajGrad(buenosAires);

        // Isto
        Grad buenosAires2 = dao.nadjiGrad("Buenos Aires");
        assertNotNull(buenosAires2);
        assertEquals("Argentina", buenosAires2.getDrzava().getNaziv());
        assertTrue(buenosAires2.getDrzava().isViza());
    }

    // Ovaj test zahtijeva da se najprije uradi zadatak 1 tj. funkcija izmijeniDrzavu u DAO klasi

    @Test
    void testIzmijeniDrzavu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Od sada nam treba viza za Veliku Britaniju...
        Drzava velikaBritanija = dao.nadjiDrzavu("Velika Britanija");
        velikaBritanija.setViza(true);
        dao.izmijeniDrzavu(velikaBritanija);

        Drzava velikaBritanija2 = dao.nadjiDrzavu("Velika Britanija");
        assertNotNull(velikaBritanija2);
        assertTrue(velikaBritanija2.isViza());

        // Uzimamo grad
        Grad london = dao.nadjiGrad("London");
        assertTrue(london.getDrzava().isViza());

        // Vraćamo da ne treba viza, kako ne bi padali ostali testovi
        velikaBritanija2.setViza(false);
        dao.izmijeniDrzavu(velikaBritanija2);
    }
}
