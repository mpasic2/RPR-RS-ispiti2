package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class GeografijaDAO {
    private static GeografijaDAO instance;
    private Connection conn;

    private PreparedStatement glavniGradUpit, dajDrzavuUpit, obrisiDrzavuUpit, obrisiGradoveZaDrzavuUpit, nadjiDrzavuUpit,
            dajGradoveUpit, dodajGradUpit, odrediIdGradaUpit, dodajDrzavuUpit, odrediIdDrzaveUpit, promijeniGradUpit, dajGradUpit,
            nadjiGradUpit, obrisiGradUpit, dajDrzaveUpit, dodajLetUpit, obrisiLetoveZaGradUpit, dajLetoveZaGradUpit;

    public static GeografijaDAO getInstance() {
        if (instance == null) instance = new GeografijaDAO();
        return instance;
    }
    private GeografijaDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            glavniGradUpit = conn.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava FROM grad, drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                glavniGradUpit = conn.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava FROM grad, drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try {
            dajDrzavuUpit = conn.prepareStatement("SELECT * FROM drzava WHERE id=?");
            dajGradUpit = conn.prepareStatement("SELECT * FROM grad WHERE id=?");
            obrisiGradoveZaDrzavuUpit = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
            obrisiDrzavuUpit = conn.prepareStatement("DELETE FROM drzava WHERE id=?");
            obrisiGradUpit = conn.prepareStatement("DELETE FROM grad WHERE id=?");
            nadjiDrzavuUpit = conn.prepareStatement("SELECT * FROM drzava WHERE naziv=?");
            nadjiGradUpit = conn.prepareStatement("SELECT * FROM grad WHERE naziv=?");
            dajGradoveUpit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            dajDrzaveUpit = conn.prepareStatement("SELECT * FROM drzava ORDER BY naziv");

            dodajGradUpit = conn.prepareStatement("INSERT INTO grad VALUES(?,?,?,?)");
            odrediIdGradaUpit = conn.prepareStatement("SELECT MAX(id)+1 FROM grad");
            dodajDrzavuUpit = conn.prepareStatement("INSERT INTO drzava VALUES(?,?,?)");
            odrediIdDrzaveUpit = conn.prepareStatement("SELECT MAX(id)+1 FROM drzava");
            dodajLetUpit = conn.prepareStatement("INSERT INTO letovi VALUES(?,?)");
            obrisiLetoveZaGradUpit = conn.prepareStatement("DELETE FROM letovi WHERE grad1=? OR grad2=?");
            dajLetoveZaGradUpit = conn.prepareStatement("SELECT grad.* FROM letovi, grad WHERE letovi.grad1=grad.id AND letovi.grad2=? " +
                    "UNION SELECT grad.* FROM letovi, grad WHERE letovi.grad2=grad.id AND letovi.grad1=?");

            promijeniGradUpit = conn.prepareStatement("UPDATE grad SET naziv=?, broj_stanovnika=?, drzava=? WHERE id=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removeInstance() {
        if (instance == null) return;
        instance.close();
        instance = null;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.db.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if ( sqlUpit.charAt( sqlUpit.length()-1 ) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Metoda za potrebe testova, vra??a bazu podataka u polazno stanje
    public void vratiBazuNaDefault() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM drzava");
        stmt.executeUpdate("DELETE FROM grad");
        stmt.executeUpdate("DELETE FROM letovi");
        // Regeneri??i bazu ne??e ponovo kreirati tabele jer u .sql datoteci stoji
        // CREATE TABLE IF NOT EXISTS
        // Ali ??e ponovo napuniti default podacima
        regenerisiBazu();
    }

    public Grad glavniGrad(String drzava) {
        try {
            Drzava d = nadjiDrzavu(drzava);
            glavniGradUpit.setString(1, drzava);
            ResultSet rs = glavniGradUpit.executeQuery();
            if (!rs.next()) return null;
            return dajGradIzResultSeta(rs, d);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Grad dajGradIzResultSeta(ResultSet rs, Drzava d) throws SQLException {
        Grad g = new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), d);
        g.setLetovi(dajLetoveZaGrad(g.getId()));
        return g;
    }

    private TreeSet<Grad> dajLetoveZaGrad(int id) {
        TreeSet<Grad> letovi = new TreeSet<>();
        try {
            dajLetoveZaGradUpit.setInt(1, id);
            dajLetoveZaGradUpit.setInt(2, id);
            ResultSet rs = dajLetoveZaGradUpit.executeQuery();
            while (rs.next())
                letovi.add(new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), null));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return letovi;
    }

    private Drzava dajDrzavu(int id) {
        try {
            dajDrzavuUpit.setInt(1, id);
            ResultSet rs = dajDrzavuUpit.executeQuery();
            if (!rs.next()) return null;
            return dajDrzavuIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Grad dajGrad(int id, Drzava d) {
        try {
            dajGradUpit.setInt(1, id);
            ResultSet rs = dajGradUpit.executeQuery();
            if (!rs.next()) return null;
            return dajGradIzResultSeta(rs, d);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Drzava dajDrzavuIzResultSeta(ResultSet rs) throws SQLException {
        Drzava d = new Drzava(rs.getInt(1), rs.getString(2), null);
        d.setGlavniGrad( dajGrad(rs.getInt(3), d ));
        return d;
    }

    public void obrisiDrzavu(String nazivDrzave) {
        try {
            nadjiDrzavuUpit.setString(1, nazivDrzave);
            ResultSet rs = nadjiDrzavuUpit.executeQuery();
            if (!rs.next()) return;
            Drzava drzava = dajDrzavuIzResultSeta(rs);

            obrisiGradoveZaDrzavuUpit.setInt(1, drzava.getId());
            obrisiGradoveZaDrzavuUpit.executeUpdate();

            obrisiDrzavuUpit.setInt(1, drzava.getId());
            obrisiDrzavuUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> rezultat = new ArrayList();
        try {
            ResultSet rs = dajGradoveUpit.executeQuery();
            while (rs.next()) {
                Drzava d = dajDrzavu(rs.getInt(4));
                Grad grad = dajGradIzResultSeta(rs, d);
                rezultat.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> rezultat = new ArrayList();
        try {
            ResultSet rs = dajDrzaveUpit.executeQuery();
            while (rs.next()) {
                Drzava drzava = dajDrzavuIzResultSeta(rs);
                rezultat.add(drzava);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public void dodajGrad(Grad grad) {
        try {
            ResultSet rs = odrediIdGradaUpit.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            dodajGradUpit.setInt(1, id);
            dodajGradUpit.setString(2, grad.getNaziv());
            dodajGradUpit.setInt(3, grad.getBrojStanovnika());
            dodajGradUpit.setInt(4, grad.getDrzava().getId());
            dodajGradUpit.executeUpdate();
            grad.setId(id);

            for(Grad letGrad : grad.getLetovi())
                dodajLet(grad, letGrad);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dodajLet(Grad grad1, Grad grad2) {
        try {
            dodajLetUpit.setInt(1, grad1.getId());
            dodajLetUpit.setInt(2, grad2.getId());
            dodajLetUpit.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet rs = odrediIdDrzaveUpit.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            dodajDrzavuUpit.setInt(1, id);
            dodajDrzavuUpit.setString(2, drzava.getNaziv());
            dodajDrzavuUpit.setInt(3, drzava.getGlavniGrad().getId());
            dodajDrzavuUpit.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            promijeniGradUpit.setString(1, grad.getNaziv());
            promijeniGradUpit.setInt(2, grad.getBrojStanovnika());
            promijeniGradUpit.setInt(3, grad.getDrzava().getId());
            promijeniGradUpit.setInt(4, grad.getId());
            promijeniGradUpit.executeUpdate();

            // Najlak??e je da obri??emo sve letove za ovaj grad, pa ih opet dodamo
            obrisiLetoveZaGrad(grad);
            for(Grad letGrad : grad.getLetovi())
                dodajLet(grad, letGrad);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String nazivDrzave) {
        try {
            nadjiDrzavuUpit.setString(1, nazivDrzave);
            ResultSet rs = nadjiDrzavuUpit.executeQuery();
            if (!rs.next()) return null;
            return dajDrzavuIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Grad nadjiGrad(String nazivGrada) {
        try {
            nadjiGradUpit.setString(1, nazivGrada);
            ResultSet rs = nadjiGradUpit.executeQuery();
            if (!rs.next()) return null;
            Drzava d = dajDrzavu(rs.getInt(4));
            return dajGradIzResultSeta(rs, d);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void obrisiLetoveZaGrad(Grad grad) {
        try {
            obrisiLetoveZaGradUpit.setInt(1, grad.getId());
            obrisiLetoveZaGradUpit.setInt(2, grad.getId());
            obrisiLetoveZaGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obrisiGrad(Grad grad) {
        try {
            obrisiLetoveZaGrad(grad);
            obrisiGradUpit.setInt(1, grad.getId());
            obrisiGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
