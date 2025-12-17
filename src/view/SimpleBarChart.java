package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class SimpleBarChart extends JPanel {

    private Map<String, Double> data;
    private final Color BAR_COLOR = new Color(52, 152, 219);

    public SimpleBarChart(Map<String, Double> data) {
        this.data = data;
        setPreferredSize(new Dimension(100, 200)); //? Tinggi grafik
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void updateData(Map<String, Double> newData) {
        this.data = newData;
        repaint(); //! Gambar ulang grafik
    }

    private String formatRupiah(double number) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        kursIndonesia.setMaximumFractionDigits(0); // Hilangkan sen (,00)

        return kursIndonesia.format(number);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.drawString("Belum ada data penjualan", 20, 100);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 30; // Jarak dari tepi

        // Cari nilai maksimum untuk skala
        double maxVal = 0;
        for (Double val : data.values()) {
            if (val > maxVal) {
                maxVal = val;
            }
        }
        if (maxVal == 0) {
            maxVal = 1; // Hindari pembagian nol
        }
        // Gambar Sumbu
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // Garis X

        int barWidth = (width - 2 * padding) / data.size();
        int x = padding;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = entry.getValue();
            int barHeight = (int) ((val / maxVal) * (height - 2 * padding));

            // Gambar Batang
            g2d.setColor(BAR_COLOR);
            g2d.fillRoundRect(x + 10, height - padding - barHeight, barWidth - 20, barHeight, 10, 10);

            // Gambar Label Tanggal (Bawah)
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(entry.getKey());
            g2d.drawString(entry.getKey(), x + (barWidth - labelWidth) / 2, height - padding + 15);

            // Gambar Nilai Rupiah (Atas Batang)
            String valueText = formatRupiah(val); // Format "Rp. 0"
            if (fm.stringWidth(valueText) > barWidth) {
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                fm = g2d.getFontMetrics();
            }

            int valWidth = fm.stringWidth(valueText);
            g2d.setColor(Color.BLACK);
            g2d.drawString(valueText, x + (barWidth - valWidth) / 2, height - padding - barHeight - 5);

            x += barWidth;
        }
    }
}
