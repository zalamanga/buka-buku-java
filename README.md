# BukaBuku Java

Katalog buku dan halaman pengelolaan berbasis **Java 17, JSF/Facelets, Hibernate, dan MySQL**. Pengunjung membaca katalog tanpa akun. Pengguna terdaftar dapat menambah, mencari, mengubah, dan menghapus metadata buku; perubahan langsung tampil pada katalog yang sama.

Ini proyek belajar dengan **koleksi bersama**: setiap akun demo memiliki hak mengelola seluruh buku. Tidak ada pembayaran, peminjaman, unduhan isi buku, atau pembagian peran admin. Gunakan akun dan data contoh untuk mencobanya.

## Jalankan dengan Docker

Siapkan Docker Desktop dalam mode Linux containers. Di PowerShell:

```powershell
git clone https://github.com/zalamanga/buka-buku-java.git
cd buka-buku-java
Copy-Item .env.example .env
```

Buka `.env`. Ganti kedua nilai password contoh dengan dua password lokal yang berbeda, lalu jalankan:

```powershell
docker compose up --build -d
docker compose ps
```

Buka **http://localhost:18093**. Database baru berisi sembilan contoh metadata buku beserta sampul dari proyek asal. **Tidak ada akun bawaan**: pilih **Masuk > Daftar akun demo**, lalu buat akun dengan password 12 sampai 128 karakter. Kembali ke halaman masuk untuk membuka pengelolaan.

Build pertama mengunduh image dan dependency. Di macOS/Linux, gunakan `cp .env.example .env`. Port web hanya dibuka ke komputer lokal; port database tidak dipublikasikan.

```powershell
docker compose logs --tail=80 web    # Periksa aplikasi bila belum dapat dibuka
docker compose logs --tail=80 db     # Periksa kesiapan database
docker compose down                 # Hentikan; data tetap tersimpan pada volume
```

Seed SQL hanya dijalankan ketika volume database pertama kali dibuat. Mengubah password `.env` setelah database terbentuk tidak otomatis mengganti password akun MySQL yang sudah ada. File `.env` diabaikan Git.

## Alur yang tersedia

| Alur | Perilaku |
| --- | --- |
| Baca dan cari katalog | Pencarian judul/penulis membaca database; teks kosong menampilkan semua buku. |
| Daftar dan masuk | Email dinormalisasi, password disimpan sebagai hash PBKDF2 dengan salt acak; sesi dibuat setelah verifikasi. |
| Tambah buku | Isi judul, penulis, tanggal terbit, dan sinopsis. Buku baru tampil tanpa sampul; upload gambar belum tersedia. |
| Ubah buku | Pilih **Ubah** pada baris buku, sunting form, lalu **Simpan buku**. |
| Hapus buku | Pilih buku, klik **Hapus buku ini**, lalu konfirmasi atau batalkan. |
| Keluar | Sesi diakhiri. Halaman pengelolaan meminta login kembali. |
| Data berubah bersamaan | Versi record mencegah perubahan lama menimpa perubahan yang sudah tersimpan. Muat ulang sebelum mencoba lagi. |
| Koneksi gagal | Halaman menampilkan pesan gagal dan tidak mengklaim data tersimpan. |

Semua permintaan perubahan memerlukan sesi/form yang sesuai, dengan validasi server dan token CSRF. Ini tidak mencakup seluruh kebutuhan aplikasi publik: belum ada pembatasan percobaan login, pemulihan password, verifikasi email, atau audit perubahan.

Jika koneksi database terputus, pool koneksi yang gagal dibuang. Permintaan berikutnya membuat koneksi baru setelah MySQL pulih. Aplikasi tidak mengulang operasi tulis secara otomatis; setelah timeout, muat ulang katalog sebelum mengirim perubahan yang sama lagi.

## Build dan tes tanpa Docker

Gunakan JDK 17 dan Maven 3.9 atau lebih baru:

```powershell
mvn verify
```

WAR terbentuk di `target/buka-buku.war`. Tes memakai database H2 dalam memori yang terpisah; tidak membaca `.env` atau menghubungi database asal.

Untuk menjalankan WAR, sediakan database MySQL dengan `database/001-schema.sql`. Atur environment **pada proses Tomcat 9**: `DB_URL` (URL JDBC MySQL), `DB_USER`, dan `DB_PASSWORD`. Deploy WAR ke direktori `webapps` Tomcat. Akses aplikasi pada context path sesuai nama WAR, misalnya `http://localhost:8080/buka-buku/`. Docker Compose sudah mengatur ketiga environment dan context root tersebut.

Stack ini mempertahankan keluarga API `javax` dari proyek asal: JSF 2.3 dan Hibernate 5.6. Hibernate 5.6 telah [berakhir masa dukungannya](https://hibernate.org/orm/releases/5.6/). Gunakan proyek ini sebagai demo lokal; migrasi ke Jakarta/Faces/Hibernate yang masih didukung merupakan pekerjaan tersendiri. Tomcat 10/11 tidak dapat langsung menggantikan Tomcat 9 untuk WAR ini.

## Susunan kode

```text
database/001-schema.sql       Schema dan metadata buku contoh
src/main/java/id/bukabuku/
  model/                     Entity Book dan User
  data/                      Konfigurasi Hibernate dan operasi transaksi
  security/                  Hash password, sesi, dan pemeriksaan CSRF
  web/                       Managed bean, validasi dan lifecycle aplikasi
src/main/webapp/              Facelets dan aset lokal
src/test/java/                Pengujian transaksi, validasi, hash dan filter
```

Konfigurasi tidak melakukan pembuatan ulang schema otomatis pada runtime. Model, DAO, dan form sudah dipisahkan; session Hibernate ditutup dan transaksi yang gagal di-rollback. Tampilan memakai CSS/aset lokal tanpa CDN.

Lihat [NOTICE.md](NOTICE.md) untuk asal kode dan gambar. Pembaruan ini tidak memberikan lisensi baru atas sampul buku atau aset sumber.
