package view;

import controller.AuthController;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthController authController;

    public LoginFrame() {
        authController = new AuthController();
        initUI();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        setTitle("Login - Smart Laundry");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //? Header  
        JLabel lblTitle = new JLabel("Silakan Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(lblTitle, BorderLayout.NORTH);

        //? Form Panel
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        panelForm.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelForm.add(txtUsername);

        panelForm.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        //? Button Panel
        JPanel panelButton = new JPanel();
        panelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        btnLogin = new JButton("Masuk");
        panelButton.add(btnLogin);
        add(panelButton, BorderLayout.SOUTH);

        //? Action Listener
        btnLogin.addActionListener(e -> prosesLogin());

        //? Fitur tambahan: Tekan Enter untuk login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void prosesLogin() {
        // TODO Auto-generated method stub
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authController.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!");

            new LaundryFrame().setVisible(true);

            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
