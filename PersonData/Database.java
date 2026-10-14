import java.sql.*;                                      // Mengimpor semua kelas JDBC (Connection, Statement, ResultSet, dll)

public class Database {
    private Connection connection;                      // Objek koneksi ke database MySQL
    private Statement statement;                        // Objek untuk menjalankan query SQL

    // constructor: membuka koneksi ke database db_people
    public Database() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db_people",// alamat database
                    "root",                                     // username MySQL
                    "");                                        // password MySQL (kosong)
            statement = connection.createStatement();           // buat statement dari koneksi
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk SELECT
    public ResultSet selectQuery(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk UPDATE, INSERT, DELETE
    public int insertUpdateDeleteQuery(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // tambahan: query dengan parameter (tanda "?") agar aman dari SQL injection
    // dan tidak error saat input mengandung tanda kutip, mis. nama "O'Brien"
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    // getter
    public Statement getStatement() {
        return statement;
    }
}
