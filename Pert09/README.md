# LAPORAN PRAKTIKUM PEMROGRAMAN BERBASIS OBJEK

| Informasi Praktikan | Keterangan |
| :--- | :--- |
| **Nama** | *Karel Abimanyu Ahpandi* |
| **NPM** | *4525210031* |
| **Kelas** | A |
| **Mata Kuliah** | Pemrograman Berbasis Objek (PBO) |
| **Pertemuan** | 09 — Package, Namespace, dan Build Tool |
| **Tanggal** | *Rabu, 28 Oktober 2026* |
| **Dosen Pengampu** | *Adi Wahyu Pribadi, S.Si., M.Kom* |

---

## Struktur Folder

```
Pert09/
├── img/                       # screenshot kode dan output (Java & PHP)
└── starter/
    ├── java/                  # proyek Maven
    │   ├── pom.xml
    │   └── src/main/java/id/ac/univpancasila/perpustakaan/
    │       ├── App.java
    │       ├── model/         # Anggota, Buku, Peminjaman
    │       ├── service/       # PerpustakaanService
    │       └── util/          # Format
    ├── php/                   # proyek Composer
    │   ├── composer.json
    │   ├── composer.lock
    │   ├── main.php
    │   └── src/
    │       ├── Model/         # Anggota, Bab, Buku, Peminjaman
    │       ├── Service/       # PerpustakaanService
    │       └── Util/          # Format
    ├── dependency-tree.txt    # hasil mvn dependency:tree
    ├── percobaan.md           # catatan percobaan merusak package/namespace
    ├── generate_deck.py       # skrip pembuat slide presentasi modul 09
    ├── .gitignore
    └── README.md
```

---

## 1. Implementasi Java

Program Java menggunakan package `id.ac.univpancasila.perpustakaan` dan dibangun dengan Maven. Aplikasi mensimulasikan sistem perpustakaan: pengelolaan koleksi buku dan anggota, transaksi peminjaman/pengembalian, serta perhitungan denda. Tampilan tabel di terminal memakai pustaka eksternal `ascii-table` (`com.github.freva:ascii-table:1.8.0`) yang dideklarasikan sebagai dependensi pada `pom.xml`.

**Cara menjalankan:**

```bash
cd java
mvn clean compile
mvn exec:java -Dexec.mainClass=id.ac.univpancasila.perpustakaan.App
```

> `pom.xml` menetapkan `maven.compiler.release` ke 25, sehingga dibutuhkan JDK 25.

### 1.1. `model/Buku.java`

**Penjelasan kode:**

Kelas `Buku` menyimpan nomor induk, judul, penulis, jumlah eksemplar, dan daftar `Bab`. `Bab` dibuat sebagai nested class (`static final`) di dalam `Buku` dengan konstruktor `private`, sehingga bab hanya bisa dibuat lewat `tambahBab()` dan menggambarkan relasi komposisi. Kelas juga menyediakan `tersedia()`, `kurangiStok()` (melempar `IllegalStateException` jika stok habis), dan `tambahStok()`. Jumlah eksemplar tidak boleh negatif, dan `getBab()` mengembalikan salinan daftar yang tidak bisa diubah.

**Screenshot kode:**

[Buku.java]<img width="677" height="1021" alt="buku java" src="https://github.com/user-attachments/assets/48a7622a-d0c5-4d8d-8cb7-a70fb9218b22" />


### 1.2. `model/Anggota.java`

**Penjelasan kode:**

Kelas `Anggota` merepresentasikan anggota perpustakaan dengan nomor anggota, nama, dan jenis keanggotaan. Seluruh atribut bersifat `final`, dan konstruktor memvalidasi agar nomor anggota serta nama tidak kosong. Kelas menyediakan getter dan `toString()`.

**Screenshot kode:**

[Anggota.java]<img width="1167" height="1017" alt="anggota java" src="https://github.com/user-attachments/assets/2bda5942-0d8b-4ebc-a10a-a7015faa3879" />


### 1.3. `model/Peminjaman.java`

**Penjelasan kode:**

Kelas `Peminjaman` menyimpan ID transaksi, anggota, buku, tanggal pinjam, tanggal jatuh tempo, dan tanggal kembali. Tanggal jatuh tempo dihitung dengan `LocalDate.plusDays()` dari tanggal pinjam dan lama peminjaman. Status pengembalian diketahui dari ada atau tidaknya tanggal kembali (`sudahKembali()`). Konstruktor menolak ID kosong serta anggota atau buku bernilai `null`.

**Screenshot kode:**

[Peminjaman.java]<img width="821" height="1042" alt="Peminjaman java" src="https://github.com/user-attachments/assets/7b9a6c9b-f842-4d3d-999d-3b98b30abf22" />


### 1.4. `service/PerpustakaanService.java`

**Penjelasan kode:**

