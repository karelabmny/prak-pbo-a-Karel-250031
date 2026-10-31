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

![Buku.java](../img/buku.java.png)

### 1.2. `model/Anggota.java`

**Penjelasan kode:**

Kelas `Anggota` merepresentasikan anggota perpustakaan dengan nomor anggota, nama, dan jenis keanggotaan. Seluruh atribut bersifat `final`, dan konstruktor memvalidasi agar nomor anggota serta nama tidak kosong. Kelas menyediakan getter dan `toString()`.

**Screenshot kode:**

![Anggota.java](../img/anggota.java.png)

### 1.3. `model/Peminjaman.java`

**Penjelasan kode:**

Kelas `Peminjaman` menyimpan ID transaksi, anggota, buku, tanggal pinjam, tanggal jatuh tempo, dan tanggal kembali. Tanggal jatuh tempo dihitung dengan `LocalDate.plusDays()` dari tanggal pinjam dan lama peminjaman. Status pengembalian diketahui dari ada atau tidaknya tanggal kembali (`sudahKembali()`). Konstruktor menolak ID kosong serta anggota atau buku bernilai `null`.

**Screenshot kode:**

![Peminjaman.java](../img/Peminjaman.java.png)

### 1.4. `service/PerpustakaanService.java`

**Penjelasan kode:**

Kelas layanan mengelola daftar koleksi, anggota, dan peminjaman. Fitur yang disediakan antara lain menambah dan mencari buku, mendaftarkan dan mencari anggota, memproses peminjaman, memproses pengembalian, serta menghitung denda. Peminjaman memeriksa keberadaan anggota dan buku, stok, serta batas maksimal tiga peminjaman aktif per anggota, lalu membuat ID transaksi berformat `PJ-001`, `PJ-002`, dan seterusnya. Tarif denda per hari adalah Rp1.000 untuk mahasiswa, Rp500 untuk dosen, dan Rp0 untuk jenis anggota lainnya.

**Screenshot kode:**

![PerpustakaanService.java](../img/PerpustakaanService.java.png)

### 1.5. `util/Format.java`

**Penjelasan kode:**

Kelas utilitas `Format` menyediakan `formatRupiah()` untuk menampilkan nominal dalam mata uang Rupiah dan `formatTanggal()` untuk tanggal berformat Indonesia (`dd MMMM yyyy`, locale `id-ID`). Tanggal `null` ditampilkan sebagai `-`. Kelas dibuat `final` dengan konstruktor `private` karena seluruh method-nya `static` dan tidak perlu dibuat menjadi objek.

**Screenshot kode:**

![Format.java](../img/format.java.png)

### 1.6. `App.java`

**Penjelasan kode:**

`App.java` merupakan titik masuk program. Aplikasi membuat tiga buku beserta babnya, mendaftarkan tiga anggota (dua mahasiswa dan satu dosen), lalu mensimulasikan dua peminjaman pada 20 Oktober 2026 dengan lama 14 hari. Peminjaman pertama dikembalikan terlambat 5 hari. Setelah itu program menampilkan tabel koleksi buku dan tabel transaksi menggunakan `AsciiTable`, serta rincian denda keterlambatan.

**Screenshot kode:**

![App.java](../img/app.java.png)

### Output Java

Output memperlihatkan daftar koleksi buku, daftar transaksi peminjaman beserta status pengembaliannya, dan simulasi denda keterlambatan.

![Output Java](../img/outputjava.png)

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

![Buku.php](../img/buku.php.png)

### 2.2. `src/Model/Bab.php`

**Penjelasan kode:**

Kelas `Bab` merepresentasikan bab pada buku dengan nomor dan judul. Atributnya `readonly`, dan objeknya dibuat melalui kelas `Buku` sebagai bentuk komposisi.

**Screenshot kode:**

![Bab.php](../img/bab.php.png)

### 2.3. `src/Model/Anggota.php`

**Penjelasan kode:**

Kelas `Anggota` menyimpan nomor anggota, nama, dan jenis keanggotaan. Atribut menggunakan property `readonly` melalui constructor promotion agar identitas tidak diubah setelah objek dibuat. Constructor memvalidasi nomor anggota dan nama agar tidak kosong.

**Screenshot kode:**

![Anggota.php](../img/anggota.php.png)

### 2.4. `src/Model/Peminjaman.php`

**Penjelasan kode:**

Kelas `Peminjaman` menyimpan data transaksi dan menggunakan library Carbon untuk menghitung tanggal jatuh tempo lewat `addDays()` pada salinan (`clone`) tanggal pinjam, sehingga tanggal pinjam asli tidak berubah. Tanggal kembali bersifat nullable (`?Carbon`) dan digunakan untuk mengetahui apakah transaksi sudah dikembalikan.

**Screenshot kode:**

![Peminjaman.php](../img/Peminjaman.php.png)

### 2.5. `src/Service/PerpustakaanService.php`

