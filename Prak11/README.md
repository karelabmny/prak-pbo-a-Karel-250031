# Analisis Before & After — Praktikum Sesi 11 (Collections, Generics, dan Unit Test Pertama)

Dokumen ini membandingkan kode **sebelum** (kerangka `starter` dengan komentar `TODO`) dan **sesudah** (TODO sudah dikerjakan) pada folder `java` dan `php`.

## 1. Ringkasan

| Area | Berkas | TODO | Status awal | Yang dikerjakan |
|---|---|---|---|---|
| Java | `Repository.java` | 4 | Method kosong / mengembalikan nilai palsu | Implementasi `simpan`, `cariById`, `cariSemua`, `hapus` |
| Java | `RepositoryTest.java` | 6 | Semua uji berisi `fail("TODO ...")` | 5 uji dilengkapi + 8 uji tambahan (total 13) |
| Java | 5 berkas `exception/*.java` | 8 komentar | **Kode sudah lengkap** dari sesi 10, hanya komentar TODO yang tertinggal | Komentar TODO dirapikan menjadi keterangan |
| Java | `pom.xml` | – | Belum ada JUnit | Tambah `junit-jupiter` (scope test) dan `maven-surefire-plugin` |
| PHP | `KoleksiBuku.php` | 6 | Method kosong / mengembalikan nilai palsu | Implementasi `simpan`, `cariByIsbn`, `filter`, `map`, `jumlahkan`, `kelompokkan` |
| PHP | `KoleksiBukuTest.php` | 6 | Semua uji berisi `$this->fail('TODO ...')` | 5 uji dilengkapi + 9 uji tambahan (total 14) |
| PHP | 4 berkas `src/Exception/*.php` | 5 komentar | **Kode sudah lengkap**, hanya komentar TODO yang tertinggal | Komentar TODO dirapikan |
| PHP | `composer.json` | – | `autoload-dev` kosong | Pemetaan `Perpustakaan\Tests\` → `tests/` |

### Perubahan lokasi berkas

Sesuai instruksi modul (Maven dan PHPUnit hanya menemukan uji di lokasi standar), berkas starter dipindahkan:

| Sebelum | Sesudah |
|---|---|
| `java/Repository.java` | `java/src/main/java/id/ac/univpancasila/perpustakaan/Repository.java` |
| `java/RepositoryTest.java` | `java/src/test/java/id/ac/univpancasila/perpustakaan/RepositoryTest.java` |
| `php/KoleksiBuku.php` | `php/src/Model/KoleksiBuku.php` |
| `php/KoleksiBukuTest.php` | `php/tests/KoleksiBukuTest.php` |

### Berkas pendukung dari sesi sebelumnya

Folder `Pert11/starter` juga memuat hasil kerja pertemuan 9 dan 10 agar proyek bisa dikompilasi dan diuji utuh:

| Asal | Java | PHP |
|---|---|---|
| Pertemuan 9 | `App.java`, `model/` (`Anggota`, `Buku`, `Peminjaman`), `util/Format.java` | `main.php`, `src/Model/` (`Anggota`, `Bab`, `Buku`, `Peminjaman`), `src/Util/Format.php`, `src/Service/PerpustakaanService.php`, `composer.lock` |
| Pertemuan 10 | `Main.java`, `exception/`, `service/LibraryService.java`, `service/LibraryServiceKotor.java` | `src/Exception/`, `src/Service/LibraryService.php`, `src/Service/LibraryServiceKotor.php` |

Pada Java, `service/PerpustakaanService.java` menggabungkan dua hal: metode instance dari pertemuan 9 (dipakai `App`) dan metode static menu dari pertemuan 10 (dipakai `Main`). Nama metodenya berbeda, sehingga keduanya bisa berada dalam satu kelas.

---

## 2. Java

### 2.1 `Repository.java` — kelas generik `Repository<T>`

Kerangka kelas sudah menyediakan `Map<String, T> data` (bertipe `LinkedHashMap`, sehingga urutan sisip terjaga) dan `Function<T, String> pengambilKunci`. Dengan fungsi pengambil kunci itu, `Repository` tidak perlu tahu apa pun tentang `T`.

#### TODO 1 — `simpan(T item)`

**Before**

```java
/** TODO 1: simpan item, dengan kunci diambil lewat pengambilKunci. */
public void simpan(T item) {
}
```

**After**

```java
/** Simpan item, dengan kunci diambil lewat pengambilKunci. Kunci yang sama menimpa item lama. */
public void simpan(T item) {
    data.put(pengambilKunci.apply(item), item);
}
```

**Analisis:** sebelumnya method kosong, sehingga `simpan()` tidak menyimpan apa pun dan semua pencarian gagal. Sekarang kunci dihitung lewat `pengambilKunci.apply(item)` lalu dipakai pada `Map.put`. Karena kunci unik, menyimpan dengan kunci yang sama **menimpa** item lama (perilaku ini diuji pada `simpanKunciSamaMenimpa`). Pencarian menjadi O(1) lewat hash, bukan perulangan linear.

#### TODO 2 — `cariById(String id)`

**Before**

```java
/**
 * TODO 2: kembalikan Optional yang berisi item bila ada, kosong bila tidak.
 *         JANGAN mengembalikan null.
 *         Petunjuk: Optional.ofNullable(...)
 */
