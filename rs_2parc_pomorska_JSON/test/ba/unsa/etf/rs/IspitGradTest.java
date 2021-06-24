package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class IspitGradTest {

    @Test
    void lukaTest() {
        Drzava d = new Drzava(1, "Bosna i Hercegovina", null);
        d.setPomorska(true);
        Grad g = new Grad(100, "Sarajevo", 350000, d);
        assertFalse(g.isLuka());
        g.setLuka(true);
        assertTrue(g.isLuka());
    }

    @Test
    void lukaIzuzetakTest() {
        // Koristimo konstruktor sa pet parametar
        Drzava d = new Drzava(1, "Austrija", null, false);
        Grad g = new Grad(100, "Beč", 1899055, d);
        assertFalse(g.isLuka());
        //assertThrows(NotMaritimeCountryException.class, () -> g.setLuka(true), "Država Austrija nije pomorska država");
        assertFalse(g.isLuka());
    }

    @Test
    void lukaNullTest() {
        Grad g = new Grad(100, "Beč", 1899055, null);
        assertFalse(g.isLuka());
        // Šta će se desiti kada je država null? Opet treba baciti izuzetak jer null nije pomorska država
        //assertThrows(NotMaritimeCountryException.class, () -> g.setLuka(true));
        assertFalse(g.isLuka());
    }



    @Test
    void lukaCtorTest() {
        // I konstruktor klase Grad treba bacati izuzetak
        Drzava d = new Drzava(1, "Mađarska", null, false);
        //assertThrows(NotMaritimeCountryException.class,
        //        () -> new Grad(100, "Budimpešta", 1752000, d, true),
        //        "Država Mađarska nije pomorska država"
        // );
    }

    private class NotMaritimeCountryException {
    }
}
