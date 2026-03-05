import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.security.SecureRandom;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.FileReader;




public class Main{
    public static void main(String[] args) throws Exception {


        /*Scanner è una classe Java che serve per leggere 
        dati da varie fonti (tastiera, file, ecc.).
        System.in è un flusso di input standard
        */ 
        Scanner scanner = new Scanner(System.in);
        System.out.println("dimmi il nome utente che vuoi utilizzare : ");
        String nome_utente = scanner.nextLine();
        System.out.print("dammi la password : ");
        //prendo tutto ciò che scrivi fino a INVIO
        String password = scanner.nextLine();

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        System.out.println("Ecco il salt generato: " + Arrays.toString(salt));

        String saltBase64 = Base64.getEncoder().encodeToString(salt); 

        //metodo hash che trasforma stringa in 256 bit o 32 byte 
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        m.update(salt);
        byte[] HashedBytes = m.digest(password.getBytes());

        //byte non sono leggibili devo costruire una stringa 
        
        StringBuilder builder = new StringBuilder();
        for(byte b : HashedBytes){
            builder.append(String.format("%02x", b)); 
        }
        /*%02x è un formato di stampa per rappresentare byte 
         in esadecimale.*/


        //scrivo su file il sale e l'hash perchè il salt mi riservirà al login
        try(PrintWriter pw = new PrintWriter(new FileWriter("password.txt"))){
            pw.println(saltBase64 + ":" + builder.toString());
        }

        //leggo quello che ho scritto e memorizzo salt + hash
        BufferedReader br = new BufferedReader(new FileReader("password.txt"));
        String line = br.readLine();
        br.close();

        String[] parti = line.split(":");
        String saltReadBase64 = parti[0];
        String hashRead = parti[1];

        byte[] saltReadDecoded = Base64.getDecoder().decode(saltReadBase64);

        System.out.println("Inserisci la tua password : ");
        String login_password = scanner.nextLine();
        MessageDigest md_login = MessageDigest.getInstance("SHA-256");

        md_login.update(saltReadDecoded);
        byte[] login_HashedBytes = md_login.digest(login_password.getBytes());

        StringBuilder builder2 = new StringBuilder();
        for(byte b : login_HashedBytes){
            builder2.append(String.format("%02x", b));
        }

        if(hashRead.equals(builder2.toString())){
            System.out.println("Login effettuato correttamente");
        }
        else{
            System.out.println("Password errata");
        }


        
        System.out.println("ecco il nome utente " + nome_utente +  " e l'hash ottenuto : " + builder.toString());

    }
}