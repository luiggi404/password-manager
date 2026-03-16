import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class password {

    // ─── Colori e font ───────────────────────────────────────────────
    private static final Color BG          = new Color(18, 18, 28);
    private static final Color PANEL_BG    = new Color(28, 28, 42);
    private static final Color ACCENT      = new Color(99, 102, 241);   // indigo
    private static final Color ACCENT_DARK = new Color(67,  70, 200);
    private static final Color TEXT        = new Color(220, 220, 235);
    private static final Color SUBTLE      = new Color(120, 120, 150);
    private static final Color SUCCESS     = new Color(52, 211, 153);
    private static final Color ERROR       = new Color(248, 113, 113);
    private static final Font  FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font  FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);

    private static final String DB_FILE = "users.txt";

    // ─── Utility: campo di testo stilizzato ──────────────────────────
    private static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FONT_FIELD);
        f.setBackground(new Color(40, 40, 60));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 100), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private static JPasswordField styledPass() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_FIELD);
        f.setBackground(new Color(40, 40, 60));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 100), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private static JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private static JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(SUBTLE);
        return l;
    }

    // ─── GUI principale ──────────────────────────────────────────────
    public static void LoginGUI() {
        JFrame frame = new JFrame("Password Manager");
        frame.setSize(420, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG);
        frame.setLayout(new BorderLayout());

        // ── Titolo ──
        JLabel title = new JLabel("Password Manager", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        // ── Pannello form ──
        JPanel form = new JPanel(new GridLayout(5, 1, 0, 10));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(24, 36, 16, 36));

        JTextField usernameField = styledField();
        JPasswordField passwordField = styledPass();

        form.add(styledLabel("Username"));
        form.add(usernameField);
        form.add(styledLabel("Password"));
        form.add(passwordField);

        JLabel labelStatus = new JLabel("", SwingConstants.CENTER);
        labelStatus.setFont(FONT_LABEL);
        form.add(labelStatus);

        frame.add(form, BorderLayout.CENTER);

        // ── Bottoni ──
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setBackground(PANEL_BG);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 36, 28, 36));

        JButton signInBtn = styledButton("Registrati", SUBTLE.darker());
        JButton loginBtn  = styledButton("Login", ACCENT);

        btnPanel.add(signInBtn);
        btnPanel.add(loginBtn);
        frame.add(btnPanel, BorderLayout.SOUTH);

        // ── Azione: Registrazione ──
        signInBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                setStatus(labelStatus, "Compila tutti i campi", ERROR);
                return;
            }

            // Controlla se l'utente esiste già
            if (userExists(username)) {
                setStatus(labelStatus, "Username già in uso", ERROR);
                return;
            }

            try {
                SecureRandom rng = new SecureRandom();
                byte[] salt = new byte[16];
                rng.nextBytes(salt);
                byte[] hashed = generateHash(salt, password);

                String saltB64 = Base64.getEncoder().encodeToString(salt);
                String hashHex = toHex(hashed);

                // Append — non sovrascrive, aggiunge riga
                try (PrintWriter pw = new PrintWriter(new FileWriter(DB_FILE, true))) {
                    pw.println(username + ":" + saltB64 + ":" + hashHex);
                }

                setStatus(labelStatus, "Registrato con successo!", SUCCESS);
            } catch (Exception ex) {
                ex.printStackTrace();
                setStatus(labelStatus, "Errore nella registrazione", ERROR);
            }
        });

        // ── Azione: Login ──
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                setStatus(labelStatus, "Compila tutti i campi", ERROR);
                return;
            }

            try {
                String[] record = findUser(username);

                if (record == null) {
                    setStatus(labelStatus, "Utente non trovato", ERROR);
                    return;
                }

                byte[] saltDecoded = Base64.getDecoder().decode(record[1]);
                byte[] hashedLogin = generateHash(saltDecoded, password);

                if (record[2].equals(toHex(hashedLogin))) {
                    setStatus(labelStatus, "Bentornato, " + username + "!", SUCCESS);
                    frame.dispose();
                    openDashboard(username);
                } else {
                    setStatus(labelStatus, "Password errata", ERROR);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                setStatus(labelStatus, "Errore nel login", ERROR);
            }
        });

        frame.setVisible(true);
    }

    // ─── Dashboard post-login ─────────────────────────────────────────
    private static void openDashboard(String username) {
        JFrame dash = new JFrame("Password Manager — " + username);
        dash.setSize(480, 300);
        dash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dash.setLocationRelativeTo(null);
        dash.getContentPane().setBackground(BG);
        dash.setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Ciao, " + username + " 👋", SwingConstants.CENTER);
        welcome.setFont(FONT_TITLE);
        welcome.setForeground(TEXT);
        welcome.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        dash.add(welcome, BorderLayout.CENTER);

        JLabel sub = new JLabel("Login effettuato con successo.", SwingConstants.CENTER);
        sub.setFont(FONT_LABEL);
        sub.setForeground(SUBTLE);
        sub.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        dash.add(sub, BorderLayout.SOUTH);

        dash.setVisible(true);
    }

    // ─── Helper: cerca utente nel file ───────────────────────────────
    private static String[] findUser(String username) throws IOException {
        File f = new File(DB_FILE);
        if (!f.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3 && parts[0].equals(username))
                    return parts;
            }
        }
        return null;
    }

    private static boolean userExists(String username) {
        try { return findUser(username) != null; }
        catch (IOException e) { return false; }
    }

    // ─── Helper: status label colorata ───────────────────────────────
    private static void setStatus(JLabel label, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
    }

    // ─── Crypto ──────────────────────────────────────────────────────
    public static byte[] generateHash(byte[] salt, String passw) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(passw.getBytes("UTF-8"));
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ─── Main ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> LoginGUI());
    }
}
