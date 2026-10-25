-- Database demo baru. Metadata katalog mengikuti contoh pada proyek asal.
-- Tidak ada akun/password atau data pengguna asli di dalam seed ini.
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE TABLE books (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  author VARCHAR(120) NOT NULL,
  release_date DATE NOT NULL,
  synopsis VARCHAR(2000) NOT NULL,
  cover VARCHAR(40),
  version BIGINT NOT NULL DEFAULT 0
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO books(title,author,release_date,synopsis,cover) VALUES
('Melangkah','J. S Khairen','2020-03-22','Contoh metadata dari katalog proyek asal.','buku1.jpg'),
('Home Sweet Loan','Almira Bastari','2022-02-15','Contoh metadata dari katalog proyek asal.','buku2.jpg'),
('Laut Bercerita','Leila S. Chudori','2017-10-25','Contoh metadata dari katalog proyek asal.','buku3.png'),
('Imperfect (Cover Film)','Meira Anastasia','2019-12-01','Contoh metadata dari katalog proyek asal.','buku4.jpg'),
('Bicara Itu Ada Seninya','Oh Su Hyang','2021-05-11','Contoh metadata dari katalog proyek asal.','buku5.png'),
('You Do You','Fellexandro Ruby','2020-12-22','Contoh metadata dari katalog proyek asal.','buku6.jpeg'),
('Start With Why','Simon Sinek','2019-06-16','Contoh metadata dari katalog proyek asal.','buku7.jpg'),
('Atomic Habits','James Clear','2019-09-15','Contoh metadata dari katalog proyek asal.','buku8.jpg'),
('Yang Telah Lama Pergi','Tere Liye','2023-08-11','Contoh metadata dari katalog proyek asal.','buku9.jpg');