public Optional<T> cariById(String id) {
    return Optional.empty();   // ganti
}
```

**After**

```java
/**
 * Kembalikan Optional yang berisi item bila ada, kosong bila tidak.
 * Tidak pernah mengembalikan null.
 */
public Optional<T> cariById(String id) {
    return Optional.ofNullable(data.get(id));
}
```

**Analisis:** versi awal selalu mengembalikan `Optional.empty()`, sehingga item yang sudah disimpan pun tidak pernah ditemukan. `Optional.ofNullable` membungkus hasil `Map.get` yang bisa `null`: ada → `Optional` berisi, tidak ada → kosong. Pemanggil dipaksa menangani kasus "tidak ada" secara eksplisit, sehingga risiko `NullPointerException` berkurang.

#### TODO 3 — `cariSemua()`

**Before**

```java
/**
 * TODO 3: kembalikan SALINAN TAK-BERUBAH dari seluruh isi.
 *         Pelajaran sesi 7 tetap berlaku: koleksi internal tidak boleh bocor.
 *         Petunjuk: List.copyOf(...)
 */
public List<T> cariSemua() {
    return List.of();   // ganti
}
```

**After**

```java
/**
 * Kembalikan SALINAN TAK-BERUBAH dari seluruh isi.
 * Pelajaran sesi 7 tetap berlaku: koleksi internal tidak boleh bocor.
 */
