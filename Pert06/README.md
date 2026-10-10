# Pertemuan 06 — Abstract Class, Interface, Enum, dan Trait

| | |
|---|---|
| **Nama** | Karel Abimanyu Ahpandi |
| **NIM** | 4525210031 |
| **Kelas** | A |
| **Mata kuliah** | Praktikum Pemrograman Berorientasi Objek (PBO) |
| **Bahasa** | Java dan PHP |

## Tentang pertemuan ini

Materi pertemuan ini adalah **abstraksi**, dengan empat alat utama:

- **Abstract class** menampung kode yang benar-benar sama di semua turunan dan menjawab "benda ini *apa*". Di sini `Kendaraan` menyimpan merek dan tahun.
- **Interface** menjawab "benda ini *bisa apa*". `Movable` (bisa bergerak) dan `Fuelable` (bisa diisi bahan bakar) dipisah karena tidak semua yang bergerak butuh bahan bakar. Itulah **Interface Segregation Principle**. Di Java satu kelas hanya boleh `extends` satu kelas tetapi boleh `implements` banyak interface.
- **Enum** menggantikan konstanta angka (misalnya `BENSIN = 1`) dengan tipe yang aman dan boleh punya perilaku, seperti `biayaPengisian()` dan `ramahLingkungan()`.
- **Trait** (khusus PHP) untuk penggunaan ulang kode secara horizontal, yaitu bisa dipakai kelas yang tidak sekerabat.

## Studi kasus

Hierarki kendaraan:

```
Kendaraan (abstract)            Movable (interface)      Fuelable (interface)
├── Mobil  ──────────────────── implements ───────────── implements
└── Sepeda ──────────────────── implements               (tidak implements Fuelable)

TipeBahanBakar (enum): BENSIN, SOLAR, LISTRIK
Loggable (trait, PHP): dipakai Mobil dan Pesanan
```

`Sepeda` sengaja tidak mengimplementasikan `Fuelable`. Jadi fungsi `isiPenuh(Fuelable)` tidak bisa menerima `Sepeda`, dan penolakannya terjadi saat kompilasi (Java) atau sebagai `TypeError` (PHP), bukan baru ketahuan saat program berjalan.

## Struktur folder

```
Pert06/
├── before/            # kode awal dari dosen (masih ada TODO)
│   ├── java/          # Movable, Fuelable, TipeBahanBakar, Kendaraan, Mobil, Main
│   └── php/           # abstraksi.php (semua struktur), main.php
├── after/             # kode setelah dikerjakan
│   ├── java/          # + Sepeda.java
│   └── php/           # abstraksi.php, main.php
├── img/
│   ├── before/        # screenshot kode sebelum
│   └── after/         # screenshot kode sesudah
└── README.md
```

## Yang dikerjakan (before → after)

### Java

| Berkas | Before | After |
|---|---|---|
| `Movable.java` | `ringkasanGerak()` masih berisi teks "TODO belum dikerjakan" | Default method yang memakai `kecepatanMaksimum()` untuk membuat ringkasan |
| `Fuelable.java` | Interface kontrak pengisian bahan bakar | Tidak diubah |
| `TipeBahanBakar.java` | Hanya `BENSIN` dan `SOLAR` dengan harga 0, `LISTRIK` belum ada | Ditambah `LISTRIK`, semua punya harga. `biayaPengisian()` = jumlah x harga, `ramahLingkungan()` hanya `true` untuk `LISTRIK` (`this == LISTRIK`) |
| `Kendaraan.java` | `umur()` masih `return 0` | `Math.max(0, tahunSekarang - tahun)` supaya tidak negatif |
| `Mobil.java` | `extends Kendaraan implements Movable, Fuelable` dengan banyak TODO | `bergerak()` mencetak pesan, `kecepatanMaksimum()` = 180, `isiBahanBakar()` menolak jumlah <= 0 dan pengisian yang melebihi kapasitas |
| `Sepeda.java` | Belum ada | Baru. `extends Kendaraan implements Movable` (tanpa `Fuelable`), 2 roda, kecepatan maksimum 30 |
| `Main.java` | `Movable` yang diproses hanya `mobil` | `sepeda` ditambahkan ke daftar `Movable`. Baris `isiPenuh(sepeda)` tetap dikomentari karena memang tidak boleh dikompilasi |

### PHP

Seluruh struktur ada di `abstraksi.php`.

