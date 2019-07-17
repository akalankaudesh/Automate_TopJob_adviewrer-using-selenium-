import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbcon {

    private static Connection con;

public static Connection getCon() throws SQLException {
    if (con==null){
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/adviewdb","root","1234");
    }
    return con;
}

}