public List<T> cariSemua() {
    return List.copyOf(data.values());
}
```

**Analisis:** `List.copyOf` membuat **salinan tak-berubah**. Pemanggil tidak bisa mengubah isi internal (`add`/`remove` melempar `UnsupportedOperationException`). Ini menerapkan prinsip enkapsulasi sesi 7 dan diuji oleh `koleksiTidakBocor`. Bila memakai `new ArrayList<>(...)`, salinan memang terpisah tetapi masih bisa diubah, dan uji tersebut akan gagal.

#### TODO 4 — `hapus(String id)`

**Before**

```java
/** TODO 4: hapus item; kembalikan true bila ada yang terhapus. */
public boolean hapus(String id) {
    return false;   // ganti
}
```

**After**

```java
/** Hapus item; kembalikan true bila ada yang terhapus. */
public boolean hapus(String id) {
    return data.remove(id) != null;
}
```

**Analisis:** `Map.remove` mengembalikan nilai lama atau `null` bila kunci tidak ada, sehingga `!= null` langsung menjadi jawaban "apakah ada yang terhapus" tanpa `containsKey` terpisah.

> **Checkpoint:** `Repository<Buku>` menolak objek selain `Buku` pada tahap **kompilasi**, karena type parameter `<T>`. `repo.simpan(new Anggota(...))` pada `Repository<Buku>` tidak akan dikompilasi.

### 2.2 `RepositoryTest.java` — JUnit 5

Setiap uji berjalan setelah `@BeforeEach siapkan()` membuat `Repository` baru, sehingga uji tidak saling memengaruhi.

#### TODO 1 — `simpanLaluTemukan`

**Before**

```java
@Test
@DisplayName("Item yang disimpan dapat ditemukan kembali")
void simpanLaluTemukan() {
    repo.simpan(new Buku("978-1", "PBO"));

    // TODO 1: pastikan cariById("978-1") berisi nilai,
    //         dan judulnya "PBO".
    //         Petunjuk: assertTrue(...isPresent()) dan assertEquals(...)
    fail("TODO 1 belum dikerjakan");
}
```

**After**

```java
@Test
@DisplayName("Item yang disimpan dapat ditemukan kembali")
void simpanLaluTemukan() {
    repo.simpan(new Buku("978-1", "PBO"));

    Optional<Buku> hasil = repo.cariById("978-1");

    assertTrue(hasil.isPresent());
    assertEquals("PBO", hasil.get().judul());
}
```

**Analisis:** uji ini membuktikan dua hal: nilainya **ada** (`isPresent`) dan **isinya benar** (judul "PBO"). Memeriksa isi, bukan sekadar "tidak null", mencegah uji yang terlalu longgar.

#### TODO 2 — `cariYangTidakAda`

**Before**

```java
@Test
@DisplayName("Mencari id yang tidak ada mengembalikan Optional kosong")
void cariYangTidakAda() {
    // TODO 2
    fail("TODO 2 belum dikerjakan");
}
```

**After**

```java
@Test
@DisplayName("Mencari id yang tidak ada mengembalikan Optional kosong")
void cariYangTidakAda() {
    assertTrue(repo.cariById("999").isEmpty());
}
```

**Analisis:** memastikan kontrak `cariById`: id yang tidak ada menghasilkan `Optional` kosong, bukan `null` dan bukan exception.

#### TODO 3 — `hapusItem`

**Before**

```java
@Test
@DisplayName("Item yang dihapus tidak dapat ditemukan lagi")
void hapusItem() {
    // TODO 3
    fail("TODO 3 belum dikerjakan");
}
```

**After**

```java
@Test
@DisplayName("Item yang dihapus tidak dapat ditemukan lagi")
void hapusItem() {
    repo.simpan(new Buku("978-1", "PBO"));

    assertTrue(repo.hapus("978-1"));
    assertTrue(repo.cariById("978-1").isEmpty());
    assertEquals(0, repo.jumlah());
}
```

**Analisis:** tiga hal diperiksa sekaligus: `hapus` mengembalikan `true`, item tidak lagi ditemukan, dan jumlah kembali 0.

#### TODO 4 — `repositoryKosong`

**Before**

```java
@Test
@DisplayName("Repository baru berisi nol item")
void repositoryKosong() {
    // TODO 4
    fail("TODO 4 belum dikerjakan");
}
```

**After**

```java
@Test
@DisplayName("Repository baru berisi nol item")
void repositoryKosong() {
    assertEquals(0, repo.jumlah());
    assertTrue(repo.cariSemua().isEmpty());
}
```

**Analisis:** memastikan keadaan awal repository bersih. Karena `@BeforeEach` membuat `Repository` baru, uji ini juga membuktikan tidak ada sisa data dari uji lain.

#### TODO 5 — `koleksiTidakBocor`

**Before**

```java
@Test
@DisplayName("Koleksi yang dikembalikan cariSemua tidak dapat diubah dari luar")
void koleksiTidakBocor() {
    repo.simpan(new Buku("978-1", "PBO"));

    // TODO 5: pastikan memanggil .add(...) pada hasil cariSemua()
    //         melemparkan UnsupportedOperationException.
    //         Petunjuk: assertThrows(UnsupportedOperationException.class, () -> ...)
    fail("TODO 5 belum dikerjakan");
}
```

**After**

```java
@Test
@DisplayName("Koleksi yang dikembalikan cariSemua tidak dapat diubah dari luar")
void koleksiTidakBocor() {
    repo.simpan(new Buku("978-1", "PBO"));

    List<Buku> salinan = repo.cariSemua();

    assertThrows(UnsupportedOperationException.class,
            () -> salinan.add(new Buku("978-2", "Basis Data")));
}
```

**Analisis:** `assertThrows` memastikan **jenis** exception yang benar (`UnsupportedOperationException`). Uji ini hanya lulus bila TODO 3 memakai `List.copyOf`.

#### TODO 6 — minimal tiga uji tambahan (dikerjakan: 8)

**Before**

```java
// TODO 6: tambahkan minimal TIGA pengujian lagi dari daftar skenario
//         yang Anda susun pada sesi 10, termasuk satu yang memastikan
//         exception yang benar dilemparkan.
```

**After**

Tiga uji menguji `Repository` sendiri:

```java
@Test
@DisplayName("Menyimpan dengan kunci yang sama menimpa item lama")
void simpanKunciSamaMenimpa() { ... }

