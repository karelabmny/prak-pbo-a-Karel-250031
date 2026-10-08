# Praktikum Sesi 6 — Abstract Class, Interface, Enum, dan Trait

**Sub-CPMK-P4 — Mahasiswa mampu mengimplementasikan abstraksi melalui abstract class, interface, enum, dan trait. (P3)**

**Keterkaitan teori:** Pertemuan 6 — Abstract Class, Interface, Enum, dan Trait  
**Durasi:** 170 menit (1 SKS praktikum) · **Bobot:** 5% dari nilai praktikum  
**Bahasa:** Java 25 dan PHP 8.4+

> Versi cetak modul ini: [`Modul-Praktikum-06-abstract-class-interface-enum-dan-trait.docx`](Modul-Praktikum-06-abstract-class-interface-enum-dan-trait.docx). Kerangka kode: [`starter/`](starter/).

---

## A. Tujuan sesi

1. Memisahkan kemampuan (interface) dari identitas (abstract class) pada satu domain.
2. Mengimplementasikan beberapa interface pada satu kelas.
3. Mengganti konstanta status dengan enum yang punya perilaku.
4. Menggunakan trait di PHP dan mengenali batas kewajarannya.

## B. Alat dan bahan

- Lingkungan hasil sesi 1.
- PlantUML.

## C. Berkas starter

Salin ke folder tugas Anda; **jangan** menyunting berkas aslinya. Setiap komentar `TODO` harus Anda lengkapi sendiri — kode yang disalin dari sumber lain akan terlihat pada sesi demo.

| Berkas | Keterangan |
|---|---|
| `starter/java/Movable.java` | Interface dengan TODO pada default method. |
| `starter/java/Fuelable.java` | Interface kontrak pengisian bahan bakar. |
| `starter/java/TipeBahanBakar.java` | Enum dengan TODO pada method biayaPengisian(). |
| `starter/java/Kendaraan.java` | Abstract class dengan TODO. |
| `starter/java/Mobil.java` | Kelas yang mengimplementasikan dua interface — banyak TODO. |
| `starter/java/Main.java` | Program uji. |
| `starter/php/abstraksi.php` | Seluruh struktur PHP dalam satu berkas, dengan TODO. |
| `starter/php/main.php` | Program uji PHP. |

## D. Langkah kerja

Kerjakan berurutan. Jangan melanjutkan ke langkah berikutnya sebelum checkpoint terpenuhi.

### Langkah 1 — Melengkapi interface dan abstract class

- Salin starter ke sesi-06/. Lengkapi Movable, Fuelable, dan Kendaraan.
- Perhatikan pembagiannya: Kendaraan adalah abstract class karena semua kendaraan punya merek dan tahun — kode yang benar-benar sama. Movable dan Fuelable adalah interface karena tidak semua yang bergerak butuh bahan bakar.
- Tuliskan di sesi-06/keputusan.md: untuk setiap dari ketiganya, mengapa ia interface atau abstract class?

> **Checkpoint —** keputusan.md memuat tiga keputusan dengan alasan yang berpijak pada "apa benda ini" vs "apa yang bisa dilakukannya".

### Langkah 2 — Melengkapi enum dengan perilaku

- Lengkapi TipeBahanBakar: setiap konstanta punya label dan harga per satuan.
- Lengkapi method biayaPengisian(double jumlah) dan ramahLingkungan().
- Tambahkan case LISTRIK bila belum ada.

**Terminal**

```bash
cd sesi-06/java
javac -d out *.java
java -cp out Main
```

> **Checkpoint —** Program mencetak tiga jenis bahan bakar dengan biaya dan status ramah lingkungan yang berbeda.

### Langkah 3 — Mengimplementasikan dua interface pada satu kelas

- Lengkapi Mobil: extends Kendaraan implements Movable, Fuelable.
- Perhatikan bahwa Java hanya mengizinkan SATU extends tetapi BANYAK implements. Catat di keputusan.md mengapa aturan itu ada.

