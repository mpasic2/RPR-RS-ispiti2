package ba.unsa.etf.rs;

public class Drzava {
    private int id;
    private String naziv;
    private Grad glavniGrad;
    private boolean pomorska;

    public Drzava(int id, String naziv, Grad glavniGrad) {
        this.id = id;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
    }

    public Drzava(int id, String naziv, Grad glavniGrad, boolean pomorska) {
        this.id = id;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
        this.pomorska = pomorska;
    }

    public boolean isPomorska() {
        return pomorska;
    }

    public void setPomorska(boolean pomorska) {
        this.pomorska = pomorska;
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

    @Override
    public String toString() { return naziv; }
}
