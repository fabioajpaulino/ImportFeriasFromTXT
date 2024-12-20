package br.com.tresclicksrh.bencorp_integrations.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    public static String status = "Não conectou...";

    public DbConnect() {
    }

    public Connection getConn(String ambiente) {

        try {
            String driverName = "org.postgresql.Driver";

            //DEV
            String serverName = "database-development-instance-1.cjce80wwgnwp.us-east-1.rds.amazonaws.com";
            String mydatabase = "three_clicks_rh_api_development";

            //PROD
            if (ambiente.toLowerCase().equals("prod")) {
                serverName = "database-production.cluster-cjce80wwgnwp.us-east-1.rds.amazonaws.com";
                mydatabase = "three_clicks_rh_api_production";
            }

            String url = "jdbc:postgresql://" + serverName + "/" + mydatabase;
            String username = "postgres";        //nome de um usuário de seu BD
            String password = "3clicksrhdb";      //sua senha de acesso

            //System.out.println("[DbConnect] -> " + url + "," + username + "," + password);

            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(url, username, password);

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
