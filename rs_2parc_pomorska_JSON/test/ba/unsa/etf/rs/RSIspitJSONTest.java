package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

public class RSIspitJSONTest {
    @Test
    void testZapisiPrazno() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");
        json.zapisi(file);
        try {
            String ulaz = Files.readString(file.toPath());
            // Spajamo ulaz u jednu liniju, ako je iz nekog razloga u više linija
            ulaz = Arrays.stream(ulaz.split("\n")).map(String::trim).collect(Collectors.joining(""));
            assertEquals("[]", ulaz);
        } catch (IOException e) {
            fail("Čitanje datoteke nije uspjelo");
        }
    }


    @Test
    void testZapisiDrzavu() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");

        ArrayList<Drzava> drzave = new ArrayList<>();
        ArrayList<Grad> gradovi = new ArrayList<>();

        Drzava bih = new Drzava(1, "BiH", null);
        Grad sarajevo = new Grad(1, "Sarajevo", 350000, bih);
        Grad cekrcici = new Grad(2, "Čekrčići", 1000, bih);
        bih.setGlavniGrad(cekrcici);

        drzave.add(bih);
        gradovi.add(sarajevo);
        gradovi.add(cekrcici);
        json.setGradovi(gradovi);
        json.setDrzave(drzave);
        json.zapisi(file);

        // Sada čitamo iz iste datoteke
        JSONFormat json2 = new JSONFormat();
        try {
            json2.ucitaj(file);
            assertEquals(1, json2.getDrzave().size());
            assertEquals("BiH", json2.getDrzave().get(0).getNaziv());
            Grad sarajevo2 = null;
            for(Grad grad : json2.getGradovi())
                if (grad.getNaziv().equals("Sarajevo"))
                    sarajevo2 = grad;
            assertNotNull(sarajevo2);
            assertEquals(350000, sarajevo2.getBrojStanovnika());
            assertEquals("BiH", sarajevo2.getDrzava().getNaziv());
        } catch (Exception e) {
            fail("Čitanje nije uspjelo");
        }
    }

    @Test
    void testZapisiDrzavuSaLukama() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");

        ArrayList<Drzava> drzave = new ArrayList<>();
        ArrayList<Grad> gradovi = new ArrayList<>();

        Drzava bih = new Drzava(1, "BiH", null, true);
        Grad sarajevo = new Grad(1, "Sarajevo", 350000, bih);
        Grad neum = new Grad(2, "Neum", 3000, bih, true);
        bih.setGlavniGrad(sarajevo);

        Drzava austrija = new Drzava(2, "Austrija", null);
        Grad bech = new Grad(3, "Beč", 1899055, austrija);
        austrija.setGlavniGrad(bech);

        drzave.add(bih);
        drzave.add(austrija);
        gradovi.add(sarajevo);
        gradovi.add(neum);
        gradovi.add(bech);
        json.setGradovi(gradovi);
        json.setDrzave(drzave);
        json.zapisi(file);

        // Sada čitamo iz iste datoteke
        JSONFormat json2 = new JSONFormat();
        try {
            json2.ucitaj(file);
            assertEquals(2, json2.getDrzave().size());

            Drzava bih1 = json2.getDrzave().get(0), austrija1 = json2.getDrzave().get(1);
            if (!bih.getNaziv().equals("BiH")) {
                // Ako su obrnutim redom
                bih1 = json2.getDrzave().get(1);
                austrija1 = json2.getDrzave().get(0);
            }

            assertEquals("BiH", bih1.getNaziv());
            assertEquals("Austrija", austrija1.getNaziv());
            assertTrue(bih1.isPomorska());
            assertFalse(austrija1.isPomorska());

            Grad sarajevo2 = null;
            for(Grad grad : json2.getGradovi())
                if (grad.getNaziv().equals("Sarajevo"))
                    sarajevo2 = grad;
            assertNotNull(sarajevo2);
            assertEquals(350000, sarajevo2.getBrojStanovnika());
            assertEquals("BiH", sarajevo2.getDrzava().getNaziv());
            assertFalse(sarajevo2.isLuka());

            Grad neum2 = null;
            for(Grad grad : json2.getGradovi())
                if (grad.getNaziv().equals("Neum"))
                    neum2 = grad;
            assertNotNull(neum2);
            assertTrue(neum2.isLuka());
        } catch (Exception e) {
            fail("Čitanje nije uspjelo");
        }
    }


    @Test
    void testCitajGrad() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");
        try {
            Files.writeString(file.toPath(), "[{\"gradovi\":[{\"brojStanovnika\":350000,\"glavni\":true,\"naziv\":\"Sarajevo\",\"luka\":false},{\"brojStanovnika\":3000,\"naziv\":\"Neum\",\"luka\":true}],\"naziv\":\"BiH\",\"pomorska\":true}]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            json.ucitaj(file);
            ArrayList<Drzava> drzave = json.getDrzave();
            ArrayList<Grad> gradovi = json.getGradovi();
            assertEquals(1, drzave.size());
            Drzava bih = drzave.get(0);
            assertEquals("BiH", bih.getNaziv());
            assertEquals("Sarajevo", bih.getGlavniGrad().getNaziv());
            assertTrue(bih.isPomorska());

            assertEquals(2, gradovi.size());
            Grad sarajevo=gradovi.get(0), neum=gradovi.get(1);
            assertEquals("Sarajevo", sarajevo.getNaziv());
            assertEquals(350000, sarajevo.getBrojStanovnika());
            assertFalse(sarajevo.isLuka());
            assertEquals("Neum", neum.getNaziv());
            assertEquals(3000, neum.getBrojStanovnika());
            assertTrue(neum.isLuka());
        } catch (Exception e) {
            fail("Čitanje nije uspjelo");
        }
    }

    @Test
    void testPogresanFormat() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");
        String[] pogresni = {
            // Atribut za gradove je gradovii umjesto gradovi
            "[{\"gradovii\":[{\"brojStanovnika\":350000,\"naziv\":\"Sarajevo\"},{\"brojStanovnika\":1000,\"glavni\":true,\"naziv\":\"Čekrčići\"}],\"naziv\":\"BiH\",\"uredjenje\":2}]",
            // Država nema naziv (umjesto toga nazivi)
            "[{\"gradovi\":[{\"brojStanovnika\":350000,\"naziv\":\"Sarajevo\"},{\"brojStanovnika\":1000,\"glavni\":true,\"naziv\":\"Čekrčići\"}],\"nazivi\":\"BiH\",\"uredjenje\":2}]",
            // Grad nema naziv (umjesto toga nazivi)
            "[{\"gradovi\":[{\"brojStanovnika\":350000,\"nazivi\":\"Sarajevo\"},{\"brojStanovnika\":1000,\"glavni\":true,\"naziv\":\"Čekrčići\"}],\"naziv\":\"BiH\",\"uredjenje\":2}]",
            // Grad nema broj stanovnika
            "[{\"gradovi\":[{\"brojStanovnika\":350000,\"naziv\":\"Sarajevo\"},{\"brojSeljaka\":1000,\"glavni\":true,\"naziv\":\"Čekrčići\"}],\"naziv\":\"BiH\",\"uredjenje\":2}]",
            // Nijedan grad nije glavni
            "[{\"gradovi\":[{\"brojStanovnika\":350000,\"naziv\":\"Sarajevo\"},{\"brojStanovnika\":1000,\"naziv\":\"Čekrčići\"}],\"naziv\":\"BiH\",\"uredjenje\":2}]",
        };
        for(String ulaz : pogresni) {
            try {
                Files.writeString(file.toPath(), ulaz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertThrows(Exception.class, () -> json.ucitaj(file));
        }
    }
}
