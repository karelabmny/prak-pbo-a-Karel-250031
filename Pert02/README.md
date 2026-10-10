# Pertemuan 02 — Enkapsulasi dan Invariant

| | |
|---|---|
| **Nama** | Karel Abimanyu Ahpandi |
| **NIM** | 4525210031 |
| **Kelas** | A |
| **Mata kuliah** | Praktikum Pemrograman Berorientasi Objek (PBO) |
| **Bahasa** | Java dan PHP |

## Tentang pertemuan ini

Materi pertemuan ini adalah **enkapsulasi yang menjaga invariant**. Invariant adalah aturan yang harus selalu benar selama sebuah objek hidup. Supaya aturan itu tidak bisa dilanggar dari luar, ada tiga hal yang dipakai:

- atribut dibuat `private`, jadi tidak bisa diubah sembarangan dari luar kelas,
- atribut yang tidak boleh berubah dibuat `final` (Java) atau `readonly` (PHP),
- data divalidasi di constructor, jadi objek yang melanggar aturan tidak pernah terbentuk.

## Studi kasus

Sistem akademik mencatat mahasiswa dengan **NIM**, **nama**, dan tiga komponen nilai (**tugas**, **UTS**, **UAS**). Aturannya:

1. NIM tidak boleh kosong dan tidak berubah setelah mahasiswa terdaftar (jadi tidak ada `setNim()`).
2. Setiap komponen nilai harus berada di rentang 0 sampai 100.
3. Nilai akhir = 30% tugas + 30% UTS + 40% UAS. Bobot disimpan sebagai konstanta, bukan ditulis langsung di dalam method.
4. Huruf mutu: `>= 80` A, `>= 70` B, `>= 60` C, `>= 50` D, selain itu E.

## Struktur folder

```
Pert02/
├── before/            # kode awal dari dosen (masih ada TODO)
│   ├── Java/          # Mahasiswa.java, Main.java
│   └── PHP/           # Mahasiswa.php, main.php
├── after/             # kode setelah TODO dilengkapi
│   ├── Java/          # Mahasiswa.java, Main.java
│   └── PHP/           # Mahasiswa.php, main.php
├── img/
│   ├── before/        # screenshot kode sebelum
│   └── after/         # screenshot kode sesudah
└── README.md
```

## Yang dikerjakan (before → after)

### Java — `Mahasiswa.java`

| Bagian | Before | After |
|---|---|---|
| Constructor | Hanya mengisi atribut, tanpa validasi | Menolak NIM `null`/kosong (setelah `trim()`) dan memanggil `pastikanNilaiSah()` untuk tiap nilai |
| `pastikanNilaiSah()` | Belum ada | Method privat pembantu. Menolak nilai di luar 0–100 (dan `NaN`) dengan `IllegalArgumentException` yang pesannya menyebut komponen yang salah |
| `nilaiAkhir()` | `return 0` | Menghitung dengan konstanta `BOBOT_TUGAS`, `BOBOT_UTS`, `BOBOT_UAS` |
| `hurufMutu()` | `return "?"` | `if` bertingkat sesuai batas A–E |
| Getter | Hanya `getNim()` dan `getNama()` | Ditambah `getNilaiAkhir()`. `setNim()` sengaja tidak dibuat |

`Main.java` tidak diubah (isinya sama dengan versi awal).

### PHP — `Mahasiswa.php`

| Bagian | Before | After |
|---|---|---|
| Constructor | Parameter dengan `readonly` sudah disediakan (constructor property promotion), tetapi validasinya masih TODO | NIM kosong (setelah `trim()`) ditolak dengan `InvalidArgumentException`, dan tiap nilai dicek lewat `pastikanNilaiSah()` |
| `pastikanNilaiSah()` | Badan method kosong | Menolak nilai di luar 0–100 |
| `nilaiAkhir()` | `return 0` | Menghitung dengan konstanta bobot |
| `hurufMutu()` | `return '?'` | `match (true)` sesuai batas A–E |

`main.php` tidak diubah.

## Cuplikan kode penting

**Java: validasi di constructor dengan method pembantu**

