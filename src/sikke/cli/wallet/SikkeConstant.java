package sikke.cli.wallet;

public class SikkeConstant {

	// Urls
	public static final String accessTokenUrl = "https://api.sikke.network/v1/oauth/token";
	public static final String refreshTokenUrl = "https://api.sikke.network/v1/oauth/refresh_token";
	public static final String registerUserUrl = "https://api.sikke.network/v1/auth/signup";
	public static final String queryBalanceUrl = "https://api.sikke.network/v1/wallet/all_asset_balance/";
	public static final String createWalletUrl = "https://api.sikke.network/v1/wallet/generate_wallet";

	public static final String GET_WALLET_BALANCE_URL = "http://api.sikke.network/v1/wallet/balance/";

	public static final String SEND_TX_URL = "https://api.sikke.network/v1/wallet/balance/";

	// Status
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_ERROR = "error";

	// Agent
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String DB_URL = "jdbc:sqlite:sikkeClient.db";

	// Wallet Type
	public static final String WALLET_TYPE_SKK = "SKK";

	// Tx Query Types
	public static final String TX_QUERY_TYPE_HASH = "HASH";
	public static final String TX_QUERY_TYPE_BLOCK = "BLOCK";
	public static final String TX_QUERY_TYPE_ADDRESS = "ADDRESS";
	public static final String TX_QUERY_TYPE_SEQUENCE = "SEQ";
	public static final String TX_QUERY_TYPE_SEPERATOR = ":";
	public static final String DEFAULT_ASSET = "SKK";
	public static final String REQUEST_POST = "POST";
	public static final String REQUEST_PUT = "PUT";
	public static final String REQUEST_GET = "GET";
	public static final int QUERY_LIMIT = 100;
	public static final String MERGE_BALANCE_TEXT = "Merge balance operation";
	public static final Object DOUBLE_UNDERSCORE = "__";

	public static java.sql.Date getCurrentDate() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Date(today.getTime());
	}

	public static long getEpochTime() {
		return System.currentTimeMillis() / 1000;
	}

	public static String centerString(String s) {
		int width = 30;
		int padSize = width - s.length();
		int padStart = s.length() + padSize / 2;
		return String.format("%" + padStart + "s", s);
	}

	public static String formatAmount(String amount_text) {
		if (amount_text.contains(".")) {
			String last_string = amount_text.substring(amount_text.length() - 1);

			while (last_string.equals("0")) {
				amount_text = amount_text.substring(0, amount_text.length() - 1);
				last_string = amount_text.substring(amount_text.length() - 1);

			}
			if (last_string.equals(".")) {
				amount_text = amount_text.substring(0, amount_text.length() - 1);
			}
		}
		return amount_text;
	}
}