@Test
@DisplayName("Menghapus id yang tidak ada mengembalikan false")
void hapusYangTidakAda() { ... }

@Test
@DisplayName("cariSemua mempertahankan urutan penyimpanan")
void cariSemuaMempertahankanUrutan() { ... }
```

Lima uji lainnya menguji skenario kegagalan `LibraryService` dari sesi 10, termasuk uji exception:

```java
@Test
@DisplayName("pinjam() melempar BukuTidakDitemukanException untuk ISBN yang tidak terdaftar")
void pinjamIsbnTidakTerdaftar() {
    LibraryService layanan = layanan();

    BukuTidakDitemukanException e = assertThrows(
            BukuTidakDitemukanException.class,
            () -> layanan.pinjam("A1", "000"));

    assertEquals("000", e.getIsbn());
}

@Test
@DisplayName("pinjam() melempar BatasPinjamTerlampauiException pada pinjaman ke-4")
void pinjamMelewatiBatas() {
    LibraryService layanan = layanan();
    layanan.tambahBuku("222", "Bumi Manusia", 10);
    layanan.pinjam("A1", "222");
    layanan.pinjam("A1", "222");
    layanan.pinjam("A1", "222");

    BatasPinjamTerlampauiException e = assertThrows(
            BatasPinjamTerlampauiException.class,
            () -> layanan.pinjam("A1", "222"));

    assertEquals(LibraryService.BATAS_PINJAM, e.getBatas());
    assertEquals("A1", e.getIdAnggota());
}
```

| Uji | Yang dibuktikan |
|---|---|
| `pinjamBerhasilMengurangiStok` | Peminjaman yang sah mengurangi stok |
| `pinjamIsbnTidakTerdaftar` | Melempar `BukuTidakDitemukanException`, `getIsbn()` benar |
| `pinjamAnggotaTidakTerdaftar` | Melempar `AnggotaTidakDitemukanException`, `getIdAnggota()` benar |
| `pinjamMelewatiBatas` | Melempar `BatasPinjamTerlampauiException` pada pinjaman ke-4, `getBatas()` bernilai 3 |
| `pinjamBukuHabis` | Melempar `BukuSedangDipinjamException`, `getJudul()` dan `getIsbn()` benar |

**Analisis:** kelima uji ini mengubah kegagalan domain menjadi pernyataan yang bisa diperiksa mesin. `assertThrows` mengembalikan objek exception, sehingga data penyebabnya (`getIsbn()`, `getBatas()`, dst.) ikut diverifikasi, bukan hanya jenisnya. Total uji: 5 + 8 = **13**.

### 2.3 `exception/*.java` — komentar TODO

Berbeda dengan berkas lain, kelima exception ini **sudah berisi kode yang benar** dari sesi 10 (atribut `final`, `super(...)` dengan pesan, getter). Yang tertinggal hanya komentar `TODO` yang menyesatkan karena seolah-olah belum dikerjakan. Contoh `BukuTidakDitemukanException`:

**Before**

```java
// TODO 1: deklarasikan atribut privat final untuk isbn.
private final String isbn;

public BukuTidakDitemukanException(String isbn) {
    // TODO 2: panggil super(...) dengan pesan yang menyebut ISBN-nya,
    // misalnya: "Buku dengan ISBN 978-1 tidak ditemukan"
    super("Buku dengan ISBN " + isbn + " tidak ditemukan");
    // TODO 3: simpan isbn ke atribut.
    this.isbn = isbn;
}

// TODO 4: sediakan getter getIsbn().
public String getIsbn() { return this.isbn; }
```

**After**

```java
// Atribut privat final untuk isbn.
private final String isbn;

public BukuTidakDitemukanException(String isbn) {
    // Pesan menyebut ISBN-nya, misalnya: "Buku dengan ISBN 978-1 tidak ditemukan"
    super("Buku dengan ISBN " + isbn + " tidak ditemukan");
    // Simpan isbn ke atribut.
    this.isbn = isbn;
}

// Getter agar pemanggil bisa mengambil kembali ISBN penyebabnya.
public String getIsbn() { return this.isbn; }
```

Pada `PerpustakaanException`, TODO 1 meminta keputusan *checked* atau *unchecked*. Induknya sudah `extends RuntimeException`; yang ditambahkan adalah alasannya:

**Before**

```java
 * TODO 1: putuskan — turunkan dari RuntimeException (unchecked) atau dari
 *         Exception (checked)? Tuliskan alasannya di skenario-uji.md.
```

**After**

```java
 * KEPUTUSAN: diturunkan dari RuntimeException (unchecked). Kegagalan domain
 * di sini menandakan permintaan yang tidak sah (ISBN salah, batas terlampaui),
 * bukan keadaan luar yang wajar, sehingga pemanggil tidak dipaksa menulis
 * try/catch di setiap tempat.
```

Pada tiga exception lainnya, awalan `TODO:` pada komentar kelas diganti menjadi kalimat keterangan.

### 2.4 `pom.xml`

**Before:** hanya `ascii-table` di `<dependencies>` dan `exec-maven-plugin` di `<plugins>`; `mvn test` tidak bisa menemukan JUnit.

**After:**

```xml
<!--
  Sesi 11 — JUnit 5. scope=test: hanya tersedia saat pengujian dan
  TIDAK ikut masuk ke berkas .jar hasil produksi.
-->
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.11.3</version>
  <scope>test</scope>
</dependency>
```

```xml
<!-- Sesi 11 — menjalankan pengujian lewat: mvn test -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.5.2</version>
</plugin>
```

`scope=test` membuat JUnit hanya tersedia saat pengujian dan tidak ikut masuk ke `.jar` produksi. Selain itu `maven.compiler.release` diturunkan dari 25 menjadi 17 agar proyek bisa dikompilasi dengan JDK 17 ke atas; kode pada proyek ini tidak memakai fitur yang lebih baru dari Java 17.

---

## 3. PHP

PHP tidak punya generics, sehingga padanannya dibuat dengan pendekatan *typed collection*: tipe parameter `simpan(Buku $b)` menolak objek selain `Buku` (dengan `declare(strict_types=1)` akan melempar `TypeError`).

### 3.1 `KoleksiBuku.php`

#### TODO 1 — `simpan(Buku $b)`

**Before**

```php
/** TODO 1: simpan buku dengan kunci ISBN-nya. */
public function simpan(Buku $b): void
{
}
```

**After**

```php
/** Simpan buku dengan kunci ISBN-nya. ISBN yang sama menimpa buku lama. */
public function simpan(Buku $b): void
{
    $this->items[$b->getNomorInduk()] = $b;
}
```

**Analisis:** kunci memakai `getNomorInduk()` (ISBN), sehingga `items` menjadi array asosiatif `isbn => Buku` — padanan `Map` di Java. ISBN yang sama menimpa buku lama.

#### TODO 2 — `cariByIsbn(string $isbn): ?Buku`

**Before**

```php
/** TODO 2: kembalikan Buku atau null. Perhatikan tipe kembaliannya: ?Buku */
public function cariByIsbn(string $isbn): ?Buku
{
    return null;   // ganti
}
```

**After**

```php
/** Kembalikan Buku atau null. Perhatikan tipe kembaliannya: ?Buku */
public function cariByIsbn(string $isbn): ?Buku
{
    return $this->items[$isbn] ?? null;
}
```

**Analisis:** operator `??` mengembalikan elemen bila ada dan `null` bila tidak, tanpa *warning* "undefined array key". Tipe kembalian `?Buku` menyatakan bahwa hasilnya bisa `null` — padanan `Optional` di Java.

#### TODO 3 — `filter(callable $predikat)`

**Before**

```php
/**
 * TODO 3: kembalikan KoleksiBuku baru berisi item yang lolos predikat.
 *         PERHATIAN: array_filter(array, callable) — array DULU.
 *         Jangan lupa array_values agar indeksnya tidak berlubang.
 */
