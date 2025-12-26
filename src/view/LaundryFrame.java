package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import controller.TransaksiController;
import controller.UserController;
import models.CuciBasah;
import models.CuciKering;
import models.Layanan;
import models.Setrika;
import utils.StrukPrinter;
import utils.UserSession;

public class LaundryFrame extends JFrame {

    private final UserController userController = new UserController();
    private JDialog inputDialog;

    private JTextField txtNama, txtHp, txtAlamat, txtBerat;
    private JComboBox<String> cmbLayanan;
    private JCheckBox chkExpress;
    private JLabel lblTotal;
    private JTable table;
    private DefaultTableModel tableModel;
    private final TransaksiController controller;

    //? Stats Penjualan
    private JLabel lblStatOmset, lblStatTransaksi, lblStatBerat;
    private JPanel statsPanel;

    //? Grafik
    private SimpleBarChart chartPanel;

    //? Warna Palet
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color PANEL_COLOR = Color.WHITE;

    //? Warna untuk Notifikasi (Tambahkan ini)
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color ERROR_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public LaundryFrame() {
        controller = new TransaksiController();
        initUI();
        controller.loadData(tableModel);
        updateStatistik();
        if (chartPanel != null) {
            chartPanel.updateData(controller.getGrafikPenjualan());
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void initUI() {
        // TODO Auto-generated method stub
        setTitle("Smart Laundry System - " + UserSession.getRole().toUpperCase());
        setSize(1200, 675);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        //? Logo
        Image appIcon = appIcon("/img/Logo.png", 128, 128);
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        //? Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        ImageIcon logoIcon = imageIcon("/img/Logo.png", 40, 40);

        JLabel lblBrand = new JLabel("Smart Laundry System", logoIcon, JLabel.LEFT);
        lblBrand.setIconTextGap(10);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setForeground(Color.WHITE);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setOpaque(false);

        JLabel lblUser = new JLabel("Halo, " + UserSession.getUsername() + " (" + UserSession.getRole() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(230, 230, 230));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin logout?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserSession.logout();
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        profilePanel.add(lblUser);
        profilePanel.add(btnLogout);

        headerPanel.add(lblBrand, BorderLayout.WEST);
        headerPanel.add(profilePanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ! Tabbe Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        //? Dashboard Panel
        if (!UserSession.isPelanggan()) {
            JPanel dashboardPanel = createDashboardPanel();
            tabbedPane.addTab("  Dashboard  ", dashboardPanel);
        }

        //? Transaksi Panel
        JPanel riwayatPanel = createRiwayatPanel();
        String tabTitle = UserSession.isPelanggan() ? "  Pesanan Saya  " : "  Riwayat Transaksi  ";
        tabbedPane.addTab(tabTitle, riwayatPanel);

        if (UserSession.isSuperAdmin()) {
            JPanel userPanel = createUserPanel();
            tabbedPane.addTab("  Kelola User  ", userPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);

        initListeners();
    }

    @SuppressWarnings("UseSpecificCatch")
    private void prosesTransaksi() {
        // TODO Auto-generated method stub
        try {
            if (UserSession.isPelanggan()) {
                // Pastikan pelanggan hanya bisa membuat pesanan untuk dirinya sendiri
                String pelangganNama = UserSession.getUsername();
                txtNama.setText(pelangganNama);
            }
            String nama = txtNama.getText();
            String hp = txtHp.getText();
            String alamat = txtAlamat.getText();
            double berat = Double.parseDouble(txtBerat.getText());
            boolean Express = chkExpress.isSelected();
            String jenis = (String) cmbLayanan.getSelectedItem();

            if (nama.isEmpty() || txtBerat.getText().isEmpty() || alamat.isEmpty()) {
                showCustomDialog("Peringatan", "Nama, Berat, dan Alamat harus diisi!", WARNING_COLOR);
                return;
            }

            Layanan layanan = switch (jenis) {
                case "Cuci Basah" ->
                    new CuciBasah(berat, Express);
                case "Cuci Kering" ->
                    new CuciKering(berat, Express);
                default ->
                    new Setrika(berat, Express);
            };
            double total = layanan.hitungTotal();

            if (controller.simpanTransaksi(nama, hp, alamat, jenis, berat, Express, total)) {
                String msg = "Transaksi Berhasil Disimpan!<br>Total: Rp " + String.format("%,.0f", total);

                if (inputDialog != null && inputDialog.isVisible()) {
                    inputDialog.dispose();
                }

                showCustomDialog("Sukses", msg, SUCCESS_COLOR);

                controller.loadData(tableModel);
                updateStatistik();
                if (chartPanel != null) {
                    chartPanel.updateData(controller.getGrafikPenjualan());
                }
                resetForm();
            } else {
                showCustomDialog("Error", "Gagal menyimpan ke database.", ERROR_COLOR);
            }
        } catch (NumberFormatException e) {
            showCustomDialog("Error", "Berat harus berupa angka valid.", ERROR_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
            showCustomDialog("Error", "Terjadi kesalahan: " + e.getMessage(), ERROR_COLOR);
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtHp.setText("");
        txtAlamat.setText(""); // Reset alamat
        txtBerat.setText("");
        lblTotal.setText("Total: Rp 0");
    }

    private void prosesUpdateStatus() {
        // TODO Auto-generated method stub
        int baris = table.getSelectedRow();
        if (baris >= 0) {
            int id = (int) tableModel.getValueAt(baris, 0);
            String status = (String) tableModel.getValueAt(baris, 7);

            controller.updateStatus(id, status);
            controller.loadData(tableModel);
            updateStatistik();

            showCustomDialog("Status Updated", "Status cucian berhasil diperbarui.", INFO_COLOR);
        } else {
            showCustomDialog("Pilih Data", "Klik baris transaksi pada terlebih dahulu!", WARNING_COLOR);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void prosesUpdateData() {
        try {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                String nama = tableModel.getValueAt(row, 2).toString();
                String layanan = tableModel.getValueAt(row, 3).toString();
                double berat = Double.parseDouble(tableModel.getValueAt(row, 4).toString());

                String tipePaket = tableModel.getValueAt(row, 5).toString();
                boolean isExpress = "Express".equalsIgnoreCase(tipePaket);

                String status = tableModel.getValueAt(row, 7).toString();

                showEditTransaksiDialog(id, nama, layanan, berat, status, isExpress);
            } else {
                showCustomDialog("Peringatan", "Pilih transaksi yang ingin diedit!", WARNING_COLOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showCustomDialog("Error", "Gagal membuka dialog update: " + e.getMessage(), ERROR_COLOR);
        }
    }

    private void prosesDelete() {
        int baris = table.getSelectedRow();

        if (baris >= 0) {
            int id = (int) tableModel.getValueAt(baris, 0);
            String nama = (String) tableModel.getValueAt(baris, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah anda yakin ingin menghapus transaksi milik " + nama + "?\nData yang dihapus tidak bisa dikembalikan.",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteTransaksi(id)) {
                    showCustomDialog("Berhasil", "Data transaksi berhasil dihapus.", SUCCESS_COLOR);
                    controller.loadData(tableModel);
                    updateStatistik();
                    chartPanel.updateData(controller.getGrafikPenjualan());
                } else {
                    showCustomDialog("Gagal", "Terjadi kesalahan saat menghapus data.", ERROR_COLOR);
                }
            }
        } else {
            showCustomDialog("Pilih Data", "Silahkan pilih baris data yang ingin dihapus!", WARNING_COLOR);
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txt;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));

        //? Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.DARK_GRAY);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        if (table.getColumnCount() > 7) {
            table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (value == null) {
                        return this;
                    }

                    String status = (String) value;
                    this.setHorizontalAlignment(CENTER);
                    this.setFont(new Font("Segoe UI", Font.BOLD, 11));

                    if (!isSelected) {
                        switch (status) {
                            case "Diterima" -> {
                                this.setForeground(WARNING_COLOR); // Tulisan Orange
                                this.setBackground(new Color(255, 248, 225)); // Background Krem
                            }
                            case "Dicuci" -> {
                                this.setForeground(INFO_COLOR); // Tulisan Biru
                                this.setBackground(new Color(235, 245, 251)); // Background Biru Muda
                            }
                            case "Selesai" -> {
                                this.setForeground(SUCCESS_COLOR); // Tulisan Hijau
                                this.setBackground(new Color(233, 247, 239)); // Background Hijau Muda
                            }
                            case "Diambil" -> {
                                this.setForeground(Color.GRAY); // Tulisan Abu
                                this.setBackground(new Color(245, 245, 245)); // Background Abu Muda
                            }
                            default -> {
                                this.setForeground(Color.BLACK);
                                this.setBackground(Color.WHITE);
                            }
                        }
                    }

                    return this;
                }
            });
        }
    }

    private void showCustomDialog(String title, String message, Color themeColor) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        // Border berwarna sesuai tipe pesan
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeColor, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(themeColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(Color.DARK_GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnOk = new JButton("Tutup");
        // Styling tombol manual di sini jika method styleButton tidak bisa diakses
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(themeColor);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setMaximumSize(new Dimension(100, 35));

        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.addActionListener(e -> dialog.dispose());

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblMsg);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(btnOk);

        dialog.add(contentPanel);
        dialog.setSize(320, 200);
        dialog.setLocationRelativeTo(this);

        try {
            dialog.setShape(new RoundRectangle2D.Double(0, 0, 320, 200, 15, 15));
        } catch (Exception ignored) {
        } // Fallback jika sistem tidak support shaping

        dialog.setVisible(true);
    }

    private JPanel createStatCard(String title, String value, Color color, String iconSymbol) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                new EmptyBorder(10, 15, 10, 15)
        ));

        //? Shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                card.getBorder()
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(Color.DARK_GRAY);

        //? Icon simbol sederhana menggunakan teks
        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIcon.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100)); // Transparan

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        //! Set ukutan preferensi agar seragam
        card.setPreferredSize(new Dimension(180, 70));

        return card;
    }

    private void updateStatistik() {
        double[] stats = controller.getStatistikHarian();
        if (lblStatOmset != null) {
            lblStatOmset.setText("Rp " + String.format("%,.0f", stats[0]));
        }
        if (lblStatTransaksi != null) {
            lblStatTransaksi.setText(String.format("%.0f", stats[1]) + " Pesanan");
        }
        if (lblStatBerat != null) {
            lblStatBerat.setText(String.format("%.1f", stats[2]) + " Kg");
        }
    }

    private void prosesCetakStruk() {
        int baris = table.getSelectedRow();

        if (baris >= 0) {
            int id = (int) tableModel.getValueAt(baris, 0);
            String tanggal = (String) tableModel.getValueAt(baris, 1);
            String nama = (String) tableModel.getValueAt(baris, 2);
            String layanan = (String) tableModel.getValueAt(baris, 3);
            double berat = (double) tableModel.getValueAt(baris, 4);
            double total = (double) tableModel.getValueAt(baris, 6);
            String status = (String) tableModel.getValueAt(baris, 7);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cetak struk untuk transaksi #" + id + "?",
                    "Konfirmasi Cetak",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                StrukPrinter printer = new StrukPrinter(id, tanggal, nama, layanan, berat, total, status);
                printer.printStruk();
            }
        } else {
            showCustomDialog("Pilih Data", "Pilih transaksi yang ingin dicetak!", WARNING_COLOR);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void hitungLive() {
        try {
            // Cek jika field berat kosong, jangan error
            if (txtBerat.getText().isEmpty()) {
                lblTotal.setText("Total: Rp 0");
                return;
            }

            double berat = Double.parseDouble(txtBerat.getText());
            boolean isExpress = chkExpress.isSelected();
            String jenis = (String) cmbLayanan.getSelectedItem();

            // Gunakan logika Model yang sudah ada
            Layanan layanan = switch (jenis) {
                case "Cuci Basah" ->
                    new CuciBasah(berat, isExpress);
                case "Cuci Kering" ->
                    new CuciKering(berat, isExpress);
                default ->
                    new Setrika(berat, isExpress);
            };
            double total = layanan.hitungTotal();

            // Update label dengan format Rupiah
            lblTotal.setText("Total: Rp " + String.format("%,.0f", total));

        } catch (NumberFormatException e) {
            // Jika user mengetik huruf, biarkan 0 atau abaikan
            lblTotal.setText("Total: Rp 0");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setPreferredSize(new Dimension(320, 0));

        JLabel lblInputTitle = new JLabel("Input Transaksi Baru");
        lblInputTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInputTitle.setForeground(PRIMARY_COLOR);
        lblInputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.add(lblInputTitle);
        inputPanel.add(Box.createVerticalStrut(20));

        // Form Fields
        if (UserSession.isPelanggan()) {
            JLabel lblInfo = new JLabel("<html><i>Nama pelanggan otomatis diisi sesuai akun Anda.</i></html>");
            lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblInfo.setForeground(Color.GRAY);
            lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
            inputPanel.add(lblInfo);
            inputPanel.add(Box.createVerticalStrut(10));
        }

        inputPanel.add(createLabel("Nama Pelanggan:"));
        txtNama = createTextField();
        if (UserSession.isPelanggan()) {
            txtNama.setText(UserSession.getUsername());
            txtNama.setEditable(false);
            txtNama.setBackground(new Color(240, 240, 240));

        }
        inputPanel.add(txtNama);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(createLabel("No Hp:"));
        txtHp = createTextField();
        txtHp.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField tf = (JTextField) input;
                String text = tf.getText().trim();
                return text.matches("^(08|62)\\d{8,12}$") || text.isEmpty(); // Sederhana
            }
        });
        inputPanel.add(txtHp);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(createLabel("Alamat:"));
        txtAlamat = createTextField();
        inputPanel.add(txtAlamat);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(createLabel("Berat (Kg):"));
        txtBerat = createTextField();
        inputPanel.add(txtBerat);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(new JLabel("Jenis Layanan:"));
        cmbLayanan = new JComboBox<>(new String[]{"Cuci Basah", "Cuci Kering", "Setrika"});
        cmbLayanan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLayanan.setBackground(Color.WHITE);
        cmbLayanan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbLayanan.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(cmbLayanan);
        inputPanel.add(Box.createVerticalStrut(10));

        chkExpress = new JCheckBox("Layanan Express (+5000/kg)");
        chkExpress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkExpress.setBackground(PANEL_COLOR);
        chkExpress.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(chkExpress);
        inputPanel.add(Box.createVerticalStrut(20));

        JButton btnHitung = createButton("Simpan Transaksi", PRIMARY_COLOR);
        btnHitung.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnHitung.addActionListener(e -> prosesTransaksi());

        lblTotal = new JLabel("Total: Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(39, 174, 96));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.add(lblTotal);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(btnHitung);
        initListeners();

        return inputPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Statistik Cards (Atas)
        statsPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // Gap diperlebar
        statsPanel.setBackground(BG_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Init cards (variable lblStat... sudah dideklarasikan di class)
        lblStatOmset = new JLabel("Rp 0");
        lblStatTransaksi = new JLabel("0");
        lblStatBerat = new JLabel("0 kg");

        JPanel card1 = createStatCard("Pendapatan Hari Ini", "Rp 0", new Color(46, 204, 113), "$");
        lblStatOmset = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        JPanel card2 = createStatCard("Total Transaksi", "0", new Color(52, 152, 219), "#");
        lblStatTransaksi = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        JPanel card3 = createStatCard("Total Berat (Kg)", "0", new Color(243, 156, 18), "Kg");
        lblStatBerat = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        statsPanel.add(card1);
        statsPanel.add(card2);
        statsPanel.add(card3);

        panel.add(statsPanel, BorderLayout.NORTH);

        // 2. Grafik (Tengah)
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblChartTitle = new JLabel("Statistik Penjualan (7 Hari Terakhir)");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChartTitle.setForeground(Color.DARK_GRAY);
        lblChartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        chartPanel = new SimpleBarChart(controller.getGrafikPenjualan());

        chartContainer.add(lblChartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        panel.add(chartContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRiwayatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerJPanel = new JPanel(new BorderLayout());
        headerJPanel.setBackground(BG_COLOR);
        headerJPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblCari = new JLabel("Cari:");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField txtCari = new JTextField(15);
        txtCari.setPreferredSize(new Dimension(150, 35));
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String keyword = txtCari.getText();
                controller.cariData(tableModel, keyword);
            }
        });

        JLabel lblTitle = new JLabel("Data Transaksi Laundry");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BG_COLOR);

        if (!UserSession.isPelanggan()) {
            JButton btnTambah = createButton("+ Tambah Data", PRIMARY_COLOR);
            btnTambah.setPreferredSize(new Dimension(140, 35));

            btnTambah.addActionListener(e -> showInputDialog());

            rightPanel.add(btnTambah);
        } else {
            JButton btnPesan = createButton("Buat Pesanan", PRIMARY_COLOR);
            btnPesan.setPreferredSize(new Dimension(140, 35));

            btnPesan.addActionListener(e -> showInputDialog());

            rightPanel.add(btnPesan);
        }

        rightPanel.add(lblCari);
        rightPanel.add(txtCari);

        headerJPanel.add(lblTitle, BorderLayout.WEST);
        headerJPanel.add(rightPanel, BorderLayout.EAST);

        panel.add(headerJPanel, BorderLayout.NORTH);

        // Tabel
        String[] kolom = {"ID", "Tanggal", "Nama", "Layanan", "Berat", "Tipe", "Biaya", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table); // Method styleTable dari kode lama

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Aksi (Bawah)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnPrint = createButton("Cetak Struk", new Color(52, 73, 94));
        JButton btnUpdateStatus = createButton("Update Status", new Color(243, 156, 18));
        JButton btnUpdateData = createButton("Update Data", new Color(41, 128, 185));
        JButton btnDelete = createButton("Hapus Data", new Color(231, 76, 60));

        // Listener tombol
        btnPrint.addActionListener(e -> prosesCetakStruk());
        btnUpdateStatus.addActionListener(e -> prosesUpdateStatus());
        btnUpdateData.addActionListener(e -> prosesUpdateData());
        btnDelete.addActionListener(e -> prosesDelete());

        if (UserSession.isSuperAdmin()) {
            bottomPanel.add(btnPrint);
            bottomPanel.add(btnUpdateStatus);
            bottomPanel.add(btnUpdateData);
            bottomPanel.add(btnDelete);
        } else if (UserSession.isKasir()) {
            bottomPanel.add(btnPrint);
            bottomPanel.add(btnUpdateStatus);
        }

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Manajemen Pengguna");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnAddUser = createButton("+ Tambah User", PRIMARY_COLOR);
        btnAddUser.setPreferredSize(new Dimension(140, 35));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnAddUser, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        //? USER
        DefaultTableModel userModel = new DefaultTableModel(new String[]{"ID", "Username", "Password", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userController.loadData(userModel);

        JTable userTable = new JTable(userModel);
        styleTable(userTable);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnEditUser = createButton("Edit User Terpilih", INFO_COLOR);
        JButton btnDelUser = createButton("Hapus User Terpilih", ERROR_COLOR);

        bottomPanel.add(btnEditUser);
        bottomPanel.add(btnDelUser);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        //? Tombol Tambah -> Panggil Dialog Pop-up
        btnAddUser.addActionListener(e -> {
            showAddUserDialog();
            userController.loadData(userModel);
        });

        //? Tombol Edit
        btnEditUser.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) userModel.getValueAt(row, 0);
                String user = (String) userModel.getValueAt(row, 1);
                String pass = (String) userModel.getValueAt(row, 2);
                String role = (String) userModel.getValueAt(row, 3);

                showEditUserDialog(id, user, pass, role, userModel);
            } else {
                showCustomDialog("Peringatan", "Pilih user yang ingin diedit!", WARNING_COLOR);
            }
        });

        //? Tombol Hapus
        btnDelUser.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) userModel.getValueAt(row, 0);
                String namaUser = (String) userModel.getValueAt(row, 1);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Yakin hapus user '" + namaUser + "'?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (userController.hapusUser(id)) {
                        userController.loadData(userModel);
                        showCustomDialog("Sukses", "User berhasil dihapus.", SUCCESS_COLOR);
                    }
                }
            } else {
                showCustomDialog("Peringatan", "Pilih user yang ingin dihapus!", WARNING_COLOR);
            }
        });

        return panel;
    }

    private void showInputDialog() {
        inputDialog = new JDialog(this, "Formulir Input Transaksi", true);

        JPanel content = createInputPanel();

        content.setPreferredSize(new Dimension(350, 550));

        inputDialog.getContentPane().add(content);
        inputDialog.pack();
        inputDialog.setLocationRelativeTo(this); // Muncul di tengah layar
        inputDialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Tambah Pengguna Baru", true);
        dialog.setSize(350, 320);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createLabel("Username: "));
        JTextField txtUsername = createTextField();
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(createLabel("Password: "));
        JTextField txtPassword = createTextField();
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(createLabel("Role: "));
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"pelanggan", "kasir"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbRole.setBackground(Color.WHITE);
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(cmbRole);
        formPanel.add(Box.createVerticalStrut(20));

        JButton btnSave = createButton("Simpan Pengguna", PRIMARY_COLOR);
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String role = (String) cmbRole.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                showCustomDialog("Input Tidak Valid", "Username dan Password tidak boleh kosong.", ERROR_COLOR);
                return;
            }

            if (userController.tambahUser(username, password, role)) {
                showCustomDialog("Berhasil", "Pengguna baru berhasil ditambahkan.", SUCCESS_COLOR);
                dialog.dispose();
            } else {
                showCustomDialog("Gagal", "Terjadi kesalahan saat menambahkan pengguna.", ERROR_COLOR);
            }
        });

        formPanel.add(btnSave);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void initListeners() {

        if (txtBerat == null || cmbLayanan == null || chkExpress == null) {
            return; // Jika komponen belum diinisialisasi, keluar dari method
        }

        txtBerat.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hitungLive();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hitungLive();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                hitungLive();
            }
        });

        cmbLayanan.addActionListener(e -> hitungLive());
        chkExpress.addActionListener(e -> hitungLive());

        // Listener tombol HP hanya angka
        txtHp.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }

    private void showEditUserDialog(int id, String currentUser, String currentPass, String currentRole, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit Pengguna", true);
        dialog.setSize(350, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JTextField txtUsername = createTextField();
        txtUsername.setText(currentUser);

        JTextField txtPassword = createTextField();
        txtPassword.setText(currentPass);

        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"pelanggan", "kasir"});
        cmbRole.setSelectedItem(currentRole);
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbRole.setBackground(Color.WHITE);
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSimpan = createButton("Simpan Perubahan", WARNING_COLOR);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(btnSimpan);

        btnSimpan.addActionListener(e -> {
            if (userController.editUser(id, txtUsername.getText(), txtPassword.getText(), (String) cmbRole.getSelectedItem())) {
                userController.loadData(model);
                JOptionPane.showMessageDialog(dialog, "Data User berhasil diupdate!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal update user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showEditTransaksiDialog(int id, String namaLama, String layLama, double beratLama, String statusLama, boolean isExpressLama) {
        JDialog dialog = new JDialog(this, "Edit Data Transaksi", true);
        dialog.setSize(350, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JTextField txtNama = createTextField();
        txtNama.setText(namaLama);

        JTextField txtBerat = createTextField();
        txtBerat.setText(String.valueOf(beratLama));

        JComboBox<String> cmbLayanan = new JComboBox<>(new String[]{"Cuci Basah", "Cuci Kering", "Setrika"});
        cmbLayanan.setSelectedItem(layLama);
        cmbLayanan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLayanan.setBackground(Color.WHITE);
        cmbLayanan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbLayanan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTotalBaru = new JLabel("Total Baru: -");
        lblTotalBaru.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalBaru.setForeground(SUCCESS_COLOR);
        lblTotalBaru.setAlignmentX(Component.LEFT_ALIGNMENT);

        // status
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Menunggu", "Diterima", "Dicuci", "Selesai", "Diambil"});
        cmbStatus.setSelectedItem(statusLama);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox chkExpress = new JCheckBox("Layanan Express (+5000/kg)");
        chkExpress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkExpress.setSelected(isExpressLama);
        chkExpress.setBackground(Color.WHITE);
        chkExpress.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSimpan = createButton("Simpan Perubahan", WARNING_COLOR);

        Runnable hitungUlang = () -> {
            try {
                if (txtBerat.getText().isEmpty()) {
                    lblTotalBaru.setText("Total Baru: Rp 0");
                    return;
                }

                double berat = Double.parseDouble(txtBerat.getText());
                String layanan = (String) cmbLayanan.getSelectedItem();
                boolean isExpress = chkExpress.isSelected();

                Layanan layananObj = switch (layanan) {
                    case "Cuci Basah" ->
                        new CuciBasah(berat, isExpress);
                    case "Cuci Kering" ->
                        new CuciKering(berat, isExpress);
                    default ->
                        new Setrika(berat, isExpress);
                };
                double harga = layananObj.hitungTotal();

                lblTotalBaru.setText("Total Baru: Rp " + String.format("%,.0f", harga));
                btnSimpan.putClientProperty("total_fix", harga);
            } catch (NumberFormatException ex) {
                lblTotalBaru.setText("Total Baru: Input Salah");
            }
        };

        txtBerat.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hitungUlang.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hitungUlang.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                hitungUlang.run();
            }
        });
        cmbLayanan.addActionListener(e -> hitungUlang.run());

        chkExpress.addActionListener(e -> hitungUlang.run());

        hitungUlang.run();

        formPanel.add(createLabel("Nama Pelanggan:"));
        formPanel.add(txtNama);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Berat (Kg):"));
        formPanel.add(txtBerat);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Jenis Layanan:"));
        formPanel.add(cmbLayanan);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(chkExpress);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Status Transaksi:"));
        formPanel.add(cmbStatus);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(lblTotalBaru);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnSimpan);

        btnSimpan.addActionListener(e -> {
            String namaBaru = txtNama.getText().trim();
            String layBaru = (String) cmbLayanan.getSelectedItem();
            String statusBaru = (String) cmbStatus.getSelectedItem();
            boolean isExpressBaru = chkExpress.isSelected();

            if (txtBerat.getText().isEmpty()) {
                return;
            }

            double beratBaru;
            try {
                beratBaru = Double.parseDouble(txtBerat.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Berat harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hitung total baru
            Layanan layanan = switch (layBaru) {
                case "Cuci Basah" ->
                    new CuciBasah(beratBaru, isExpressBaru);
                case "Cuci Kering" ->
                    new CuciKering(beratBaru, isExpressBaru);
                default ->
                    new Setrika(beratBaru, isExpressBaru);
            };
            double totalBaru = layanan.hitungTotal();

            // Simpan perubahan
            if (controller.updateDataTransaksi(id, namaBaru, layBaru, beratBaru, isExpressBaru, statusBaru, totalBaru)) {
                controller.loadData(tableModel);
                updateStatistik();
                if (chartPanel != null) {
                    chartPanel.updateData(controller.getGrafikPenjualan());
                }
                JOptionPane.showMessageDialog(dialog, "Data transaksi berhasil diupdate!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal update data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

}
