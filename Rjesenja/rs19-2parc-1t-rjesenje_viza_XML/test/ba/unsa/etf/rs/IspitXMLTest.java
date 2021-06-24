package ba.unsa.etf.rs;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IspitXMLTest {
    @Test
    void testZapisiPrazno() {
        XMLFormat xml = new XMLFormat();
        File file = new File("test.xml");
        xml.zapisi(file);
        try {
            String ulaz = Files.readString(file.toPath());
            // Spajamo ulaz u jednu liniju, ako je iz nekog razloga u više linija
            ulaz = Arrays.stream(ulaz.split("\n")).map(String::trim).collect(Collectors.joining(""));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave/>", ulaz);
        } catch (IOException e) {
            fail("Čitanje datoteke nije uspjelo");
        }
    }

    @Test
    void testZapisiDrzavu() {
        XMLFormat xml = new XMLFormat();
        File file = new File("test.xml");

        ArrayList<Drzava> drzave = new ArrayList<>();
        ArrayList<Grad> gradovi = new ArrayList<>();

        Drzava bih = new Drzava(1, "BiH", null);
        Grad sarajevo = new Grad(1, "Sarajevo", 350000, bih);
        Grad cekrcici = new Grad(2, "Čekrčići", 1000, bih);
        bih.setGlavniGrad(cekrcici);

        drzave.add(bih);
        gradovi.add(sarajevo);
        gradovi.add(cekrcici);
        xml.setGradovi(gradovi);
        xml.setDrzave(drzave);
        xml.zapisi(file);
        try {
            String ulaz = Files.readString(file.toPath());
            // Spajamo ulaz u jednu liniju, ako je iz nekog razloga u više linija
            ulaz = Arrays.stream(ulaz.split("\n")).map(String::trim).collect(Collectors.joining(""));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><naziv>BiH</naziv><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzave>", ulaz);
        } catch (IOException e) {
            fail("Čitanje datoteke nije uspjelo");
        }
    }

    @Test
    void testCitajPrazno() {
        XMLFormat xml = new XMLFormat();
        File file = new File("test.xml");
        try {
            Files.writeString(file.toPath(), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave/>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            xml.ucitaj(file);
            assertEquals(0, xml.getDrzave().size());
            assertEquals(0, xml.getGradovi().size());
        } catch (Exception e) {
            fail("Čitanje nije uspjelo");
        }
    }

    @Test
    void testCitajGrad() {
        XMLFormat xml = new XMLFormat();
        File file = new File("test.xml");
        try {
            Files.writeString(file.toPath(), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><naziv>BiH</naziv><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzave>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            xml.ucitaj(file);
            ArrayList<Drzava> drzave = xml.getDrzave();
            ArrayList<Grad> gradovi = xml.getGradovi();
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
        XMLFormat xml = new XMLFormat();
        File file = new File("test.xml");
        String[] pogresni = {
            // Otvoren tag drzave zatvoren drzavee
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><naziv>BiH</naziv><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzavee>",
            // Root tag je drzavee umjesto drzave
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzavee><drzava><naziv>BiH</naziv><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzavee>",
            // Država nema naziv (umjesto toga nazivi)
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><nazivi>BiH</nazivi><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzave>",
            // Grad nema naziv (umjesto toga nazivi)
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><naziv>BiH</naziv><grad><nazivi>Sarajevo</nazivi><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojStanovnika>1000</brojStanovnika></grad></drzava></drzave>",
            // Grad nema broj stanovnika
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><drzave><drzava><naziv>BiH</naziv><grad><naziv>Sarajevo</naziv><brojStanovnika>350000</brojStanovnika></grad><grad glavni=\"true\"><naziv>Čekrčići</naziv><brojSeljaka>1000</brojSeljaka></grad></drzava></drzave>",
        };
        for(String ulaz : pogresni) {
            try {
                Files.writeString(file.toPath(), ulaz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertThrows(Exception.class, () -> xml.ucitaj(file));
        }
    }
}
