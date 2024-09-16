package br.com.tresclicksrh.bencorp_integrations.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    public static String status = "Não conectou...";

    public DbConnect() {
    }

    public Connection getConn(Connection connection) {

        try {
            String driverName = "org.postgresql.Driver";

            String serverName = "database-development-instance-1.cjce80wwgnwp.us-east-1.rds.amazonaws.com";    //caminho do servidor do BD
            String mydatabase = "three_clicks_rh_api_development";        //nome do seu banco de dados
            String url = "jdbc:postgresql://" + serverName + "/" + mydatabase;
            String username = "postgres";        //nome de um usuário de seu BD
            String password = "3clicksrhdb";      //sua senha de acesso

            //System.out.println("[DbConnect] -> " + url + "," + username + "," + password);

            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                status = ("STATUS--->Conectado com sucesso!");
            } else {
                status = ("STATUS--->Não foi possivel realizar conexão");
            }

            //System.out.println("[DbConnect] -> Conn Status: " + status);
            return connection;

        } catch (ClassNotFoundException e) {  //Driver não encontrado
           // System.out.println("[DbConnect] -> O driver especificado nao foi encontrado.");
            return null;
        } catch (SQLException e) {
            //System.out.println("[DbConnect] -> Nao foi possível conectar ao Banco de Dados.");
            return null;
        }

    }

    public static String statusConection() {
        return status;
    }

}
