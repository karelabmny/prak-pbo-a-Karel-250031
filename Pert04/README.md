# Pertemuan 04 — Pewarisan (Inheritance)

| | |
|---|---|
| **Nama** | Karel Abimanyu Ahpandi |
| **NIM** | 4525210031 |
| **Kelas** | A |
| **Mata kuliah** | Praktikum Pemrograman Berorientasi Objek (PBO) |
| **Bahasa** | Java dan PHP |

## Tentang pertemuan ini

Materi pertemuan ini adalah **pewarisan**: kelas induk menampung hal yang benar-benar sama, dan kelas turunan menambah atau menyesuaikan. Poin-poin yang dipakai:

- `extends` untuk membuat kelas turunan.
- `protected` supaya atribut bisa dibaca turunan tetapi tidak oleh dunia luar.
- `super(...)` (Java) / `parent::__construct(...)` (PHP) untuk memanggil constructor induk. Panggilan ini wajib ada dan harus dijalankan lebih dulu.
- `@Override` dan `super.hitungGaji()` / `parent::hitungGaji()`: turunan **menambah** perilaku induk, bukan menyalin rumusnya.
- Kelas `abstract` sebagai induk yang tidak boleh di-`new` langsung. Pembahasan lengkapnya ada di pertemuan 6.

## Studi kasus

Sistem penggajian dengan hierarki pegawai:

```
Pegawai (abstract)
├── PegawaiTetap          gaji = gaji pokok + tunjangan masa kerja (2% per tahun, maksimum 40%)
│   └── Dosen             (PHP) ditambah tunjangan fungsional 10%
├── PegawaiKontrak        gaji = gaji pokok (tanpa tunjangan masa kerja)
└── PegawaiHarian         (PHP) gaji = gaji per hari x hari kerja
```

## Struktur folder

```
Pert04/
├── before/            # kode awal dari dosen (masih ada TODO)
│   ├── java/          # Pegawai, PegawaiTetap, PegawaiKontrak, Main
│   └── php/           # Pegawai.php (semua kelas), main.php
├── after/             # kode setelah dikerjakan
│   ├── java/          # Pegawai, PegawaiTetap, PegawaiKontrak, Main
│   └── php/           # Pegawai.php (semua kelas), main.php
├── img/
│   ├── before/        # screenshot kode sebelum
│   └── after/         # screenshot kode sesudah
└── README.md
```

Di PHP, seluruh hierarki ditaruh dalam satu berkas (`Pegawai.php`) supaya mudah dibandingkan dengan versi Java.

## Yang dikerjakan (before → after)

### PHP — `Pegawai.php` dan `main.php`

| Bagian | Before | After |
|---|---|---|
| `Pegawai` constructor | Validasi gaji belum ada | Menolak gaji pokok negatif dengan `InvalidArgumentException` |
| `Pegawai::hitungGaji()` | `return 0` | `return $this->gajiPokok` |
| `PegawaiTetap::hitungGaji()` | `return 0` | Gaji dasar dari `parent::hitungGaji()` dikali `(1 + tunjangan)`, tunjangan dibatasi `min(..., 0.40)` |
| `PegawaiKontrak` | Sudah ada | Tidak meng-override `hitungGaji()`, karena aturan gaji dasar dari induk sudah cukup |
| `Dosen` | Belum ada | Turunan `PegawaiTetap`, menambah tunjangan fungsional 10% di atas hasil `parent::hitungGaji()` |
| `PegawaiHarian` | Belum ada | Turunan `Pegawai`, gaji = gaji per hari x hari kerja |
| `main.php` | Hanya 2 pegawai | Ditambah `Dosen` dan `PegawaiHarian` ke daftar |

### Java — `Pegawai`, `PegawaiTetap`, `PegawaiKontrak`, `Main`

Isi folder `after/java` **sama dengan `before/java`**, yaitu masih berupa kerangka dari dosen. `Pegawai.hitungGaji()` dan `PegawaiTetap.hitungGaji()` masih `return 0` dan komentar TODO belum dihapus. Detailnya ada di bagian **Catatan**.

## Cuplikan kode penting

**PHP: turunan menambah perilaku induk lewat `parent::`**

