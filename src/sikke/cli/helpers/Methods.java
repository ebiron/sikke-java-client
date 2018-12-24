/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.awt.JobAttributes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;

import sikke.cli.defs.TxResponse;
import sikke.cli.defs.User;
import sikke.cli.defs.WalletResponse;
import sikke.cli.defs.sikkeApi;
import sikke.cli.defs.tx;
import sikke.cli.defs.wallet;
import sikke.cli.wallet.AES256Cipher;
import sikke.cli.wallet.AppHelper;
import sikke.cli.wallet.Base58;
import sikke.cli.wallet.ECDSAHelper;
import sikke.cli.wallet.SikkeConstant;
import sikke.cli.wallet.WalletKey;

import static sikke.cli.helpers._System.helper;
import static sikke.cli.helpers._System.system;

/**
 *
 * @author selim
 */
public class Methods {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private Connection connect() {
		// SQLite connection string
		String url = system.getDB();
		Connection conn = null;
		try {
			// Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public JsonArray getTransactions(String[] params) throws Exception {
		String ErrMsg = "";
		int len = params.length;
		String whereClause = "";
		JsonArray array = new JsonArray();
		Connection conn = null;
		if (ErrMsg.isEmpty()) {
			if (len == 1 && params[0] != null && !params[0].isEmpty()) {
				String[] criterias = params[0].split(SikkeConstant.TX_QUERY_TYPE_SEPERATOR);
				if (criterias.length == 2) {
					String key = criterias[0].toUpperCase();
					String value = criterias[1];
					if (key.equals(SikkeConstant.TX_QUERY_TYPE_ADDRESS)) {
						whereClause = " where t._from = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_HASH)) {
						whereClause = " where t.hash = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_SEQUENCE)) {
						whereClause = " where t.seq = '" + value + "'";
					} else if (key.equals(SikkeConstant.TX_QUERY_TYPE_BLOCK)) {
						whereClause = " where t.block = '" + value + "'";
					} else {
						ErrMsg = "\n Unknown query type.Available queries; [address,hash,seq,block]:value";
						System.out.println("> " + ErrMsg);
					}
					if (ErrMsg.isEmpty()) {
						String sql = "SELECT * FROM tx t" + whereClause;
						try {
							conn = this.connect();
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sql);

							int i = 0;
							while (rs.next()) {
								JsonObject jo = new JsonObject();
								jo.addProperty("nonce", rs.getString("nonce"));
								jo.addProperty("hash", rs.getString("hash"));
								jo.addProperty("prev_hash", rs.getString("prev_hash"));
								jo.addProperty("amount", rs.getString("amount"));
								jo.addProperty("fee", rs.getString("fee"));
								jo.addProperty("asset", rs.getString("asset"));
								jo.addProperty("from", rs.getString("_from"));
								jo.addProperty("to", rs.getString("_to"));
								jo.addProperty("action_time", rs.getString("action_time"));
								jo.addProperty("seq", rs.getInt("seq"));
								jo.addProperty("desc", rs.getString("desc"));
								array.add(jo);
								i++;
							}
							System.out.println(array);
						} catch (SQLException e) {
							e.printStackTrace();
							System.out.println(e.getMessage());
						} finally {
							if (conn != null) {
								conn.close();
							}
						}
					}
				} else {
					ErrMsg += "\n criteria not found";
					System.err.println("> " + ErrMsg);
				}
			}
		}
		JsonObject result = new JsonObject();
		result.add("", array.getAsJsonArray());
		return array;
	}

