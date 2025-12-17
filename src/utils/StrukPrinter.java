package utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class StrukPrinter implements Printable {

    private final int idTransaksi;
    private final String tanggal;
    private final String nama;
    private final String layanan;
    private final double berat;
    private final double total;
    private final String status;

    public StrukPrinter(int idTransaksi, String tanggal, String nama, String layanan, double berat, double total, String status) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.nama = nama;
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

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        //? Font
        Font fontHeader = new Font("Monospaced", Font.BOLD, 12);
        Font fontContent = new Font("Monospaced", Font.PLAIN, 10);
        int y = 20;

        //? Header Struk
        g2d.setFont(fontHeader);
        g2d.drawString("SMART LAUNDRY SYSTEM", 10, y);
        y += 15;
        g2d.setFont(fontContent);
        g2d.drawString("Jl. Jambangan No. 12 A, Surabaya", 10, y);
        y += 15;
        g2d.drawString("--------------------------------", 10, y);
        y += 15;

        g2d.drawString("Tgl : " + this.tanggal, 10, y);
        y += 12;
        g2d.drawString("No  : #" + idTransaksi, 10, y);
        y += 12;
        g2d.drawString("Plg : " + nama, 10, y);
        y += 15;
        g2d.drawString("--------------------------------", 10, y);

        //? RIncian Item
        g2d.drawString("Layanan : " + layanan, 10, y);
        y += 12;
        g2d.drawString("Berat   : " + berat + " Kg", 10, y);
        y += 12;
        g2d.drawString("Status  : " + status, 10, y);
        y += 15;
        g2d.drawString("--------------------------------", 10, y);
        y += 15;

        //? Total Harga
        g2d.setFont(fontHeader);
        g2d.drawString("TOTAL   : Rp " + String.format("%,.0f", total), 10, y);
        y += 20;

        // Footer
        g2d.setFont(fontContent);
        g2d.drawString("Terima Kasih atas kepercayaan Anda", 10, y);
        y += 12;
        g2d.drawString("Simpan struk ini utk pengambilan", 10, y);

        return PAGE_EXISTS;
    }

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

    

}