public function filter(callable $predikat): self
{
    return new self();   // ganti
}
```

**After**

```php
/**
 * Kembalikan KoleksiBuku baru berisi item yang lolos predikat.
 * PERHATIAN: array_filter(array, callable) — array DULU.
 * array_values dipakai agar indeksnya tidak berlubang.
 */
public function filter(callable $predikat): self
{
    return new self(array_values(array_filter($this->items, $predikat)));
}
```

**Analisis:** `array_filter` mempertahankan kunci asli, sehingga indeks bisa berlubang. `array_values` merapikannya. Hasil dibungkus `new self(...)` agar tetap bertipe `KoleksiBuku` dan bisa dirantai. Perhatikan urutan argumen: `array_filter(array, callable)` — array **dulu**.

#### TODO 4 — `map(callable $f)`

**Before**

```php
/**
 * TODO 4: kembalikan larik hasil pemetaan.
 *         PERHATIAN: array_map(callable, array) — callable DULU.
 *         Urutan argumennya BERBEDA dari array_filter. Ini sumber
 *         kesalahan yang sangat sering terjadi.
 *
 * @return mixed[]
 */
public function map(callable $f): array
{
    return [];   // ganti
}
```

**After**

```php
/**
 * Kembalikan larik hasil pemetaan.
 * PERHATIAN: array_map(callable, array) — callable DULU.
 * Urutan argumennya BERBEDA dari array_filter. Ini sumber
 * kesalahan yang sangat sering terjadi.
 *
 * @return mixed[]
 */
