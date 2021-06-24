package ba.unsa.etf.rpr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class JSONFormat {
    private ArrayList<Drzava> drzave = new ArrayList<>();
    private ArrayList<Grad> gradovi = new ArrayList<>();

    public void ucitaj(File file) throws Exception {
        String ulaz = Files.readString(file.toPath());

        JSONArray jdrzave = new JSONArray(ulaz);
        for (int i=0; i<jdrzave.length(); i++) {
            JSONObject jdrzava = jdrzave.getJSONObject(i);
            Drzava drzava = new Drzava(0, jdrzava.getString("naziv"), null);

            JSONArray jgradovi = jdrzava.getJSONArray("gradovi");
            for (int j=0; j<jgradovi.length(); j++) {
                JSONObject jgrad = jgradovi.getJSONObject(j);
                Grad grad = new Grad(0, jgrad.getString("naziv"), jgrad.getInt("brojStanovnika"), drzava);
                if (jgrad.has("glavni") && jgrad.getBoolean("glavni"))
                    drzava.setGlavniGrad(grad);

                if (jgrad.has("letovi")) {
                    JSONArray letovi = jgrad.getJSONArray("letovi");
                    for (int k = 0; k < letovi.length(); k++) {
                        String gradLet = letovi.getString(k);
                        grad.getLetovi().add(new Grad(0, gradLet, 0, null));
                    }
                }
                gradovi.add(grad);
            }
            if (drzava.getGlavniGrad() == null)
                throw new Exception("Nijedan grad nije glavni");
            drzave.add(drzava);
        }
    }

    public void zapisi(File file)  {
        JSONArray jdrzave = new JSONArray();
        for(Drzava drzava : drzave) {
            JSONObject jdrzava = new JSONObject();
            jdrzava.put("naziv", drzava.getNaziv());
            JSONArray jgradovi = new JSONArray();
            for (Grad grad : gradovi) {
                if (grad.getDrzava().getId() == drzava.getId()) {
                    JSONObject jgrad = new JSONObject();
                    jgrad.put("naziv", grad.getNaziv());
                    jgrad.put("brojStanovnika", grad.getBrojStanovnika());
                    if (grad.getId() == drzava.getGlavniGrad().getId())
                        jgrad.put("glavni", true);

                    JSONArray letovi = new JSONArray();
                    for (Grad gradLet : grad.getLetovi()) {
                        letovi.put(gradLet.getNaziv());
                    }
                    if (!letovi.isEmpty())
                        jgrad.put("letovi", letovi);
                    jgradovi.put(jgrad);
                }
            }
            jdrzava.put("gradovi", jgradovi);
            jdrzave.put(jdrzava);
        }
        try {
            Files.writeString(file.toPath(), jdrzave.toString());
        } catch (IOException e) {
            return;
        }
    }

    public ArrayList<Drzava> getDrzave() {
        return drzave;
    }

    public void setDrzave(ArrayList<Drzava> drzave) {
        this.drzave = drzave;
    }

    public ArrayList<Grad> getGradovi() {
        return gradovi;
    }

    public void setGradovi(ArrayList<Grad> gradovi) {
        this.gradovi = gradovi;
    }
}
