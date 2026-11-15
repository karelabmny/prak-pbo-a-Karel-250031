package id.ac.univpancasila.perpustakaan;

import java.util.NoSuchElementException;
import java.util.Scanner;

import id.ac.univpancasila.perpustakaan.service.LibraryService;
import id.ac.univpancasila.perpustakaan.service.PerpustakaanService;

public class Main {

    public static void main(String[] args) {
        LibraryService libService = new LibraryService();
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("\n=== MENU PERPUSTAKAAN (PERT 10) ===");
                System.out.println("1. Tambah Anggota");
                System.out.println("2. Tambah Buku");
                System.out.println("3. Pinjam Buku");
                System.out.println("0. Keluar");
                System.out.print("Pilih menu: ");

                String pilihan = scanner.nextLine().trim();
                switch (pilihan) {
                    case "1" -> PerpustakaanService.tambahAnggota(libService, scanner);
                    case "2" -> PerpustakaanService.tambahBuku(libService, scanner);
                    case "3" -> PerpustakaanService.pinjamBuku(libService, scanner);
                    case "0" -> {
                        System.out.println("Sampai jumpa!");
                        return;
                    }
                    default -> System.out.println("[Peringatan] Menu tidak dikenal: '" + pilihan + "'");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\n[Info] Input berakhir. Program selesai.");
        }
    }
}
