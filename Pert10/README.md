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

![PerpustakaanException.java](<img width="845" height="622" alt="Perpustakaan java" src="https://github.com/user-attachments/assets/73fd5c47-8e44-4f2d-b796-7dc2471a560a" />
)

### 1.2. exception/BukuTidakDitemukanException.java

**Penjelasan kode:**

Exception ini dilempar saat ISBN tidak ada di katalog. Selain pesan, exception menyimpan `isbn` dalam atribut `private final` sehingga data penyebabnya bisa diambil lewat `getIsbn()`, tanpa harus membaca isi kalimat pesan.

**Screenshot kode:**

![BukuTidakDitemukanException.java](<img width="850" height="712" alt="Bukutidakditemukan java" src="https://github.com/user-attachments/assets/4a8fb4d2-416a-4972-b3ce-c9b109edaadb" />
)

### 1.3. exception/AnggotaTidakDitemukanException.java

**Penjelasan kode:**

Dilempar saat ID anggota belum terdaftar. Polanya sama dengan exception buku, tetapi data yang dibawa adalah `idAnggota` dengan getter `getIdAnggota()`.

**Screenshot kode:**

![AnggotaTidakDitemukanException.java](<img width="800" height="477" alt="Anggotatidakditemukan java" src="https://github.com/user-attachments/assets/fb74daaf-fa04-4c5b-bfa8-bf9288f02011" />
)

### 1.4. exception/BukuSedangDipinjamException.java

**Penjelasan kode:**

Dilempar saat stok buku habis. Exception membawa dua data, yaitu `isbn` (untuk diolah sistem) dan `judul` (agar pesan lebih mudah dipahami pengguna).

**Screenshot kode:**

![BukuSedangDipinjamException.java](<img width="830" height="645" alt="Bukusedangdipinjam java" src="https://github.com/user-attachments/assets/96d92082-c216-49b0-a02f-f07708bc4a07" />
)

### 1.5. exception/BatasPinjamTerlampauiException.java

**Penjelasan kode:**

Dilempar saat anggota sudah meminjam sebanyak batas maksimum. Exception membawa `idAnggota` dan `batas`, sehingga pemanggil bisa menampilkan batasnya tanpa perlu tahu aturannya sendiri.

**Screenshot kode:**

![BatasPinjamTerlampauiException.java](<img width="996" height="642" alt="Bataspinjamterlampaui java" src="https://github.com/user-attachments/assets/b5ebebe3-66f6-4ff6-a142-c4298ca0621e" />
)

### 1.6. service/LibraryServiceKotor.java

**Penjelasan kode:**

Ini versi awal sebagai bahan refactoring. Method `pinjam()` memakai `if` bersarang empat tingkat sehingga sulit dibaca. Saat gagal, method hanya mengembalikan `false` tanpa alasan, jadi pemanggil tidak tahu apa penyebabnya, apakah anggota belum terdaftar, buku tidak ada, stok habis, atau batas pinjam penuh.

**Screenshot kode:**

![LibraryServiceKotor.java](<img width="702" height="1001" alt="LibraryServiceKotor java" src="https://github.com/user-attachments/assets/e41bc604-29af-4d36-87e2-865121b318df" />
)

### 1.7. service/LibraryService.java

**Penjelasan kode:**

Ini hasil refactoring memakai guard clause. Setiap syarat dicek satu per satu dari atas: anggota ada, buku ada, stok tersedia, dan batas pinjam belum penuh. Kalau ada yang tidak terpenuhi, langsung dilempar exception yang sesuai. Kalau semua lolos, stok dikurangi dan jumlah pinjaman anggota bertambah. Alurnya lurus dan mudah ditambah aturan baru. Batas pinjam disimpan di konstanta `BATAS_PINJAM` bernilai 3.

**Screenshot kode:**

![LibraryService.java](<img width="1030" height="1027" alt="libraryservice java" src="https://github.com/user-attachments/assets/1b63a4c0-3c24-4db3-a58f-6a19ba878d42" />
)

### 1.8. service/PerpustakaanService.java

**Penjelasan kode:**