| Bagian | Before | After |
|---|---|---|
| `enum TipeBahanBakar` | Hanya `Bensin` dan `Solar` | Ditambah `Listrik`. Dilengkapi `label()`, `hargaPerSatuan()`, `biayaPengisian()` (dengan `match`), dan `ramahLingkungan()` |
| `trait Loggable` | Badan method `log()` masih TODO | Mencetak `[jam] NamaKelas: pesan` memakai `date('H:i:s')` dan `static::class` |
| `Kendaraan::umur()` | `return 0` | Sudah diisi, tetapi ada kesalahan penulisan (lihat Catatan) |
| `Mobil` | Sudah `implements Movable, Fuelable` dan memakai trait `Loggable`, tetapi `bergerak()`, `kecepatanMaksimum()`, dan `isiBahanBakar()` masih kosong | Ketiganya diisi: pesan bergerak, kecepatan maksimum 180, dan `isiBahanBakar()` yang memvalidasi jumlah lalu menambah `isiTangki` |
| `Sepeda` | Belum ada | Baru. Implements `Movable` saja |
| `Pesanan` | Belum ada | Baru. Kelas yang tidak sekerabat dengan `Kendaraan` tetapi memakai `Loggable` |
| `main.php` | Hanya `$mobil` | Ditambah `$sepeda` dan pemanggilan `(new Pesanan())->log(...)` |

## Cuplikan kode penting

**Java: satu kelas, satu `extends`, banyak `implements`**

```java
public class Mobil extends Kendaraan implements Movable, Fuelable { ... }

public class Sepeda extends Kendaraan implements Movable { ... }   // bukan Fuelable
```

**Java: parameter bertipe interface, bukan kelas konkret (`Main.java`)**

```java
static void isiPenuh(Fuelable kendaraan) {
    kendaraan.isiBahanBakar(kendaraan.kapasitasTangki());
    // ...
}
// isiPenuh(mobil);    -> OK
// isiPenuh(sepeda);   -> ditolak kompilator, Sepeda bukan Fuelable
```

**Java: enum dengan perilaku**

```java
public enum TipeBahanBakar {
    BENSIN("Bensin", 1200),
    SOLAR("Solar", 10500),
    LISTRIK("Listrik", 2500);

    public double biayaPengisian(double jumlah) { return jumlah * hargaPerSatuan; }
    public boolean ramahLingkungan()            { return this == LISTRIK; }
}
```

**PHP: enum dengan `match` dan trait yang dipakai dua kelas tak sekerabat**

```php
enum TipeBahanBakar: string
{
    case Bensin = 'bensin';
    case Solar = 'solar';
    case Listrik = 'listrik';

    public function hargaPerSatuan(): float
    {
        return match ($this) {
            self::Bensin => 12000,
            self::Solar => 10500,
            self::Listrik => 2500,
        };
    }
}

trait Loggable
{
    public function log(string $pesan): void
    {
        printf("[%s] %s: %s \n", date('H:i:s'), static::class, $pesan);
    }
}

final class Mobil extends Kendaraan implements Movable, Fuelable { use Loggable; /* ... */ }
final class Pesanan { use Loggable; }    // tidak ada hubungan dengan Kendaraan
```

## Cara menjalankan

Butuh JDK 17 ke atas dan PHP 8.1 ke atas (enum dan `readonly` di PHP butuh 8.1).

**Java**

```bash
cd Pert06/after/java
javac -d out *.java
java -cp out Main
```

**PHP**

```bash
cd Pert06/after/php
php main.php
```

## Output program

**Java**

```
=== Semua Movable ===
Toyota Avanzamelaju di jalan raya
    Kecepatan Maksimum180.0km/jam
Polygondikayuh santai di jalan
    Kecepatan Maksimum30.0km/jam

=== Hanya yang Fuelable ===
  Diisi penuh Bensin — biaya Rp54,000

=== Enum punya perilaku ===
  Bensin   ramah lingkungan? false  biaya 10 satuan: Rp12,000
  Solar    ramah lingkungan? false  biaya 10 satuan: Rp105,000
  Listrik  ramah lingkungan? true   biaya 10 satuan: Rp25,000
```

Format angka di Java (`%,.0f`) mengikuti pengaturan bahasa komputer, jadi pemisah ribuan bisa berupa koma atau titik.

**PHP**