```java
public Mahasiswa(String nim, String nama, double nilaiTugas, double nilaiUts, double nilaiUas) {
    if (nim == null || nim.trim().isEmpty()) {
        throw new IllegalArgumentException("NIM tidak boleh kosong atau null");
    }

    pastikanNilaiSah("nilai tugas", nilaiTugas);
    pastikanNilaiSah("nilai UTS", nilaiUts);
    pastikanNilaiSah("nilai UAS", nilaiUas);
    // ... baru mengisi atribut
}

private static void pastikanNilaiSah(String namaKomponen, double nilai) {
    if (Double.isNaN(nilai) || nilai < NILAI_MIN || nilai > NILAI_MAX) {
        throw new IllegalArgumentException(
                namaKomponen + " harus berada dalam rentang 0 sampai 100");
    }
}
```

**PHP: `readonly` dan `match`**

```php
public function __construct(
    private readonly string $nim,
    private readonly string $nama,
    private float $nilaiTugas,
    private float $nilaiUts,
    private float $nilaiUas
) { /* validasi sama seperti di Java */ }

public function hurufMutu(): string
{
    $na = $this->nilaiAkhir();

    return match (true) {
        $na >= 80 => 'A',
        $na >= 70 => 'B',
        $na >= 60 => 'C',
        $na >= 50 => 'D',
        default => 'E',
    };
}
```

## Cara menjalankan

Butuh JDK 17 ke atas dan PHP 8.1 ke atas.

**Java**

```bash
cd Pert02/after/Java
javac -d out *.java
java -cp out Main
```

**PHP**

```bash
cd Pert02/after/PHP
php main.php
```

## Output program

**Java**

```
=== Rekap Nilai ===
  2024001    Ani Lestari        akhir= 84.90  mutu=A
  2024002    Budi Santoso       akhir= 59.30  mutu=D
  2024003    Citra Wijaya       akhir= 92.00  mutu=A

=== Objek menolak data yang melanggar aturan ===
  Ditolak: nilai tugas harus berada dalam rentang 0 sampai 100
  Ditolak: NIM tidak boleh kosong atau null
```

**PHP**

```
=== Rekap Nilai ===
  2024001    Ani Lestari        akhir= 84.90  mutu=A
  2024002    Budi Santoso       akhir= 59.30  mutu=D
  2024003    Citra Wijaya       akhir= 92.00  mutu=A

=== Objek menolak data yang melanggar aturan ===
  Ditolak: Nilai Tugas harus berada di antara 0 dan 100.
  Ditolak: NIM tidak boleh kosong.
```

Dua kasus di bagian bawah sengaja dibuat salah (nilai 150 dan NIM kosong) untuk membuktikan objek menolak data yang melanggar aturan.

## Screenshot

### Before

**`Mahasiswa.java` (bagian 1)**

![Mahasiswa.java before bagian 1](img/before/mahasiswajava-1.png)

**`Mahasiswa.java` (bagian 2)**

![Mahasiswa.java before bagian 2](img/before/mahasiswajava-2.png)

**`Main.java`**

![Main.java before](img/before/mainjava.png)

**`Mahasiswa.php`**

![Mahasiswa.php before](img/before/mahasiswaphp.png)

**`main.php`**

![main.php before](img/before/mainphp.png)

### After

**`Mahasiswa.java`**

![Mahasiswa.java after](img/after/mahasiswajava.png)

**`Main.java`**

![Main.java after](img/after/mainjava.png)

**`Mahasiswa.php`**

![Mahasiswa.php after](img/after/mahasiswaphp.png)

**`main.php`**

![main.php after](img/after/mainphp.png)

## Catatan

- Pesan error di Java dan PHP sedikit berbeda ("rentang 0 sampai 100" vs "di antara 0 dan 100"). Maksudnya sama, hanya redaksinya.
- Versi PHP belum punya `getNilaiAkhir()` seperti di Java. `nilaiAkhir()` tetap bisa dipanggil langsung, jadi tidak memengaruhi hasil.

## Kesimpulan

Dengan atribut `private`, `final`/`readonly`, dan validasi di constructor, objek `Mahasiswa` tidak mungkin berada dalam keadaan yang melanggar aturan. Karena pengecekan nilai dipusatkan di satu method pembantu, aturan 0–100 cukup ditulis sekali dan tidak perlu diulang di tiap tempat.