```php
class PegawaiTetap extends Pegawai
{
    protected const TUNJANGAN_PER_TAHUN = 0.02;
    protected const TUNJANGAN_MAKSIMUM  = 0.40;

    public function hitungGaji(): float
    {
        $tunjangan = min(
            $this->masaKerjaTahun * self::TUNJANGAN_PER_TAHUN,
            self::TUNJANGAN_MAKSIMUM
        );
        return parent::hitungGaji() * (1 + $tunjangan);   // tidak menyalin rumus induk
    }
}

class Dosen extends PegawaiTetap
{
    protected const TUNJANGAN_FUNGSIONAL = 0.10;

    public function hitungGaji(): float
    {
        return parent::hitungGaji() * (1 + self::TUNJANGAN_FUNGSIONAL);
    }
}
```

Perhitungan contoh: Ani (pokok 6.000.000, masa kerja 15 tahun) mendapat tunjangan 15 x 2% = 30%, jadi 6.000.000 x 1,3 = **7.800.000**. Dosen Citra (pokok 7.000.000, 10 tahun) mendapat 20% dulu (8.400.000), lalu 10% fungsional menjadi **9.240.000**.

## Cara menjalankan

Butuh JDK 17 ke atas dan PHP 8.1 ke atas.

**PHP**

```bash
cd Pert04/after/php
php main.php
```

**Java**

```bash
cd Pert04/after/java
javac -d out *.java
java -cp out Main
```

## Output program

**PHP**

```
=== Daftar Gaji ===
  198701012010   TETAP     Ani Lestari          Rp7.800.000,00
  K-2024-007     KONTRAK   Budi Santoso         Rp5.000.000,00
  198501012009   DOSEN     Citra Dewi           Rp9.240.000,00
  H-24-001       HARIAN    Dedi Kurniawan       Rp4.400.000,00

  Total beban gaji: Rp26.440.000,00

Periksa: Ani (pokok 6.000.000, masa kerja 15 tahun)
  tunjangan 15 x 2% = 30%, jadi gaji seharusnya Rp7.800.000,00
```

**Java** (belum dilengkapi, lihat Catatan)

```
=== Daftar Gaji ===
  198701012010   TETAP     Ani Lestari          Rp0.00
  K-2024-007     KONTRAK   Budi Santoso         Rp0.00

  Total beban gaji: Rp0.00

Periksa: Ani (pokok 6.000.000, masa kerja 15 tahun)
  tunjangan 15 x 2% = 30%, jadi gaji seharusnya Rp7.800.000,00
```

## Screenshot

### Before

**`Pegawai.java`**

![Pegawai.java before](img/before/pegawaijava.png)

**`PegawaiTetap.java`**

![PegawaiTetap.java before](img/before/pgwttpjava.png)

**`PegawaiKontrak.java`**

![PegawaiKontrak.java before](img/before/pgwkontrakjava.png)

**`Main.java`**

![Main.java before](img/before/mainjava.png)

**`Pegawai.php`**

![Pegawai.php before](img/before/pegawaiphp.png)

**`main.php`**

![main.php before](img/before/mainphp.png)

### After

**`Pegawai.java`**

![Pegawai.java after](img/after/pgwjava.png)

**`PegawaiTetap.java`**

![PegawaiTetap.java after](img/after/pgwttpjava.png)

**`PegawaiKontrak.java`**

![PegawaiKontrak.java after](img/after/pgwkntrkjava.png)

**`Main.java`**

![Main.java after](img/after/mainjava.png)

**`Pegawai.php` (bagian 1)**

![Pegawai.php after bagian 1](img/after/pegawaiphp.png)

**`Pegawai.php` (bagian 2)**

![Pegawai.php after bagian 2](img/after/pegawaiphp-1.png)

**`main.php`**

![main.php after](img/after/mainphp.png)

## Catatan

- **Versi Java belum dikerjakan.** File di `after/java` identik dengan `before/java`. `Pegawai.hitungGaji()` dan `PegawaiTetap.hitungGaji()` masih `return 0`, validasi gaji negatif belum ada, dan kelas `Dosen` serta `PegawaiHarian` belum dibuat. Karena itu output Java menampilkan Rp0,00 untuk semua pegawai. Rumus yang benar sudah ada di versi PHP.

## Kesimpulan

Dengan pewarisan, bagian yang sama (nip, nama, gaji pokok, cara mencetak) cukup ditulis sekali di `Pegawai`, dan tiap jenis pegawai hanya menulis bagian yang berbeda. Memanggil `parent::hitungGaji()` membuat perubahan rumus di induk otomatis ikut ke semua turunan, sedangkan menyalin rumus ke turunan membuat kodenya mudah tidak sinkron. Bagian Java masih perlu dilengkapi dengan pola yang sama seperti versi PHP.
