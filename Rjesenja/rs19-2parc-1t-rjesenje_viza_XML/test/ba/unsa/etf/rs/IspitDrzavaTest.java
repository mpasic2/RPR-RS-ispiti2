package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IspitDrzavaTest {
    @Test
    void testNovaDrzava() {
        Drzava francuska = new Drzava(0, "Francuska", null);
        assertFalse(francuska.isViza());
    }

    @Test
    void testIzmjenaDrzave() {
        // Testiramo novi konstruktor klase država
        Drzava argentina = new Drzava(0, "Argentina", null);
        argentina.setViza(true);
        assertTrue(argentina.isViza());
    }
}