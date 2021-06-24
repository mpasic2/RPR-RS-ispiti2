package ba.unsa.etf.rs;

public class Drzava {
    private int id;
    private String naziv;
    private Grad glavniGrad;
    private boolean viza;

    public Drzava(int id, String naziv, Grad glavniGrad) {
        this.id = id;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
        viza = false;
    }

    public Drzava() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }

    public boolean isViza() {
        return viza;
    }

    public void setViza(boolean viza) {
        this.viza = viza;
    }

    @Override
    public String toString() { return naziv; }
}