Kelas ini mengurus interaksi dengan pengguna: form tambah anggota, tambah buku, dan pinjam buku. Input dicek lebih dulu, misalnya ID, ISBN, dan judul tidak boleh kosong, serta jumlah eksemplar harus angka lebih dari 0. Pada form peminjaman, ID anggota yang kosong menampilkan peringatan lalu form diulang dari awal, dan mengetik `exit` mengakhiri form. `catch (PerpustakaanException)` menampilkan pesan error domain beserta datanya (ISBN, ID anggota, judul, atau batas) dengan mengecek jenis exception-nya. Jika input berakhir tiba-tiba, `NoSuchElementException` ditangkap agar program berhenti dengan rapi, sedangkan `catch (Exception)` menangani kesalahan lain.

**Screenshot kode:**

![PerpustakaanService.java](<img width="896" height="1031" alt="PerpustakaanService,java" src="https://github.com/user-attachments/assets/505cc9d3-12ea-4fe6-8d5e-dc04154f4d18" />
)

### 1.9. Main.java

**Penjelasan kode:**

Titik masuk program. Menampilkan menu berulang (1 Tambah Anggota, 2 Tambah Buku, 3 Pinjam Buku, 0 Keluar) dan memanggil method yang sesuai di `PerpustakaanService`. Satu objek `LibraryService` dipakai bersama selama program berjalan, sehingga data anggota dan buku tetap tersimpan antar menu. Pilihan di luar menu menampilkan peringatan, dan jika input berakhir program selesai dengan pesan informasi.

**Screenshot kode:**

![Main.java](<img width="1137" height="992" alt="main java" src="https://github.com/user-attachments/assets/cffe2b4f-23e3-4513-8ba5-23ade39bcdd6" />
)

### Output Java

<img width="612" height="877" alt="Outputjava" src="https://github.com/user-attachments/assets/f7830e8e-7234-46ad-aa9d-bb3abdfa2174" />


---

## 2. Implementasi PHP