public function map(callable $f): array
{
    return array_map($f, array_values($this->items));
}
```

**Analisis:** `array_map(callable, array)` — callable **dulu**, kebalikan dari `array_filter`. Ini sumber kesalahan yang sering terjadi. `array_values` memastikan hasil berindeks 0..n-1, bukan berkunci ISBN.

#### TODO 5 — `jumlahkan(callable $f)`

**Before**

```php
/** TODO 5: jumlahkan hasil $f atas seluruh item. Petunjuk: array_reduce */
public function jumlahkan(callable $f): float
{
    return 0.0;   // ganti
}
```

**After**

```php
/** Jumlahkan hasil $f atas seluruh item. */
public function jumlahkan(callable $f): float
{
    return array_reduce(
        $this->items,
        fn (float $total, Buku $b): float => $total + (float) $f($b),
        0.0
    );
}
```

**Analisis:** `array_reduce` melipat seluruh item menjadi satu angka. Nilai awal `0.0` memastikan koleksi kosong menghasilkan `0.0` (bukan `null`), sesuai tipe kembalian `float`.

#### TODO 6 — `kelompokkan(callable $kunci)`

**Before**

```php
/**
 * TODO 6: kelompokkan item berdasarkan kunci hasil $kunci($buku).
 * @return array<string,Buku[]>
 */
public function kelompokkan(callable $kunci): array
{
    return [];   // ganti
}
```

**After**

```php
/**
 * Kelompokkan item berdasarkan kunci hasil $kunci($buku).
 * @return array<string,Buku[]>
 */
