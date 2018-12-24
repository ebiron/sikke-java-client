/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import static javax.swing.filechooser.FileSystemView.getFileSystemView;
import static sikke.cli.SikkeCli.helper;
import sikke.cli.defs.User;
import sikke.cli.wallet.AES256Cipher;
import sikke.cli.wallet.AppHelper;
import sikke.cli.wallet.SikkeConstant;
import sikke.cli.wallet.WalletKey;

/**
 *
 * @author selim
 */
public class _System {

	public static Helpers helper = new Helpers();
	public static _System system = new _System();

	public void initApp() throws FileNotFoundException, UnsupportedEncodingException, Exception {
		initFolder();
	}

	public String getOS(String property) {
		return System.getProperty(property);
	}

	private Connection connect() {
		// SQLite connection string
		String url = system.getDB();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	private void initFolder() throws FileNotFoundException, Exception {
		String os = getOS("os.name");
		String path = null;
		String ErrMsg = null;
		if (os.contains("Linux")) {
			path = getFileSystemView().getHomeDirectory().toString() + "/.sikke";
		} else if (os.contains("Windows")) {
			path = System.getenv("APPDATA") + "\\Sikke";
		}

		if (ErrMsg == null) {
			File f = new File(path);
			if (!f.exists()) { // && f.isDirectory()
				File createDir1 = new File(path);
				createDir1.mkdir();
				System.out.println("App folder created at > " + path);
				createDefaultConf();
				createTables();
				initUser();
			}
		} else {
			// err dön
		}
	}

	private void initUser() throws Exception {
		Gson g = new Gson();
		System.out.print("Sikke User Email : ");
		Scanner in = new Scanner(System.in);
		String email = in.nextLine();
		System.out.print("Password : ");
		in = new Scanner(System.in);
		String password = in.nextLine();
		String post_query = "grant_type=password&password=" + password + "&username=" + email;

		String response = helper.sendPost("/v1/oauth/token", post_query, null);
		System.err.println(response);
		User res = g.fromJson(response.toString(), User.class);

		if (res.status.equals("success")) {
			Methods methods = new Methods();
			String sql = "INSERT "
					+ "INTO system_user (user_id,access_token,alias_name,email,expires_in,name,refresh_token,rt_expires_in,surname,token_type,crypt_key,crypt_iv,encrypted_password)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);

				byte[] pin_byte = AES256Cipher.getRandomAesCryptKey();
				byte[] iv_byte = AES256Cipher.getRandomAesCryptIv();
				String pin_hex = AppHelper.toHexString(pin_byte);
				String iv_hex = AppHelper.toHexString(iv_byte);
				String password_encrypt = AES256Cipher.encrypt(pin_byte, iv_byte, password);

				pstmt.setString(1, res.user_id);
				pstmt.setString(2, res.access_token);
				pstmt.setString(3, res.alias_name);
				pstmt.setString(4, res.email);
				pstmt.setInt(5, res.expires_in);
				pstmt.setString(6, res.name);
				pstmt.setString(7, res.refresh_token);
				pstmt.setInt(8, res.rt_expires_in);
				pstmt.setString(9, res.surname);
				pstmt.setString(10, res.token_type);
				pstmt.setString(11, pin_hex);
				pstmt.setString(12, iv_hex);
				pstmt.setString(13, password_encrypt);
				pstmt.executeUpdate();

				WalletKey walletKey = WalletKey.getWalletKeys();
				sql = "INSERT INTO wallets (address,email,label,private_key,public_key,is_default) VALUES(?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, walletKey.getAddress());
				pstmt.setString(2, res.email);
				pstmt.setString(3, "");
				pstmt.setString(4, walletKey.getPrivateKey());
				pstmt.setString(5, walletKey.getPublicKey());
				pstmt.setInt(6, 1);
				pstmt.executeUpdate();
				System.err.println(
						"Default wallet created : \n Wallet address : " + walletKey.getAddress() + "\n Public key : "
								+ walletKey.getPublicKey() + "\n Private key : " + walletKey.getPrivateKey());

			} catch (SQLException e) {
				System.err.println("");
				File dir = new File(getPath());
				File[] listFiles = dir.listFiles();
				for (File file : listFiles) {
					file.delete();
				}
				dir.delete();
			}
		} else {
			System.err.println("");
			File dir = new File(getPath());
			File[] listFiles = dir.listFiles();
			for (File file : listFiles) {
				file.delete();
			}
			dir.delete();
		}
	}

