package id.ac.univpancasila.perpustakaan;

import id.ac.univpancasila.perpustakaan.exception.AnggotaTidakDitemukanException;
import id.ac.univpancasila.perpustakaan.exception.BatasPinjamTerlampauiException;
import id.ac.univpancasila.perpustakaan.exception.BukuSedangDipinjamException;
import id.ac.univpancasila.perpustakaan.exception.BukuTidakDitemukanException;
import id.ac.univpancasila.perpustakaan.service.LibraryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sesi 11 — unit test pertama Anda.
 *
 * Letakkan berkas ini di src/test/java/id/ac/univpancasila/perpustakaan/
 * Jalankan dengan: mvn test
 *
 * Konvensi Maven: hanya berkas di src/test/java yang bernama *Test
 * yang akan dijalankan sebagai pengujian.
 */
class RepositoryTest {

    /** Objek sederhana untuk diuji. */
    record Buku(String isbn, String judul) {}

    private Repository<Buku> repo;

    @BeforeEach
    void siapkan() {
        // Dijalankan ulang sebelum SETIAP pengujian, sehingga tiap uji
        // berangkat dari keadaan yang bersih dan tidak saling memengaruhi.
        repo = new Repository<>(Buku::isbn);
    }

    @Test
    @DisplayName("Item yang disimpan dapat ditemukan kembali")
    void simpanLaluTemukan() {
        repo.simpan(new Buku("978-1", "PBO"));

        Optional<Buku> hasil = repo.cariById("978-1");

        assertTrue(hasil.isPresent());
        assertEquals("PBO", hasil.get().judul());
    }

    @Test
    @DisplayName("Mencari id yang tidak ada mengembalikan Optional kosong")
    void cariYangTidakAda() {
        assertTrue(repo.cariById("999").isEmpty());
    }

    @Test
    @DisplayName("Item yang dihapus tidak dapat ditemukan lagi")
    void hapusItem() {
        repo.simpan(new Buku("978-1", "PBO"));

        assertTrue(repo.hapus("978-1"));
        assertTrue(repo.cariById("978-1").isEmpty());
        assertEquals(0, repo.jumlah());
    }

    @Test
    @DisplayName("Repository baru berisi nol item")
    void repositoryKosong() {
        assertEquals(0, repo.jumlah());
        assertTrue(repo.cariSemua().isEmpty());
    }

    @Test
    @DisplayName("Koleksi yang dikembalikan cariSemua tidak dapat diubah dari luar")
    void koleksiTidakBocor() {
        repo.simpan(new Buku("978-1", "PBO"));

        List<Buku> salinan = repo.cariSemua();

        assertThrows(UnsupportedOperationException.class,
                () -> salinan.add(new Buku("978-2", "Basis Data")));
    }

    // ---- TODO 6: uji tambahan ----

    @Test
    @DisplayName("Menyimpan dengan kunci yang sama menimpa item lama")
    void simpanKunciSamaMenimpa() {
        repo.simpan(new Buku("978-1", "PBO"));
        repo.simpan(new Buku("978-1", "PBO Edisi 2"));

        assertEquals(1, repo.jumlah());
        assertEquals("PBO Edisi 2", repo.cariById("978-1").orElseThrow().judul());
    }

    @Test
    @DisplayName("Menghapus id yang tidak ada mengembalikan false")
    void hapusYangTidakAda() {
        repo.simpan(new Buku("978-1", "PBO"));

        assertFalse(repo.hapus("tidak-ada"));
        assertEquals(1, repo.jumlah());
    }

    @Test
    @DisplayName("cariSemua mempertahankan urutan penyimpanan")
    void cariSemuaMempertahankanUrutan() {
        Buku a = new Buku("978-1", "PBO");
        Buku b = new Buku("978-2", "Basis Data");
        Buku c = new Buku("978-3", "Desain Web");
        repo.simpan(a);
        repo.simpan(b);
        repo.simpan(c);

        assertEquals(List.of(a, b, c), repo.cariSemua());
    }

    // ---- Skenario kegagalan dari sesi 10 (LibraryService + exception domain) ----

    private LibraryService layanan() {
        LibraryService layanan = new LibraryService();
        layanan.tambahBuku("111", "Laskar Pelangi", 1);
        layanan.daftarkanAnggota("A1");
        return layanan;
    }

    @Test
    @DisplayName("pinjam() yang sah mengurangi stok")
    void pinjamBerhasilMengurangiStok() {
        LibraryService layanan = layanan();

        assertTrue(layanan.pinjam("A1", "111"));
        assertEquals(0, layanan.sisaEksemplar("111"));
    }

    @Test
    @DisplayName("pinjam() melempar BukuTidakDitemukanException untuk ISBN yang tidak terdaftar")
    void pinjamIsbnTidakTerdaftar() {
        LibraryService layanan = layanan();

        BukuTidakDitemukanException e = assertThrows(
                BukuTidakDitemukanException.class,
                () -> layanan.pinjam("A1", "000"));

        assertEquals("000", e.getIsbn());
    }

    @Test
    @DisplayName("pinjam() melempar AnggotaTidakDitemukanException untuk anggota yang belum terdaftar")
    void pinjamAnggotaTidakTerdaftar() {
        LibraryService layanan = layanan();

        AnggotaTidakDitemukanException e = assertThrows(
                AnggotaTidakDitemukanException.class,
                () -> layanan.pinjam("X9", "111"));

        assertEquals("X9", e.getIdAnggota());
    }

    @Test
    @DisplayName("pinjam() melempar BatasPinjamTerlampauiException pada pinjaman ke-4")
    void pinjamMelewatiBatas() {
        LibraryService layanan = layanan();
        layanan.tambahBuku("222", "Bumi Manusia", 10);
        layanan.pinjam("A1", "222");
        layanan.pinjam("A1", "222");
        layanan.pinjam("A1", "222");

        BatasPinjamTerlampauiException e = assertThrows(
                BatasPinjamTerlampauiException.class,
                () -> layanan.pinjam("A1", "222"));

        assertEquals(LibraryService.BATAS_PINJAM, e.getBatas());
        assertEquals("A1", e.getIdAnggota());
    }

    @Test
    @DisplayName("pinjam() melempar BukuSedangDipinjamException saat stok habis")
    void pinjamBukuHabis() {
        LibraryService layanan = layanan();
        layanan.daftarkanAnggota("A2");
        layanan.pinjam("A1", "111");   // eksemplar terakhir

        BukuSedangDipinjamException e = assertThrows(
                BukuSedangDipinjamException.class,
                () -> layanan.pinjam("A2", "111"));

        assertEquals("Laskar Pelangi", e.getJudul());
        assertEquals("111", e.getIsbn());
    }
}