	public JsonArray listAccounts(String[] params) throws Exception {
		String error = "";
		JsonArray result = new JsonArray();
		Connection conn = null;
		if (error.isEmpty()) {
			String sql = "select w.address,a.asset,a.balance from wallets w left join ( select t._from,t.asset, ifnull(sum(t.amount),0) as balance from wallets w, tx t where w.address = t._from group by t._from,t.asset) a on w.address = a._from";
			try {
				conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					JsonObject jo = new JsonObject();
					jo.addProperty(rs.getString("address") + "_" + rs.getString("asset"), rs.getString("balance"));
					result.add(jo);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
		}
		return result;
	}

	public JsonArray createWallet(String[] params) throws Exception {
		String error = null;
		JsonArray result = null;
		int params_len = 1;
		String label = null;
		int isDefault = 0;
		Connection conn = null;
		try {
			conn = this.connect();
			if (params != null) {
				if (params.length > 1) {
					error = "Too many parameters, please see help menu.";
					result = new JsonArray();
					result.add(error);
					return result;
				} else if (params.length == 1) {
					label = params[0];
				}
			}
			User user = getUser(conn);
			WalletKey walletKey = WalletKey.getWalletKeys();
			return createWallet(label, isDefault, null, null, null, walletKey, user, conn);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return null;
	}

	private User getUser(Connection conn) {
		String sql = "SELECT * FROM system_user";
		User user = null;
		try {
			if (conn == null) {
				conn = this.connect();
			}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				user = new User();
				user.email = rs.getString("email");
				user.user_id = rs.getString("user_id");
				user.access_token = rs.getString("access_token");
				user.refresh_token = rs.getString("refresh_token");
				user.crypt_key = rs.getString("crypt_key");
				user.crypt_iv = rs.getString("crypt_iv");
				user.encrypted_password = rs.getString("encrypted_password");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return user;
	}

	public JsonArray createWallet(String label, int isDefault, Double limitHourly, Double limitDaily,
			Double limitMaxAmount, WalletKey walletKey, User user, Connection conn) {
		JsonArray result = new JsonArray();
		JsonObject jo = null;
		try {
			if (conn == null) {
				conn = this.connect();
			}
			if (user == null) {
				user = getUser(conn);
			}
			jo = new JsonObject();
			String email = user.email;
			String sql = "INSERT INTO wallets (address,email,label,private_key,public_key,is_default,limit_hourly,limit_daily,limit_max_amount) VALUES(?,?,?,?,?,?,?,?,?)";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, walletKey.getAddress());
			pstmt.setString(2, email);
			pstmt.setString(3, label != null ? label : "");
			pstmt.setString(4, walletKey.getPrivateKey());
			pstmt.setString(5, walletKey.getPublicKey());
			pstmt.setInt(6, isDefault);
			pstmt.setString(7, String.valueOf(limitHourly));
			pstmt.setString(8, String.valueOf(limitDaily));
			pstmt.setString(9, String.valueOf(limitMaxAmount));
			pstmt.executeUpdate();

			jo.addProperty("address", walletKey.getAddress());
			jo.addProperty("email", email);
			jo.addProperty("label", label != null ? label : "");
			jo.addProperty("private_key", walletKey.getPrivateKey());
			jo.addProperty("public_key", walletKey.getPublicKey());
			jo.addProperty("is_default", isDefault);
			jo.addProperty("limit_hourly", limitHourly);
			jo.addProperty("limit_daily", limitDaily);
			jo.addProperty("limit_max_amount", limitMaxAmount);
			result.add(jo);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}

	public JsonArray getBalance(String[] params) throws Exception {
		String error = "";
		String sql;
		String whereClause = "";
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		Connection conn = null;

		if (error.isEmpty()) {
			if (paramsLength == 1) {
				if (params[0].length() > 3) {
					whereClause = " and t._from = '" + params[0] + "'";
				} else {
					whereClause = " and t.asset = '" + params[0] + "'";
				}
			} else if (paramsLength == 2) {
				String asset, from = "";
				if (params[0].length() > 3) {
					from = params[0];
					asset = params[1];
				} else {
					from = params[1];
					asset = params[0];
				}
				whereClause = " and t.asset = '" + asset + "' and t._from = '" + from + "'";
			}
			sql = "SELECT t._from as address, t.asset as asset, sum(t.amount) balance FROM wallets w, tx t where w.address = t._from"
					+ whereClause + " group by t._from, t.asset";

			try {
				conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				// loop through the result set
				while (rs.next()) {
					JsonObject jo = new JsonObject();
					jo.addProperty(rs.getString("address") + "_" + rs.getString("asset"), rs.getString("balance"));
					result.add(jo);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
		}
		return result;
	}

	public JsonArray send(String[] params) throws Exception {
		String error = "";
		String whereClause = "";
		String sql = null;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		String from = null;
		String to = null;
		String asset = null;
		String privateKey = null;
		String publicKey = null;
		double amount = 0;
		String desc = null;
		int hidden = 0;
		Gson g = new Gson();
		Connection conn = null;
		if (error.isEmpty()) {
			try {
				conn = this.connect();
				if (params == null || params.length < 2) {
					error = "Insufficient parameter set";
					result.add(error);
					return result;
				}
				for (int i = 0; i < params.length; i++) {
					String param = params[i];
					if (param.toLowerCase().startsWith("from:")) {
						from = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("to:")) {
						to = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("asset:")) {
						asset = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("amount:")) {
						amount = new Double(param.split(":")[1]);
					} else if (param.toLowerCase().startsWith("desc:")) {
						desc = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("hidden:")) {
						hidden = Integer.parseInt(param.split(":")[1]);
					}
				}
				if (to == null) {
					error = "Address to be sent cannot bu empty";
					result.add(error);
					return result;
				}
				if (from == null) {
					sql = "SELECT w.address,w.private_key,w.public_key FROM wallets w where w.is_default= 1";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					if (rs.next()) {
						from = rs.getString("address");
						privateKey = rs.getString("private_key");
						publicKey = rs.getString("public_key");
					} else {
						error = "Default wallet not found";
						result.add(error);
						return result;
					}
				} else {
					Statement stmt = conn.createStatement();
					sql = "select w.address,w.private_key,w.public_key from wallets w where w.address ='" + from + "'";
					ResultSet rs = stmt.executeQuery(sql);
					if (rs.next()) {
						from = rs.getString("address");
						privateKey = rs.getString("private_key");
						publicKey = rs.getString("public_key");
					} else {
						error = "Wallet not found your database.You must import your private key";
						result.add(error);
						return result;
					}
				}
				if (asset == null) {
					asset = SikkeConstant.DEFAULT_ASSET;
				}
				if (amount <= 0) {
					error = "Amount must be greater than (0)zero";
					result.add(error);
					return result;
				}

				URL balanceCheckUrl = new URL(SikkeConstant.GET_WALLET_BALANCE_URL + from);
				BufferedReader in = new BufferedReader(new InputStreamReader(balanceCheckUrl.openStream()));
				String strBalance = in.readLine();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				Balance balance = gson.fromJson(strBalance, Balance.class);

				if (balance.balance >= amount) {
					String amountStr = SikkeConstant.formatAmount(String.valueOf(amount));
					long nonce = SikkeConstant.getEpochTime();
					StringBuilder sbTextToBeSigned = new StringBuilder();
					sbTextToBeSigned.append(from).append(SikkeConstant.DOUBLE_UNDERSCORE).append(to)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(asset)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(nonce);
					PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
					String signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);
					StringBuilder sbPostQuery = new StringBuilder();

					sbPostQuery.append("tx_w_number").append(from).append("&tx_to_w_number=").append(to)
							.append("&tx_sign=").append(signedTx).append("&tx_amount=").append(amountStr)
							.append("&tx_desc=").append(desc).append("&tx_asset=").append(asset).append("&w_pub_key=")
							.append(publicKey).append("&tx_nonce=").append(nonce).append("&is_hidden=").append(hidden);
					String postQuery = "tx_w_number=" + from + "&tx_to_w_number=" + to + "&tx_sign=" + signedTx
							+ "&tx_amount=" + amountStr + "&tx_desc=" + desc + "&tx_asset=" + asset + "&w_pub_key="
							+ publicKey + "&tx_nonce=" + nonce + "&is_hidden=0";

					String response = helper.sendPost("/v1/tx", postQuery, null);
					System.err.println(response);
					TxResponse txResponse = g.fromJson(response.toString(), TxResponse.class);
					System.out.println(txResponse);
					if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
						tx tx = txResponse.tx;
						if (new Long(nonce) == Long.parseLong(tx.nonce)) {
							insertTx(conn, tx);
						}
					}
				} else {
					error = "Not enough balance.";
					System.err.println(error);
					result.add(error);
					return result;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
		}
		return result;
	}

	private void insertTx(Connection conn, tx tx) throws SQLException {
		String sql;
		sql = "INSERT INTO tx (_id,seq,amount,fee,fee_asset,hash,prev_hash,nonce,_from,_to,asset,action_time,completion_time,confirm_rate,[desc],[group],status,type,subtype) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, tx._id); // id
		pstmt.setInt(2, tx.seq);// seq
		pstmt.setString(3, String.valueOf(tx.amount));
		pstmt.setString(4, String.valueOf(tx.fee));// fee
		pstmt.setString(5, tx.fee_asset);// fee_asset
		pstmt.setString(6, tx.hash);// hash
		pstmt.setString(7, tx.prev_hash);// prev_hash
		pstmt.setString(8, tx.nonce);// nonce
		pstmt.setString(9, tx.wallet);// from
		pstmt.setString(10, tx.to); // to
		pstmt.setString(11, tx.asset);// asset
		pstmt.setString(12, String.valueOf(tx.action_time));// action time
		pstmt.setString(13, String.valueOf(tx.complete_time));// completion_time
		pstmt.setString(14, tx.confirmRate);// block
		pstmt.setString(15, tx.desc);// desc
		pstmt.setString(16, tx.group);// group
		pstmt.setInt(17, tx.status);// status
		pstmt.setInt(18, tx.type);// type
		pstmt.setInt(19, tx.subtype);// subtype
		pstmt.executeUpdate();
	}

	public JsonArray importWallet(String[] params) throws Exception {
		String error = "";
		String sql;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		Connection conn = null;
		if (error.isEmpty()) {
			if (paramsLength == 1 && params[0] != null && !params[0].isEmpty()) {
				String encodedPrivateKey = params[0];
				WalletKey walletKey = WalletKey.getWalletKeysFromPrivateKey(encodedPrivateKey);
				sql = "select w.address from wallets w where w.address = '" + walletKey.getAddress() + "'";
				try {
					conn = this.connect();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					if (rs.next()) {
						error = "The wallet you want to import already exists";
						System.out.println(error);
						result.add(error);
						rs.close();
					} else {
						rs.close();
						stmt.close();
						User user = getUser(conn);
						sql = "INSERT INTO wallets (address,email,label,private_key,public_key,is_default,limit_hourly,limit_daily,limit_max_amount) VALUES(?,?,?,?,?,?,?,?,?)";

						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, walletKey.getAddress());
						pstmt.setString(2, user.email);
						pstmt.setString(3, "");
						pstmt.setString(4, walletKey.getPrivateKey());
						pstmt.setString(5, walletKey.getPublicKey());
						pstmt.setInt(6, 0);
						pstmt.setString(7, "");
						pstmt.setString(8, "");
						pstmt.setString(9, "");
						pstmt.executeUpdate();
						error = "Address:[" + walletKey.getAddress() + "],\nPrivate key:[" + walletKey.getPrivateKey()
								+ "] wallet was imported.";
						result.add(error);
						return result;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.close();
					}
				}
			}
		}
		return result;
	}

	public JsonArray makeDefault(String[] params) throws Exception {
		String error = "";
		String sql;
		JsonArray result = new JsonArray();
		int paramsLength = params.length;
		Connection conn = null;
		if (error.isEmpty()) {
			if (paramsLength == 1 && params[0] != null && !params[0].isEmpty()) {
				String walletAddress = params[0];
				sql = "select w.id from wallets w where w.address = '" + walletAddress + "'";
				try {
					conn = this.connect();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					if (rs.next()) {
						int id = rs.getInt("id");
						stmt.addBatch("UPDATE wallets SET is_default = 0");
						stmt.addBatch("UPDATE wallets SET is_default  = 1  WHERE id ='" + id + "'");
						stmt.executeBatch();
						error = "Wallet address [" + walletAddress + "] is defaulted.";
						result.add(error);
					} else {
						error = "The wallet you want to make default is not found.";
						System.out.println(error);
						result.add(error);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.close();
					}
				}
			}
		}
		return result;
	}

	public JsonArray createAccountAndSave(String[] params) throws Exception {
		String error = null;
		JsonArray result = null;
		String aliasName = null;
		Double limitHourly = null;
		Double limitDaily = null;
		Double limitMaxAmount = null;
		Connection conn = this.connect();

		int isDefault = 0;
		if (params != null || params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				String param = params[i];
				if (param.toLowerCase().startsWith("alias_name:")) {
					aliasName = param.split(":")[1];
				} else if (param.toLowerCase().startsWith("limit_hourly:")) {
					limitHourly = Double.parseDouble(param.split(":")[1]);
				} else if (param.toLowerCase().startsWith("limit_daily:")) {
					limitDaily = Double.parseDouble(param.split(":")[1]);
				} else if (param.toLowerCase().startsWith("limit_max_amount:")) {
					limitMaxAmount = Double.parseDouble(param.split(":")[1]);
				} else if (param.toLowerCase().startsWith("default:")) {
					isDefault = Integer.parseInt(param.split(":")[1]);
				}
			}
		}
		WalletKey walletKey = WalletKey.getWalletKeys();
		return createAccountAndSave(aliasName, limitHourly, limitDaily, limitMaxAmount, isDefault, walletKey, conn);
	}

	private JsonArray createAccountAndSave(String aliasName, Double limitHourly, Double limitDaily,
			Double limitMaxAmount, int isDefault, WalletKey walletKey, Connection conn) throws Exception {
		JsonArray jsonArray = new JsonArray();
		Gson g = new Gson();
		User user = null;
		String error = "";

		try {
			if (conn == null) {
				conn = this.connect();
			}
			user = getUser(conn);
			long nonce = SikkeConstant.getEpochTime();
			String nonceStr = String.valueOf(nonce);
			StringBuilder sb = new StringBuilder();
			if (user == null) {
				error = "User could not found.";
				jsonArray.add(error);
				return jsonArray;
			}
			if (walletKey == null) {
				error = "Wallet could not cerated.";
				jsonArray.add(error);
				return jsonArray;
			}

			String privateKey = walletKey.getPrivateKey();
			PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
			String signedTx = ECDSAHelper.sign(nonceStr, pvKey);

			String plainText = AES256Cipher.decrypt(AppHelper.hexStringToByteArray(user.crypt_key),
					AppHelper.hexStringToByteArray(user.crypt_iv), user.encrypted_password);
			byte[] u_password = AES256Cipher.key128Bit(plainText);
			String encryptedPvtKey = AES256Cipher.encryptPvt(u_password, privateKey);

			sb.append("w_pub_key=" + walletKey.getPublicKey());
			sb.append("&sign=" + signedTx);
			// sb.append("&w_zeugma=" + encryptedPvtKey);
			sb.append("&nonce=" + nonceStr);
			sb.append("&w_owner_id=" + user.user_id);
			sb.append("&w_status=" + 1);
			if (aliasName != null) {
				sb.append("&w_alias_name=" + aliasName);
			}
			if (limitHourly != null) {
				sb.append("&w_limit_hourly=" + String.valueOf(limitHourly));
			}
			if (limitDaily != null) {
				sb.append("&w_limit_daily=" + String.valueOf(limitDaily));
			}
			if (limitMaxAmount != null) {
				sb.append("&w_limit_max_amount=" + String.valueOf(limitMaxAmount));
			}
			String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
			System.err.println(response);
			WalletResponse walletResponse = g.fromJson(response.toString(), WalletResponse.class);

			if (walletResponse != null) {
				if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
					wallet wallet = walletResponse.wallet;
					if (wallet != null) {
						jsonArray = createWallet(wallet.alias_name, isDefault, limitHourly, limitDaily, limitMaxAmount,
								walletKey, user, conn);
						return jsonArray;
					}
				} else {
					error = "Wallet creation failed.";
					jsonArray.add(error);
					return jsonArray;
				}
			}
			error = "Wallet creation failed.";
			jsonArray.add(error);
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
		return jsonArray;
	}

	public JsonArray syncWallet(String[] params) throws Exception {
		JsonArray jsonArray = new JsonArray();
		Gson g = new Gson();
		String sql = null;
		String error = "";
		StringBuilder sb = null;
		String limitHourly = null, aliasName = null, limitMaxAmount = null, limitDaily = null, privateKey = null,
				nonce = null, publicKey = null, signedTx = null, address = null;
		Connection conn = null;
		User user = null;
		try {
			conn = this.connect();
			user = getUser(conn);
			Statement stmt = conn.createStatement();
			if (params == null || params.length == 0) {
				sql = "SELECT w.* FROM wallets w";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					sb = new StringBuilder();
					aliasName = rs.getString("label");
					limitHourly = rs.getString("limit_hourly");
					limitDaily = rs.getString("limit_daily");
					limitMaxAmount = rs.getString("limit_max_amount");
					privateKey = rs.getString("private_key");
					nonce = String.valueOf(SikkeConstant.getEpochTime());
					publicKey = rs.getString("public_key");
					int isDefault = rs.getInt("is_default");

					PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);
					signedTx = ECDSAHelper.sign(nonce, pvKey);

					sb.append("w_pub_key=" + publicKey);
					sb.append("&sign=" + signedTx);
					sb.append("&w_owner_id=" + user.user_id);
					sb.append("&nonce=" + nonce);
					sb.append("&w_status=" + 1);
					sb.append("&w_status=" + 1);
					sb.append("&w_is_default=" + rs.getInt("is_default"));

					if (aliasName != null) {
						sb.append("&w_alias_name=" + aliasName);
					}
					if (limitDaily != null) {
						sb.append("&w_limit_daily=" + limitDaily);
					}
					if (limitHourly != null) {
						sb.append("&limit_hourly=" + limitHourly);
					}
					if (limitMaxAmount != null) {
						sb.append("&w_limit_max_amount=" + limitMaxAmount);
					}
					String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
					WalletResponse walletResponse = g.fromJson(response, WalletResponse.class);
					if (walletResponse != null) {
						if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
							wallet wallet = walletResponse.wallet;
							if (wallet != null) {
								JsonObject jsonObj = new JsonObject();
								jsonObj.addProperty("address", wallet.address);
								jsonArray.add(jsonObj);
							}
						}
					}
				}
				jsonArray.add(error);
				return jsonArray;
			} else {
				for (int i = 0; i < params.length; i++) {
					String param = params[i];
					if (param.toLowerCase().startsWith("address:")) {
						address = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_daily:")) {
						limitDaily = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_hourly:")) {
						limitHourly = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("limit_max_amount:")) {
						limitMaxAmount = param.split(":")[1];
					} else if (param.toLowerCase().startsWith("alias_name:")) {
						aliasName = param.split(":")[1];
					}
				}
				if (address == null) {
					error = "Address field cannot be empty";
					jsonArray.add(error);
					return jsonArray;
				}
				sql = "select * from wallets w where w.address ='" + address + "'";
				ResultSet rs = stmt.executeQuery(sql);

				if (rs.next()) {
					sb = new StringBuilder();
					aliasName = aliasName == null ? rs.getString("label") : aliasName;
					limitHourly = limitHourly == null ? rs.getString("limit_hourly") : limitHourly;
					limitDaily = limitDaily == null ? rs.getString("limit_daily") : limitDaily;
					limitMaxAmount = limitMaxAmount == null ? rs.getString("limit_max_amount") : limitMaxAmount;

					privateKey = rs.getString("private_key");
					publicKey = rs.getString("public_key");
					nonce = String.valueOf(SikkeConstant.getEpochTime());
					PrivateKey pvKey = ECDSAHelper.importPrivateKey(privateKey);

					signedTx = ECDSAHelper.sign(nonce, pvKey);

					sb.append("w_pub_key=" + publicKey);
					sb.append("&sign=" + signedTx);
					sb.append("&w_owner_id=" + user.user_id);
					sb.append("&nonce=" + nonce);
					sb.append("&w_status=" + 1);
					sb.append("&w_is_default=" + rs.getInt("is_default"));

					if (aliasName != null) {
						sb.append("&w_alias_name=" + aliasName);
					}
					if (limitDaily != null) {
						sb.append("&w_limit_daily=" + limitDaily);
					}
					if (limitHourly != null) {
						sb.append("&limit_hourly=" + limitHourly);
					}
					if (limitMaxAmount != null) {
						sb.append("&w_limit_max_amount=" + limitMaxAmount);
					}
					String response = helper.sendPost("/v1/wallet", sb.toString(), SikkeConstant.REQUEST_PUT);
					WalletResponse walletResponse = g.fromJson(response, WalletResponse.class);
					if (walletResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
						sql = "update wallets set label=?, limit_daily=?, limit_hourly=?, limit_max_amount=? where address = ?";

						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aliasName);
						pstmt.setString(2, limitDaily);
						pstmt.setString(3, limitHourly);
						pstmt.setString(4, limitMaxAmount);
						pstmt.setString(5, address);
						pstmt.executeUpdate();

						JsonObject jo = new JsonObject();
						jo.addProperty("aliasName", aliasName);
						jo.addProperty("address", address);
						jo.addProperty("limitHourly", limitHourly);
						jo.addProperty("limitDaily", limitDaily);
						jsonArray.add(jo);
					}
				} else {
					error = "No such wallet was found.";
					jsonArray.add(error);
					return jsonArray;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return jsonArray;
	}

	public JsonArray help(String[] params) {

		JsonArray jsonArray = new JsonArray();
		StringBuilder sb = new StringBuilder();

		sb.append(SikkeConstant.centerString("\n...:::Sikke Client Help Menu:::..."));
		sb.append("\n[] means optional field   ,   () means mandatory field");
		sb.append("\n\n--" + String.format("%1$-15s %2$10s", "getTransactions",
				" : ([address:value] | [hash:value] | [seq:value] | [block:value])  --  Example request: address:'SKK1N5WHL2m6WcfqF29Uj...'"));
		sb.append("\n--" + String.format("%1$-15s", "listAccounts"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "createWallet", " : [[label]]"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "createAndSaveWallet",
				" : [[alias_name:value] & [limit_daily:value] & [limit_hourly:value] & [limit_max_amount:value]]"));// TODO
		sb.append("\n--" + String.format("%1$-15s %2$10s", "synchWallet", " : [[label]]"));// TODO
		sb.append("\n--" + String.format("%1$-15s %2$10s", "getBalance", " : [[address],[asset]]"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "sendTx",
				" : ([from:value],(to:value),[asset:value],(amount),[desc])  -- if the sender wallet is not specified. The default wallet is used."));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "importWallet", " : ((private key))"));
		sb.append("\n--" + String.format("%1$-15s %2$10s", "makeDefault", " : ((address))"));

		sb.append("\n--" + String.format("%1$-15s %2$10s", "makeDefault", " : ((address))"));

		System.out.println(sb.toString());

		jsonArray.add(SikkeConstant.centerString("...:::Sikke Client Help Menu:::..."));
		jsonArray.add("[] means optional field  , () means mandatory field");
		jsonArray.add(String.format("%1$-20s %2$10s", "--getTransactions",
				" : ([address:value],[hash:value] | [seq:value] | [block:value])  --  Example request: (address:SKK1N5WHL2m6WcfqF29Uj...)"));
		jsonArray.add(String.format("%1$-20s %2$10s", "--listAccounts", " : "));
		jsonArray.add(String.format("%1$-20s %2$10s", "--createWallet", " : [[label]]"));
		jsonArray.add(String.format("%1$-20s %2$10s", "--createAndSaveWallet",
				" : [[alias_name:value] & [limit_daily:value] & [limit_hourly:value] & [limit_max_amount:value]]"));// TODO
		jsonArray.add(String.format("%1$-20s %2$10s", "--synchWallet", " : [[label]]"));// TODO
		jsonArray.add(String.format("%1$-20s %2$10s", "--getBalance", " : [[address],[asset]]"));
		jsonArray.add(String.format("%1$-20s %2$10s", "--sendTx",
				" : ([from:value],(to:value),[asset:value],(amount),[desc])  -- if the sender wallet is not specified. The default wallet is used."));
		jsonArray.add(String.format("%1$-20s %2$10s", "--importWallet", " : ((private key))"));
		jsonArray.add(String.format("%1$-20s %2$10s", "--makeDefault", " : ((address))"));
		return jsonArray;
	}

