var sikke = require('sikke');

var sikkeClient = new sikke.Client({
	host : '127.0.0.1', // host: '185.195.254.26',
	port : 4319,
	user : 'parifixusdxxt',
	pass : 'vmz5a51va623*a1v15a'
});
/*
 * sikkeClient.cmd('createAccount', "parifix2", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('createAccountAndSave', "alias_name:sikke2",
 * "limit_hourly:100", "limit_daily:500", "limit_max_amount:1500", function(err,
 * data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('mergeBalance', function(err, data) { console.log(data) })
 */

sikkeClient.cmd('repairTx', function(err, data) {
	console.log("Selammmm")
	console.log(data)
})

/*
 * sikkeClient.cmd('syncTx', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('syncWallet',
 * "address:SKK1Lxh42NcQvsNEvspiALD2tenSCxdkRUisg", "limit_hourly:9000",
 * "limit_daily:90000", "limit_max_amount:200000", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('getTransactions', "seq:11", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('listAccounts', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('getBalance', "SKK1P8fQ23UDua311wjc53zxsjvVQ4s6JcFnx",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('send', "from:SKK1KuAwe4kR1SfdoXDiQStVtRPvdTVaKy2ry",
 * "to:SKK1QGJeuVYm7zcs3JGjFZ6asgXsKrkUMDAbC", "amount:1", "desc:aqqq",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('importWallet',
 * "7rMnVkKaqAj7uDoGthqYUSGjHYYkFDw3FEeMT2CYCnqX", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('makeDefault', "SKK1KuAwe4kR1SfdoXDiQStVtRPvdTVaKy2ry",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('help', ,function(err, data) { console.log(data) })
 */
