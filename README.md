# 🙍‍♂️ PeopleData (Java Swing CRUD + MySQL)

Aplikasi desktop berbasis **Java Swing GUI** untuk mengelola data orang (People) dengan operasi **CRUD** (Create, Read, Update, Delete). Data disimpan di **database MySQL** sehingga tidak hilang saat program ditutup (tidak lagi memakai `ArrayList`).

## ❤️ Janji

Saya Jaka Permana Herawan dengan NIM 2509371 mengerjakan Tugas Praktikum 5 pada Mata Kuliah Desain dan Pemrograman Berorientasi Objek (DPBO) untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

## 🎨 Desain & Fungsi Program

### Struktur database (`db_people.sql`)

Database `db_people`, tabel `person`:

| Kolom | Tipe | Component di form | Keterangan |
|---|---|---|---|
| `id` | VARCHAR(20), **PRIMARY KEY** | `JTextField` | ID unik orang |
| `nama` | VARCHAR(100) | `JTextField` | Nama lengkap |
| `thn_lahir` | INT | `JTextField` | Harus berupa angka |
| `kategori` | VARCHAR(30) | `JComboBox` | Mahasiswa / Dosen / Staf / Alumni |
| `golongan_darah` | VARCHAR(2) | `JComboBox` | A / B / AB / O |
| `jenis_kelamin` | VARCHAR(10) | `JRadioButton` | Laki-Laki / Perempuan (satu pilihan, memakai `ButtonGroup`) |
| `email` | VARCHAR(100) | `JTextField` | Divalidasi formatnya (`nama@domain.com`) |

### Class

| File | Peran |
|---|---|
| `Database.java` | Mengelola koneksi JDBC ke MySQL. Menyediakan `selectQuery()`, `insertUpdateDeleteQuery()`, `getStatement()`, dan tambahan `prepareStatement()` untuk query berparameter. |
| `Person.java` | Model data satu orang (atribut, constructor, getter/setter). Dipakai untuk membungkus isi form sebelum disimpan ke database. |
| `PeopleMenu.java` | Logika GUI + CRUD. Seluruh data dibaca/ditulis langsung ke database. |
| `PeopleMenu.form` | Tampilan GUI (IntelliJ Swing UI Designer). |

### Komponen utama form (`PeopleMenu.form`)

- `titleLabel` - judul aplikasi
- `idField`, `namaField`, `tahunLahirField`, `emailField` - input teks
- `kategoriComboBox`, `golonganDarahComboBox` - input pilihan
- `lakiLakiRadioButton`, `perempuanRadioButton` - pilihan jenis kelamin (dikelompokkan dengan `ButtonGroup`)
- `addUpdateButton` - tombol **Add** (mode tambah) / **Update** (mode ubah)
- `cancelButton` - mengosongkan form
- `deleteButton` - menghapus data (hanya tampil saat sebuah baris dipilih)
- `peopleTable` - menampilkan seluruh data dari database

### Fitur sesuai spesifikasi 

1. **Semua CRUD terhubung ke database** - `SELECT`, `INSERT`, `UPDATE`, `DELETE`.
2. **Tanpa `ArrayList`** - data tabel dibaca dari `ResultSet`; saat baris diklik, nilai form diambil langsung dari model tabel.
3. **Error jika ada kolom kosong saat insert/update** - dialog menyebutkan kolom mana saja yang masih kosong.
4. **Error jika ID sudah ada saat insert** - dicek ke database (`SELECT COUNT(*)`) sebelum `INSERT`. Saat update, ID juga tidak boleh sama dengan ID baris lain.

### Fitur tambahan

- **Confirmation prompt** sebelum data dihapus (`JOptionPane.showConfirmDialog`, pilihan Yes/No).
- **Validasi input**: Tahun Lahir harus angka (1900 sampai tahun ini) dan Email harus berformat valid.
- **Pesan error koneksi**: jika MySQL belum menyala, muncul dialog penjelasan lalu program berhenti dengan rapi.
- **Tampilan rapi**: form dibagi menjadi panel "Form Data" (2 kolom), baris tombol, dan panel "Daftar Orang"; tabel memakai header berwarna, baris belang-belang, dan lebar kolom yang diatur.
- **`PreparedStatement`**: query memakai tanda `?` sehingga aman dari SQL injection dan tidak error saat input mengandung tanda kutip (mis. nama `O'Brien`).

## ✨ Alur Program

1. **Start** - `PeopleMenu()` membuat objek `Database` (koneksi ke `jdbc:mysql://localhost:3306/db_people`). Jika koneksi gagal, muncul dialog error lalu program berhenti.
2. **Read** - `refreshTable()` memanggil `setTable()`, yang menjalankan `SELECT * FROM person ORDER BY id` dan mengisi `JTable` dari `ResultSet`.
3. **Create** - user mengisi form lalu menekan **Add**. Karena `selectedIndex == -1`, program memanggil `insertData()`: `validateForm()` (kolom kosong dan format) → cek ID duplikat → `INSERT INTO person ...` → tabel diperbarui → form dikosongkan.
4. **Pilih baris** - saat sebuah baris diklik, `selectedIndex` diisi nomor baris, ID asli disimpan di `selectedId`, form terisi data baris tersebut, tombol berubah menjadi **Update**, dan tombol **Delete** muncul.
5. **Update** - user mengubah isi form lalu menekan **Update**. Karena `selectedIndex != -1`, program memanggil `updateData()`: `validateForm()` → cek ID tidak bentrok dengan baris lain → `UPDATE person SET ... WHERE id = <ID asli>` → tabel diperbarui → form dikosongkan.
6. **Delete** - user menekan **Delete**. Muncul dialog konfirmasi; jika memilih **Yes**, `deleteData()` menjalankan `DELETE FROM person WHERE id = ?` dan tabel diperbarui. Jika memilih **No**, tidak ada yang terjadi.
7. **Cancel** - `clearForm()` mengosongkan form, mengembalikan tombol ke **Add**, menyembunyikan **Delete**, dan mengatur `selectedIndex = -1`.

## 📁 Struktur Folder

```
TP5DPBO2526C.../
├── PersonData/
│   ├── Database.java
│   ├── Person.java
│   ├── PeopleMenu.java
│   └── PeopleMenu.form
├── lib/
│   └── mysql-connector-j-27.7.0.jar
├── Dokumentasi/
│   └── (Video)
├── db_people.sql
└── README.md
```

## 📷 Dokumentasi Saat Program Dijalankan

### 1. Ada Kolom yang Kosong
[Kolom Form Kosong](Dokumentasi/Form%20Kolom%20Masih%20Kosong.mp4)

### 2. Tahun Lahir Bukan Angka
[Tahun Lahir Bukan Angka](Dokumentasi/Tahun%20Lahir%20Bukan%20Angka.mp4)

### 3. Email Tidak Valid
[Email Tidak Valid](Dokumentasi/Email%20Tidak%20Valid.mp4)

### 4. ID Sudah Digunakan (Duplikat)
[ID Duplikat](Dokumentasi/ID%20Sudah%20Digunakan.mp4)

### 5. Create - menambah Data (Terhubung Ke Database)
[Create](Dokumentasi/Create-TerhubungKeDatabase.mp4)

### 6. Update - mengubah Data (Terhubung Ke Database)
[Update](Dokumentasi/Update-TerhubungKeDatabase.mp4)

### 7. Delete - Menghapus Data (Terhubung Ke Database)
[Delete](Dokumentasi/Delete-TerhubungKeDatabase.mp4)





