public class Person {
    private String id;                      // Menyimpan ID unik person
    private String nama;                    // Menyimpan nama lengkap person
    private int tahunLahir;                 // Menyimpan tahun kelahiran person
    private String kategori;                // Menyimpan kategori person
    private String golonganDarah;           // Menyimpan opsi golongan darah dari (JComboBox)
    private String jenisKelamin;            // Menyimpan jenis kelamin person (JRadioButton)
    private String email;                   // Menyimpan email person (JTextField)

    // Konstruktor tanpa parameter
    public Person() {
    }

    // Konstruktor dengan parameter
    public Person(String id, String nama, int tahunLahir, String kategori, String golonganDarah,String jenisKelamin, String email) {
        this.id = id;                       // Mengisi atribut id dengan nilai parameter id
        this.nama = nama;                   // Mengisi atribut nama dengan nilai parameter nama
        this.tahunLahir = tahunLahir;       // Mengisi atribut tahunLahir dengan nilai parameter tahunLahir
        this.kategori = kategori;           // Mengisi atribut kategori dengan nilai parameter kategori
        this.golonganDarah = golonganDarah; // Mengisi atribut golonganDarah dengan nilai parameter golonganDarah
        this.jenisKelamin = jenisKelamin;   // Mengisi atribut jenisKelamin dengan nilai paraneter jenisKelamin
        this.email = email;                 // Mengisi atribut email dengan nilai paraneter email
    }

    // Getter dan Setter untuk id
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Getter dan Setter untuk nama
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    // Getter dan Setter untuk tahun lahir
    public int getTahunLahir() { return tahunLahir; }
    public void setTahunLahir(int tahunLahir) { this.tahunLahir = tahunLahir; }

    // Getter dan Setter untuk kategori
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    // Getter dan Setter untuk golongan darah
    public String getGolonganDarah() { return golonganDarah; }
    public void setGolonganDarah(String golonganDarah) { this.golonganDarah = golonganDarah; }

    // Getter dan Setter untuk jenis kelamin
    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
 
    // Getter dan Setter untuk email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}