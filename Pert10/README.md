# LAPORAN PRAKTIKUM PEMROGRAMAN BERBASIS OBJEK

| Informasi Praktikan | Keterangan |
|---|---|
| Nama | Karel Abimanyu Ahpandi |
| NPM | 4525210031 |
| Kelas | A |
| Mata Kuliah | Pemrograman Berbasis Objek (PBO) |
| Pertemuan | 10 — Exception Handling dan Pemrograman Defensif |
| Tanggal | [isi tanggal pengerjaan] |
| Dosen Pengampu | Adi Wahyu Pribadi, S.Si., M.Kom |

---

## 1. Implementasi Java

Program Java menggunakan package `id.ac.univpancasila.perpustakaan` dan dilengkapi `pom.xml` (Java 17). Aplikasi berupa menu di terminal untuk menambah anggota, menambah buku, dan meminjam buku. Kegagalan peminjaman tidak lagi dijawab dengan `false`, tetapi dengan exception khusus yang menjelaskan penyebabnya.

### 1.1. exception/PerpustakaanException.java

**Penjelasan kode:**

Ini adalah induk dari semua exception di aplikasi perpustakaan. Kelas ini abstract dan turunan `RuntimeException` (unchecked), jadi pemanggil tidak dipaksa menulis try-catch. Karena abstract, kelas ini tidak bisa dibuat langsung dan hanya dipakai lewat turunannya. Konstruktornya `protected` dan ada dua bentuk, yaitu dengan pesan saja atau dengan pesan beserta penyebab (`Throwable`). Karena punya induk yang sama, semua kegagalan perpustakaan bisa ditangkap sekaligus dengan satu `catch`.

**Screenshot kode:**

![PerpustakaanException.java](screenshots/java-PerpustakaanException.java.png)

### 1.2. exception/BukuTidakDitemukanException.java

**Penjelasan kode:**

Exception ini dilempar saat ISBN tidak ada di katalog. Selain pesan, exception menyimpan `isbn` dalam atribut `private final` sehingga data penyebabnya bisa diambil lewat `getIsbn()`, tanpa harus membaca isi kalimat pesan.

**Screenshot kode:**

![BukuTidakDitemukanException.java](screenshots/java-BukuTidakDitemukanException.java.png)

### 1.3. exception/AnggotaTidakDitemukanException.java

**Penjelasan kode:**

Dilempar saat ID anggota belum terdaftar. Polanya sama dengan exception buku, tetapi data yang dibawa adalah `idAnggota` dengan getter `getIdAnggota()`.

**Screenshot kode:**

![AnggotaTidakDitemukanException.java](screenshots/java-AnggotaTidakDitemukanException.java.png)

### 1.4. exception/BukuSedangDipinjamException.java

**Penjelasan kode:**

Dilempar saat stok buku habis. Exception membawa dua data, yaitu `isbn` (untuk diolah sistem) dan `judul` (agar pesan lebih mudah dipahami pengguna).

**Screenshot kode:**

![BukuSedangDipinjamException.java](screenshots/java-BukuSedangDipinjamException.java.png)

### 1.5. exception/BatasPinjamTerlampauiException.java

**Penjelasan kode:**

Dilempar saat anggota sudah meminjam sebanyak batas maksimum. Exception membawa `idAnggota` dan `batas`, sehingga pemanggil bisa menampilkan batasnya tanpa perlu tahu aturannya sendiri.

**Screenshot kode:**

![BatasPinjamTerlampauiException.java](screenshots/java-BatasPinjamTerlampauiException.java.png)

### 1.6. service/LibraryServiceKotor.java

**Penjelasan kode:**

Ini versi awal sebagai bahan refactoring. Method `pinjam()` memakai `if` bersarang empat tingkat sehingga sulit dibaca. Saat gagal, method hanya mengembalikan `false` tanpa alasan, jadi pemanggil tidak tahu apa penyebabnya, apakah anggota belum terdaftar, buku tidak ada, stok habis, atau batas pinjam penuh.

**Screenshot kode:**

![LibraryServiceKotor.java](screenshots/java-LibraryServiceKotor.java.png)

### 1.7. service/LibraryService.java

**Penjelasan kode:**

Ini hasil refactoring memakai guard clause. Setiap syarat dicek satu per satu dari atas: anggota ada, buku ada, stok tersedia, dan batas pinjam belum penuh. Kalau ada yang tidak terpenuhi, langsung dilempar exception yang sesuai. Kalau semua lolos, stok dikurangi dan jumlah pinjaman anggota bertambah. Alurnya lurus dan mudah ditambah aturan baru. Batas pinjam disimpan di konstanta `BATAS_PINJAM` bernilai 3.

