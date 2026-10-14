import javax.swing.*;                               // Mengimpor komponen GUI Swing
import javax.swing.table.DefaultTableCellRenderer;  // Mengimpor renderer untuk kustomisasi sel tabel
import javax.swing.table.DefaultTableModel;         // Mengimpor model data untuk JTable
import javax.swing.table.JTableHeader;              // Mengimpor komponen header tabel
import java.awt.*;                                  // Mengimpor kelas AWT untuk komponen grafis dan warna
import java.awt.event.ActionEvent;                  // Mengimpor kelas penanganan event tombol
import java.awt.event.ActionListener;               // Mengimpor interface pendengar event tindakan
import java.awt.event.MouseAdapter;                 // Mengimpor kelas adaptor untuk event mouse
import java.awt.event.MouseEvent;                   // Mengimpor kelas penampung informasi event mouse
import java.sql.PreparedStatement;                  // Mengimpor kelas query berparameter
import java.sql.ResultSet;                          // Mengimpor kelas penampung hasil query SELECT
import java.sql.SQLException;                       // Mengimpor kelas error database
import java.time.Year;                              // Mengimpor kelas untuk mengambil tahun saat ini

public class PeopleMenu extends JFrame {
    // component GUI utama
    private JPanel mainPanel;                       // Panel utama penampung seluruh elemen GUI
    private JLabel titleLabel;                      // Label untuk judul aplikasi
    private JTextField idField;                     // Field input untuk ID person
    private JTextField namaField;                   // Field input untuk nama person
    private JTextField tahunLahirField;             // Field input untuk tahun lahir person
    private JTextField emailField;                  // Field input untuk alamat email person
    private JComboBox<String> kategoriComboBox;     // Dropdown pilihan kategori person
    private JComboBox<String> golonganDarahComboBox;// Dropdown pilihan golongan darah person
    private JRadioButton lakiLakiRadioButton;       // Radio button pilihan jenis kelamin Laki-Laki
    private JRadioButton perempuanRadioButton;      // Radio button pilihan jenis kelamin Perempuan
    private ButtonGroup jenisKelaminGroup;          // Grup untuk mengelompokkan radio button jenis kelamin
    private JButton addUpdateButton;                // Tombol untuk menambah atau memperbarui data
    private JButton cancelButton;                   // Tombol untuk membatalkan aksi/reset form
    private JButton deleteButton;                   // Tombol untuk menghapus data terpilih
    private JTable peopleTable;                     // Komponen tabel penampil daftar person

    // data
    private Database database;                      // Objek koneksi ke database MySQL (pengganti penyimpanan data di memori)
    private int selectedIndex = -1;                 // -1 = tidak ada baris yang dipilih
    private String selectedId = null;               // ID asli baris yang dipilih (dipakai di WHERE saat update/delete)

