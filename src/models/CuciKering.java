package models;

public class CuciKering extends Layanan {

    private final double harga = 6000;

    public CuciKering(double berat, boolean isExpress) {
        super(berat, isExpress);
    }

    @Override
    public double hitungTotal() {
        return (berat * harga) + biayaExpress();
    }

}
