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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class prova{


    public void LoginGUI(){
        
        //finestra
        JFrame frame = new JFrame("Login password");
        frame.setSize(450,250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6,1,5,5));

        //campi dove andremo a scrivere
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JLabel labelstatus = new JLabel("",SwingConstants.CENTER);

        //Bottoni
        JButton registerButton = new JButton("Registrati");
        JButton loginButton = new JButton("Login");
        
        //aggiunta componenti
        frame.add(new JLabel("Username: "));
        frame.add(usernameField);
        frame.add(new JLabel("Password: "));
        frame.add(passwordField);
        frame.add(registerButton);
        frame.add(loginButton);
        frame.add(labelstatus);

        //registrazione
        registerButton.addActionListener(e -> {
            try{
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);

                String saltBase64 = Base64.getEncoder().encodeToString(salt); 

                //metodo hash che trasforma stringa in 256 bit o 32 byte 
                byte[] HashedBytes = generateHash(salt, password);

                //byte non sono leggibili devo costruire una stringa 
                
                StringBuilder builder = new StringBuilder();
                for(byte b : HashedBytes){
                    builder.append(String.format("%02x", b)); 
                }
                /*%02x è un formato di stampa per rappresentare byte 
                in esadecimale.*/

                try(PrintWriter pw = new PrintWriter(new FileWriter("password.txt"))){
                    pw.println(username + ":" + saltBase64 + ":" + builder.toString());
                }
                labelstatus.setText("Registrazione completata!");
            }

            catch(Exception ex){
                ex.printStackTrace();
                labelstatus.setText("Errore nella registrazione");
            }
   
        });

        loginButton.addActionListener(e -> {
            try{
                BufferedReader br = new BufferedReader(new FileReader("password.txt"));
                String line = br.readLine();
                br.close();
                if(line == null){
                    labelstatus.setText("Nessun utente registrato");
                    return;
                }
                String[] parti = line.split(":");
                String usernameLogin = parti[0];
                String saltReadBase64 = parti[1];
                String hashRead = parti[2];


                byte[] saltReadDecoded = Base64.getDecoder().decode(saltReadBase64);

                String usernameInput = usernameField.getText();
                String passwordInput = new String(passwordField.getPassword());
                byte[] login_HashedBytes = generateHash(saltReadDecoded, passwordInput);

                if (!usernameInput.equals(usernameLogin)) {
                    labelstatus.setText("Username errato");
                    return;
                }
                StringBuilder builder2 = new StringBuilder();
                for(byte b : login_HashedBytes){
                    builder2.append(String.format("%02x", b));
                }

                if(hashRead.equals(builder2.toString())){
                   labelstatus.setText("Login effettuato correttamente");
                }
                else{
                    labelstatus.setText("Password errata");
                }

            }
            catch(Exception ex){
                ex.printStackTrace();
                labelstatus.setText("Errore nel Login...");
            }
        });

        frame.setVisible(true);
    }

    public static byte[] generateHash(byte[] salt,String password) throws Exception{
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        m.update(salt);
        byte[] HashedBytes = m.digest(password.getBytes());
        return HashedBytes;
    }   

    public static void main(String[] args) throws Exception {
        prova app = new prova();
        app.LoginGUI();
    }

}