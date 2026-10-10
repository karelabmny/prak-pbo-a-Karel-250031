# Pertemuan 05 — Polimorfisme

| | |
|---|---|
| **Nama** | Karel Abimanyu Ahpandi |
| **NIM** | 4525210031 |
| **Kelas** | A |
| **Mata kuliah** | Praktikum Pemrograman Berorientasi Objek (PBO) |
| **Bahasa** | Java dan PHP |

## Struktur folder

```
Pert05/
├── before/            # kode awal dari dosen (masih ada TODO)
│   ├── Java/          # BangunDatar, Lingkaran, Persegi, AntiPattern, Main
│   └── PHP/           # BangunDatar.php, main.php, notifikasi.php
├── after/             # kode setelah dikerjakan
│   ├── java/          # + Segitiga, Trapesium, AntiPatternRefaktor
│   └── php/           # BangunDatar.php, main.php, notifikasi.php
├── img/
│   ├── before/        # screenshot kode sebelum
│   └── after/         # screenshot kode sesudah
└── README.md
```

## Yang dikerjakan (before → after)

### Java

| Berkas | Before | After |
|---|---|---|
| `Lingkaran.java` | `luas()` dan `keliling()` masih `return 0`, belum ada validasi | Menolak jari-jari <= 0. Luas `Math.PI * r * r`, keliling `2 * Math.PI * r` |
| `Persegi.java` | `luas()` dan `keliling()` masih `return 0` | Menolak sisi <= 0. Luas `sisi * sisi`, keliling `4 * sisi` |
| `Segitiga.java` | Belum ada | Baru. Menerima alas, tinggi, dan sisi miring. Luas `0.5 * alas * tinggi`, keliling = jumlah tiga sisi |
| `Trapesium.java` | Belum ada | Baru. Menerima sisi atas, sisi bawah, tinggi, sisi kiri, sisi kanan. Luas `0.5 * (atas + bawah) * tinggi` |
| `Main.java` | Array berisi `Lingkaran` dan `Persegi` | Ditambah `Segitiga` dan `Trapesium`. Logika perulangan tidak disentuh |
| `AntiPattern.java` | Versi tanpa polimorfisme (bahan Langkah 5) | Tidak diubah, dipertahankan sebagai pembanding |
| `AntiPatternRefaktor.java` | Belum ada | Baru. Versi polimorfik dengan `interface Bangun` dan `record` |
| `BangunDatar.java` | Kelas induk abstrak | Tidak diubah |

### PHP

| Berkas | Before | After |
|---|---|---|
| `BangunDatar.php` | `Lingkaran` dan `Persegi` dengan TODO | `Lingkaran`, `Persegi` dilengkapi (validasi, `M_PI`). Ditambah `Segitiga` (rumus Heron + pengecekan syarat segitiga) dan `Trapesium` |
| `main.php` | Array berisi `Lingkaran` dan `Persegi` | Ditambah `Segitiga` dan `Trapesium` |
| `notifikasi.php` | Kerangka dengan TODO | Kelas abstrak `Notifikasi` (`kirim()`, `saluran()`), tiga turunan `Email`, `SMS`, `WhatsApp`, dan `kirimSemua()` yang cukup memanggil `$notifikasi->kirim($pesan)` |

## Screenshot

### Before

**`Lingkaran.java`**

![Lingkaran.java before](img/before/lingkaranjava.png)

**`Persegi.java`**

![Persegi.java before](img/before/persegijava.png)

**`Main.java`**

![Main.java before](img/before/mainjava.png)

**`BangunDatar.php`**

![BangunDatar.php before](img/before/bangundatarphp.png)

**`main.php`**

![main.php before](img/before/mainphp.png)

**`notifikasi.php`**

![notifikasi.php before](img/before/notifikasiphp.png)

### After

**`Lingkaran.java`**

![Lingkaran.java after](img/after/lingkaranjava.png)

**`Segitiga.java`**

![Segitiga.java after](img/after/segitigajava.png)

**`Trapesium.java`**

![Trapesium.java after](img/after/trapesiumjava.png)

**`Main.java`**

![Main.java after](img/after/mainjava.png)

**`BangunDatar.php` (bagian 1)**

![BangunDatar.php after bagian 1](img/after/bangundatarphp-1.png)

**`BangunDatar.php` (bagian 2)**

![BangunDatar.php after bagian 2](img/after/bangundatarphp-2.png)

**`main.php`**

![main.php after](img/after/mainphp.png)

**`notifikasi.php`**

![notifikasi.php after](img/after/notifikasiphp.png)


## Kesimpulan

Dengan polimorfisme, perulangan di `Main` tidak perlu tahu bentuk apa yang sedang dihitung, dan menambah bangun baru (`Segitiga`, `Trapesium`) cukup dengan membuat kelas turunan baru dan menambahkannya ke array tanpa mengubah logika perulangan. Perbandingan `AntiPattern` dengan `AntiPatternRefaktor` menunjukkan keuntungannya: pengetahuan cara menghitung luas ada di tiap kelas, bukan terkumpul di satu method `if / else if` yang harus disunting setiap ada tipe baru.
