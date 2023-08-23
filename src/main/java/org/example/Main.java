package org.example;

import java.sql.*;
import static spark.Spark.*;
public class Main {

    private static final String connectionUrl = "jdbc:mysql://localhost:3307/mysql";
    private static final String username = "root";
    private static final String password = "topsecretpassword";
    private static Connection connection;
    public static void main(String[] args) {

        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        get("/dolls", (req, res) -> { getAllDolls(); return "Done";});
        post("/dolls/:nume/:pret/:stoc", (req, res) -> {
            insertDoll(req.params(":nume"), Double.parseDouble(req.params(":pret")), Integer.parseInt(req.params(":stoc")));
            return "Done insert";
        });
        delete("/dolls/:id", (req, res) -> {
            deleteDoll(Integer.parseInt(req.params(":id")));
            return "Done delete";
        });
        post("/dolls/:id/:nume/:pret/:stoc", (req, res) -> {
            updateDoll(Integer.parseInt(req.params(":id")), req.params(":nume"), Double.parseDouble(req.params(":pret")), Integer.parseInt(req.params(":stoc")));
            return "Done update";
        });
        get("/dolls/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Doll doll = getDoll(id);
            if (doll != null) {
                return doll.toString();
            } else {
                return "Doll not found";
            }
        });
    }

    public static void getAllDolls() throws SQLException {
        Statement ps = connection.createStatement();
        ResultSet rs = ps.executeQuery("SELECT * FROM `Doll`");
        while (rs.next()) {
            Doll d = new Doll(rs.getString("nume"), rs.getDouble("pret"), rs.getInt("stoc"));
            System.out.println(d);
        }
    }

    private static void insertDoll(String nume, Double pret, int stoc) throws SQLException {
        try {
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO `Doll` (`nume`, `pret`, `stoc`) VALUES ( ?, ?, ?);");
            ps2.setString(1, nume);
            ps2.setDouble(2, pret);
            ps2.setInt(3, stoc);
            ps2.execute();
        } catch (SQLException e) {
            System.out.println("Error while inserting Doll: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteDoll(int id) throws SQLException {
        PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `Doll` WHERE id = ? ;");
        ps2.setInt(1, id);
        ps2.execute();
    }

    private static void updateDoll(int id, String nume, Double pret, int stoc) throws SQLException {
        PreparedStatement ps2 = connection.prepareStatement("UPDATE `Doll` SET `nume` = ? , `pret` = ? , `stoc` = ? WHERE id = ? ;");

        ps2.setString(1, nume);
        ps2.setDouble(2, pret);
        ps2.setInt(3, stoc);
        ps2.setInt(4, id);

        ps2.execute();
    }

    private static Doll getDoll(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Doll` WHERE id = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String nume = rs.getString("nume");
            double pret = rs.getDouble("pret");
            int stoc = rs.getInt("stoc");

            Doll doll = new Doll(nume, pret, stoc);
            System.out.println(doll);
            return doll;
        } else {
            return null;
        }
    }
}