> **Checkpoint —** Mobil dapat dimasukkan ke variabel bertipe Kendaraan, Movable, maupun Fuelable.

### Langkah 4 — Membuat Sepeda dan membuktikan Interface Segregation

- Buat kelas Sepeda: extends Kendaraan implements Movable — TETAPI BUKAN Fuelable.
- Coba panggil isiPenuh(sepeda) di Main. Catat pesan kompilatornya di keputusan.md.
- Jelaskan mengapa penolakan pada tahap KOMPILASI ini justru menguntungkan.

> **Checkpoint —** Kompilasi menolak isiPenuh(sepeda), dan pesan kesalahannya sudah tercatat.

### Langkah 5 — Trait di PHP

- Di sesi-06/php/, lengkapi trait Loggable dengan method log(string $pesan).
- Pakai trait itu pada Mobil DAN pada satu kelas yang sama sekali bukan kerabat kendaraan — misalnya Pesanan. Inilah yang dimaksud penggunaan ulang horizontal.
- Catat di keputusan.md: kapan trait menjadi berbahaya?

**Terminal**

```bash
cd ../php
php main.php
```

> **Checkpoint —** Dua kelas yang tidak sekerabat sama-sama bisa memanggil log().

### Langkah 6 — Latihan mandiri: interface Peminjamable

- Tanpa starter. Rancang interface Peminjamable dengan method bolehDipinjam(): bool dan masaPinjamHari(): int.
- Terapkan pada tiga kelas: Buku (14 hari), Majalah (3 hari), dan Skripsi (tidak boleh dipinjam).
- Buat juga enum StatusPinjam dengan case Tersedia, Dipinjam, Terlambat, Hilang, lengkap dengan method keterangan().
- Simpan di sesi-06/latihan/ (Java dan PHP).

> **Checkpoint —** Satu fungsi dapat memproses ketiga jenis koleksi tanpa satu pun pemeriksaan tipe.

### Langkah 7 — Class diagram

- Buat sesi-06/uml/abstraksi.puml yang menampilkan abstract class, interface (dengan notasi <<interface>>), dan enum secara benar.
- Bedakan panah pewarisan (garis penuh, kepala segitiga kosong) dari panah realisasi interface (garis putus, kepala segitiga kosong).

> **Checkpoint —** Diagram membedakan pewarisan dan realisasi interface dengan notasi yang benar.

## E. Latihan mandiri di lab

_Dikerjakan bila langkah kerja selesai lebih awal. Tidak wajib, tetapi menambah nilai pada aspek penerapan konsep._

1. Tambahkan default method pada interface Movable, lalu override di salah satu implementornya. Jelaskan kapan default method berguna dan kapan menyesatkan.
2. Buat satu interface yang sengaja gemuk (delapan method), implementasikan pada kelas yang hanya butuh dua di antaranya. Rasakan masalahnya, lalu pecah menjadi beberapa interface kecil.

## F. Tugas rumah

1. Jawab dalam 150 kata: mengapa bolehDipinjam() lebih baik ditaruh di interface daripada dijawab dengan if ($item instanceof Skripsi)? Simpan di sesi-06/refleksi.md.
2. Persiapan sesi 7: baca deskripsi Sistem Perpustakaan pada modul sesi 7 dan tuliskan daftar kelas yang menurut Anda diperlukan.

## G. Luaran yang dikumpulkan

Seluruh luaran diserahkan melalui repositori Git pribadi Anda, dengan riwayat commit yang menunjukkan proses pengerjaan — bukan satu commit tunggal di akhir.

| Berkas / folder | Keterangan |
|---|---|
| `sesi-06/java/` | Movable, Fuelable, TipeBahanBakar, Kendaraan, Mobil, Sepeda, Main. |
| `sesi-06/php/` | abstraksi.php dan main.php dengan trait Loggable. |
| `sesi-06/latihan/` | Peminjamable dan StatusPinjam di kedua bahasa. |
| `sesi-06/uml/abstraksi.puml` | Diagram dengan notasi interface dan enum yang benar. |
| `sesi-06/keputusan.md` | Empat catatan keputusan rancangan dan dua pesan kompilator. |

