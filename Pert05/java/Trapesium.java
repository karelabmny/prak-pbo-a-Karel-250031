public class Trapesium extends BangunDatar {

    private final double sisiAtas;
    private final double sisiBawah;
    private final double tinggi;
    private final double sisiKiri;
    private final double sisiKanan;

    public Trapesium(double sisiAtas, double sisiBawah, double tinggi, double sisiKiri, double sisiKanan) {
        // TODO 1: tolak sisi <= 0.
        if (sisiAtas <= 0 || sisiBawah <= 0 || tinggi <= 0) {
            throw new IllegalArgumentException("Sisi dan tinggi harus lebih besar dari 0");
        }
        super("Trapesium");
        this.sisiAtas = sisiAtas;
        this.sisiBawah = sisiBawah;
        this.tinggi = tinggi;
        this.sisiKanan = sisiKanan;
        this.sisiKiri = sisiKiri;
    }

    // TODO 2: lengkapi luas() dan keliling().
    @Override
    public double luas() {
        return 0.5 * (sisiAtas + sisiBawah) * tinggi;
    }

    @Override
    public double keliling() {
        return sisiAtas + sisiBawah + sisiKanan + sisiKiri;
    }

    @Override 
    public String toString() {
        return getNama() + "(atas=" + sisiAtas + ", bawah =" + sisiBawah + ", tinggi = " + tinggi + ") luas=" + luas();
    }

}
