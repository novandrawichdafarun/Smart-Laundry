package models;

public class CuciBasah extends Layanan {

    private final double harga = 3000;

    public CuciBasah(double berat, boolean isExpress) {
        super(berat, isExpress);
    }

    @Override
    public double hitungTotal() {
        return (berat * harga) + biayaExpress();
    }

}