**Screenshot kode:**

![LibraryService.java](screenshots/java-LibraryService.java.png)

### 1.8. service/PerpustakaanService.java

**Penjelasan kode:**

Kelas ini mengurus interaksi dengan pengguna: form tambah anggota, tambah buku, dan pinjam buku. Input dicek lebih dulu, misalnya ID, ISBN, dan judul tidak boleh kosong, serta jumlah eksemplar harus angka lebih dari 0. Pada form peminjaman, ID anggota yang kosong menampilkan peringatan lalu form diulang dari awal, dan mengetik `exit` mengakhiri form. `catch (PerpustakaanException)` menampilkan pesan error domain beserta datanya (ISBN, ID anggota, judul, atau batas) dengan mengecek jenis exception-nya. Jika input berakhir tiba-tiba, `NoSuchElementException` ditangkap agar program berhenti dengan rapi, sedangkan `catch (Exception)` menangani kesalahan lain.

**Screenshot kode:**

![PerpustakaanService.java](screenshots/java-PerpustakaanService.java.png)

### 1.9. Main.java

**Penjelasan kode:**

Titik masuk program. Menampilkan menu berulang (1 Tambah Anggota, 2 Tambah Buku, 3 Pinjam Buku, 0 Keluar) dan memanggil method yang sesuai di `PerpustakaanService`. Satu objek `LibraryService` dipakai bersama selama program berjalan, sehingga data anggota dan buku tetap tersimpan antar menu. Pilihan di luar menu menampilkan peringatan, dan jika input berakhir program selesai dengan pesan informasi.

**Screenshot kode:**

![Main.java](screenshots/java-Main.java.png)

### Output Java

Output pertama memperlihatkan menu utama, lalu form tambah anggota dengan ID `A-001`, dan form tambah buku dengan ISBN `111` berjudul Laskar Pelangi (1 eksemplar) serta ISBN `222` berjudul Bumi Manusia (5 eksemplar). Setiap kali berhasil, program menampilkan pesan berhasil dan kembali ke menu.

![Output Java 1](screenshots/output-java-1.png)

Output kedua memperlihatkan form peminjaman. Peminjaman pertama berhasil. Setelah itu muncul empat jenis kegagalan: buku sedang dipinjam (stok habis), ISBN tidak ditemukan, anggota tidak ditemukan, dan batas pinjam 3 buku tercapai. Masing-masing disertai data penyebabnya. ID anggota kosong ditolak dengan peringatan, lalu `exit` mengembalikan program ke menu dan `0` mengakhirinya.

![Output Java 2](screenshots/output-java-2.png)

---

## 2. Implementasi PHP

