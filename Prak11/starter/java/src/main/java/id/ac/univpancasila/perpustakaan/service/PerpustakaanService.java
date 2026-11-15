package id.ac.univpancasila.perpustakaan.service;

import id.ac.univpancasila.perpustakaan.exception.AnggotaTidakDitemukanException;
import id.ac.univpancasila.perpustakaan.exception.BatasPinjamTerlampauiException;
import id.ac.univpancasila.perpustakaan.exception.BukuSedangDipinjamException;
import id.ac.univpancasila.perpustakaan.exception.BukuTidakDitemukanException;
import id.ac.univpancasila.perpustakaan.exception.PerpustakaanException;
import id.ac.univpancasila.perpustakaan.model.Anggota;
import id.ac.univpancasila.perpustakaan.model.Buku;
import id.ac.univpancasila.perpustakaan.model.Peminjaman;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Lapisan layanan sistem perpustakaan.
 */
public class PerpustakaanService {

    private final List<Buku> daftarKoleksi = new ArrayList<>();
    private final List<Anggota> daftarAnggota = new ArrayList<>();
    private final List<Peminjaman> daftarPeminjaman = new ArrayList<>();
    private int counterPeminjaman = 1;

    public void tambahKoleksi(Buku buku) {
        if (buku != null && cariKoleksi(buku.getNomorInduk()) == null) {
            daftarKoleksi.add(buku);
        }
    }

    public Buku cariKoleksi(String nomorInduk) {
        for (Buku b : daftarKoleksi) {
            if (b.getNomorInduk().equalsIgnoreCase(nomorInduk)) {
                return b;
            }
        }
        return null;
    }

    public List<Buku> getDaftarKoleksi() {
        return Collections.unmodifiableList(daftarKoleksi);
    }

    public void daftarkanAnggota(Anggota anggota) {
        if (anggota != null && cariAnggota(anggota.getNomorAnggota()) == null) {
            daftarAnggota.add(anggota);
        }
    }

    public Anggota cariAnggota(String nomorAnggota) {
        for (Anggota a : daftarAnggota) {
            if (a.getNomorAnggota().equalsIgnoreCase(nomorAnggota)) {
                return a;
            }
        }
        return null;
    }

    public List<Anggota> getDaftarAnggota() {
        return Collections.unmodifiableList(daftarAnggota);
    }

    public Peminjaman pinjam(String nomorAnggota, String nomorInduk, LocalDate tanggalPinjam, int lamaHari) {
        Anggota anggota = cariAnggota(nomorAnggota);
        if (anggota == null) {
            throw new IllegalArgumentException("Anggota tidak ditemukan: " + nomorAnggota);
        }

        Buku buku = cariKoleksi(nomorInduk);
        if (buku == null) {
            throw new IllegalArgumentException("Buku tidak ditemukan: " + nomorInduk);
        }

        if (!buku.tersedia()) {
            throw new IllegalStateException("Stok buku sedang habis: " + buku.getJudul());
        }

        // Batas maksimal 3 pinjaman aktif
        long pinjamanAktif = daftarPeminjaman.stream()
                .filter(p -> p.getAnggota().getNomorAnggota().equals(nomorAnggota) && !p.sudahKembali())
                .count();
        if (pinjamanAktif >= 3) {
            throw new IllegalStateException("Anggota " + anggota.getNama() + " sudah mencapai batas maksimal 3 peminjaman.");
        }

        buku.kurangiStok();
        String idPeminjaman = String.format("PJ-%03d", counterPeminjaman++);
        Peminjaman peminjaman = new Peminjaman(idPeminjaman, anggota, buku, tanggalPinjam, lamaHari);
        daftarPeminjaman.add(peminjaman);
        return peminjaman;
    }

    public long kembalikan(String idPeminjaman, LocalDate tanggalKembali) {
        Peminjaman peminjaman = daftarPeminjaman.stream()
                .filter(p -> p.getIdPeminjaman().equalsIgnoreCase(idPeminjaman))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Data peminjaman tidak ditemukan: " + idPeminjaman));

        if (peminjaman.sudahKembali()) {
            throw new IllegalStateException("Peminjaman ini sudah dikembalikan sebelumnya.");
        }

        peminjaman.setTanggalKembali(tanggalKembali);
        peminjaman.getBuku().tambahStok();

        return hitungDenda(peminjaman);
    }

