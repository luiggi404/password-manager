import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class prova {

    // ─── Palette colori ───────────────────────────────────────────────────────
    private static final Color BG        = new Color(18, 18, 30);
    private static final Color PANEL_BG  = new Color(28, 28, 45);
    private static final Color ACCENT    = new Color(99, 102, 241);   // indigo
    private static final Color ACCENT_H  = new Color(129, 140, 248);
    private static final Color TEXT      = new Color(226, 232, 240);
    private static final Color SUBTEXT   = new Color(148, 163, 184);
    private static final Color FIELD_BG  = new Color(15, 15, 25);
    private static final Color SUCCESS   = new Color(52, 211, 153);
    private static final Color ERROR     = new Color(248, 113, 113);

    // ─── Stato applicazione ───────────────────────────────────────────────────
    private JFrame     frame;
    private CardLayout cards;
    private JPanel     root;

    // pannello registrazione
    private JTextField     regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmField;
    private JLabel         regStatusLabel;

    // pannello login
    private JTextField     loginUsernameField;
    private JPasswordField loginPasswordField;
    private JLabel         loginStatusLabel;

    // pannello dashboard
    private JLabel dashWelcomeLabel;

    // ─── Costruttore / avvio ──────────────────────────────────────────────────
    public prova() {
        frame = new JFrame("🔐 Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 560);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        cards = new CardLayout();
        root  = new JPanel(cards);
        root.setBackground(BG);

        root.add(buildRegisterPanel(), "register");
        root.add(buildLoginPanel(),    "login");
        root.add(buildDashboard(),     "dashboard");

        frame.setContentPane(root);
        frame.setVisible(true);
        cards.show(root, "register");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PANNELLO REGISTRAZIONE
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildRegisterPanel() {
        JPanel panel = darkPanel();
        panel.setLayout(new BorderLayout());

        // intestazione
        JPanel header = darkPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(40, 40, 20, 40));

        JLabel icon  = iconLabel("🔐");
        JLabel title = styledLabel("Crea account", 24, Font.BOLD, TEXT);
        JLabel sub   = styledLabel("Registra le tue credenziali master", 13, Font.PLAIN, SUBTEXT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(icon);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        panel.add(header, BorderLayout.NORTH);

        // form
        JPanel form = darkPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        regUsernameField = styledTextField("Nome utente");
        regPasswordField = styledPasswordField("Password master");
        regConfirmField  = styledPasswordField("Conferma password");
        regStatusLabel   = statusLabel("");

        form.add(fieldGroup("👤  Nome utente", regUsernameField));
        form.add(Box.createVerticalStrut(14));
        form.add(fieldGroup("🔑  Password master", regPasswordField));
        form.add(Box.createVerticalStrut(14));
        form.add(fieldGroup("✅  Conferma password", regConfirmField));
        form.add(Box.createVerticalStrut(6));
        form.add(regStatusLabel);
        panel.add(form, BorderLayout.CENTER);

        // footer
        JPanel footer = darkPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(20, 40, 30, 40));

        JButton btnReg = accentButton("Registrati");
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReg.addActionListener(e -> handleRegister());

        JButton btnSwitch = linkButton("Hai già un account? Accedi");
        btnSwitch.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSwitch.addActionListener(e -> cards.show(root, "login"));

        footer.add(btnReg);
        footer.add(Box.createVerticalStrut(12));
        footer.add(btnSwitch);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PANNELLO LOGIN
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildLoginPanel() {
        JPanel panel = darkPanel();
        panel.setLayout(new BorderLayout());

        JPanel header = darkPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(40, 40, 20, 40));

        JLabel icon  = iconLabel("🔓");
        JLabel title = styledLabel("Bentornato", 24, Font.BOLD, TEXT);
        JLabel sub   = styledLabel("Accedi al tuo password manager", 13, Font.PLAIN, SUBTEXT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(icon);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        panel.add(header, BorderLayout.NORTH);

        JPanel form = darkPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 40, 0, 40));

        loginUsernameField = styledTextField("Nome utente");
        loginPasswordField = styledPasswordField("Password master");
        loginStatusLabel   = statusLabel("");

        form.add(fieldGroup("👤  Nome utente", loginUsernameField));
        form.add(Box.createVerticalStrut(14));
        form.add(fieldGroup("🔑  Password master", loginPasswordField));
        form.add(Box.createVerticalStrut(6));
        form.add(loginStatusLabel);
        panel.add(form, BorderLayout.CENTER);

        JPanel footer = darkPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(20, 40, 30, 40));

        JButton btnLogin = accentButton("Accedi");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        JButton btnSwitch = linkButton("Non hai un account? Registrati");
        btnSwitch.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSwitch.addActionListener(e -> cards.show(root, "register"));

        footer.add(btnLogin);
        footer.add(Box.createVerticalStrut(12));
        footer.add(btnSwitch);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DASHBOARD (post-login)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel panel = darkPanel();
        panel.setLayout(new BorderLayout());

        JPanel center = darkPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(80, 40, 40, 40));

        JLabel icon = iconLabel("🏠");
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        dashWelcomeLabel = styledLabel("Bentornato!", 22, Font.BOLD, SUCCESS);
        dashWelcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = styledLabel("Login effettuato correttamente.", 14, Font.PLAIN, SUBTEXT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel todo = styledLabel("(Qui puoi espandere il tuo password manager)", 12, Font.ITALIC, new Color(71, 85, 105));
        todo.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(icon);
        center.add(Box.createVerticalStrut(16));
        center.add(dashWelcomeLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(sub);
        center.add(Box.createVerticalStrut(40));
        center.add(todo);
        panel.add(center, BorderLayout.CENTER);

        JPanel footer = darkPanel();
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));
        footer.setBorder(new EmptyBorder(0, 40, 30, 40));

        JButton btnLogout = accentButton("Logout");
        btnLogout.setBackground(new Color(55, 25, 25));
        btnLogout.addActionListener(e -> {
            loginPasswordField.setText("");
            loginStatusLabel.setText("");
            cards.show(root, "login");
        });
        footer.add(btnLogout);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGICA — tua, invariata, solo spostata in metodi
    // ═════════════════════════════════════════════════════════════════════════

    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirm  = new String(regConfirmField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setStatus(regStatusLabel, "⚠  Compila tutti i campi.", ERROR); return;
        }
        if (!password.equals(confirm)) {
            setStatus(regStatusLabel, "⚠  Le password non coincidono.", ERROR); return;
        }

        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            byte[] hashedBytes = generateHash(salt, password);
            String hashHex = bytesToHex(hashedBytes);

            // salva su file username + salt + hash
            try (PrintWriter pw = new PrintWriter(new FileWriter("password.txt"))) {
                pw.println(username + ":" + saltBase64 + ":" + hashHex);
            }

            setStatus(regStatusLabel, "✓  Account creato! Ora accedi.", SUCCESS);
            regPasswordField.setText("");
            regConfirmField.setText("");

            // passa al login dopo 1 secondo
            Timer t = new Timer(1000, e -> cards.show(root, "login"));
            t.setRepeats(false);
            t.start();

        } catch (Exception ex) {
            setStatus(regStatusLabel, "Errore: " + ex.getMessage(), ERROR);
        }
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setStatus(loginStatusLabel, "⚠  Compila tutti i campi.", ERROR); return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader("password.txt"));
            String line = br.readLine();
            br.close();

            if (line == null) {
                setStatus(loginStatusLabel, "Nessun account trovato. Registrati.", ERROR); return;
            }

            String[] parti = line.split(":");
            if (parti.length < 3) {
                setStatus(loginStatusLabel, "File corrotto. Registrati di nuovo.", ERROR); return;
            }

            String savedUsername = parti[0];
            byte[] saltDecoded   = Base64.getDecoder().decode(parti[1]);
            String savedHash     = parti[2];

            if (!savedUsername.equals(username)) {
                setStatus(loginStatusLabel, "✗  Utente non trovato.", ERROR); return;
            }

            byte[] loginHash = generateHash(saltDecoded, password);
            String loginHex  = bytesToHex(loginHash);

            if (savedHash.equals(loginHex)) {
                setStatus(loginStatusLabel, "✓  Accesso riuscito!", SUCCESS);
                dashWelcomeLabel.setText("Bentornato, " + username + "!");
                Timer t = new Timer(800, e -> cards.show(root, "dashboard"));
                t.setRepeats(false);
                t.start();
            } else {
                setStatus(loginStatusLabel, "✗  Password errata.", ERROR);
            }

        } catch (FileNotFoundException ex) {
            setStatus(loginStatusLabel, "Nessun account trovato. Registrati.", ERROR);
        } catch (Exception ex) {
            setStatus(loginStatusLabel, "Errore: " + ex.getMessage(), ERROR);
        }
    }

    // ─── Tua logica hash — invariata ──────────────────────────────────────────
    public static byte[] generateHash(byte[] salt, String password) throws Exception {
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        m.update(salt);
        return m.digest(password.getBytes());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPER UI
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        return p;
    }

    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JLabel iconLabel(String emoji) {
        JLabel l = new JLabel(emoji);
        l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        return l;
    }

    private JLabel statusLabel(String text) {
        JLabel l = styledLabel(text, 12, Font.PLAIN, SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(4, 0, 0, 0));
        return l;
    }

    private void setStatus(JLabel label, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(SUBTEXT);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        styleField(f);
        return f;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(SUBTEXT);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT_H);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 80), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        f.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2, true),
                    new EmptyBorder(9, 11, 9, 11)
                ));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 55, 80), 1, true),
                    new EmptyBorder(10, 12, 10, 12)
                ));
                f.repaint();
            }
        });
    }

    private JPanel fieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(BG);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = styledLabel(labelText, 12, Font.BOLD, SUBTEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(field);
        return group;
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setText(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(320, 46));
        b.setMaximumSize(new Dimension(320, 46));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT_H); }
            public void mouseExited(MouseEvent e)  { b.setBackground(ACCENT); }
        });
        return b;
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(SUBTEXT);
        b.setBackground(BG);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(ACCENT_H); }
            public void mouseExited(MouseEvent e)  { b.setForeground(SUBTEXT); }
        });
        return b;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(prova::new);
    }
}