Program PHP menggunakan namespace `Perpustakaan\Exception` dan `Perpustakaan\Service`. Autoload kelas diatur lewat `composer.json` (PSR-4, namespace `Perpustakaan\` dipetakan ke folder `src/`). Logikanya sama dengan versi Java, yaitu peminjaman yang gagal melempar exception khusus.

### 2.1. src/Exception/PerpustakaanException.php

**Penjelasan kode:**

Induk semua exception perpustakaan, turunan `RuntimeException`. PHP tidak punya checked exception, jadi semua exception bersifat unchecked. Karena itu keterangan `@throws` di komentar menjadi penting sebagai petunjuk bagi pembaca kode.

**Screenshot kode:**

![PerpustakaanException.php](screenshots/php-PerpustakaanException.php.png)

### 2.2. src/Exception/BukuTidakDitemukanException.php

**Penjelasan kode:**

Dilempar saat ISBN tidak ada di katalog. Memakai constructor property promotion dengan `readonly`, sehingga `$isbn` otomatis tersimpan dan tidak bisa diubah. Datanya diambil lewat `getIsbn()`.

**Screenshot kode:**

![BukuTidakDitemukanException.php](screenshots/php-BukuTidakDitemukanException.php.png)

### 2.3. src/Exception/AnggotaTidakDitemukanException.php

**Penjelasan kode:**

Dilempar saat anggota belum terdaftar. Membawa `$idAnggota` yang bisa diambil lewat `getIdAnggota()`.

**Screenshot kode:**

![AnggotaTidakDitemukanException.php](screenshots/php-AnggotaTidakDitemukanException.php.png)

### 2.4. src/Exception/BukuSedangDipinjamException.php

**Penjelasan kode:**

Dilempar saat stok buku habis. Membawa `$isbn` dan `$judul`, dengan getter `getIsbn()` dan `getJudul()`. Nama file harus sama persis dengan nama kelasnya agar autoload PSR-4 dapat menemukannya.

**Screenshot kode:**

![BukuSedangDipinjamException.php](screenshots/php-BukuSedangDipinjamException.php.png)

### 2.5. src/Exception/BatasPinjamTerlampauiException.php

**Penjelasan kode:**

Dilempar saat anggota sudah mencapai batas pinjam. Membawa `$idAnggota` dan `$batas`, dengan getter `getIdAnggota()` dan `getBatas()`.

**Screenshot kode:**

![BatasPinjamTerlampauiException.php](screenshots/php-BatasPinjamTerlampauiException.php.png)

### 2.6. src/Service/LibraryServiceKotor.php

**Penjelasan kode:**

Versi awal bahan refactoring. Method `pinjam()` bersarang empat tingkat dan hanya mengembalikan `true` atau `false` tanpa menjelaskan penyebab gagal. File ini memakai `declare(strict_types=1)` dan berada di namespace `Perpustakaan\Service`.

**Screenshot kode:**

![LibraryServiceKotor.php](screenshots/php-LibraryServiceKotor.php.png)

### 2.7. src/Service/LibraryService.php

**Penjelasan kode:**

Hasil refactoring dengan guard clause. Method `pinjam()` mengecek anggota, buku, stok, dan batas pinjam secara berurutan dan melempar exception yang sesuai bila gagal. Jika semua syarat terpenuhi, stok berkurang dan pinjaman anggota bertambah. Komentar `@throws` mendaftar semua exception yang mungkin dilempar.

**Screenshot kode:**

![LibraryService.php](screenshots/php-LibraryService.php.png)

### 2.8. src/Main.php

**Penjelasan kode:**

Titik masuk program PHP, diletakkan di dalam folder `src`. Di bagian awal, file ini memakai `vendor/autoload.php` milik Composer jika sudah dipasang, dan jika belum memakai autoloader sederhana (`spl_autoload_register`) dengan aturan PSR-4 yang sama. Setelah itu dibuat dua buku dan dua anggota, lalu dijalankan tujuh percobaan peminjaman. Setiap kegagalan ditangkap dengan `catch (PerpustakaanException)` dan pesannya ditampilkan. Fungsi `createReport()` kemudian menulis `laporan_buku.txt`, dengan blok `finally` yang memastikan file selalu ditutup. Pada laporan, ISBN diubah kembali ke `string` dengan `(string)` karena PHP otomatis mengubah kunci array numerik seperti `'111'` menjadi `int`, yang akan menimbulkan `TypeError` pada method bertipe `string` bila `strict_types` aktif.

**Screenshot kode:**

![Main.php](screenshots/php-Main.php.png)

### Output PHP

Output memperlihatkan tujuh percobaan peminjaman. Percobaan pertama berhasil, lalu tiga percobaan berikutnya gagal karena buku sedang dipinjam, ISBN tidak ditemukan, dan anggota tidak terdaftar. Dua percobaan setelahnya berhasil, dan yang terakhir gagal karena batas 3 buku tercapai. Di akhir, laporan berhasil disimpan dan file ditutup lewat blok `finally`.

![Output PHP](screenshots/output-php.png)

---

## 3. Cara Menjalankan

**Java** (dari folder `Pert10/starter/java`):

```bash
# Linux / Mac / Git Bash
javac -d out $(find src -name '*.java')
java -cp out id.ac.univpancasila.perpustakaan.Main
```

```powershell
# Windows PowerShell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out id.ac.univpancasila.perpustakaan.Main
```

Gunakan Java 17 atau lebih baru. Tambahkan anggota (menu 1) dan buku (menu 2) lebih dulu sebelum meminjam (menu 3).

**PHP** (dari folder `Pert10/starter/php`, butuh PHP 8.1 atau lebih baru):

```bash
php src/Main.php
```

---

## 4. Kesimpulan

Praktikum ini mengganti kegagalan yang disampaikan lewat `return false` dengan exception khusus yang menjelaskan penyebabnya dan membawa datanya. Seluruh exception diturunkan dari satu induk bertipe unchecked (`RuntimeException`), sehingga kegagalan domain perpustakaan bisa ditangkap sekaligus tanpa memaksa setiap pemanggil menulis try-catch. Struktur `if` bersarang diubah menjadi guard clause sehingga alurnya lebih lurus dan mudah dikembangkan. Pada sisi PHP, praktikum ini juga menunjukkan bahwa autoload PSR-4 menuntut nama file sama persis dengan nama kelasnya.
