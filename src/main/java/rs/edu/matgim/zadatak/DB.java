package rs.edu.matgim.zadatak;

import java.sql.*;

public class DB {

    //Connection connection;
    String connectionString = "jdbc:sqlite:src\\main\\java\\KompanijaZaPrevoz.db";
    
    
    //print firma
    public void printFirma() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Firma");
            while (rs.next()) {
                int IdFil = rs.getInt("IdFir");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");
                String Tel1 = rs.getString("Tel1");
                String Tel2 = rs.getString("Tel2");

                System.out.println(String.format("%d\t%s\t%s\t%s\t%s", IdFil, Naziv, Adresa, Tel1, Tel2));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    
    //ukupna duzina
    public void UkupnaDuzina(){
         try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) 
        {
            String upit = "SELECT Z.IDZap,Z.ImePrezime,SUM(Duzina) FROM Vozi V, Zaposlen Z, Putovanje P WHERE Z.IDZap=V.IDZap AND V.IDPut=P.IDPut GROUP BY Z.IDZap,Z.ImePrezime";
            ResultSet rs = s.executeQuery(upit);
            while (rs.next()) {
                int IdZap = rs.getInt(1);
                String Naziv = rs.getString(2);
                int UkupnaDuzina = rs.getInt(3);

                System.out.println(String.format("%d  %20s  %d\n", IdZap, Naziv, UkupnaDuzina));
            }
            

        } catch (SQLException ex) {
            System.out.println("Greska u ukupnoj duzini");
            System.out.println(ex);
        }
    }
    
    //zaposli vozaca
    public int Zadatak(String imeIPrezime, String Kategorija)
    {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) 
        {
            //nalazim id vozaca
            String u1 = "SELECT MAX(IDZap)+1 FROM Zaposlen";
            ResultSet rs = s.executeQuery(u1);
            int IdZap = rs.getInt(1);
            
            //unosim vozaca u tabelu Zaposlen
            String UnesiVozaca = "INSERT INTO Zaposlen (IdZap,ImePrezime,Staz) VALUES(?,?,0)";
            PreparedStatement st1 = conn.prepareStatement(UnesiVozaca);
            st1.setInt(1,IdZap);
            st1.setString(2,imeIPrezime);
            st1.executeUpdate();

            //unosim vozaca u tabelu Vozac
            String UnesiKategoriju = "INSERT INTO Vozac (Kategorija,IdZap) VALUES(?,?)";
            PreparedStatement st2 = conn.prepareStatement(UnesiKategoriju);
            st2.setString(1,Kategorija);
            st2.setInt(2, IdZap);
            st2.executeUpdate();
            
            //provera koliko ima najavljenih putovanja
            String u2 = "SELECT COUNT(IDPut) FROM Putovanje WHERE Status='N'";
            ResultSet rs2 = s.executeQuery(u2);
            int BrojNajavljenih = rs2.getInt(1);
            //System.out.println(BrojNajavljenih); //neka provera
            
            //ukoliko nema najavljenih putovanja
            if(BrojNajavljenih==0)
            {
                return -1;
            }
            
            else
            {
                String u3 = "SELECT COUNT(DISTINCT P.IDPut) FROM Putovanje P, Vozi V WHERE P.IDPut = V.IDPut and P.Status='N'";
                ResultSet rs3=s.executeQuery(u3);
                int BrojDodeljenih = rs3.getInt(1);
                
                //ako su sva putovanja vec dodeljena
                if(BrojDodeljenih == BrojNajavljenih)
                {
                    String minIdPut = "SELECT MIN(IDPut) FROM Putovanje WHERE Status='N'";
                    ResultSet min=s.executeQuery(minIdPut);
                    int IdPut=min.getInt(1);
                    
                    String dodelaPutovanja="INSERT INTO Vozi (IDZap,IDPut) VALUES(?,?)";
                    PreparedStatement stDodela = conn.prepareStatement(dodelaPutovanja);
                    stDodela.setInt(1,IdZap);
                    stDodela.setInt(2, IdPut);                    
                }
                
                //ako postoji neko putovanje koje nije dodeljeno
                else
                {
                    String stringic = "SELECT MIN(P.IDPut) FROM Putovanje P WHERE P.Status='N' EXCEPT SELECT V.IDPut FROM  Vozi V";
                    ResultSet rsrs=s.executeQuery(stringic);
                    int IdPut1=rsrs.getInt(1);
                    
                    String dodelaPutovanja1="INSERT INTO Vozi (IDZap,IDPut) VALUES(?,?)";
                    PreparedStatement stDodela1 = conn.prepareStatement(dodelaPutovanja1);
                    stDodela1.setInt(1,IdZap);
                    stDodela1.setInt(2, IdPut1);
                }
                
                System.out.println("Uspesna realizacija!");
                return 1;
            }

        } catch (SQLException ex) {
            System.out.println("Dogodila se greska.");
            System.out.println(ex);
        }
        return 1;
    }


}
