package conjugaison;
/*
*Classe : MSAccessBase
*@author : Chatbour
*@date : 21 Octobre 2008
*/

import java.sql.*;

public class MysqlConnexion {

    // l'urle chemin de la base
    private String BaseDeDonnees;

    // Nom d'utilisateur
    private String user;

    // Mot de passe
    private String password;

    // Connection vers la base
    private Connection connection;


    /* Constructeur */
    public MysqlConnexion(String BaseDeDonnees, String user, String password) {
        this.BaseDeDonnees = BaseDeDonnees;
        this.user = user;
        this.password = password;
    }


    /*
    *Connection � la base
    *@return : true si la connexion est r�ussie, false si �chou�e
    */
    public boolean connect() {
        try {
            // Chargement du driver ODBC
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Pilote etabli sans erreur");
           // Connexion � la base
            String connectionString = "jdbc:mysql://localhost:3306/"+BaseDeDonnees+"?useUnicode=true&;characterEncoding=ISO-8859-1";
            connection = DriverManager.getConnection(connectionString, user, password);
            System.out.println("Connexion etabli");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Probleme avec le driver ODBC");
            return false;
        }
        catch (SQLException e) {
            System.out.println("Impossible de se connecter � la base");
            return false;
        }
        return true;
    }


    /*
    *D�connexion de la base
    *@return : true si la d�connexion est r�ussie, false sinon
    */
    public boolean disconnect() {
        try {
            connection.close();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    /*
    *Envoi d'une requ�te de s�lection
    *@param : sql
    *@return : result
    */
    public ResultSet SQLSelect(String sql) throws SQLException {
        Statement statement = null;
        ResultSet result = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
            System.out.println("kko");
            return result;
            
        }
        catch (SQLException e) {
            result.close();
            statement.close();
            return null;
        }
    }

    /*
    *Envoi d'une requ�te de mise � jour (insert, update, delete)
    *@param : sql
    */
    public void SQLUpdate(String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.out.println("ee");
        }
    }
}