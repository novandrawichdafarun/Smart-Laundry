package utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.ImageIcon;

public class StrukPrinter implements Printable {

    private final int idTransaksi;
    private final String tanggal;
    private final String nama;
    private final String alamat;
    private final String layanan;
    private final double berat;
    private final double total;
    private final String status;

    public StrukPrinter(int idTransaksi, String tanggal, String nama, String alamat, String layanan, double berat, double total, String status) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.nama = nama;
        this.alamat = alamat;
        this.layanan = layanan;
        this.berat = berat;
        this.total = total;
        this.status = status;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        //? Font
        Font fontHeader = new Font("SansSerif", Font.BOLD, 14);
        Font fontContent = new Font("Monospaced", Font.PLAIN, 10);
        Font fontPrice = new Font("Monospaced", Font.BOLD, 12);

        //? Logo
        Image logo = new ImageIcon(getClass().getResource("/img/Logo.png")).getImage();

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int pageWidth = (int) pageFormat.getImageableWidth();
        int y = 20;

        //? Header Struk
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawImage(logo, (pageWidth - 50) / 2, 10, 50, 50, null);
        y += 60;

        drawCenteredString(g2d, "SMART LAUNDRY", pageWidth, y);
        y += 20;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        drawCenteredString(g2d, "Jl. Jambangan No. 12 A, Surabaya", pageWidth, y);
        y += 15;
        drawCenteredString(g2d, "WA: 0812-3456-7890", pageWidth, y);
        y += 20;

        g2d.drawLine(10, y, pageWidth - 10, y);
        y += 20;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 11));

        //? Tanggal & ID
        g2d.drawString("Tgl: " + tanggal, 10, y);
        drawRightAlignedString(g2d, "#" + idTransaksi, pageWidth, y);
        y += 20;

        //? Pelanggan
        g2d.drawString("Nama Pelanggan: " + nama, 10, y);
        y += 20;

        g2d.drawString("Alamat Pelanggan: " + alamat, 10, y);
        y += 20;

        g2d.drawLine(10, y, pageWidth - 10, y);
        y += 20;

        //? item layanan
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2d.drawString(layanan, 10, y); // Nama Layanan
        y += 15;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 11));

        //? Total Berat
        g2d.drawString(berat + " Kg", 10, y);

        //? Total biaya
        drawRightAlignedString(g2d, "Rp " + String.format("%,.0f", total), pageWidth, y);
        y += 25;

        //? Total pembayaran
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("TOTAL BAYAR", 10, y);
        drawRightAlignedString(g2d, "Rp " + String.format("%,.0f", total), pageWidth, y);
        y += 30;

        g2d.drawLine(10, y, pageWidth - 10, y);
        y += 20;

        //? Footer
        g2d.setFont(new Font("Monospaced", Font.ITALIC, 10));
        drawCenteredString(g2d, "Status Saat Ini: " + status, pageWidth, y);
        y += 15;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
        drawCenteredString(g2d, "-- Terima Kasih --", pageWidth, y);

        return PAGE_EXISTS;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void printStruk() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        //? Menampilkan dialog print sistem
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        int stringWidth = g2d.getFontMetrics().stringWidth(text);
        int x = (width - stringWidth) / 2;
        g2d.drawString(text, x, y);
    }

    public void drawRightAlignedString(Graphics2D g2d, String text, int width, int y) {
        int stringWidth = g2d.getFontMetrics().stringWidth(text);
        int x = width - stringWidth - 10;
        g2d.drawString(text, x, y);
    }

}
