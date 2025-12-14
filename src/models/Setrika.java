package models;

public class Setrika extends Layanan {

    private final double harga = 4000;

    public Setrika(double berat, boolean isExpress) {
        super(berat, isExpress);
    }

    @Override
    public double hitungTotal() {
        return (berat * harga) + biayaExpress();
    }

}