## H. Rubrik penilaian sesi ini

| Aspek | Bobot | Kriteria |
|---|---|---|
| Kebenaran fungsional | 35% | Seluruh perintah pada langkah kerja berjalan dan menghasilkan keluaran yang diminta. |
| Penerapan konsep sesi ini | 30% | Konsep yang menjadi Sub-CPMK sesi ini diterapkan dengan tepat, bukan sekadar membuat program berjalan. |
| Keterbacaan dan konvensi | 10% | Penamaan bermakna, format konsisten, komentar seperlunya, riwayat commit wajar. |
| Demo dan pertanyaan lisan | 25% | Mampu menjelaskan setiap baris kode sendiri dan menjawab pertanyaan demo. MENGGUGURKAN: tanpa demo, tugas tidak dinilai. |

## I. Pertanyaan demo

> Pertanyaan berikut akan diajukan saat Anda mendemokan pekerjaan sesi ini. Daftar ini sengaja dibuka agar Anda mempersiapkan **pemahaman**, bukan hafalan. Anda boleh memakai alat bantu apa pun saat mengerjakan — tetapi kode yang tidak dapat Anda jelaskan sendiri tidak dinilai.

1. Mengapa Kendaraan Anda buat abstract class, sedangkan Movable interface? Apa yang menjadi dasar pemisahannya?
2. Tunjukkan kelas yang mengimplementasikan lebih dari satu interface. Mengapa Java tidak mengizinkan extends lebih dari satu, tetapi mengizinkan implements banyak?
3. Saya ingin menambah Generator: butuh bahan bakar tapi tidak bergerak. Interface mana yang ia implementasikan? Buat sekarang.
4. Pada method isiPenuh(Fuelable), mengapa Sepeda ditolak saat kompilasi dan bukan saat program berjalan? Mana yang lebih baik?
5. Apa keuntungan enum TipeBahanBakar dibanding tiga konstanta int? Buktikan dengan mencoba memberi nilai yang tidak terdaftar.
6. Di kode PHP Anda, tunjukkan dua kelas yang memakai trait Loggable. Apakah keduanya sekerabat? Mengapa trait cocok di sini?
7. Kapan trait menjadi berbahaya? Berikan satu contoh konkret.

## J. Kesalahan yang sering terjadi

| Gejala | Penyebab yang lazim | Cara memperbaiki |
|---|---|---|
| Kompilasi gagal: Mobil is not abstract and does not override abstract method | Ada method interface atau abstract yang belum diimplementasikan. | Implementasikan seluruh method kontrak. IDE biasanya bisa membuatkan kerangkanya. |
| PHP: enum tidak dikenali | Enum baru ada sejak PHP 8.1. | Periksa php -v. Perbarui PHP jika di bawah 8.1. |
| Trait dan kelas punya method bernama sama | Konflik nama; PHP memberi prioritas ke method kelas. | Gunakan insteadof dan as untuk menyelesaikan konflik secara eksplisit, atau ganti nama method. |
| Interface diberi atribut non-konstanta | Interface tidak boleh menyimpan keadaan. | Pindahkan atribut ke abstract class atau ke kelas implementor. |
| Diagram tidak membedakan interface dari kelas | Notasi <<interface>> tidak ditulis. | Di PlantUML gunakan kata kunci interface, dan panah ..\|> untuk realisasi. |

## Lembar verifikasi demo

Diisi oleh dosen atau asisten pada saat demo. Tugas tanpa lembar terverifikasi tidak dinilai.

|  |  |
|---|---|
| Nama / NIM |  |
| Tanggal demo |  |
| Nilai sesi ini |  |
| Catatan penguji |  |
| Paraf penguji |  |

---

_Modul ini disusun 10 September 2026. Versi teknologi yang dirujuk (Java 25 LTS, PHP 8.4/8.5, Laravel 13, Spring Boot 4.1) diverifikasi pada tanggal tersebut dan perlu diperiksa ulang sebelum semester berjalan._