    public long hitungDenda(Peminjaman peminjaman) {
        if (peminjaman.getTanggalKembali() == null) {
            return 0;
        }

        long terlambatHari = ChronoUnit.DAYS.between(peminjaman.getTanggalJatuhTempo(), peminjaman.getTanggalKembali());
        if (terlambatHari <= 0) {
            return 0;
        }

        String jenis = peminjaman.getAnggota().getJenisKeanggotaan().toLowerCase();
        long tarifPerHari;
        if (jenis.contains("mahasiswa")) {
            tarifPerHari = 1000;
        } else if (jenis.contains("dosen")) {
            tarifPerHari = 500;
        } else {
            tarifPerHari = 0; // Anggota kehormatan bebas denda
        }

        return terlambatHari * tarifPerHari;
    }

    public List<Peminjaman> getDaftarPeminjaman() {
        return Collections.unmodifiableList(daftarPeminjaman);
    }

    // ---- Menu interaktif dari pertemuan 10 (dipakai oleh Main) ----


    public static void tambahAnggota(LibraryService libService, Scanner scanner) {
        System.out.println("\n--- Form Tambah Anggota ---");
        System.out.print("Masukkan ID Anggota: ");
        String idAnggota = scanner.nextLine().trim();

        if (idAnggota.isEmpty()) {
            System.out.println("[Gagal] ID Anggota tidak boleh kosong!");
            return;
        }

        libService.daftarkanAnggota(idAnggota);
        System.out.println(">> BERHASIL: Anggota dengan ID '" + idAnggota + "' telah terdaftar.");
    }

    public static void tambahBuku(LibraryService libService, Scanner scanner) {
        System.out.println("\n--- Form Tambah Buku ---");
        System.out.print("Masukkan ISBN Buku: ");
        String isbn = scanner.nextLine().trim();

        if (isbn.isEmpty()) {
            System.out.println("[Gagal] ISBN tidak boleh kosong!");
            return;
        }

        System.out.print("Masukkan Judul Buku: ");
        String judul = scanner.nextLine().trim();

        if (judul.isEmpty()) {
            System.out.println("[Gagal] Judul buku tidak boleh kosong!");
            return;
        }

        System.out.print("Masukkan Jumlah Eksemplar: ");
        int eksemplar = 0;
        try {
            eksemplar = Integer.parseInt(scanner.nextLine().trim());
            if (eksemplar <= 0) {
                System.out.println("[Gagal] Jumlah eksemplar harus lebih dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error Input] Jumlah eksemplar harus berupa angka!");
            return;
        }

        libService.tambahBuku(isbn, judul, eksemplar);
        System.out.println(">> BERHASIL: Buku '" + judul + "' (" + eksemplar + " eksemplar) ditambahkan.");
    }

    public static void pinjamBuku(LibraryService libService, Scanner scanner) {
        System.out.println("=== SISTEM PERPUSTAKAAN (PERT 10) ===");

        while (true) {
            try {
                System.out.println("\n --- Form Peminjaman Buku ---");
                System.out.print("Masukan ID Anggota (Ketik 'exit' untuk keluar): ");
                String idAnggota = scanner.nextLine().trim();

                if (idAnggota.equalsIgnoreCase("exit")) {
                    break;
                }

                if (idAnggota.isEmpty()) {
                    System.out.println("[Peringatan] ID Anggota tidak boleh kosong!");
                    continue;
                }

                System.out.print("Masukan ISBN Buku: ");
                String isbn = scanner.nextLine().trim();

                libService.pinjam(idAnggota, isbn);
                System.out.println("Peminjaman Berhasil!");

            } catch (PerpustakaanException e) {
                System.out.println("[Error Domain] " + e.getMessage());

                if (e instanceof BukuTidakDitemukanException ex) {
                    System.out.println("ISBN bermasalah: " + ex.getIsbn());
                } else if (e instanceof AnggotaTidakDitemukanException ex) {
                    System.out.println("Id Anggota bermasalah: " + ex.getIdAnggota());
                } else if (e instanceof BukuSedangDipinjamException ex) {
                    System.out.println(" -> Judul Buku: " + ex.getJudul() + " (ISBN: " + ex.getIsbn() + ")");
                } else if (e instanceof BatasPinjamTerlampauiException ex) {
                    System.out.println(" -> Batas Maksimum: " + ex.getBatas() + " buku");
                }
            } catch (NoSuchElementException e) {
                System.out.println("\n[Info] Input berakhir. Keluar dari form peminjaman.");
                break;
            } catch (Exception e) {
                System.out.println("[Error Sistem] Terjadi kesalahan input " + e.getMessage());
            }
        }
    }
}
