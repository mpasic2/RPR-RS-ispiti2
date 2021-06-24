package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IspitDrzavaTest {
    @Test
    void testNovaDrzava() {
        Drzava francuska = new Drzava(0, "Francuska", null, Drzava.Uredjenje.DIKTATURA);
        assertEquals(Drzava.Uredjenje.DIKTATURA, francuska.getUredjenje());
    }

    @Test
    void testIzmjenaDrzave() {
        Drzava argentina = new Drzava(0, "Argentina", null, Drzava.Uredjenje.KRALJEVINA);
        argentina.setUredjenje(Drzava.Uredjenje.FEDERACIJA);
        assertEquals(Drzava.Uredjenje.FEDERACIJA, argentina.getUredjenje());
    }
}