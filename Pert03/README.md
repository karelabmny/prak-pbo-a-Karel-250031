# Pertemuan 03 — Constructor Berdelegasi, Anggota Statis, dan Konstanta

| | |
|---|---|
| **Nama** | Karel Abimanyu Ahpandi |
| **NIM** | 4525210031 |
| **Kelas** | A |
| **Mata kuliah** | Praktikum Pemrograman Berorientasi Objek (PBO) |
| **Bahasa** | Java dan PHP |

## Tentang pertemuan ini

Ada tiga materi di pertemuan ini:

- **Constructor berdelegasi.** Di Java, constructor yang ringkas memanggil constructor lengkap dengan `this(...)`, jadi validasi cukup ditulis di satu tempat. PHP tidak punya constructor overloading, sebagai gantinya dipakai *default parameter* dan *named constructor* (static factory).
- **Anggota statis.** Field `static` dimiliki kelas, bukan objek. Dipakai untuk penghitung jumlah rekening. Method `static` dipakai kalau method itu tidak membaca keadaan objek mana pun, misalnya penghitung bunga.
- **Konstanta.** Angka ajaib (bunga, biaya admin, batas penarikan) diganti dengan konstanta bernama.

## Studi kasus

Kelas `RekeningBank` dengan nomor rekening, pemilik, dan saldo. Aturan (invariant) yang dijaga:

1. Saldo tidak pernah negatif.
2. Nomor rekening tidak berubah setelah objek dibuat.
3. Setoran dan penarikan selalu bernilai positif.

Fitur lainnya: penarikan dibatasi Rp5.000.000 sekali transaksi, ada potongan biaya admin Rp5.000 (saldo tidak boleh jadi negatif), bunga tahunan 2,5%, dan penghitung jumlah rekening yang pernah dibuat.

## Struktur folder

```
Pert03/
├── before/            # kode awal dari dosen (masih ada TODO)
│   ├── Java/          # RekeningBank.java, Main.java
│   └── PHP/           # RekeningBank.php, main.php
├── after/             # kode setelah TODO dilengkapi
│   ├── java/          # RekeningBank.java, Main.java
│   └── php/           # RekeningBank.php, main.php
├── img/
│   ├── before/        # screenshot kode sebelum
│   └── after/         # screenshot kode sesudah
└── README.md
```

## Yang dikerjakan (before → after)

### Java — `RekeningBank.java`

| Bagian | Before | After |
|---|---|---|
| Konstanta | Masih angka ajaib (TODO 1) | `BUNGA_TAHUNAN` (0.025), `BIAYA_ADMINISTRASI` (5000), `BATAS_PENARIKAN_SEKALI` (5000000) |
| Penghitung | Belum ada | `private static int jumlahRekening = 0` |
| Constructor ringkas | Menyalin isi sendiri (saldo = 0) | Didelegasikan: `this(nomor, pemilik, 0)` |
| Constructor lengkap | Belum ada validasi | Menolak nomor kosong dan saldo awal negatif, lalu menaikkan `jumlahRekening` di sini saja |
| `setor()` | Kosong | Menolak jumlah <= 0, lalu menambah saldo |
| `tarik()` | Kosong | Menolak jumlah <= 0, jumlah melebihi saldo, dan jumlah melebihi batas sekali tarik |
| `potongBiayaAdmin()` | Kosong | `saldo = Math.max(0, saldo - BIAYA_ADMINISTRASI)` |
| `getJumlahRekening()` | `return -1` | `return jumlahRekening` |
| `bungaSetahun()` | `return 0` | `pokok * BUNGA_TAHUNAN` |

`Main.java` tidak diubah.

### PHP — `RekeningBank.php`

| Bagian | Before | After |
|---|---|---|
| Konstanta | Belum ada | `BUNGA_TAHUNAN`, `BUNGA_ADMIN`, `BATAS_PENARIKAN` |
| Penghitung | Belum ada | `private static int $jumlahRekening = 0` |
| Constructor | Hanya mengisi saldo | Menolak nomor kosong dan saldo awal negatif, lalu menaikkan penghitung |
| `rekeningPelajar()` | Melempar `RuntimeException` (belum dikerjakan) | `return new static($nomor, $pemilik)` |
| `getJumlahRekening()` | `return -1` | `return self::$jumlahRekening` |
| `bungaSetahun()` | `return 0` | `$pokok * self::BUNGA_TAHUNAN` |
| `setor()`, `tarik()`, `potongBiayaAdmin()` | Kosong | Sudah terisi, tetapi logikanya belum sesuai versi Java (lihat bagian **Catatan**) |

`main.php` tidak diubah.

## Cuplikan kode penting

**Java: constructor berdelegasi dan penghitung statis**

```java
private static int jumlahRekening = 0;

public RekeningBank(String nomor, String pemilik) {
    this(nomor, pemilik, 0);          // delegasi, validasi tidak disalin
}

public RekeningBank(String nomor, String pemilik, double saldoAwal) {
    if (nomor == null || nomor.isEmpty()) {
        throw new IllegalArgumentException("Nomor Rekening Gak Boleh Kosong");
    }
    if (saldoAwal < 0) {
        throw new IllegalArgumentException("Saldo Awal Gak Boleh Negatif");
    }
    this.nomor = nomor;
    this.pemilik = pemilik;
    this.saldo = saldoAwal;

    jumlahRekening++;                 // hanya di constructor lengkap
}
```