```
=== Semua Movable ===
{this->merek} melaju di jalan raya
    kecepatan maksimum 180 km/jam
Polygon mengayuh di jalan
    kecepatan maksimum 40 km/jam

=== Hanya yang Fuelable ===
  Diisi penuh Bensin — biaya Rp540.000

=== Enum punya perilaku ===
  Bensin   ramah lingkungan? tidak  biaya 10 satuan: Rp120.000
  Solar    ramah lingkungan? tidak  biaya 10 satuan: Rp105.000
  Listrik  ramah lingkungan? ya     biaya 10 satuan: Rp25.000

=== Trait dipakai kelas yang tidak sekerabat ===
[11:49:16] Mobil: servis berkala selesai 
[11:49:16] Pesanan: pesanan #1042 dibuat 
```

Jam pada baris log akan berbeda setiap kali program dijalankan.

## Screenshot

### Before

**`Movable.java`**

![Movable.java before](img/before/movable.png)

**`TipeBahanBakar.java`**

![TipeBahanBakar.java before](img/before/tipebahanbakarjava.png)

**`Kendaraan.java`**

![Kendaraan.java before](img/before/kendaraanjava.png)

**`Mobil.java`**

![Mobil.java before](img/before/mobiljava.png)

**`Main.java`**

![Main.java before](img/before/mainjava.png)

**`abstraksi.php` (bagian 1)**

![abstraksi.php before bagian 1](img/before/abstraksiphp-1.png)

**`abstraksi.php` (bagian 2)**

![abstraksi.php before bagian 2](img/before/abstraksiphp-2.png)

### After

**`Movable.java`**

![Movable.java after](img/after/movablejava.png)

**`TipeBahanBakar.java`**

![TipeBahanBakar.java after](img/after/tipebahanbakarjava.png)

**`Kendaraan.java`**

![Kendaraan.java after](img/after/kendaraanjava.png)

**`Mobil.java`**

![Mobil.java after](img/after/mobiljava.png)

**`Sepeda.java`**

![Sepeda.java after](img/after/sepedajava.png)

**`Main.java`**

![Main.java after](img/after/mainjava.png)

**`abstraksi.php` (bagian 1)**

![abstraksi.php after bagian 1](img/after/abstraksiphp-1.png)

**`abstraksi.php` (bagian 2)**

![abstraksi.php after bagian 2](img/after/abstraksiphp-2.png)

**`main.php`**

![main.php after](img/after/mainphp.png)

## Catatan

Hal-hal yang perlu diperhatikan pada hasil akhir:

**Java**

- Harga `BENSIN` tertulis `1200`, sedangkan petunjuk di kerangka menyebut `12000`. Akibatnya biaya Bensin di Java (Rp12.000 untuk 10 satuan) berbeda dengan PHP (Rp120.000).
- `Mobil.isiBahanBakar()` memvalidasi jumlah tetapi belum menambah `isiTangki`, jadi `getIsiTangki()` tetap 0 setelah pengisian.
- Teks keluaran kurang spasi ("Toyota Avanzamelaju", "Kecepatan Maksimum180.0km/jam") karena string digabung tanpa spasi.

**PHP**

- `Mobil::bergerak()` mencetak teks `{this->merek}` apa adanya. Seharusnya `{$this->merek}` supaya yang tampil nama merek.
- `Kendaraan::umur()` berisi `max(0, $tahunSekarang, - $this->tah)`, yaitu properti `tah` tidak ada dan pengurangannya tidak tertulis benar. Method ini tidak dipanggil di `main.php`, tetapi akan error kalau dipanggil. Seharusnya `max(0, $tahunSekarang - $this->tahun)`.
- Di `Mobil::isiBahanBakar()`, nama kelas exception tertulis `InvalidArgumentExeception` (typo, kurang huruf `c`). Kalau jalur error itu tercapai, PHP akan melapor bahwa kelasnya tidak ditemukan.
- Interface `Movable` di PHP tidak punya padanan default method seperti di Java, jadi ringkasan gerak dicetak langsung di `main.php`.

## Kesimpulan

Pembagian peran jadi lebih jelas: abstract class (`Kendaraan`) untuk kode yang sama, interface (`Movable`, `Fuelable`) untuk kemampuan, enum (`TipeBahanBakar`) untuk pilihan tetap yang punya perilaku, dan trait (`Loggable`) untuk berbagi kode antar kelas yang tidak sekerabat. Karena `Movable` dan `Fuelable` dipisah, `Sepeda` tidak dipaksa punya bahan bakar, dan kesalahan memberikannya ke `isiPenuh()` langsung ketahuan oleh kompilator atau `TypeError`, bukan saat program sudah berjalan.
