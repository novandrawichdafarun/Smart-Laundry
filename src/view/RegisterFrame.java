/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.Color;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import controller.AuthController;

public class RegisterFrame extends JFrame {

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JButton btnRegister;
    private AuthController authController;

    //? Warna Palet
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); //* Biru */
    private final Color BG_COLOR = new Color(236, 240, 241); //* Abu-abu muda */ 

    //? Warna untuk Notifikasi
    private final Color SUCCESS_COLOR = new Color(46, 204, 113); // Hijau
    private final Color ERROR_COLOR = new Color(231, 76, 60);    // Merah
    private final Color WARNING_COLOR = new Color(243, 156, 18); // Oranye

    public RegisterFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Register - Smart Laundry");
        setSize(550, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //? Logo
        Image appIcon = appIcon("/img/Logo.png", 128, 128);
        if (appIcon != null) {
            setIconImage(appIcon);
        }

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
        ImageIcon logoIcon = imageIcon("/img/logo.png", 100, 100);

        JLabel lblIcon = new JLabel(logoIcon);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Smart Laundry");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Silakan mendaftar untuk melanjutkan");
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
        btnRegister = new JButton("DAFTAR");
        styleButton(btnRegister, PRIMARY_COLOR);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        //? Registrasi Panel
        JLabel lblLogin = new JLabel("Sudah punya akun? Login disini");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLogin.setForeground(PRIMARY_COLOR);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(lblIcon);
        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(lblTitle);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblUser);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtUsername);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblPass);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtPassword);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnRegister);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblLogin);

        add(mainPanel);

        //? Action Listener
        btnRegister.addActionListener(e -> prosesRegister());
        getRootPane().setDefaultButton(btnRegister);
    }

    public void prosesRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showCustomDialog("Peringatan", "Username dan Password tidak boleh kosong.", WARNING_COLOR);
            return;
        }

        authController = new AuthController();
        boolean registered = authController.register(username, password);

        if (registered) {
            showCustomDialog("Sukses", "Registrasi berhasil! Silakan login.", SUCCESS_COLOR);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            showCustomDialog("Gagal", "Registrasi gagal. Username mungkin sudah terdaftar.", ERROR_COLOR);
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

    private ImageIcon imageIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
            Image srcImg = originalIcon.getImage();

            BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = resizedImg.createGraphics();

            // Aktifkan Anti-Aliasing dan Interpolasi kualitas tinggi
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gambar ulang
            g2.drawImage(srcImg, 0, 0, width, height, null);
            g2.dispose();

            return new ImageIcon(resizedImg);
        } catch (Exception e) {
            System.err.println("Gagal load gambar: " + path);
            return null;
        }
    }

    private Image appIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
            Image srcImg = originalIcon.getImage();

            // Buat canvas kosong dengan ukuran yang diinginkan
            java.awt.image.BufferedImage resizedImg = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

            // Gambar ulang dengan setting kualitas TERTINGGI
            java.awt.Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(srcImg, 0, 0, width, height, null);
            g2.dispose();

            return resizedImg;
        } catch (Exception e) {
            System.err.println("Gagal load icon: " + path);
            return null;
        }
    }

}