public function kelompokkan(callable $kunci): array
{
    $hasil = [];
    foreach ($this->items as $b) {
        $hasil[(string) $kunci($b)][] = $b;
    }
    return $hasil;
}
```

**Analisis:** `$hasil[kunci][] = $b` otomatis membuat larik baru pada kunci yang belum ada, lalu menambahkan item — padanan `Collectors.groupingBy` di Java. Kunci di-cast ke `string` agar sesuai anotasi `array<string,Buku[]>`.

### 3.2 `KoleksiBukuTest.php` — PHPUnit

Setiap uji berjalan setelah `setUp()` membuat `KoleksiBuku` baru. Metode pembantu `isiTigaBuku()` mengisi tiga buku: dua tersedia (`978-1`, `978-3`) dan satu habis (`978-2`).

#### TODO 1–5

**Before:** setiap uji berisi `$this->fail('TODO n belum dikerjakan')`, sehingga **pasti gagal**.

**After**

```php
public function testItemYangDisimpanDapatDitemukanKembali(): void
{
    $this->koleksi->simpan(new Buku('978-1', 'PBO', 'Budi', 2));

    $hasil = $this->koleksi->cariByIsbn('978-1');

    $this->assertNotNull($hasil);
    $this->assertSame('PBO', $hasil->getJudul());
}

public function testMencariIsbnYangTidakAdaMengembalikanNull(): void
{
    $this->assertNull($this->koleksi->cariByIsbn('999'));
}

public function testKoleksiBaruBerisiNolItem(): void
{
    // count() bekerja karena kelasnya implements Countable.
    $this->assertCount(0, $this->koleksi);
    $this->assertSame(0, count($this->koleksi));
}

public function testFilterHanyaMengembalikanYangLolosPredikat(): void
{
    $this->isiTigaBuku();

    $tersedia = $this->koleksi->filter(fn (Buku $b): bool => $b->tersedia());

    $this->assertCount(2, $tersedia);
    $this->assertSame(
        ['978-1', '978-3'],
        array_map(fn (Buku $b): string => $b->getNomorInduk(), $tersedia->semua())
    );
}

public function testJumlahkanMenghitungTotalDenganBenar(): void
{
    $this->isiTigaBuku();

    $total = $this->koleksi->jumlahkan(fn (Buku $b): float => $b->getJumlahEksemplar());

    $this->assertSame(3.0, $total);
}
```

**Analisis:**

- `assertSame` memakai perbandingan ketat (nilai **dan** tipe), lebih ketat daripada `assertEquals`.
- `assertCount(0, $this->koleksi)` bisa dipakai pada objek karena `KoleksiBuku implements Countable`.
- Uji `filter` membandingkan daftar ISBN yang benar-benar lolos, bukan hanya jumlahnya, sehingga tidak bisa lulus secara kebetulan.
- Uji `jumlahkan` membandingkan dengan `3.0` (float), sesuai tipe kembalian.

#### TODO 6 — minimal lima skenario (dikerjakan: 9), termasuk uji exception

**Before**

```php
// TODO 6: terjemahkan minimal LIMA skenario dari skenario-uji.md sesi 10
//         menjadi kasus uji di sini, termasuk satu yang memastikan
//         exception yang benar dilemparkan.
//         Petunjuk: $this->expectException(BukuTidakDitemukanException::class);
```

**After** — contoh uji exception:

```php
public function testPinjamIsbnTidakTerdaftarMelemparBukuTidakDitemukan(): void
{
    $layanan = $this->layanan();

    $this->expectException(BukuTidakDitemukanException::class);
    $layanan->pinjam('A1', '000');
}

