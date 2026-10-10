<?php

declare(strict_types=1);

class Mahasiswa
{
    public const BOBOT_TUGAS = 0.30;
    public const BOBOT_UTS = 0.30;
    public const BOBOT_UAS = 0.40;

    private const NILAI_MIN = 0;
    private const NILAI_MAX = 100;

    public function __construct(
        private readonly string $nim,
        private readonly string $nama,
        private float $nilaiTugas,
        private float $nilaiUts,
        private float $nilaiUas
    ) {
        if (trim($this->nim) === '') {
            throw new InvalidArgumentException(
                "NIM tidak boleh kosong."
            );
        }

        self::pastikanNilaiSah('Nilai Tugas', $this->nilaiTugas);
        self::pastikanNilaiSah('Nilai UTS', $this->nilaiUts);
        self::pastikanNilaiSah('Nilai UAS', $this->nilaiUas);
    }

    private static function pastikanNilaiSah(
        string $namaKomponen,
        float $nilai
    ): void {
        if ($nilai < self::NILAI_MIN || $nilai > self::NILAI_MAX) {
            throw new InvalidArgumentException(
                "$namaKomponen harus berada di antara 0 dan 100."
            );
        }
    }

    public function nilaiAkhir(): float
    {
        return ($this->nilaiTugas * self::BOBOT_TUGAS)
            + ($this->nilaiUts * self::BOBOT_UTS)
            + ($this->nilaiUas * self::BOBOT_UAS);
    }

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

    public function getNim(): string
    {
        return $this->nim;
    }

    public function getNama(): string
    {
        return $this->nama;
    }

    public function __toString(): string
    {
        return sprintf(
            '%-10s %-18s akhir=%6.2f  mutu=%s',
            $this->nim,
            $this->nama,
            $this->nilaiAkhir(),
            $this->hurufMutu()
        );
    }
}
