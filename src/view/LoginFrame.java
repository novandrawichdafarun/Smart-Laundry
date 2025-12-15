package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import controller.AuthController;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final AuthController authController;

    //? Warna Palet
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); //* Biru */
    private final Color BG_COLOR = new Color(236, 240, 241); //* Abu-abu muda */ 
    private final Color TEXT_COLOR = new Color(44, 62, 80); //* Abu-bu tua */

    //? Warna untuk Notifikasi
    private final Color SUCCESS_COLOR = new Color(46, 204, 113); // Hijau
    private final Color ERROR_COLOR = new Color(231, 76, 60);    // Merah
    private final Color WARNING_COLOR = new Color(243, 156, 18); // Oranye

    public LoginFrame() {
        authController = new AuthController();
        initUI();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        setTitle("Login - Smart Laundry");
        setSize(550, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //? Background
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        //? Panel Utama 
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        //? Shadow 
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(
                new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)));

        //? Header  
        JLabel lblIcon = new JLabel("🧺", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Smart Laundry");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Silakan login untuk melanjutkan");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //? Form Panel
        JLabel lblUser = new JLabel("Username");
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtUsername = new JTextField();
        styleTextField(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);

        //? Button Panel
        btnLogin = new JButton("MASUK");
        styleButton(btnLogin, PRIMARY_COLOR);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        //? Menambahkan komponen ke panel dengan jarak (RigidArea)
        mainPanel.add(lblIcon);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblTitle);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(lblUser);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtUsername);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblPass);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtPassword);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnLogin);

        add(mainPanel); //! Tambahkan kartu ke frame

        //? Action Listener
        btnLogin.addActionListener(e -> prosesLogin());
        //? Tekan Enter untuk login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void prosesLogin() {
        // TODO Auto-generated method stub
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showCustomDialog("Peringatan", "Username dan Password harus diisi!", WARNING_COLOR);
            return;
        }

        if (authController.login(user, pass)) {
            showCustomDialog("Berhasil", "Login Berhasil!\nSelamat Datang.", SUCCESS_COLOR);

            new Timer(500, e -> {
                new LaundryFrame().setVisible(true);
                this.dispose();
                ((Timer) e.getSource()).stop();
            }).start();
        } else {
            showCustomDialog("Gagal", "Username atau Password salah!", ERROR_COLOR);
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        //? Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(color);
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void showCustomDialog(String title, String message, Color themeColor) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true); // Hilangkan border window bawaan
        dialog.setLayout(new BorderLayout());

        // Panel Utama Dialog
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(themeColor, 2)); // Border warna tema
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        //? Judul
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(themeColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //? Pesan
        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>"); // HTML untuk word wrap
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMsg.setForeground(Color.DARK_GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        //? Tombol OK
        JButton btnOk = new JButton("OK");
        styleButton(btnOk, themeColor);
        btnOk.setPreferredSize(new Dimension(80, 30));
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.addActionListener(e -> dialog.dispose());
        getRootPane().setDefaultButton(btnOk);

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblMsg);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(btnOk);

        dialog.add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this); // Muncul di tengah parent
        dialog.setSize(300, 180); // Ukuran fix agar rapi

        // Efek rounded corner (Opsional, jika didukung sistem)
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 300, 180, 15, 15));

        dialog.setVisible(true);
    }

}