**Penjelasan kode:**

Kelas layanan mengelola koleksi buku, anggota, dan transaksi. Proses peminjaman memeriksa keberadaan anggota dan buku, stok buku, serta batas tiga peminjaman aktif. Pengembalian memperbarui tanggal kembali dan stok buku. Denda dihitung dari selisih hari antara jatuh tempo dan tanggal kembali (memakai `startOfDay()` dan `diffInDays()` dari Carbon) dikali tarif sesuai jenis keanggotaan: Rp1.000 untuk mahasiswa, Rp500 untuk dosen, dan Rp0 untuk lainnya.

**Screenshot kode:**

![PerpustakaanService.php](../img/PerpustakaanService.php.png)

### 2.6. `src/Util/Format.php`

**Penjelasan kode:**

Kelas `Format` menyediakan `formatRupiah()` yang memakai `number_format()` dengan awalan `Rp`, dan `formatTanggal()` yang memformat tanggal ke bahasa Indonesia dengan Carbon (`locale('id')` dan `isoFormat('D MMMM Y')`). Tanggal `null` ditampilkan sebagai `-`.

**Screenshot kode:**

![Format.php](../img/format.php.png)

### 2.7. `main.php`

**Penjelasan kode:**

`main.php` adalah titik masuk aplikasi PHP. File ini memanggil `vendor/autoload.php`, membuat objek buku dan anggota, melakukan simulasi peminjaman serta pengembalian terlambat dengan tanggal dari Carbon, lalu menampilkan daftar buku, daftar transaksi, dan perhitungan denda dengan `printf`.

**Screenshot kode:**

![main.php](../img/main.php.png)

### Output PHP

Output memperlihatkan koleksi buku, transaksi peminjaman, status pengembalian, dan rincian perhitungan denda keterlambatan.

![Output PHP](../img/outputphp.png)

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

## 4. Percobaan Merusak Pemetaan Package dan Namespace

Rincian lengkap ada di [`percobaan.md`](percobaan.md). Ringkasannya:

| Percobaan | Perubahan | Hasil | Perbaikan |
| :--- | :--- | :--- | :--- |
| **PHP** | `namespace` di `src/Model/Buku.php` diubah menjadi `Perpustakaan\Domain` tanpa memindahkan berkas | `Fatal error: Class "Perpustakaan\Model\Buku" not found` | Kembalikan namespace ke `Perpustakaan\Model` (atau pindahkan berkas ke `src/Domain/`) |
| **Java** | `package` di `model/Buku.java` diubah menjadi `...perpustakaan.domain` tanpa memindahkan berkas | `mvn compile` gagal: `file does not contain class ...model.Buku` | Kembalikan `package` ke `...perpustakaan.model`, lalu `mvn clean compile` hingga `BUILD SUCCESS` |

Kesimpulannya, pada PHP namespace harus selaras dengan jalur berkas menurut aturan PSR-4, sedangkan pada Java kesesuaian antara `package` dan struktur folder adalah aturan kompilasi yang mutlak.

---

## 5. Pengelolaan Repositori Git

- **`target/` dan `vendor/` tidak di-commit.** Keduanya adalah hasil build yang bisa dibuat ulang dengan `mvn compile` dan `composer install`, bisa bermasalah antar sistem operasi, dan membuat repositori membengkak. Keduanya sudah tercantum di `.gitignore`.
- **`composer.lock` wajib di-commit.** Berkas ini mengunci versi eksak seluruh paket (termasuk dependensi turunannya) sehingga semua anggota tim mendapatkan build yang identik.

---

## 6. Berkas Pendukung

| Berkas | Keterangan |
| :--- | :--- |
| `dependency-tree.txt` | Keluaran `mvn dependency:tree` (BUILD SUCCESS) yang menunjukkan `ascii-table:1.8.0` terpasang |
| `percobaan.md` | Catatan percobaan merusak package/namespace beserta analisis dan perbaikannya |
| `generate_deck.py` | Skrip Python (`python-pptx`) untuk membuat slide presentasi modul 09. Jalur penyimpanan pada bagian `__main__` masih berupa path lokal dan perlu disesuaikan. Jalankan dengan `pip install python-pptx` lalu `python generate_deck.py` |

---

## 7. Kesimpulan

Praktikum ini menerapkan pemisahan kelas berdasarkan tanggung jawab ke dalam model, service, dan utilitas. Java mengorganisasi kelas menggunakan package serta Maven sebagai build tool (lengkap dengan dependensi `ascii-table`), sedangkan PHP menggunakan namespace dan Composer untuk pengelolaan dependency serta autoload PSR-4. Percobaan merusak pemetaan membuktikan bahwa deklarasi `package` dan `namespace` harus selaras dengan struktur folder. Aplikasi perpustakaan dapat menjalankan simulasi pengelolaan buku, anggota, peminjaman, pengembalian, dan denda keterlambatan.