Program PHP menggunakan namespace `Perpustakaan\Exception` dan `Perpustakaan\Service`. Autoload kelas diatur lewat `composer.json` (PSR-4, namespace `Perpustakaan\` dipetakan ke folder `src/`). Logikanya sama dengan versi Java, yaitu peminjaman yang gagal melempar exception khusus.

### 2.1. src/Exception/PerpustakaanException.php

**Penjelasan kode:**

Induk semua exception perpustakaan, turunan `RuntimeException`. PHP tidak punya checked exception, jadi semua exception bersifat unchecked. Karena itu keterangan `@throws` di komentar menjadi penting sebagai petunjuk bagi pembaca kode.

**Screenshot kode:**

![PerpustakaanException.php](<img width="830" height="407" alt="Perpustakaan php" src="https://github.com/user-attachments/assets/464716e3-5cbc-4d6d-ac5e-dfcb090564b9" />
)

### 2.2. src/Exception/BukuTidakDitemukanException.php

**Penjelasan kode:**

Dilempar saat ISBN tidak ada di katalog. Memakai constructor property promotion dengan `readonly`, sehingga `$isbn` otomatis tersimpan dan tidak bisa diubah. Datanya diambil lewat `getIsbn()`.

**Screenshot kode:**

![BukuTidakDitemukanException.php](<img width="867" height="577" alt="Bukutidakditemukan php" src="https://github.com/user-attachments/assets/1bb72bc7-885d-4309-bbe7-622ad411ffe4" />
)

### 2.3. src/Exception/AnggotaTidakDitemukanException.php

**Penjelasan kode:**

Dilempar saat anggota belum terdaftar. Membawa `$idAnggota` yang bisa diambil lewat `getIdAnggota()`.

**Screenshot kode:**

![AnggotaTidakDitemukanException.php](<img width="852" height="481" alt="Anggotatidakditemukan php" src="https://github.com/user-attachments/assets/3c4dc32b-c81f-4f11-90e6-79f86ef7d3f6" />
)

### 2.4. src/Exception/BukuSedangDipinjamException.php

**Penjelasan kode:**

Dilempar saat stok buku habis. Membawa `$isbn` dan `$judul`, dengan getter `getIsbn()` dan `getJudul()`. Nama file harus sama persis dengan nama kelasnya agar autoload PSR-4 dapat menemukannya.

**Screenshot kode:**

![BukuSedangDipinjamException.php](<img width="870" height="647" alt="Bukusedangdipinjam php" src="https://github.com/user-attachments/assets/565c3979-2c2b-4ff4-9810-5c27cac4a3ec" />
)

### 2.5. src/Exception/BatasPinjamTerlampauiException.php

**Penjelasan kode:**

Dilempar saat anggota sudah mencapai batas pinjam. Membawa `$idAnggota` dan `$batas`, dengan getter `getIdAnggota()` dan `getBatas()`.

**Screenshot kode:**

![BatasPinjamTerlampauiException.php](<img width="902" height="692" alt="Bataspinjamterlampaui php" src="https://github.com/user-attachments/assets/cbaefb84-dd5a-48d5-bddd-bd29a562917a" />
)

### 2.6. src/Service/LibraryServiceKotor.php

**Penjelasan kode:**

Versi awal bahan refactoring. Method `pinjam()` bersarang empat tingkat dan hanya mengembalikan `true` atau `false` tanpa menjelaskan penyebab gagal. File ini memakai `declare(strict_types=1)` dan berada di namespace `Perpustakaan\Service`.

**Screenshot kode:**

![LibraryServiceKotor.php](<img width="762" height="1030" alt="libararyservicekotor php" src="https://github.com/user-attachments/assets/cb630713-ff0b-4c7e-820b-59e6b3f86a13" />
)

### 2.7. src/Service/LibraryService.php

**Penjelasan kode:**

Hasil refactoring dengan guard clause. Method `pinjam()` mengecek anggota, buku, stok, dan batas pinjam secara berurutan dan melempar exception yang sesuai bila gagal. Jika semua syarat terpenuhi, stok berkurang dan pinjaman anggota bertambah. Komentar `@throws` mendaftar semua exception yang mungkin dilempar.

**Screenshot kode:**

![LibraryService.php](<img width="782" height="1035" alt="libraryservice php" src="https://github.com/user-attachments/assets/62cde85f-d7c4-4703-ac31-3d4f613eebfc" />
)

### 2.8. src/Main.php

**Penjelasan kode:**

Titik masuk program PHP, diletakkan di dalam folder `src`. Di bagian awal, file ini memakai `vendor/autoload.php` milik Composer jika sudah dipasang, dan jika belum memakai autoloader sederhana (`spl_autoload_register`) dengan aturan PSR-4 yang sama. Setelah itu dibuat dua buku dan dua anggota, lalu dijalankan tujuh percobaan peminjaman. Setiap kegagalan ditangkap dengan `catch (PerpustakaanException)` dan pesannya ditampilkan. Fungsi `createReport()` kemudian menulis `laporan_buku.txt`, dengan blok `finally` yang memastikan file selalu ditutup. Pada laporan, ISBN diubah kembali ke `string` dengan `(string)` karena PHP otomatis mengubah kunci array numerik seperti `'111'` menjadi `int`, yang akan menimbulkan `TypeError` pada method bertipe `string` bila `strict_types` aktif.

**Screenshot kode:**

![Main.php](<img width="881" height="1030" alt="main php" src="https://github.com/user-attachments/assets/ef05e087-eb3c-4777-b559-1b29b01ad42c" />
)

### Output PHP

Output memperlihatkan tujuh percobaan peminjaman. Percobaan pertama berhasil, lalu tiga percobaan berikutnya gagal karena buku sedang dipinjam, ISBN tidak ditemukan, dan anggota tidak terdaftar. Dua percobaan setelahnya berhasil, dan yang terakhir gagal karena batas 3 buku tercapai. Di akhir, laporan berhasil disimpan dan file ditutup lewat blok `finally`.

![Output PHP](<img width="757" height="426" alt="outputphp" src="https://github.com/user-attachments/assets/e0f89a7b-0701-4920-bf6a-4bc968632bf8" />
)

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
