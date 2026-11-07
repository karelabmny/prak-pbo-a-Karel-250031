<?php

declare(strict_types=1);
$composerAutoload = __DIR__ . '/../vendor/autoload.php';
if (is_file($composerAutoload)) {
    require $composerAutoload;
} else {
    spl_autoload_register(function (string $class): void {
        $prefix = 'Perpustakaan\\';
        if (strncmp($class, $prefix, strlen($prefix)) !== 0) {
            return;
        }
        $file = __DIR__ . '/' . str_replace('\\', '/', substr($class, strlen($prefix))) . '.php';
        if (is_file($file)) {
            require $file;
        }
    });
}

use Perpustakaan\Exception\PerpustakaanException;
use Perpustakaan\Service\LibraryService;

$s = new LibraryService();
$s->tambahBuku('111', 'Laskar Pelangi', 1);
$s->tambahBuku('222', 'Bumi Manusia', 5);
$s->daftarkanAnggota('A-001');
$s->daftarkanAnggota('A-002');

$percobaan = [
    ['A-001', '111'], // berhasil
    ['A-002', '111'], // buku habis
    ['A-002', '999'], // buku tidak ada
    ['X-999', '222'], // anggota tidak terdaftar
    ['A-001', '222'], // berhasil
    ['A-001', '222'], // berhasil
    ['A-001', '222'], // batas pinjam tercapai
];

foreach ($percobaan as [$id, $isbn]) {
    echo "Anggota $id meminjam ISBN '$isbn' ... ", PHP_EOL;
    try {
        $s->pinjam($id, $isbn);
        echo "[BERHASIL]\n";
    } catch (PerpustakaanException $e) {
        echo "[GAGAL] " . $e->getMessage() . "\n";
    }
}

createReport($s);

function createReport(LibraryService $s)
{
    echo PHP_EOL . "--- Ekspor Laporan Buku ---" . PHP_EOL;
    $namaFile = 'laporan_buku.txt';
    $fileHandle = fopen(__DIR__ . '/' . $namaFile, 'w');
    if (!$fileHandle) {
        echo "[ERROR] Gagal membuka file untuk menulis.\n";
        exit(1);
    }

    try {
        fwrite($fileHandle, "==========================================================================\n");
        fwrite($fileHandle, "                    LAPORAN DAFTAR BUKU PERPUSTAKAAN                      \n");
        fwrite($fileHandle, "==========================================================================\n");
        fwrite($fileHandle, sprintf("%-12s | %-35s | %-8s | %-15s\n", "ISBN", "JUDUL BUKU", "STOK", "STATUS"));
        fwrite($fileHandle, "--------------------------------------------------------------------------\n");
        $daftarJudul = $s->getSemuaJudul();
        foreach ($daftarJudul as $isbn => $judulBuku) {
            $isbn = (string) $isbn;
            $stokSisa = $s->sisaEksemplar($isbn);
            $status = ($stokSisa > 0) ? "Tersedia" : "Tidak Tersedia";
            fwrite($fileHandle, sprintf("%-12s | %-35s | %-8d | %-15s\n", $isbn, $judulBuku, $stokSisa, $status));
        }
        fwrite($fileHandle, "==========================================================================\n");
        echo ">> BERHASIL: Laporan buku beserta statusnya disimpan ke berkas '{$namaFile}'.\n";

    } catch (\Exception $e) {
        echo "[ERROR SISTEM] Gagal menyelesaikan laporan: " . $e->getMessage() . "\n";
    } finally {
        if (is_resource($fileHandle)) {
            fclose($fileHandle);
            echo "[INFO] File '{$namaFile}' telah ditutup dengan aman melalui blok finally.\n";
        }
    }
}