Penghitung dinaikkan hanya di constructor lengkap. Kalau dinaikkan di kedua constructor, rekening yang dibuat lewat constructor ringkas terhitung dua kali (4, bukan 3).

**PHP: default parameter dan named constructor**

```php
public function __construct(
    private readonly string $nomor,
    private readonly string $pemilik,
    float $saldoAwal = 0,             // pengganti constructor overloading
) { /* validasi + penghitung */ }

public static function rekeningPelajar(string $nomor, string $pemilik): static
{
    return new static($nomor, $pemilik);   // new static, bukan new self
}
```

`new static` dipakai supaya kalau ada kelas turunan, objek yang dibuat adalah objek turunannya (late static binding), bukan selalu `RekeningBank`.

## Cara menjalankan

Butuh JDK 17 ke atas dan PHP 8.1 ke atas.

**Java**

```bash
cd Pert03/after/java
javac -d out *.java
java -cp out Main
```

**PHP**

```bash
cd Pert03/after/php
php main.php
```

## Output program

**Java**

```
Jumlah rekening di awal: 0
Rekening[111] Ani            Rp1,000,000.00
Rekening[222] Budi           Rp0.00
Rekening[333] Citra          Rp250,000.00
Jumlah rekening sekarang: 3   (seharusnya 3, bukan 4)

=== Operasi ===
Setelah setor 500.000  -> Rekening[111] Ani            Rp1,500,000.00
  Ditolak: Saldo tidak mencukupi
Budi setelah potong admin: Rekening[222] Budi           Rp0.00   (saldo tidak boleh negatif)
Bunga setahun dari saldo Ani: Rp37,500.00
```

Format angka di Java (`%,.2f`) mengikuti pengaturan bahasa komputer, jadi pemisah ribuan bisa berupa koma atau titik.

**PHP**

```
Jumlah rekening di awal: 0
Rekening[111] Ani            Rp1.000.000,00
Rekening[222] Budi           Rp0,00
Rekening[333] Citra          Rp250.000,00
Jumlah rekening sekarang: 3   (seharusnya 3)

=== Operasi ===
Setelah setor 500.000  -> Rekening[111] Ani            Rp500.000,00
  Ditolak: Jumlah tidak boleh melebihi saldo
Budi setelah potong admin: Rekening[222] Budi           Rp0,00   (saldo tidak boleh negatif)
Bunga setahun dari saldo Ani: Rp12.500,00
```

## Screenshot

### Before

**`RekeningBank.java`**

![RekeningBank.java before](img/before/rekbankjava.png)

**`Main.java`**

![Main.java before](img/before/mainjava.png)

**`RekeningBank.php`**

![RekeningBank.php before](img/before/rekbankphp.png)

**`main.php`**

![main.php before](img/before/mainphp.png)

### After

**`RekeningBank.java`**

![RekeningBank.java after](img/after/rekbankjava.png)

**`Main.java`**

![Main.java after](img/after/mainjava.png)

**`RekeningBank.php`**

![RekeningBank.php after](img/after/rekbankphp.png)

**`main.php`**

![main.php after](img/after/mainphp.png)

## Catatan

Hal-hal yang perlu diperhatikan pada hasil akhir:

- **Versi Java sudah sesuai** dengan yang diminta di `Main.java`: jumlah rekening 3, penarikan Rp9.999.999 ditolak, saldo Budi tidak negatif. Di tes ini penarikan ditolak karena saldo tidak cukup (pengecekan saldo dilakukan sebelum pengecekan batas sekali tarik), jadi batas Rp5.000.000 sendiri tidak teruji oleh `Main.java`.
- **Versi PHP belum setara dengan Java.** Pada `RekeningBank.php`:
  - `setor()` mengurangi saldo dan `tarik()` menambah saldo (tertukar). Akibatnya saldo Ani setelah "setor 500.000" menjadi Rp500.000, bukan Rp1.500.000, dan bunga yang dihitung ikut salah (Rp12.500, seharusnya Rp37.500).
  - `potongBiayaAdmin()` masih kosong.
  - Konstanta `BUNGA_ADMIN` dan `BATAS_PENARIKAN` sudah dideklarasikan tetapi belum dipakai. Batas sekali tarik belum dicek.
  - Pesan error saldo awal tertulis "harus negatif", seharusnya "tidak boleh negatif".
- Penamaan konstanta di PHP (`BUNGA_ADMIN`) tidak sama dengan di Java (`BIAYA_ADMINISTRASI`). Tidak memengaruhi hasil, hanya kurang konsisten.

## Kesimpulan

Constructor berdelegasi membuat validasi cukup ditulis di satu tempat, dan penghitung statis hanya benar kalau dinaikkan di constructor lengkap saja. Konstanta bernama menggantikan angka ajaib sehingga nilai bunga, biaya, dan batas bisa diubah di satu baris. Di PHP, padanan constructor overloading adalah default parameter dan named constructor dengan `new static`.
