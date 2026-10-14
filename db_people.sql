CREATE DATABASE db_people;
USE db_people;

CREATE TABLE person (
    id             VARCHAR(20)  NOT NULL,   -- ID unik (primary key)
    nama           VARCHAR(100) NOT NULL,
    thn_lahir      INT          NOT NULL,
    kategori       VARCHAR(30)  NOT NULL,   -- Mahasiswa / Dosen / Staf / Alumni
    golongan_darah VARCHAR(2)   NOT NULL,   -- A / B / AB / O
    jenis_kelamin  VARCHAR(10)  NOT NULL,   -- Laki-Laki / Perempuan
    email          VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO person (id, nama, thn_lahir, kategori, golongan_darah, jenis_kelamin, email) VALUES
('P001', 'Repan',        2003, 'Mahasiswa', 'A',  'Laki-Laki', 'repan@student.upi.edu'),
('P002', 'Bu rosa',      1985, 'Dosen',     'B',  'Perempuan', 'rosa@upi.edu'),
('P003', 'Jamal',        1990, 'Staf',      'O',  'Laki-Laki', 'jamal@upi.edu'),
('P004', 'Rian',         2002, 'Mahasiswa', 'AB', 'Laki-Laki', 'rian@student.upi.edu'),
('P005', 'Marco',        1998, 'Alumni',    'O',  'Laki-Laki', 'marco@gmail.com'),
('P006', 'Pak Herbert',  1980, 'Dosen',     'A',  'Laki-Laki', 'herbert@upi.edu'),
('P007', 'Rizky Febian', 2004, 'Mahasiswa', 'B',  'Laki-Laki', 'rizky@student.upi.edu'),
('P008', 'Maya',         1995, 'Staf',      'AB', 'Perempuan', 'maya@upi.edu');