Kelas layanan mengelola daftar koleksi, anggota, dan peminjaman. Fitur yang disediakan antara lain menambah dan mencari buku, mendaftarkan dan mencari anggota, memproses peminjaman, memproses pengembalian, serta menghitung denda. Peminjaman memeriksa keberadaan anggota dan buku, stok, serta batas maksimal tiga peminjaman aktif per anggota, lalu membuat ID transaksi berformat `PJ-001`, `PJ-002`, dan seterusnya. Tarif denda per hari adalah Rp1.000 untuk mahasiswa, Rp500 untuk dosen, dan Rp0 untuk jenis anggota lainnya.

**Screenshot kode:**

[PerpustakaanService.java]<img width="726" height="1035" alt="PerpustakaanService java" src="https://github.com/user-attachments/assets/5ce70e48-f5e2-48f3-957b-1da21f2aab58" />


### 1.5. `util/Format.java`

**Penjelasan kode:**

Kelas utilitas `Format` menyediakan `formatRupiah()` untuk menampilkan nominal dalam mata uang Rupiah dan `formatTanggal()` untuk tanggal berformat Indonesia (`dd MMMM yyyy`, locale `id-ID`). Tanggal `null` ditampilkan sebagai `-`. Kelas dibuat `final` dengan konstruktor `private` karena seluruh method-nya `static` dan tidak perlu dibuat menjadi objek.

**Screenshot kode:**

[Format.java]<img width="1277" height="802" alt="format java" src="https://github.com/user-attachments/assets/cb1928d0-e254-4a41-9135-3c40a35e64ff" />

### 1.6. `App.java`

**Penjelasan kode:**

`App.java` merupakan titik masuk program. Aplikasi membuat tiga buku beserta babnya, mendaftarkan tiga anggota (dua mahasiswa dan satu dosen), lalu mensimulasikan dua peminjaman pada 20 Oktober 2026 dengan lama 14 hari. Peminjaman pertama dikembalikan terlambat 5 hari. Setelah itu program menampilkan tabel koleksi buku dan tabel transaksi menggunakan `AsciiTable`, serta rincian denda keterlambatan.

**Screenshot kode:**

[App.java]<img width="962" height="1035" alt="app java" src="https://github.com/user-attachments/assets/92ff4f15-39f6-45a0-885a-61e64f98c774" />


### Output Java

Output memperlihatkan daftar koleksi buku, daftar transaksi peminjaman beserta status pengembaliannya, dan simulasi denda keterlambatan.

[Output Java]<img width="1165" height="601" alt="outputjava" src="https://github.com/user-attachments/assets/464a1aba-3dfc-45a0-acd3-d915420df25b" />


---

## 2. Implementasi PHP

Program PHP menggunakan namespace `Perpustakaan\Model`, `Perpustakaan\Service`, dan `Perpustakaan\Util`. Autoload kelas dikelola oleh Composer melalui `vendor/autoload.php`, sehingga tidak perlu melakukan `require_once` satu per satu untuk setiap kelas. Seluruh berkas memakai `declare(strict_types=1)`.

**Cara menjalankan:**

```bash
cd php
composer install
php main.php
```

> `composer.json` mensyaratkan PHP `^8.2 || ^8.4` dan `nesbot/carbon` `^2.72 || ^3.0`.

### 2.1. `src/Model/Buku.php`

**Penjelasan kode:**

Kelas `Buku` menyimpan data buku dan daftar bab. Bab ditambahkan melalui `tambahBab()`, yang membuat objek `Bab` di dalam kelas `Buku` sebagai bentuk komposisi. Kelas juga memiliki `tersedia()`, `kurangiStok()` (melempar `RuntimeException` jika stok habis), dan `tambahStok()`. Jumlah eksemplar yang negatif ditolak di konstruktor.

**Screenshot kode:**

[Buku.php]<img width="782" height="1035" alt="buku php" src="https://github.com/user-attachments/assets/4a1c1738-f542-497f-841b-a2015a88803f" />

### 2.2. `src/Model/Bab.php`

**Penjelasan kode:**

Kelas `Bab` merepresentasikan bab pada buku dengan nomor dan judul. Atributnya `readonly`, dan objeknya dibuat melalui kelas `Buku` sebagai bentuk komposisi.

**Screenshot kode:**

[Bab.php]<img width="702" height="780" alt="bab php" src="https://github.com/user-attachments/assets/816eb2be-f5dd-40ac-9b94-f2fe225f1295" />


### 2.3. `src/Model/Anggota.php`

**Penjelasan kode:**

Kelas `Anggota` menyimpan nomor anggota, nama, dan jenis keanggotaan. Atribut menggunakan property `readonly` melalui constructor promotion agar identitas tidak diubah setelah objek dibuat. Constructor memvalidasi nomor anggota dan nama agar tidak kosong.

**Screenshot kode:**

[Anggota.php]<img width="857" height="950" alt="anggota php" src="https://github.com/user-attachments/assets/bf838cd7-39df-4718-8197-9855d1a13020" />


### 2.4. `src/Model/Peminjaman.php`

**Penjelasan kode:**

