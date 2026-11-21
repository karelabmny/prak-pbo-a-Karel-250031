<?php
declare(strict_types=1);

namespace Perpustakaan\Tests;

use PHPUnit\Framework\TestCase;
use Perpustakaan\Exception\AnggotaTidakDitemukanException;
use Perpustakaan\Exception\BatasPinjamTerlampauiException;
use Perpustakaan\Exception\BukuSedangDipinjamException;
use Perpustakaan\Exception\BukuTidakDitemukanException;
use Perpustakaan\Model\Buku;
use Perpustakaan\Model\KoleksiBuku;
use Perpustakaan\Service\LibraryService;
use TypeError;

/**
 * Sesi 11 — unit test PHPUnit.
 *
 * Letakkan di tests/ dan pastikan autoload-dev pada composer.json sudah
 * memetakan Perpustakaan\Tests\ ke tests/.
 *
 * Jalankan: ./vendor/bin/phpunit tests
 */
final class KoleksiBukuTest extends TestCase
{
    private KoleksiBuku $koleksi;

    protected function setUp(): void
    {
        // Dijalankan ulang sebelum SETIAP pengujian.
        $this->koleksi = new KoleksiBuku();
    }

    /** Isi tiga buku: dua tersedia (978-1, 978-3), satu habis (978-2). */
    private function isiTigaBuku(): void
    {
        $this->koleksi->simpan(new Buku('978-1', 'PBO', 'Budi', 2));
        $this->koleksi->simpan(new Buku('978-2', 'Basis Data', 'Sari', 0));
        $this->koleksi->simpan(new Buku('978-3', 'Desain Web', 'Budi', 1));
    }

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

    // ---- TODO 6: uji tambahan ----

    public function testSimpanIsbnYangSamaMenimpaBukuLama(): void
    {
        $this->koleksi->simpan(new Buku('978-1', 'PBO', 'Budi', 2));
        $this->koleksi->simpan(new Buku('978-1', 'PBO Edisi 2', 'Budi', 2));

        $this->assertCount(1, $this->koleksi);
        $this->assertSame('PBO Edisi 2', $this->koleksi->cariByIsbn('978-1')->getJudul());
    }

    public function testFilterTidakMengubahKoleksiAsli(): void
    {
        $this->isiTigaBuku();

        $this->koleksi->filter(fn (Buku $b): bool => $b->tersedia());

        $this->assertCount(3, $this->koleksi);
    }

    public function testMapMengembalikanDaftarJudulBerurutan(): void
    {
        $this->isiTigaBuku();

        $judul = $this->koleksi->map(fn (Buku $b): string => $b->getJudul());

        $this->assertSame(['PBO', 'Basis Data', 'Desain Web'], $judul);
    }

    public function testKelompokkanBerdasarkanPenulis(): void
    {
        $this->isiTigaBuku();

        $kelompok = $this->koleksi->kelompokkan(fn (Buku $b): string => $b->getPenulis());

        $this->assertSame(['Budi', 'Sari'], array_keys($kelompok));
        $this->assertCount(2, $kelompok['Budi']);
        $this->assertCount(1, $kelompok['Sari']);
    }

    public function testMenyimpanBukanBukuMelemparTypeError(): void
    {
        // strict_types + type hint Buku pada simpan() menolak tipe lain.
        $this->expectException(TypeError::class);

        new KoleksiBuku(['bukan buku']);
    }

    // ---- Skenario kegagalan dari sesi 10 (LibraryService + exception domain) ----

    private function layanan(): LibraryService
    {
        $layanan = new LibraryService();
        $layanan->tambahBuku('111', 'Laskar Pelangi', 1);
        $layanan->daftarkanAnggota('A1');
        return $layanan;
    }

    public function testPinjamIsbnTidakTerdaftarMelemparBukuTidakDitemukan(): void
    {
        $layanan = $this->layanan();

        $this->expectException(BukuTidakDitemukanException::class);
        $layanan->pinjam('A1', '000');
    }

    public function testPinjamAnggotaTidakTerdaftarMelemparAnggotaTidakDitemukan(): void
    {
        $layanan = $this->layanan();

        $this->expectException(AnggotaTidakDitemukanException::class);
        $layanan->pinjam('X9', '111');
    }

    public function testPinjamMelewatiBatasMelemparBatasPinjamTerlampaui(): void
    {
        $layanan = $this->layanan();
        $layanan->tambahBuku('222', 'Bumi Manusia', 10);
        $layanan->pinjam('A1', '222');
        $layanan->pinjam('A1', '222');
        $layanan->pinjam('A1', '222');

        try {
            $layanan->pinjam('A1', '222');
            $this->fail('Seharusnya BatasPinjamTerlampauiException dilempar.');
        } catch (BatasPinjamTerlampauiException $e) {
            $this->assertSame(3, $e->getBatas());
            $this->assertSame('A1', $e->getIdAnggota());
        }
    }

    public function testPinjamBukuHabisMelemparBukuSedangDipinjam(): void
    {
        $layanan = $this->layanan();
        $layanan->daftarkanAnggota('A2');
        $layanan->pinjam('A1', '111');   // eksemplar terakhir

        try {
            $layanan->pinjam('A2', '111');
            $this->fail('Seharusnya BukuSedangDipinjamException dilempar.');
        } catch (BukuSedangDipinjamException $e) {
            $this->assertSame('Laskar Pelangi', $e->getJudul());
            $this->assertSame('111', $e->getIsbn());
        }
    }
}
