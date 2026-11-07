# Skenario Kegagalan — Sistem Perpustakaan

Lengkapi tabel di bawah dengan **minimal 10 skenario**. Setiap skenario harus
benar-benar Anda coba jalankan, bukan sekadar dibayangkan.

Daftar ini menjadi bahan langsung untuk unit test pada sesi 11. Susun dengan
serius — Anda sendiri yang akan memakainya pekan depan.

| No | Masukan / tindakan | Kegagalan yang diharapkan | Jenis exception | Pesan yang ditampilkan |
|----|--------------------|---------------------------|-----------------|------------------------|
| 1  | Pinjam ISBN yang tidak terdaftar | Buku tidak ditemukan | BukuTidakDitemukanException | |
| 2  | | | | |
| 3  | | | | |
| 4  | | | | |
| 5  | | | | |
| 6  | | | | |
| 7  | | | | |
| 8  | | | | |
| 9  | | | | |
| 10 | | | | |

## Petunjuk mencari skenario

Jangan hanya memikirkan kegagalan yang "wajar". Cari juga yang aneh:

- Masukan pengguna: huruf saat diminta angka, masukan kosong, spasi saja,
  angka negatif, angka sangat besar.
- Keadaan data: anggota belum terdaftar, buku belum terdaftar, eksemplar habis,
  batas pinjam tercapai, mengembalikan buku yang tidak sedang dipinjam.
- Urutan operasi: mengembalikan sebelum meminjam, meminjam buku yang sama
  dua kali oleh anggota yang sama.
- Sumber daya: berkas ekspor tidak dapat ditulis.

## Pertanyaan yang harus dijawab di bawah tabel

1. Mana di antara skenario Anda yang menandakan **kesalahan pengguna**, dan
   mana yang menandakan **kesalahan pemrogram**? Bagaimana perbedaan itu
   memengaruhi pilihan jenis exception-nya?

2. Anda memilih PerpustakaanException diturunkan dari RuntimeException atau
   dari Exception? Tuliskan alasannya.

3. (Setelah Latihan) Ceritakan apa yang terjadi ketika Anda membuat satu blok
   catch kosong dengan sengaja, lalu menjalankan skenario yang gagal.
