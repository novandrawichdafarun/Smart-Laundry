package models;

public abstract class Layanan {

    protected double berat;
    protected boolean isExpress;

    public Layanan(double berat, boolean isExpress) {
        this.berat = berat;
        this.isExpress = isExpress;
    }

    public abstract double hitungTotal();

    protected double biayaExpress() {
        return isExpress ? 5000 * berat : 0;
    }
}
