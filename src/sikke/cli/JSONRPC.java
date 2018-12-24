/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sikke.cli.helpers.Methods;

/**
 *
 * @author selim
 */
class JSONRPC {

	Methods methods = new Methods();

	public JsonArray Methods(String method, String[] params) throws Exception {
		String result = null;
		JsonArray rs = null;
		switch (method) {
		case "createAccount":
			rs = methods.createWallet(params);
			break;
		case "createAccountAndSave":
			rs = methods.createAccountAndSave(params);
			break;
		case "syncWallet":
			rs = methods.syncWallet(params);
			break;
		case "getTransactions":
			rs = methods.getTransactions(params);
			break;
		case "listAccounts":
			rs = methods.listAccounts(params);
			break;
		case "getBalance":
			rs = methods.getBalance(params);
			break;
		case "send":
			rs = methods.send(params);
			break;
		case "importWallet":
			rs = methods.importWallet(params);
			break;
		case "makeDefault":
			rs = methods.makeDefault(params);
			break;
		case "mergeBalance":
			rs = methods.mergeBalance(params);
			break;
		case "repairTx":
			rs = methods.repairTx(params);
			break;
		case "syncTx":
			rs = methods.syncTx(params);
			break;
		case "help":
			rs = methods.help(params);
			break;
		default:
			result = "Invalid method!";
			break;
		}
		return rs;
	}

}