    public static void main(String[] args) {
        // buat object window
        PeopleMenu menu = new PeopleMenu();

        // atur ukuran window
        menu.setSize(900, 650);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // constructor
    public PeopleMenu() {
        // buat koneksi ke database; jika gagal, tampilkan pesan lalu hentikan program
        try {
            database = new Database();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat terhubung ke database!\n"
                            + "Pastikan MySQL sudah berjalan, database 'db_people' sudah di-import,\n"
                            + "dan MySQL Connector (.jar) sudah ditambahkan ke project.\n\n"
                            + "Detail: " + ex.getMessage(),
                    "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // isi tabel dari database
        refreshTable();

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        // atur isi combo box kategori
        String[] kategoriData = { "???", "Mahasiswa", "Dosen", "Staf", "Alumni" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // atur isi combo box golongan darah (atribut baru)
        String[] golDarahData = { "???", "A", "B", "AB", "O" };
        golonganDarahComboBox.setModel(new DefaultComboBoxModel<>(golDarahData));

        // kelompokkan radio button jenis kelamin
        jenisKelaminGroup = new ButtonGroup();
        jenisKelaminGroup.add(lakiLakiRadioButton);
        jenisKelaminGroup.add(perempuanRadioButton);

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan -> minta konfirmasi dulu
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pilihan = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah kamu yakin ingin menghapus data ini?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // hapus hanya jika user memilih "Yes"
                if (pilihan == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan
        // (nilai diambil langsung dari tabel)
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = peopleTable.getSelectedRow();

                // abaikan jika klik di area kosong tabel
                if (selectedIndex == -1) {
                    return;
                }

                // simpan value (kolom 0 = "No", data dimulai dari kolom 1)
                String curId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curTahun = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();
                String curGolDarah = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();
                String curJenisKelamin = peopleTable.getModel().getValueAt(selectedIndex, 6).toString();
                String curEmail = peopleTable.getModel().getValueAt(selectedIndex, 7).toString();

                // simpan ID asli baris ini (ID boleh diubah saat update, jadi perlu disimpan)
                selectedId = curId;

                // ubah isi textfield dan combo box
                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                golonganDarahComboBox.setSelectedItem(curGolDarah);
                emailField.setText(curEmail);
                if (curJenisKelamin.equals("Laki-Laki")) {
                    lakiLakiRadioButton.setSelected(true);
                } else {
                    perempuanRadioButton.setSelected(true);
                }

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    // mengisi tabel dari database (SELECT)
    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Gol. Darah", "Jenis Kelamin", "Email" };

        // buat objek tabel (sel tidak bisa diedit langsung)
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (ResultSet resultSet = database.selectQuery("SELECT * FROM person ORDER BY id")) {
            // isi tabel dengan hasil query
            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[8];
                row[0] = ++i;                                       // nomor urut
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("thn_lahir");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("golongan_darah");
                row[6] = resultSet.getString("jenis_kelamin");
                row[7] = resultSet.getString("email");
                tmp.addRow(row);
            }
        } catch (SQLException e) {
            showDatabaseError("memuat data", e);
        }

        return tmp;
    }

    // memuat ulang isi tabel + menerapkan styling
    // (styling harus diterapkan ulang karena setModel() membuat ulang kolom tabel)
    private void refreshTable() {
        peopleTable.setModel(setTable());
        styleTable();
    }

    // mempercantik tampilan tabel
    private void styleTable() {
        peopleTable.setRowHeight(28);
        peopleTable.setShowVerticalLines(false);
        peopleTable.setShowHorizontalLines(true);
        peopleTable.setGridColor(new Color(225, 228, 232));
        peopleTable.setIntercellSpacing(new Dimension(0, 1));
        peopleTable.setSelectionBackground(new Color(190, 215, 245));
        peopleTable.setSelectionForeground(Color.BLACK);
        peopleTable.setFillsViewportHeight(true);
        peopleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // header: latar gelap, teks putih tebal di tengah
        JTableHeader header = peopleTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setHorizontalAlignment(CENTER);
                setOpaque(true);
                setBackground(new Color(44, 62, 80));
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // isi sel: baris belang-belang, kolom Nama, Kategori & Email rata kiri, sisanya di tengah
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setHorizontalAlignment((column == 2 || column == 4 || column == 7) ? LEFT : CENTER);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };

        // lebar kolom: No, ID, Nama, Tahun Lahir, Kategori, Gol. Darah, Jenis Kelamin, Email
        int[] widths = { 45, 65, 140, 80, 95, 75, 110, 180 };
        for (int i = 0; i < widths.length; i++) {
            peopleTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            peopleTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        peopleTable.getColumnModel().getColumn(0).setMaxWidth(60);
    }

    // Metode untuk memeriksa apakah ID sudah ada di database
    // (saat update, ID milik baris yang sedang diedit tidak dianggap duplikat)
    private boolean isIdExists(String id) throws SQLException {
        String sql = (selectedId == null)
                ? "SELECT COUNT(*) FROM person WHERE id = ?"
                : "SELECT COUNT(*) FROM person WHERE id = ? AND id <> ?";

        try (PreparedStatement ps = database.prepareStatement(sql)) {
            ps.setString(1, id);
            if (selectedId != null) {
                ps.setString(2, selectedId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;                            // lebih dari 0 berarti ID sudah ada
            }
        }
    }

    // validasi input form; return true jika semua valid
    private boolean validateForm() {
        // 1. cek semua kolom input, kumpulkan nama kolom yang masih kosong
        StringBuilder kosong = new StringBuilder();
        if (idField.getText().trim().isEmpty()) kosong.append("\n- ID");
        if (namaField.getText().trim().isEmpty()) kosong.append("\n- Nama");
        if (tahunLahirField.getText().trim().isEmpty()) kosong.append("\n- Tahun Lahir");
        if (kategoriComboBox.getSelectedIndex() <= 0) kosong.append("\n- Kategori");
        if (golonganDarahComboBox.getSelectedIndex() <= 0) kosong.append("\n- Golongan Darah");
        if (!lakiLakiRadioButton.isSelected() && !perempuanRadioButton.isSelected()) {
            kosong.append("\n- Jenis Kelamin");
        }
        if (emailField.getText().trim().isEmpty()) kosong.append("\n- Email");

        if (kosong.length() > 0) {
            JOptionPane.showMessageDialog(null, "Kolom berikut masih kosong:" + kosong,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. tahun lahir harus angka dan masuk akal
        try {
            int tahun = Integer.parseInt(tahunLahirField.getText().trim());
            int tahunIni = Year.now().getValue();
            if (tahun < 1900 || tahun > tahunIni) {
                JOptionPane.showMessageDialog(null,
                        "Tahun lahir harus antara 1900 dan " + tahunIni + "!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. format email sederhana: nama@domain.tld
        if (!emailField.getText().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            JOptionPane.showMessageDialog(null, "Format email tidak valid!\nContoh: nama@domain.com", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // membaca isi form menjadi objek Person (dipanggil setelah validateForm() lolos)
    private Person getPersonFromForm() {
        return new Person(
                idField.getText().trim(),
                namaField.getText().trim(),
                Integer.parseInt(tahunLahirField.getText().trim()),
                kategoriComboBox.getSelectedItem().toString(),
                golonganDarahComboBox.getSelectedItem().toString(),
                lakiLakiRadioButton.isSelected() ? "Laki-Laki" : "Perempuan",
                emailField.getText().trim());
    }

    // menampilkan dialog error saat operasi database gagal
    private void showDatabaseError(String aksi, SQLException e) {
        JOptionPane.showMessageDialog(null, "Gagal " + aksi + ":\n" + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
    }

    // CREATE (INSERT)
    public void insertData() {
        // tampilkan error jika ada kolom kosong / format salah
        if (!validateForm()) {
            return;
        }

        // ambil value dari form
        Person person = getPersonFromForm();

        try {
            // tampilkan error jika ID sudah ada di database
            if (isIdExists(person.getId())) {
                JOptionPane.showMessageDialog(null,
                        "ID '" + person.getId() + "' sudah digunakan! Gunakan ID lain.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // tambahkan data ke database
            String sqlQuery = "INSERT INTO person "
                    + "(id, nama, thn_lahir, kategori, golongan_darah, jenis_kelamin, email) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = database.prepareStatement(sqlQuery)) {
                ps.setString(1, person.getId());
                ps.setString(2, person.getNama());
                ps.setInt(3, person.getTahunLahir());
                ps.setString(4, person.getKategori());
                ps.setString(5, person.getGolonganDarah());
                ps.setString(6, person.getJenisKelamin());
                ps.setString(7, person.getEmail());
                ps.executeUpdate();
            }

            // update tabel
            refreshTable();

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (SQLException ex) {
            showDatabaseError("menambahkan data", ex);
        }
    }

    // UPDATE
    public void updateData() {
        // tampilkan error jika ada kolom kosong / format salah
        if (!validateForm()) {
            return;
        }

        // ambil data dari form
        Person person = getPersonFromForm();

        try {
            // ID boleh diubah, tetapi tidak boleh sama dengan ID baris lain
            if (isIdExists(person.getId())) {
                JOptionPane.showMessageDialog(null,
                        "ID '" + person.getId() + "' sudah digunakan! Gunakan ID lain.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ubah data person di database (baris dicari berdasarkan ID asli)
            String sqlQuery = "UPDATE person SET id = ?, nama = ?, thn_lahir = ?, kategori = ?, "
                    + "golongan_darah = ?, jenis_kelamin = ?, email = ? WHERE id = ?";
            int terubah;
            try (PreparedStatement ps = database.prepareStatement(sqlQuery)) {
                ps.setString(1, person.getId());
                ps.setString(2, person.getNama());
                ps.setInt(3, person.getTahunLahir());
                ps.setString(4, person.getKategori());
                ps.setString(5, person.getGolonganDarah());
                ps.setString(6, person.getJenisKelamin());
                ps.setString(7, person.getEmail());
                ps.setString(8, selectedId);
                terubah = ps.executeUpdate();
            }

            // update tabel
            refreshTable();

            // jika tidak ada baris yang berubah, data kemungkinan sudah dihapus dari database
            if (terubah == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan di database!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                clearForm();
                return;
            }

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (SQLException ex) {
            showDatabaseError("mengubah data", ex);
        }
    }

    // DELETE
    public void deleteData() {
        try {
            // hapus data dari database
            String sqlQuery = "DELETE FROM person WHERE id = ?";
            try (PreparedStatement ps = database.prepareStatement(sqlQuery)) {
                ps.setString(1, selectedId);
                ps.executeUpdate();
            }

            // update tabel
            refreshTable();

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Delete berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
        } catch (SQLException ex) {
            showDatabaseError("menghapus data", ex);
        }
    }

    // mengosongkan form dan mengembalikan ke mode "Add"
    public void clearForm() {
        // kosongkan semua textfield dan combo box
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        emailField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        golonganDarahComboBox.setSelectedIndex(0);
        jenisKelaminGroup.clearSelection();

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
        selectedId = null;

        // hilangkan highlight baris pada tabel
        peopleTable.clearSelection();
    }
}