public function testMenyimpanBukanBukuMelemparTypeError(): void
{
    // strict_types + type hint Buku pada simpan() menolak tipe lain.
    $this->expectException(TypeError::class);

    new KoleksiBuku(['bukan buku']);
}
```

Kesembilan uji tambahan:

| Uji | Yang dibuktikan |
|---|---|
| `testSimpanIsbnYangSamaMenimpaBukuLama` | ISBN yang sama menimpa, jumlah tetap 1 |
| `testFilterTidakMengubahKoleksiAsli` | `filter` mengembalikan koleksi baru, yang asli tetap 3 item |
| `testMapMengembalikanDaftarJudulBerurutan` | `map` menghasilkan daftar judul sesuai urutan simpan |
| `testKelompokkanBerdasarkanPenulis` | Pengelompokan per penulis (Budi: 2, Sari: 1) |
| `testMenyimpanBukanBukuMelemparTypeError` | `new KoleksiBuku(['bukan buku'])` ditolak dengan `TypeError` |
| `testPinjamIsbnTidakTerdaftarMelemparBukuTidakDitemukan` | Exception yang benar untuk ISBN tak terdaftar |
| `testPinjamAnggotaTidakTerdaftarMelemparAnggotaTidakDitemukan` | Exception yang benar untuk anggota tak terdaftar |
| `testPinjamMelewatiBatasMelemparBatasPinjamTerlampaui` | Exception yang benar pada pinjaman ke-4, `getBatas()` bernilai 3 |
| `testPinjamBukuHabisMelemparBukuSedangDipinjam` | Exception yang benar saat stok habis |

**Analisis:** `expectException` memeriksa jenis exception, sedangkan blok `try/catch` dengan `$this->fail(...)` dipakai ketika data di dalam exception (`getBatas()`, `getJudul()` dst.) juga ingin diperiksa. Uji `TypeError` membuktikan jaminan tipe pada `simpan(Buku $b)` yang menggantikan generics. Total uji: 5 + 9 = **14**.

### 3.3 `composer.json`

**Before:** `autoload-dev` kosong.

```json
"autoload-dev": {
    "psr-4": {}
}
```

**After**

```json
"autoload-dev": {
    "psr-4": {
        "Perpustakaan\\Tests\\": "tests/"
    }
}
```

Tanpa pemetaan ini, PHPUnit tidak mengenali namespace `Perpustakaan\Tests\` dan melaporkan *No tests executed*. PHPUnit sendiri **belum** ada di `require-dev` — jalankan `composer require --dev phpunit/phpunit` agar `composer.lock` ikut diperbarui. Dependensi `nesbot/carbon` dari pertemuan 9 tetap dipertahankan.

### 3.4 `src/Exception/*.php` — komentar TODO

Sama seperti Java: kode sudah benar (*constructor property promotion* dengan `private readonly`), hanya komentar TODO yang tertinggal.

**Before**

```php
// TODO 4: membawa $isbn DAN $judul.
```

**After**

```php
// Membawa $isbn DAN $judul.
```

---

## 4. Perbandingan Java vs PHP

| Konsep | Java | PHP |
|---|---|---|
| Penyimpanan berkunci | `Map<String, T>` (`LinkedHashMap`) | array asosiatif `isbn => Buku` |
| Jaminan tipe | Generics `<T>`, dicek saat kompilasi | Type hint `Buku $b`, dicek saat runtime |
| Hasil "mungkin tidak ada" | `Optional<T>` | `?Buku` + `??` |
| Salinan aman | `List.copyOf(...)` | Array PHP disalin-saat-ubah (*copy-on-write*), `array_values(...)` |
| Filter | `stream().filter(...)` | `array_filter(array, callable)` |
| Map | `stream().map(...)` | `array_map(callable, array)` |
| Pengelompokan | `Collectors.groupingBy` | `foreach` + `$hasil[$kunci][] = ...` |
| Uji exception | `assertThrows(...)` | `expectException(...)` |
| Setup uji | `@BeforeEach` | `setUp()` |

---

## 5. Cara menjalankan

**Java** (membutuhkan JDK 17 atau lebih baru sesuai `maven.compiler.release` di `pom.xml`):

```bash
cd java
mvn test
mvn compile exec:java -Dexec.mainClass=id.ac.univpancasila.perpustakaan.App
mvn compile exec:java -Dexec.mainClass=id.ac.univpancasila.perpustakaan.Main
```

`mvn test` menjalankan unit test sesi 11, `App` adalah demo sesi 9, dan `Main` adalah menu interaktif sesi 10.

**PHP** (membutuhkan PHP 8.2+):

```bash
cd php
composer install
composer require --dev phpunit/phpunit
./vendor/bin/phpunit tests
php main.php
```
