package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

// Testovi za metodu GeografijaDAO.izmijeniDrzavu

public class IspitIzmjenaDAOTest {
    @Test
    void testIzmjenaNazivDAO() {
        // Regenerišemo bazu
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        // Mijenjam naziv države Francuska
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");
        francuska.setNaziv("République française");
        dao.izmijeniDrzavu(francuska);

        boolean imaFrancuska=false, imaRepublique=false;
        for(Drzava drzava : dao.drzave()) {
            if (drzava.getNaziv().equals("Francuska"))
                imaFrancuska = true;
            if (drzava.getNaziv().equals("République française"))
                imaRepublique = true;
        }
        assertFalse(imaFrancuska);
        assertTrue(imaRepublique);
    }

    @Test
    void testIzmjenaGlavniGradDAO() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        // Dodajemo državu BiH
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad sarajevo = dao.nadjiGrad("Beč");
        Drzava bih = new Drzava(0, "Bosna i Hercegovina", sarajevo);
        dao.dodajDrzavu(bih);

        // Dodajemo novi grad Čekrčići
        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        Grad cekrcici = new Grad(0, "Čekrčići", 1000, bih2);
        dao.dodajGrad(cekrcici);

        // Proglašavamo Čekrčiće za glavni grad BiH
        Grad cekrcici2 = dao.nadjiGrad("Čekrčići");
        bih2.setGlavniGrad(cekrcici2);
        dao.izmijeniDrzavu(bih2);

        // Provjeravamo da li su Čekrčići sada glavni grad BiH
        Drzava bih3 = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertEquals("Čekrčići", bih3.getGlavniGrad().getNaziv());
    }

}