	private void createDefaultConf() throws FileNotFoundException, UnsupportedEncodingException {

		String conf = getPath() + "sikke.conf";
		File f = new File(conf);
		if (!f.exists()) { // && f.isDirectory()
			try (PrintWriter writer = new PrintWriter(conf, "UTF-8")) {
				writer.println("server=1");
				writer.println("rpcuser=default_user");
				writer.println("rpcpassword=default_password");
				writer.println("rpcport=4319");
				writer.println("rpallowip=*");
				writer.close();
			}
			createNewDatabase(getPath() + "wallets.dat");
		}
	}

	public String getPath() {
		String os = getOS("os.name");
		String path = null;
		if (os.contains("Linux")) {
			path = getFileSystemView().getHomeDirectory().toString();
		} else if (os.contains("Windows")) {
			path = System.getenv("APPDATA");
		}
		String conf_file = os.contains("Linux") ? "/.sikke/" : "\\Sikke\\";

		return path + conf_file;
	}

	public String getDB() {
		return "jdbc:sqlite:" + getPath() + "wallets.dat";
	}

	public String getIP() throws IOException {
		String port = getConf("rpcport");
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String ip = in.readLine();
		return ip;
	}

	public String getCallbackURL() throws IOException {
		String port = getConf("rpcport");
		return getIP() + ":" + port + "/newTransaction";
	}

	public static void createNewDatabase(String fileName) {
		String url = "jdbc:sqlite:" + fileName;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(url);
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
			}
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void createTables() throws Exception {
		String system_user = "CREATE TABLE IF NOT EXISTS system_user (\n"
				+ "    id                 INTEGER PRIMARY KEY,\n" + "    user_id            TEXT    NOT NULL,\n"
				+ "    access_token       TEXT    NOT NULL,\n" + "    alias_name         TEXT    NOT NULL,\n"
				+ "    email              TEXT    NOT NULL,\n" + "    expires_in         INT     NOT NULL,\n"
				+ "    name               TEXT    NOT NULL,\n" + "    refresh_token      TEXT    NOT NULL,\n"
				+ "    rt_expires_in      INT     NOT NULL,\n" + "    surname            TEXT    NOT NULL,\n"
				+ "    token_type         TEXT    NOT NULL,\n" + "    capacity           REAL,\n"
				+ "    crypt_key          TEXT,\n" + "    crypt_iv           TEXT,\n" + "    encrypted_password TEXT\n"
				+ ");";

		String tx = "CREATE TABLE IF NOT EXISTS tx (\n" + "    id              INTEGER PRIMARY KEY,\n"
				+ "    _id             INTEGER UNIQUE,\n" + "    seq             INTEGER,\n"
				+ "    amount          TEXT,\n" + "    fee             TEXT,\n" + "    fee_asset       STRING,\n"
				+ "    hash            TEXT,\n" + "    prev_hash       TEXT,\n" + "    nonce           TEXT,\n"
				+ "    _from           TEXT,\n" + "    _to             TEXT,\n" + "    asset           TEXT,\n"
				+ "    action_time     TEXT,\n" + "    completion_time TEXT,\n" + "    confirm_rate    INTEGER,\n"
				+ "    [desc]          TEXT,\n" + "    [group]         STRING,\n" + "    status          INTEGER,\n"
				+ "    type            INTEGER,\n" + "    subtype         INTEGER\n" + ")";

		String wallets = "CREATE TABLE IF NOT EXISTS wallets (id integer PRIMARY KEY, address text, email text, label text, private_key text, public_key text, limit_hourly text, limit_daily text, limit_max_amount text, is_default integer);";

		String outdatedWallet = "CREATE TABLE IF NOT EXISTS outdated_wallet (\n"
				+ "    id          INTEGER  PRIMARY KEY AUTOINCREMENT\n" + "                         NOT NULL,\n"
				+ "    address     STRING   NOT NULL,\n" + "    insert_date DATETIME DEFAULT (CURRENT_DATE) \n"
				+ "                         NOT NULL\n" + ")";

		helper.createTable(system_user);
		helper.createTable(tx);
		helper.createTable(wallets);
		helper.createTable(outdatedWallet);
	}

	public String getConf(String param) throws FileNotFoundException, IOException {
		String conf = getPath() + "sikke.conf";
		FileInputStream fstream = new FileInputStream(conf);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
			String strLine;
			String value = null;
			while ((strLine = br.readLine()) != null) {
				String[] flag = strLine.split("=");

				if (flag[0].equals(param)) {
					value = flag[1];
				}
			}
			return value != null ? value : "-";
		}
	}
}
