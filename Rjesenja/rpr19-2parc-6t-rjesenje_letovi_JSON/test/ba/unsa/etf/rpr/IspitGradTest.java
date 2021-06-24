package ba.unsa.etf.rpr;

import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IspitGradTest {

    @Test
    void letoviTest() {
        Grad g = new Grad(100, "Sarajevo", 350000, null);
        TreeSet<Grad> letovi = g.getLetovi();
        assertEquals(0, letovi.size());
        letovi.add(new Grad(0, "Grad1", 100, null));
        letovi.add(new Grad(0, "Grad2", 200, null));
        g.setLetovi(letovi);
        assertEquals(2, g.getLetovi().size());
    }
}