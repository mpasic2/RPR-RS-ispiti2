package ba.unsa.etf.rpr;

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
    void testZapisiDrzavuLetovi() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");

        ArrayList<Drzava> drzave = new ArrayList<>();
        ArrayList<Grad> gradovi = new ArrayList<>();

        Drzava bih = new Drzava(1, "BiH", null);
        Grad sarajevo = new Grad(1, "Sarajevo", 350000, bih);
        Grad cekrcici = new Grad(2, "Čekrčići", 1000, bih);
        bih.setGlavniGrad(cekrcici);
        sarajevo.getLetovi().add(cekrcici);

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

            assertEquals(1, sarajevo2.getLetovi().size());
            Grad cekrcici2 = null;
            for (Grad grad : sarajevo2.getLetovi())
                if (grad.getNaziv().equals("Čekrčići"))
                    cekrcici2 = grad;
            assertNotNull(cekrcici2);

            Grad cekrcici3 = null;
            for(Grad grad : json2.getGradovi())
                if (grad.getNaziv().equals("Čekrčići"))
                    cekrcici3 = grad;
            assertNotNull(cekrcici3);
        } catch (Exception e) {
            fail("Čitanje nije uspjelo");
        }
    }

    @Test
    void testCitajGrad() {
        JSONFormat json = new JSONFormat();
        File file = new File("test.json");
        try {
            Files.writeString(file.toPath(), "[{\"gradovi\":[{\"brojStanovnika\":350000,\"naziv\":\"Sarajevo\"},{\"brojStanovnika\":1000,\"glavni\":true,\"naziv\":\"Čekrčići\"}],\"naziv\":\"BiH\",\"uredjenje\":2}]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            json.ucitaj(file);
            ArrayList<Drzava> drzave = json.getDrzave();
            ArrayList<Grad> gradovi = json.getGradovi();
            assertEquals(1, drzave.size());
            assertEquals("BiH", drzave.get(0).getNaziv());
            assertEquals("Čekrčići", drzave.get(0).getGlavniGrad().getNaziv());

            assertEquals(2, gradovi.size());
            assertEquals("Sarajevo", gradovi.get(0).getNaziv());
            assertEquals(350000, gradovi.get(0).getBrojStanovnika());
            assertEquals("Čekrčići", gradovi.get(1).getNaziv());
            assertEquals(1000, gradovi.get(1).getBrojStanovnika());
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
