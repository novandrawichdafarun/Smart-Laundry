package view;

import controller.TransaksiController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.*;

public class LaundryFrame extends JFrame {

    private JTextField txtNama, txtHp, txtAlamat, txtBerat;
    private JComboBox<String> cmbLayanan;
    private JCheckBox chkExpress;
    private JLabel lblTotal;
    private JTable table;
    private DefaultTableModel tableModel;
    private TransaksiController controller;

    public LaundryFrame() {
        controller = new TransaksiController();
        initUI();
        controller.loadData(tableModel);
    }

    private void initUI() {
        // TODO Auto-generated method stub
        setTitle("Smart Laundry System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(8, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Nama Pelanggan:"));
        txtNama = new JTextField();
        panelForm.add(txtNama);

        panelForm.add(new JLabel("No Hp:"));
        txtHp = new JTextField();
        panelForm.add(txtHp);

        panelForm.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        panelForm.add(txtAlamat);

        panelForm.add(new JLabel("Berat (Kg):"));
        txtBerat = new JTextField();
        panelForm.add(txtBerat);

        panelForm.add(new JLabel("Layanan:"));
        cmbLayanan = new JComboBox<>(new String[]{"Cuci Kering", "Setrika"});
        panelForm.add(cmbLayanan);

        panelForm.add(new JLabel("Opsi:"));
        chkExpress = new JCheckBox("Express (+5000/kg)");
        panelForm.add(chkExpress);

        JButton btnHitung = new JButton("Hitung & Simpan");
        lblTotal = new JLabel("Total: Rp 0");
        panelForm.add(btnHitung);
        panelForm.add(lblTotal);

        add(panelForm, BorderLayout.NORTH);

        String[] kolom = {"ID", "Nama", "Layanan", "Berat", "Biaya", "Status"};
        tableModel = new DefaultTableModel(kolom, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnUpdate = new JButton("Update Status Cucian (Klik baris tabel dulu)");
        add(btnUpdate, BorderLayout.SOUTH);

        btnHitung.addActionListener(e -> prosesTransaksi());
        btnUpdate.addActionListener(e -> prosesUpdateStatus());
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

            Layanan layanan;
            if (jenis.equals("Cuci Kering")) {
                layanan = new CuciKering(berat, Express);
            } else {
                layanan = new Setrika(berat, Express);
            }
            double total = layanan.hitungTotal();

            if (controller.simpanTransaksi(nama, hp, alamat, jenis, berat, Express, total)) {
                JOptionPane.showMessageDialog(this, "Berhasil! Total: Rp " + String.format("%,.0f", total));
                controller.loadData(tableModel);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input berat harus angka!");
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
            String status = (String) tableModel.getValueAt(baris, 5);

            controller.updateStatus(id, status);
            controller.loadData(tableModel);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
        }
    }
}
