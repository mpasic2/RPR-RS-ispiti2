package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Provjera da li je polje Viza očuvano u bazi

public class IspitDAOTest {

    @Test
    void testDodajDrzavu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Uzimamo bilo koji grad kao glavni (nije bitno)
        Grad pariz = dao.nadjiGrad("Pariz");

        // Kreiramo države za koje treba i ne treba viza
        Drzava madjarska = new Drzava(0, "Mađarska", pariz, Drzava.Uredjenje.REPUBLIKA);
        dao.dodajDrzavu(madjarska);
        Drzava monako = new Drzava(0, "Monako", pariz, Drzava.Uredjenje.KRALJEVINA);
        dao.dodajDrzavu(monako);

        // Uzimamo nove verzije država iz baze da bismo osigurali da su korektno upisane u bazu
        Drzava madjarska2 = dao.nadjiDrzavu("Mađarska");
        Drzava monako2 = dao.nadjiDrzavu("Monako");
        assertNotNull(madjarska2);
        assertNotNull(monako2);
        assertEquals(Drzava.Uredjenje.REPUBLIKA, madjarska2.getUredjenje());
        assertEquals(Drzava.Uredjenje.KRALJEVINA, monako2.getUredjenje());

        // Dodajemo grad Monako
        Grad monakoGrad = new Grad(0, "Monako", 38300, monako2);
        dao.dodajGrad(monakoGrad);

        // Isto
        Grad monakoGrad2 = dao.nadjiGrad("Monako");
        assertNotNull(monakoGrad2);
        assertEquals("Monako", monakoGrad2.getDrzava().getNaziv());
        assertEquals(Drzava.Uredjenje.KRALJEVINA, monakoGrad2.getDrzava().getUredjenje());
    }

    @Test
    void testDodajDrzavu2() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Uzimamo bilo koji grad kao glavni (nije bitno)
        Grad pariz = dao.nadjiGrad("Pariz");

        // Kreiramo države za koje treba i ne treba viza
        Drzava usa = new Drzava(0, "Sjedinjene Američke Države", pariz, Drzava.Uredjenje.FEDERACIJA);
        dao.dodajDrzavu(usa);
        Drzava sjevernaKoreja = new Drzava(0, "Sjeverna Koreja", pariz, Drzava.Uredjenje.DIKTATURA);
        dao.dodajDrzavu(sjevernaKoreja);

        // Uzimamo nove verzije država iz baze da bismo osigurali da su korektno upisane u bazu
        Drzava usa2 = dao.nadjiDrzavu("Sjedinjene Američke Države");
        Drzava sjevernaKoreja2 = dao.nadjiDrzavu("Sjeverna Koreja");
        assertNotNull(usa2);
        assertNotNull(sjevernaKoreja2);
        assertEquals(Drzava.Uredjenje.FEDERACIJA, usa2.getUredjenje());
        assertEquals(Drzava.Uredjenje.DIKTATURA, sjevernaKoreja2.getUredjenje());

        // Dodajemo grad Buenos Aires
        Grad newYork = new Grad(0, "New York", 8175133, usa2);
        dao.dodajGrad(newYork);

        // Isto
        Grad newYork2 = dao.nadjiGrad("New York");
        assertNotNull(newYork2);
        assertEquals("Sjedinjene Američke Države", newYork2.getDrzava().getNaziv());
        assertEquals(Drzava.Uredjenje.FEDERACIJA, newYork2.getDrzava().getUredjenje());
    }

    // Ovaj test zahtijeva da se najprije uradi zadatak 1 tj. funkcija izmijeniDrzavu u DAO klasi

    @Test
    void testIzmijeniDrzavu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Od sada Velika Britanija je federacija
        Drzava velikaBritanija = dao.nadjiDrzavu("Velika Britanija");
        velikaBritanija.setUredjenje(Drzava.Uredjenje.FEDERACIJA);
        dao.izmijeniDrzavu(velikaBritanija);

        Drzava velikaBritanija2 = dao.nadjiDrzavu("Velika Britanija");
        assertNotNull(velikaBritanija2);
        assertEquals(Drzava.Uredjenje.FEDERACIJA, velikaBritanija2.getUredjenje());

        // Uzimamo grad
        Grad london = dao.nadjiGrad("London");
        assertEquals(Drzava.Uredjenje.FEDERACIJA, london.getDrzava().getUredjenje());

        // Vraćamo da je Velika Britanija kraljevina
        velikaBritanija2.setUredjenje(Drzava.Uredjenje.KRALJEVINA);
        dao.izmijeniDrzavu(velikaBritanija2);
    }
}
