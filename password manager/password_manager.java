import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Base64;



public class password_manager {

    public static void LoginGUI(){

        //creazione finestra
        JFrame frame = new JFrame("password manager");
        frame.setSize(450,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6,1,5,5));

        JLabel labelStatus = new JLabel("",SwingConstants.CENTER);

        //cosa sta nella finestra
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("login");
        JButton signIn_Button = new JButton("sign in");

        //aggiungo quello che ho creato alla finestra
        frame.add(new JLabel("username:"));
        frame.add(usernameField);
        frame.add(new JLabel("password:"));
        frame.add(passwordField);
        frame.add(signIn_Button);
        frame.add(loginButton);
        frame.add(labelStatus);


        signIn_Button.addActionListener(e->{
            try{
                String username = usernameField.getText();

                String password = new String(passwordField.getPassword());
                
                //sale
                SecureRandom randomico = new SecureRandom();
                byte[] salt = new byte[16];
                randomico.nextBytes(salt);
                //genero hash per la password
                byte[] hashed = generateHash(salt,password);
                
                //codifico l'hash per far si che sia leggibile su file
                String salted = Base64.getEncoder().encodeToString(salt);

                //rendo leggibile la password(cioe l'hash)
                StringBuilder builder = new StringBuilder();
                for(byte b : hashed){
                    builder.append(String.format("%02x", b));
                }
                //metto tutto su file cosi da leggerlo dopo per il login
                try(PrintWriter pw = new PrintWriter(new FileWriter("password.txt"))){
                    pw.println(username + ":" + salted + ":" + builder);
                }

                labelStatus.setText("Registrato!!");
            }
            catch (Exception ex){
                ex.printStackTrace();
                labelStatus.setText("Registrazione fallita");
            }
            });

            loginButton.addActionListener(e->{

                try{
                    //leggo dal file le informazioni che mi servono...
                    BufferedReader br = new BufferedReader(new FileReader("password.txt"));
                    String line = br.readLine();
                    br.close();
                    String[] signIn_info = line.split(":");

                    //info dal file
                    String username_Signin = signIn_info[0];
                    String salt_Signin = signIn_info[1];
                    String password_Signin = signIn_info[2];

                    //info dall'user
                    String username_login = usernameField.getText();
                    String password_login = new String(passwordField.getPassword());
                    byte[] saltDecoded = Base64.getDecoder().decode(salt_Signin);
                    byte[] hashedLogin = generateHash(saltDecoded, password_login);
                    
                    if (!username_Signin.equals(username_login)) {
                        labelStatus.setText("Username errato");
                        return;
                    }

                    StringBuilder builder2 = new StringBuilder();
                    for(byte b : hashedLogin){
                        builder2.append(String.format("%02x", b));
                    } 

                    if(password_Signin.equals(builder2.toString())){
                        labelStatus.setText("Login effettuato!");
                    }
                    else{
                        labelStatus.setText("password errata");
                    }

                }
                catch(Exception ex){
                    ex.printStackTrace();
                    labelStatus.setText("Errore nel login...");
                }
            });

        frame.setVisible(true);

    }

    public static byte[] generateHash(byte[] salt, String passw) throws Exception{

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashed = md.digest(passw.getBytes());
        return hashed;
    }

    public static void main(String[] args) {
        password_manager pw = new password_manager();
        pw.LoginGUI();
    }

}
