/**
 * Sesi 2 — enkapsulasi yang menjaga invariant.
 *
 * Deskripsi masalah:
 * "Sistem akademik mencatat mahasiswa dengan NIM, nama, dan tiga komponen
 * nilai: tugas, UTS, dan UAS. NIM tidak pernah berubah setelah mahasiswa
 * terdaftar. Setiap komponen nilai berada dalam rentang 0 sampai 100.
 * Nilai akhir dihitung 30% tugas, 30% UTS, 40% UAS."
 *
 * Tuliskan lebih dulu daftar invariant-nya di analisis.md,
 * baru lengkapi TODO di bawah ini.
 */
public class Mahasiswa {

    // Konstanta bobot — jangan menulis angka 0.30 dan 0.40 di dalam method.
    public static final double BOBOT_TUGAS = 0.30;
    public static final double BOBOT_UTS = 0.30;
    public static final double BOBOT_UAS = 0.40;

    private static final double NILAI_MIN = 0;
    private static final double NILAI_MAX = 100;

    // TODO 1: deklarasikan atribut. Perhatikan mana yang boleh berubah
    // dan mana yang tidak. Gunakan final untuk yang tidak boleh berubah.
    private final String nim;
    private final String nama;
    private double nilaiTugas;
    private double nilaiUts;
    private double nilaiUas;

    public Mahasiswa(String nim, String nama, double nilaiTugas, double nilaiUts, double nilaiUas) {
        if (nim == null || nim.trim().isEmpty()) {
            throw new IllegalArgumentException("NIM tidak boleh kosong atau null");
        }

        pastikanNilaiSah("nilai tugas", nilaiTugas);
        pastikanNilaiSah("nilai UTS", nilaiUts);
        pastikanNilaiSah("nilai UAS", nilaiUas);

        this.nim = nim;
        this.nama = nama;
        this.nilaiTugas = nilaiTugas;
        this.nilaiUts = nilaiUts;
        this.nilaiUas = nilaiUas;
    }

    private static void pastikanNilaiSah(String namaKomponen, double nilai) {
        if (Double.isNaN(nilai) || nilai < NILAI_MIN || nilai > NILAI_MAX) {
            throw new IllegalArgumentException(
                    namaKomponen + " harus berada dalam rentang 0 sampai 100");
        }
    }

    /**
     * TODO 5: hitung nilai akhir memakai konstanta bobot di atas.
     */
    public double nilaiAkhir() {
        return nilaiTugas * BOBOT_TUGAS
                + nilaiUts * BOBOT_UTS
                + nilaiUas * BOBOT_UAS;
    }

    /**
     * TODO 6: kembalikan huruf mutu berdasarkan nilai akhir.
     * >= 80 -> "A" >= 70 -> "B" >= 60 -> "C" >= 50 -> "D" selain itu "E"
     */
    public String hurufMutu() {
        double nilai = nilaiAkhir();
        if (nilai >= 80)
            return "A";
        if (nilai >= 70)
            return "B";
        if (nilai >= 60)
            return "C";
        if (nilai >= 50)
            return "D";
        return "E";
    }

    // ── Getter ────────────────────────────────────────────────
    // TODO 7: sediakan getter untuk nim, nama, dan nilaiAkhir.
    // JANGAN membuat setNim(). Baca ulang invariant Anda kalau tergoda.

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public double getNilaiAkhir() {
        return nilaiAkhir();
    }

    @Override
    public String toString() {
        return String.format("%-10s %-18s akhir=%6.2f  mutu=%s",
                nim, nama, nilaiAkhir(), hurufMutu());
    }
}