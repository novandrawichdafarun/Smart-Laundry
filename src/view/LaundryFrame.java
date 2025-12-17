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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import controller.TransaksiController;
import models.CuciBasah;
import models.CuciKering;
import models.Layanan;
import models.Setrika;
import utils.StrukPrinter;

public class LaundryFrame extends JFrame {

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
        chartPanel.updateData(controller.getGrafikPenjualan());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void initUI() {
        // TODO Auto-generated method stub
        setTitle("Smart Laundry System - Dashboard");
        setSize(1100, 700);
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

        ImageIcon logoIcon = imageIcon("/img/logo.png", 40, 40);

        JLabel lblBrand = new JLabel("Smart Laundry System", logoIcon, JLabel.LEFT);
        lblBrand.setIconTextGap(10);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Admin Panel | " + java.time.LocalDate.now());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(230, 230, 230));

        headerPanel.add(lblBrand, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        //? Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setPreferredSize(new Dimension(320, 0));

        //? Judul Input
        JLabel lblInputTitle = new JLabel("Input Transaksi Baru");
        lblInputTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInputTitle.setForeground(PRIMARY_COLOR);
        lblInputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.add(lblInputTitle);
        inputPanel.add(Box.createVerticalStrut(20));

        //? Form Fields
        inputPanel.add(createLabel("Nama Pelanggan:"));
        txtNama = createTextField();
        inputPanel.add(txtNama);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(createLabel("No Hp:"));
        txtHp = createTextField();
        txtHp.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField tf = (JTextField) input;
                String text = tf.getText().trim();

                String regex = "^(08|62)\\d{8,12}$";

                if (text.matches(regex)) {
                    tf.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                    return true;
                } else {
                    showCustomDialog("Validasi Gagal", "Format No Hp salah!<br>Harus diawali '08' atau '62' dan berisi angka.", WARNING_COLOR);
                    tf.setBorder(BorderFactory.createLineBorder(Color.RED));
                    return false;
                }
            }
        });
        txtHp.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();

                //! Jika bukan angka, batalkan input (consume)
                if (!Character.isDigit(c)) {
                    e.consume();
                }
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
        txtBerat.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField tf = (JTextField) input;
                try {
                    double berat = Double.parseDouble(tf.getText());
                    if (berat <= 0) {
                        JOptionPane.showMessageDialog(null, "Berat harus lebih dari 0!");
                        return false;
                    }
                    return true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Berat harus berupa angka valid!");
                    return false;
                }
            }
        });
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

        //? Button & Label Total
        JButton btnHitung = createButton("Simpan Transaksi", PRIMARY_COLOR);
        btnHitung.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTotal = new JLabel("Total: Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(39, 174, 96)); //! Hijau
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.add(lblTotal);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(btnHitung);

        //! Panel input ke ScrollPane (layar kecil)
        JScrollPane scrollInput = new JScrollPane(inputPanel);
        scrollInput.setBorder(null);
        add(scrollInput, BorderLayout.WEST);

        //? Data Panel
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBackground(BG_COLOR);
        dataPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        //? Grafik 
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblChartTitle = new JLabel("Statistik Penjualan (7 Hari Terakhir)");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChartTitle.setForeground(Color.GRAY);
        lblChartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        chartPanel = new SimpleBarChart(controller.getGrafikPenjualan());

        chartContainer.add(lblChartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        JPanel rightContent = new JPanel(new BorderLayout());
        rightContent.setBackground(BG_COLOR);
        rightContent.add(chartContainer, BorderLayout.NORTH);

        //? Panel Statistik
        statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(BG_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

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

        JPanel topDataPanel = new JPanel(new BorderLayout());
        topDataPanel.setBackground(BG_COLOR);
        topDataPanel.add(statsPanel, BorderLayout.NORTH);

        JLabel lblTableTitle = new JLabel("Riwayat Transaksi");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        topDataPanel.add(lblTableTitle, BorderLayout.SOUTH);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BG_COLOR);
        tableContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] kolom = {"ID", "Tanggal", "Nama", "Layanan", "Berat", "Biaya", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        dataPanel.add(new JLabel("Riwayat Transaksi"), BorderLayout.NORTH);
        dataPanel.getComponent(0).setFont(new Font("Segoe UI", Font.BOLD, 16));

        dataPanel.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(new JLabel("Riwayat Transaksi"), BorderLayout.NORTH);
        tableContainer.getComponent(0).setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_COLOR);

        JButton btnPrint = createButton("Cetak Struk", new Color(52, 73, 94));
        JButton btnUpdate = createButton("Update Status Cucian", new Color(243, 156, 18)); //! Orange
        JButton btnDelete = createButton("Hapus Data", new Color(231, 76, 60)); //! Merah

        tableContainer.add(bottomPanel, BorderLayout.SOUTH);
        tableContainer.add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.add(btnPrint);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);

        rightContent.add(tableContainer, BorderLayout.CENTER);

        dataPanel.add(topDataPanel, BorderLayout.NORTH);
        dataPanel.add(bottomPanel, BorderLayout.SOUTH);

        dataPanel.add(rightContent, BorderLayout.CENTER);

        add(dataPanel, BorderLayout.CENTER);

        //? Action Listener
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

        btnHitung.addActionListener(e -> prosesTransaksi());
        btnUpdate.addActionListener(e -> prosesUpdateStatus());
        btnDelete.addActionListener(e -> prosesDelete());
        btnPrint.addActionListener(e -> prosesCetakStruk());
    }

    private void prosesTransaksi() {
        // TODO Auto-generated method stub
        try {
            String nama = txtNama.getText();
            String hp = txtHp.getText();
            String alamat = txtAlamat.getText();
            double berat = Double.parseDouble(txtBerat.getText());
            boolean Express = chkExpress.isSelected();
            String jenis = (String) cmbLayanan.getSelectedItem();

            if (nama.isEmpty() || txtBerat.getText().isEmpty()) {
                showCustomDialog("Peringatan", "Nama dan Berat harus diisi!", WARNING_COLOR);
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
                showCustomDialog("Sukses", msg, SUCCESS_COLOR);

                controller.loadData(tableModel);
                updateStatistik();
                resetForm();
            } else {
                showCustomDialog("Error", "Gagal menyimpan ke database.", ERROR_COLOR);
            }
        } catch (NumberFormatException e) {
            showCustomDialog("Input Salah", "Berat harus berupa angka!", ERROR_COLOR);
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
            String status = (String) tableModel.getValueAt(baris, 6);

            controller.updateStatus(id, status);
            controller.loadData(tableModel);
            updateStatistik();

            showCustomDialog("Status Updated", "Status cucian berhasil diperbarui.", INFO_COLOR);
        } else {
            showCustomDialog("Pilih Data", "Klik baris transaksi pada terlebih dahulu!", WARNING_COLOR);
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

        if (table.getColumnCount() > 6) {
            table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    // Panggil super agar seleksi baris (warna biru saat diklik) tetap jalan
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    String status = (String) value;
                    this.setHorizontalAlignment(CENTER); // Tengahkan teks
                    this.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Font sedikit lebih tebal

                    // Logika Warna (Hanya ubah warna jika baris TIDAK sedang dipilih/diklik)
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
        lblStatOmset.setText("Rp " + String.format("%,.0f", stats[0]));
        lblStatTransaksi.setText(String.format("%.0f", stats[1]) + " Pesanan");
        lblStatBerat.setText(String.format("%.1f", stats[2]) + " Kg");
    }

    private void prosesCetakStruk() {
        int baris = table.getSelectedRow();

        if (baris >= 0) {
            int id = (int) tableModel.getValueAt(baris, 0);
            String tanggal = (String) tableModel.getValueAt(baris, 1);
            String nama = (String) tableModel.getValueAt(baris, 2);
            String layanan = (String) tableModel.getValueAt(baris, 3);
            double berat = (double) tableModel.getValueAt(baris, 4);
            double total = (double) tableModel.getValueAt(baris, 5);
            String status = (String) tableModel.getValueAt(baris, 6);

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

}
