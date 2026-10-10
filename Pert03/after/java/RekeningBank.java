public class RekeningBank {

    private static final double BUNGA_TAHUNAN = 0.025;
    private static final double BIAYA_ADMINISTRASI = 5000;
    private static final double BATAS_PENARIKAN_SEKALI = 5000000;

    private static int jumlahRekening = 0;

    private final String nomor;
    private final String pemilik;
    private double saldo;

    public RekeningBank(String nomor, String pemilik) {
        this(nomor, pemilik, 0);
    }

    public RekeningBank(String nomor, String pemilik, double saldoAwal) {
        if (nomor == null || nomor.isEmpty()) {
            throw new IllegalArgumentException("Nomor Rekening Gak Boleh Kosong");
        }

        if (saldoAwal < 0) {
            throw new IllegalArgumentException("Saldo Awal Gak Boleh Negatif");
        }

        this.nomor = nomor;
        this.pemilik = pemilik;
        this.saldo = saldoAwal;

        jumlahRekening++;
    }

    public void setor(double jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah Setoran harus positif");
        }
        saldo += jumlah;
    }

    public void tarik(double jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah penarikan harus positif");
        }
        if (jumlah > saldo) {
            throw new IllegalArgumentException("Saldo tidak mencukupi");
        }
        if (jumlah > BATAS_PENARIKAN_SEKALI) {
            throw new IllegalArgumentException("Melebihi batas penarikan sekali transaksi");
        }
        saldo -= jumlah;
    }

    public void potongBiayaAdmin() {
        saldo = Math.max(0, saldo - BIAYA_ADMINISTRASI);
    }

    /** TODO 9: method statis — kembalikan jumlah rekening yang pernah dibuat. */
    public static int getJumlahRekening() {
        return jumlahRekening; // ganti
    }

    public static double bungaSetahun(double pokok) {
        return pokok * BUNGA_TAHUNAN;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNomor() {
        return nomor;
    }

    @Override
    public String toString() {
        return String.format("Rekening[%s] %-14s Rp%,.2f", nomor, pemilik, saldo);
    }
}