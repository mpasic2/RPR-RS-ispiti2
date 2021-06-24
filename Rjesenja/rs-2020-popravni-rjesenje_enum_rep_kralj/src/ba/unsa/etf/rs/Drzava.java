package ba.unsa.etf.rs;

public class Drzava {
    public enum Uredjenje {
        REPUBLIKA("Republika"), KRALJEVINA("Kraljevina"), FEDERACIJA("Federacija"), DIKTATURA("Diktatura");

        private final String name;

        private Uredjenje(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private int id;
    private String naziv;
    private Grad glavniGrad;
    private Uredjenje uredjenje = Uredjenje.REPUBLIKA;

    public Drzava(int id, String naziv, Grad glavniGrad) {
        this.id = id;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
        this.uredjenje = Uredjenje.REPUBLIKA;
    }

    public Drzava(int id, String naziv, Grad glavniGrad, Uredjenje uredjenje) {
        this.id = id;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
        this.uredjenje = uredjenje;
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

    public Uredjenje getUredjenje() {
        return uredjenje;
    }

    public void setUredjenje(Uredjenje uredjenje) {
        this.uredjenje = uredjenje;
    }

    @Override
    public String toString() { return naziv; }
}
