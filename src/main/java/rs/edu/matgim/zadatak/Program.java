package rs.edu.matgim.zadatak;

import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        DB _db = new DB();
        _db.UkupnaDuzina();
        
        System.out.println();
        
        //_db.Zadatak("Petar","B");
        
        Scanner sc= new Scanner(System.in); 
        String ime = sc.nextLine();
        String kategorija = sc.nextLine();
        _db.Zadatak(ime,kategorija);
        
    }
}
