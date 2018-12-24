/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpServer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import static javax.swing.filechooser.FileSystemView.getFileSystemView;
import sikke.cli.defs.User;
import sikke.cli.helpers.Helpers;
import sikke.cli.helpers._System;
import static sikke.cli.helpers._System.helper;

public class SikkeCli {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();

	public static void createNewDatabase(String fileName) {

		String url = "jdbc:sqlite:" + fileName;

		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws IOException, Exception, FileNotFoundException {
		String ErrMsg = "";
		system.initApp();
		try {
			int port = Integer.parseInt(system.getConf("rpcport")) > 999 ? Integer.parseInt(system.getConf("rpcport"))
					: -1;
			String sikke_server = system.getConf("server");
			String rpc_user = system.getConf("rpcuser");
			String rpc_pw = system.getConf("rpcpassword");

			ErrMsg = sikke_server.equals("-") ? "server required!\n" : "";
			ErrMsg += rpc_user.equals("-") ? "rpcuser required!\n" : "";
			ErrMsg += rpc_pw.equals("-") ? "rpcpassword required!\n" : "";
			ErrMsg += port < 1000 ? "rpcpassword required!\n" : "";

			if (ErrMsg.isEmpty()) {
				HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
				System.out.println("Sikke Server Started @ " + port);
				server.createContext("/", new EchoPostHandler());
				server.createContext("/newTransaction", new EchoTransactionHandler());
				server.createContext("/echoHeader", new EchoHeaderHandler());
				server.createContext("/echoGet", new EchoGetHandler());
				server.createContext("/echoPost", new EchoPostHandler());
				server.setExecutor(null);
				server.start();
			} else {
				System.err.println(ErrMsg + "Please check your conf file > " + system.getPath() + "sikke.conf");
			}
		} catch (FileNotFoundException e) {
			System.err.println("Please try again.");
		}
	}
}

class test {

	String status;
	String wallet;
}

class Bean {
	String status;
	Body wallet;
}

class Body {
	String address;

}