	public JsonArray mergeBalance(String[] params) throws Exception {
		String error = null;
		JsonArray result = new JsonArray();
		Gson g = new Gson();
		Connection conn = null;
		String sql = null;
		wallet receiverWallet = null;
		wallet senderWallet = null;
		try {
			sql = "select * from wallets w,system_user u where u.email = w.email";
			conn = this.connect();
			if (params != null) {
				if (params.length == 0) {
					sql += " and w.is_default = 1";
				} else if (params.length == 1) {
					sql += " and w.address='" + params[0] + "'";
				}
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				if (rs.next()) {
					receiverWallet = new wallet();
					receiverWallet.privateKey = rs.getString("private_key");
					receiverWallet.address = rs.getString("address");
					receiverWallet.publicKey = rs.getString("public_key");
				}
				if (receiverWallet == null) {
					error = "Default wallet(receiver wallet) could not found.";
					result.add(error);
					return result;
				}
				sql = "SELECT w1.address,w1.private_key,w1.public_key,a.amount FROM wallets w1,(SELECT t._from,t.asset,sum(t.amount) amount FROM wallets w,tx t WHERE w.address = t._from and w.is_default = 0 GROUP BY t._from,t.asset) a WHERE w1.address = a._from and a.amount >0";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				String desc = null, signedTx = null, response = null;
				StringBuilder sbTextToBeSigned, sbPostQuery;
				PrivateKey pvKey = null;
				while (rs.next()) {
					senderWallet = new wallet();
					senderWallet.address = rs.getString("address");
					senderWallet.privateKey = rs.getString("private_key");
					senderWallet.publicKey = rs.getString("public_key");
					senderWallet.balance = rs.getDouble("amount");

					String asset = SikkeConstant.DEFAULT_ASSET;
					long nonce = SikkeConstant.getEpochTime();
					String amountStr = SikkeConstant.formatAmount(String.valueOf(senderWallet.balance));

					sbTextToBeSigned = new StringBuilder().append(senderWallet.address)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(receiverWallet.address)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(asset)
							.append(SikkeConstant.DOUBLE_UNDERSCORE).append(String.valueOf(nonce));

					pvKey = ECDSAHelper.importPrivateKey(senderWallet.privateKey);
					signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);

					desc = SikkeConstant.MERGE_BALANCE_TEXT + " from:[" + senderWallet.address + "]-["
							+ receiverWallet.address + "]";
					sbPostQuery = new StringBuilder().append("tx_w_number=").append(senderWallet.address)
							.append("&tx_to_w_number=").append(receiverWallet.address).append("&tx_sign=")
							.append(signedTx).append("&tx_amount=").append(amountStr).append("&tx_desc=").append(desc)
							.append("&tx_asset=").append(asset).append("&w_pub_key=").append(senderWallet.publicKey)
							.append("&tx_nonce=").append(String.valueOf(nonce)).append("&is_hidden=0");

					response = helper.sendPost("/v1/tx", sbPostQuery.toString(), null);
					System.err.println(response);
					TxResponse txResponse = g.fromJson(response.toString(), TxResponse.class);
					if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
						insertTx(conn, txResponse.tx);
					} else if (txResponse.status.equals(SikkeConstant.STATUS_ERROR)) {
						URL balanceCheckUrl = new URL(SikkeConstant.GET_WALLET_BALANCE_URL + senderWallet.address);
						BufferedReader in = new BufferedReader(new InputStreamReader(balanceCheckUrl.openStream()));
						String strBalance = in.readLine();
						Gson gson = new Gson();
						Balance balance = gson.fromJson(strBalance, Balance.class);

						if (balance != null && balance.balance > 0) {
							amountStr = SikkeConstant.formatAmount(String.valueOf(balance.balance));

							sbTextToBeSigned = new StringBuilder().append(senderWallet.address)
									.append(SikkeConstant.DOUBLE_UNDERSCORE).append(receiverWallet.address)
									.append(SikkeConstant.DOUBLE_UNDERSCORE).append(amountStr)
									.append(SikkeConstant.DOUBLE_UNDERSCORE).append(asset)
									.append(SikkeConstant.DOUBLE_UNDERSCORE).append(String.valueOf(nonce));

							signedTx = ECDSAHelper.sign(sbTextToBeSigned.toString(), pvKey);

							sbPostQuery = new StringBuilder().append("tx_w_number=").append(senderWallet.address)
									.append("&tx_to_w_number=").append(receiverWallet.address).append("&tx_sign=")
									.append(signedTx).append("&tx_amount=").append(amountStr).append("&tx_desc=")
									.append(desc).append("&tx_asset=").append(asset).append("&w_pub_key=")
									.append(senderWallet.publicKey).append("&tx_nonce=").append(String.valueOf(nonce))
									.append("&is_hidden=0");

							response = helper.sendPost("/v1/tx", sbPostQuery.toString(), null);
							if (txResponse.status.equals(SikkeConstant.STATUS_SUCCESS)) {
								insertTx(conn, txResponse.tx);
							} else if (txResponse.status.equals(SikkeConstant.STATUS_ERROR)) {
								insertOutdatedWallet(conn, senderWallet);
							}
						}
						insertOutdatedWallet(conn, senderWallet);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return null;
	}

	private void insertOutdatedWallet(Connection conn, wallet senderWallet) throws SQLException {
		String sql;
		sql = "insert into outdated_wallet(address) value(?)";
		PreparedStatement psmtm = conn.prepareStatement(sql);
		psmtm.setString(1, senderWallet.address);
		psmtm.executeUpdate();
	}

	public JsonArray repairTx(String[] params) throws Exception {
		Connection con = null;
		String sql = null;
		String address = null;
		JsonArray result = new JsonArray();
		String error = null;
		Gson g = new Gson();
		try {
			sql = "SELECT a.*,ifnull(b.maxSeqNum, 0) maxSeqNumber FROM (SELECT w.address,w.public_key,w.private_key FROM outdated_wallet o,wallets w WHERE w.address = o.address) a LEFT JOIN (SELECT _from,max(t.seq) AS maxSeqNum FROM tx t GROUP BY t._from ) b ON a.address = b._from";
			con = this.connect();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				address = rs.getString("address");
				String publicKey = rs.getString("public_key");
				int maxSeqNumber = rs.getInt("maxSeqNumber");
				int skip = 0;
				int limit = SikkeConstant.QUERY_LIMIT;
				int totalRecordBasedOnAddress = 0;

				while (skip >= 0) {
					StringBuilder sbTx = new StringBuilder();
					sbTx.append("wallet=").append(address).append("&w_pub_key=").append(publicKey).append("&seq_gt=")
							.append(maxSeqNumber).append("&limit=").append(String.valueOf(limit)).append("&skip=")
							.append(String.valueOf(skip)).append("&sort=desc");

					String response = helper.sendGet("/v1/tx?", sbTx.toString());
					System.err.println(response);

					JsonObject json = (JsonObject) new JsonParser().parse(response);
					JsonArray jsonArray = (JsonArray) json.get("tx_items");

					if (jsonArray.size() > 0) {
						totalRecordBasedOnAddress += jsonArray.size();
						List<tx> txList = gson.fromJson(jsonArray, new TypeToken<List<tx>>() {
						}.getType());

						if (txList != null && txList.size() > 0) {
							if (txList.size() > limit) {
								skip++;
							} else {
								skip = -1;
							}
							for (tx tx : txList) {
								sql = "insert into tx (_id,seq,amount,fee,fee_asset,hash,prev_hash,nonce,_from,_to,asset,action_time,completion_time,confirm_rate,[desc],[group],status,type,subtype) "
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) on conflict (_id) do update set "
										+ "amount =" + tx.amount + ",fee='" + tx.fee + "'" + ",fee_asset='"
										+ tx.fee_asset + "'" + ",hash='" + tx.hash + "'" + ",prev_hash='" + tx.prev_hash
										+ "'" + ",nonce='" + tx.nonce + "'" + ",action_time=" + tx.action_time
										+ ",completion_time=" + tx.complete_time + ",_from='" + tx.wallet + "'"
										+ ",_to='" + tx.to + "'" + ",asset='" + tx.asset + "'" + ",[group]=" + tx.group
										+ ",seq=" + tx.seq + ",[desc]='" + tx.desc + "'" + ",confirm_rate="
										+ tx.confirmRate + ",status=" + tx.status + ",type=" + tx.type + ",subtype="
										+ tx.subtype;
								PreparedStatement pstmt = con.prepareStatement(sql);
								pstmt.setString(1, tx._id); // id
								pstmt.setInt(2, tx.seq);// seq
								pstmt.setString(3, String.valueOf(tx.amount));
								pstmt.setString(4, tx.fee);// fee
								pstmt.setString(5, tx.fee_asset);// fee_asset
								pstmt.setString(6, tx.hash);// hash
								pstmt.setString(7, tx.prev_hash);// prev_hash
								pstmt.setString(8, tx.nonce);// nonce
								pstmt.setString(9, tx.wallet);// from
								pstmt.setString(10, tx.to); // to
								pstmt.setString(11, tx.asset);// asset
								pstmt.setString(12, String.valueOf(tx.action_time));// action time
								pstmt.setString(13, String.valueOf(tx.complete_time));// completion_time
								pstmt.setString(14, tx.confirmRate);// confirmRate
								pstmt.setString(15, tx.desc);// desc
								pstmt.setString(16, tx.group);// group
								pstmt.setInt(17, tx.status);// status
								pstmt.setInt(18, tx.type);// type
								pstmt.setInt(19, tx.subtype);// subtype
								pstmt.executeUpdate();
							}
						}
					} else {
						skip = -1;
					}
				}
				error = totalRecordBasedOnAddress + " tx inserted/updated on wallet [" + address + "]";
				result.add(error);
				// TODO comment açýlacak
				/*
				 * sql = "delete from outdated_wallet where address = ?"; PreparedStatement
				 * pstmt = con.prepareStatement(sql); pstmt.setString(1, address);
				 * pstmt.executeUpdate();
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}
		if (address == null) {
			error = "No wallet to repair.";
			result.add(error);
		}
		return result;
	}

	public JsonArray syncTx(String[] params) throws Exception {
		Connection con = null;
		String sql = null;
		JsonArray result = new JsonArray();
		String error = null;
		Gson g = new Gson();
		try {
			sql = "SELECT u.email, w.*, ifnull(a.maxSeqNumber, 0) maxSeqNum FROM system_user u,wallets w LEFT JOIN (SELECT t._from,max(t.seq) AS maxSeqNumber FROM tx t GROUP BY t._from) a ON w.address = a._from WHERE u.email = w.email";
			con = this.connect();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String address = rs.getString("address");
				String publicKey = rs.getString("public_key");
				int maxSeqNumber = rs.getInt("maxSeqNum");
				int skip = 0;
				int limit = SikkeConstant.QUERY_LIMIT;
				int totalRecordBasedOnAddress = 0;

				while (skip >= 0) {
					StringBuilder sbTx = new StringBuilder();
					sbTx.append("wallet=").append(address).append("&w_pub_key=").append(publicKey).append("&seq_gt=")
							.append(maxSeqNumber).append("&limit=").append(String.valueOf(limit)).append("&skip=")
							.append(String.valueOf(skip)).append("&sort=desc");

					String response = helper.sendGet("/v1/tx?", sbTx.toString());
					System.err.println(response);

					JsonObject json = (JsonObject) new JsonParser().parse(response);
					JsonArray jsonArray = (JsonArray) json.get("tx_items");

					if (jsonArray.size() > 0) {
						totalRecordBasedOnAddress += jsonArray.size();
						List<tx> txList = gson.fromJson(jsonArray, new TypeToken<List<tx>>() {
						}.getType());

						if (txList != null && txList.size() > 0) {
							if (txList.size() > limit) {
								skip++;
							} else {
								skip = -1;
							}
							for (tx tx : txList) {
								sql = "insert into tx (_id,seq,amount,fee,fee_asset,hash,prev_hash,nonce,_from,_to,asset,action_time,completion_time,confirm_rate,[desc],[group],status,type,subtype) "
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) on conflict (_id) do update set "
										+ "amount =" + tx.amount + ",fee='" + tx.fee + "'" + ",fee_asset='"
										+ tx.fee_asset + "'" + ",hash='" + tx.hash + "'" + ",prev_hash='" + tx.prev_hash
										+ "'" + ",nonce='" + tx.nonce + "'" + ",action_time=" + tx.action_time
										+ ",completion_time=" + tx.complete_time + ",_from='" + tx.wallet + "'"
										+ ",_to='" + tx.to + "'" + ",asset='" + tx.asset + "'" + ",[group]=" + tx.group
										+ ",seq=" + tx.seq + ",[desc]='" + tx.desc + "'" + ",confirm_rate="
										+ tx.confirmRate + ",status=" + tx.status + ",type=" + tx.type + ",subtype="
										+ tx.subtype;
								PreparedStatement pstmt = con.prepareStatement(sql);
								pstmt.setString(1, tx._id); // id
								pstmt.setInt(2, tx.seq);// seq
								pstmt.setString(3, String.valueOf(tx.amount));
								pstmt.setString(4, tx.fee);// fee
								pstmt.setString(5, tx.fee_asset);// fee_asset
								pstmt.setString(6, tx.hash);// hash
								pstmt.setString(7, tx.prev_hash);// prev_hash
								pstmt.setString(8, tx.nonce);// nonce
								pstmt.setString(9, tx.wallet);// from
								pstmt.setString(10, tx.to); // to
								pstmt.setString(11, tx.asset);// asset
								pstmt.setString(12, String.valueOf(tx.action_time));// action time
								pstmt.setString(13, String.valueOf(tx.complete_time));// completion_time
								pstmt.setString(14, tx.confirmRate);// confirmRate
								pstmt.setString(15, tx.desc);// desc
								pstmt.setString(16, tx.group);// group
								pstmt.setInt(17, tx.status);// status
								pstmt.setInt(18, tx.type);// type
								pstmt.setInt(19, tx.subtype);// subtype
								pstmt.executeUpdate();
							}
						}
					} else {
						skip = -1;
					}
				}
				error = totalRecordBasedOnAddress + " tx inserted/updated on wallet [" + address + "]";
				result.add(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return result;
	}
}

class Balance {

	double balance;
}