Kelas `Peminjaman` menyimpan data transaksi dan menggunakan library Carbon untuk menghitung tanggal jatuh tempo lewat `addDays()` pada salinan (`clone`) tanggal pinjam, sehingga tanggal pinjam asli tidak berubah. Tanggal kembali bersifat nullable (`?Carbon`) dan digunakan untuk mengetahui apakah transaksi sudah dikembalikan.

**Screenshot kode:**

[Peminjaman.php]<img width="747" height="1030" alt="Peminjaman php" src="https://github.com/user-attachments/assets/06a54858-cc11-4772-9db7-3bd8085f48f0" />


### 2.5. `src/Service/PerpustakaanService.php`

**Penjelasan kode:**

Kelas layanan mengelola koleksi buku, anggota, dan transaksi. Proses peminjaman memeriksa keberadaan anggota dan buku, stok buku, serta batas tiga peminjaman aktif. Pengembalian memperbarui tanggal kembali dan stok buku. Denda dihitung dari selisih hari antara jatuh tempo dan tanggal kembali (memakai `startOfDay()` dan `diffInDays()` dari Carbon) dikali tarif sesuai jenis keanggotaan: Rp1.000 untuk mahasiswa, Rp500 untuk dosen, dan Rp0 untuk lainnya.

**Screenshot kode:**

[PerpustakaanService.php]<img width="667" height="1040" alt="PerpustakaanService php" src="https://github.com/user-attachments/assets/b5db6181-a450-4329-99ed-66e7f77c0394" />


### 2.6. `src/Util/Format.php`

**Penjelasan kode:**

Kelas `Format` menyediakan `formatRupiah()` yang memakai `number_format()` dengan awalan `Rp`, dan `formatTanggal()` yang memformat tanggal ke bahasa Indonesia dengan Carbon (`locale('id')` dan `isoFormat('D MMMM Y')`). Tanggal `null` ditampilkan sebagai `-`.

**Screenshot kode:**

[Format.php]<img width="737" height="707" alt="format php" src="https://github.com/user-attachments/assets/49a007d0-8564-47b6-9dfe-ccbf5be447ab" />


### 2.7. `main.php`

**Penjelasan kode:**

`main.php` adalah titik masuk aplikasi PHP. File ini memanggil `vendor/autoload.php`, membuat objek buku dan anggota, melakukan simulasi peminjaman serta pengembalian terlambat dengan tanggal dari Carbon, lalu menampilkan daftar buku, daftar transaksi, dan perhitungan denda dengan `printf`.

**Screenshot kode:**

[main.php]<img width="752" height="1032" alt="main php" src="https://github.com/user-attachments/assets/7a365fc2-de1d-4ac3-a1ac-077ac94a32ac" />


### Output PHP

Output memperlihatkan koleksi buku, transaksi peminjaman, status pengembalian, dan rincian perhitungan denda keterlambatan.

[Output PHP]<img width="1135" height="492" alt="outputphp" src="https://github.com/user-attachments/assets/e083d7bd-3963-43ce-a2aa-8e7b710dcaca" />


---

## 3. Package, Namespace, dan Build Tool

| Teknologi | Implementasi |
| :--- | :--- |
| **Package Java** | `id.ac.univpancasila.perpustakaan` beserta subpackage `model`, `service`, dan `util` |
| **Build tool Java** | Maven, dengan konfigurasi pada `java/pom.xml` (`groupId` `id.ac.univpancasila.perpustakaan`, `artifactId` `perpustakaan`, versi `1.0.0`) |
| **Dependensi Java** | `com.github.freva:ascii-table:1.8.0` untuk tabel di terminal; buktinya ada di `dependency-tree.txt` |
| **Menjalankan Java** | Plugin `exec-maven-plugin` 3.5.0 melalui `mvn exec:java` |
| **Namespace PHP** | `Perpustakaan\Model`, `Perpustakaan\Service`, dan `Perpustakaan\Util` |
| **Dependency manager PHP** | Composer, dengan konfigurasi pada `php/composer.json` |
| **Autoload PHP** | PSR-4 (`"Perpustakaan\\": "src/"`) melalui `vendor/autoload.php` |
| **Library tanggal PHP** | Carbon (`nesbot/carbon`) untuk perhitungan tanggal jatuh tempo dan keterlambatan |

---

## 7. Kesimpulan

Praktikum ini menerapkan pemisahan kelas berdasarkan tanggung jawab ke dalam model, service, dan utilitas. Java mengorganisasi kelas menggunakan package serta Maven sebagai build tool (lengkap dengan dependensi `ascii-table`), sedangkan PHP menggunakan namespace dan Composer untuk pengelolaan dependency serta autoload PSR-4. Percobaan merusak pemetaan membuktikan bahwa deklarasi `package` dan `namespace` harus selaras dengan struktur folder. Aplikasi perpustakaan dapat menjalankan simulasi pengelolaan buku, anggota, peminjaman, pengembalian, dan denda keterlambatan.
