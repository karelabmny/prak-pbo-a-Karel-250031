public class Segitiga extends BangunDatar {

    private final double alas;
    private final double tinggi;
    private final double sisiMiring;

    public Segitiga(double alas, double tinggi, double sisiMiring) {
        super("Segitiga");
        this.alas = alas;
        this.tinggi = tinggi;
        this.sisiMiring = sisiMiring;
        // TODO 1: tolak sisi <= 0.
        if (alas <= 0 || tinggi <= 0 || sisiMiring <= 0) {
            throw new IllegalArgumentException("Sisi harus lebih besar dari 0");
        }
    }

    // TODO 2: lengkapi luas() dan keliling().
    @Override
    public double luas() {
        return 0.5 * alas * tinggi;

    }

    @Override
    public double keliling() {
        return alas + tinggi + sisiMiring;
    }

    @Override 
    public String toString() {
        return getNama() + "(alas=" + alas + ", tinggi =" + tinggi + ", sisi = " + sisiMiring + ") luas=" + luas();
    }

}
