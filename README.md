<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Welcome file</title>
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__left">
    <div class="stackedit__toc">
      
<ul>
<li><a href="#welcome-to-sikke-client">Welcome to Sikke Client</a>
<ul>
<li><a href="#sikke-client-command-functions">Sikke Client Command Functions</a></li>
<li><a href="#register">register</a></li>
<li><a href="#login">login</a></li>
<li><a href="#logout">logout</a></li>
<li><a href="#makedefault">makeDefault</a></li>
<li><a href="#importwallet">importWallet</a></li>
<li><a href="#listwallets">listWallets</a></li>
<li><a href="#gettransactions">getTransactions</a></li>
<li><a href="#createwallet">createWallet</a></li>
<li><a href="#createwalletandsave">createWalletAndSave</a></li>
<li><a href="#mergebalance">mergeBalance</a></li>
<li><a href="#getbalances">getBalances</a></li>
<li><a href="#send">send</a></li>
<li><a href="#syncwallet">syncWallet</a>
<ul>
<li></li>
</ul>
</li>
<li><a href="#importwallets">importWallets</a></li>
<li><a href="#exportwallets">exportWallets</a></li>
<li><a href="#help">help</a></li>
</ul>
</li>
</ul>

    </div>
  </div>
  <div class="stackedit__right">
    <div class="stackedit__html">
      <p><img src="https://raw.githubusercontent.com/sikke-official/sikke-java-client/master/sikke_client_logo.jpg" alt="enter image description here"></p>
<h1 id="welcome-to-sikke-client">Welcome to Sikke Client</h1>
<p><strong>Sikke Platform</strong> is an ecosystem in which transfer and other transactions related to Sikke (SKK) crypto coinand other crypto currencies created in the coin platform are made; smart contracts are created and approved; and future transactions can be made.</p>
<h2 id="sikke-client-command-functions">Sikke Client Command Functions</h2>
<blockquote>
<p><code>| means OR , &amp; means AND</code><br>
<code>[] means Optional , () means Mandatory</code>,</p>
</blockquote>
<h2 id="register">register</h2>
<blockquote>
<p><em><strong>register</strong></em> function accepts two required parameters. One of these is the email and the other is the password. If you are not registered in the system, you can register and perform wallet operations.</p>
</blockquote>
<pre><code> register (email:Value), (password:Value)
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>email</td>
<td>New user email</td>
<td>mandatory</td>
</tr>
<tr>
<td>password</td>
<td>New user password</td>
<td>mandatory</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usage(console):</p>
</blockquote>
<blockquote></blockquote>
<pre><code>   register email:test@gmail.com password:your_password
</code></pre>
<h2 id="login">login</h2>
<blockquote>
<p><em><strong>login</strong></em> function accepts two required parameters to login operation, one user username(email) and one user password. You must be logged in to make wallet transactions at the SIKKE Client.</p>
</blockquote>
<pre><code> login (email:Value), (password:Value)
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>email</td>
<td>New user email</td>
<td>mandatory</td>
</tr>
<tr>
<td>password</td>
<td>New user password</td>
<td>mandatory</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt;login email:test@gmail.com password:your_password
</code></pre>
<h2 id="logout">logout</h2>
<blockquote>
<p><em><strong>logout</strong></em> function allows the user to log out of the system. The user leaving the system must be logged in to perform the operation again.</p>
</blockquote>
<pre><code>&gt;logout
</code></pre>
<h2 id="makedefault">makeDefault</h2>
<blockquote>
<p>The <em><strong>makeDefault</strong></em> function receives the <strong>SIKKE wallet address</strong> starting with <strong>SKK</strong> as a mandatory parameter.<br>
The wallet address is marked by default.</p>
</blockquote>
<pre><code> makeDefault (address)
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>address</td>
<td>SIKKE wallet address</td>
<td>mandatory</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt; makeDefault SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA
</code></pre>
<h2 id="importwallet">importWallet</h2>
<blockquote>
<p><em><strong>importWallet</strong></em> function receives the <strong>SIKKE private address</strong> .<br>
By importing the private key address of the existing  <strong>SIKKE</strong> wallet into the coin client system, a new wallet is created in this system.</p>
</blockquote>
<pre><code> importWallet (privateKey)
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>privateKey</td>
<td>SIKKE Private Key Address</td>
<td>mandatory</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt;importWallet 8o4taasJhgRaXJb5T1opıpnhzKHFfFGPSZ2HcWEznnvybz
</code></pre>
<h2 id="listwallets">listWallets</h2>
<blockquote>
<p><em><em><strong>listWallets</strong></em></em> function does not receive any parameters.<br>
This command lists the wallets and wallet balance information in the <strong>SIKKE</strong> client system of the logged-in user.</p>
</blockquote>
<blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt;listWallets
</code></pre>
<h2 id="gettransactions">getTransactions</h2>
<blockquote>
<p><em><strong>getTransactions</strong></em> function runs in 2 different modes. First of all, no parameters are accepted. If no parameter is entered, the user’s last 100 transaction records will be returned. In the other mode, only 1 parameter must be entered. The records are returned according to the selected parameter type.</p>
</blockquote>
<blockquote>
<p>First Mode:</p>
</blockquote>
<pre><code> &gt; getTransactions
</code></pre>
<blockquote>
<p>Second Mode:</p>
</blockquote>
<pre><code> &gt; getTransactions ADDRESS|HASH|SEQ|BLOCK : Value 
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>ADDRESS/HASH/SEQ/BLOCK : Value</td>
<td>Single Parameter</td>
<td>mandatory</td>
</tr>
</tbody>
</table><blockquote>
<pre><code> Example Usages(console):
</code></pre>
</blockquote>
<p><strong>&gt; First Mode:</strong></p>
<blockquote></blockquote>
<pre><code>&gt;getTransactions
</code></pre>
<p><strong>&gt; Second Mode:</strong></p>
<blockquote></blockquote>
<blockquote>
<p><em><strong>ADDRESS:Value</strong></em></p>
</blockquote>
<pre><code>&gt;getTransactions ADDRESS:SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA
</code></pre>
<blockquote>
<p><em><strong>HASH:Value</strong></em></p>
</blockquote>
<pre><code>&gt;getTransactions HASH:8bce4ekkdl557678b9eb41f934654646595fd7b427032f2f48d9ddfgggfb7ea326791kk8dj  
</code></pre>
<blockquote>
<p><em><strong>SEQ:Value</strong></em></p>
</blockquote>
<pre><code>&gt;getTransactions SEQ:10001
</code></pre>
<blockquote>
<p><em><strong>BLOCK:Value</strong></em></p>
</blockquote>
<pre><code>&gt;getTransactions BLOCK:10000
</code></pre>
<h2 id="createwallet">createWallet</h2>
<blockquote>
<p><em><strong>createWallet</strong></em> function receives the optional wallet aliasName parameter.<br>
The command creates a <strong>SIKKE</strong> wallet starting with the SKK prefix in the SIKKE client local database.</p>
</blockquote>
<pre><code> createWallet aliasName
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>aliasName</td>
<td>SIKKE Wallet Alias Name</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt; createWallet 
&gt; createWallet your_sikke_wallet_alias_name
</code></pre>
<h2 id="createwalletandsave">createWalletAndSave</h2>
<p><em><strong>createWalletAndSave</strong></em> function receives the optional parameters. The command creates <strong>SIKKE</strong> wallet in <strong>SIKKE</strong> client system. The created wallet is synchronized to the coin API network. When you create your <strong>SIKKE</strong> wallet, you can see this wallet on all SIKKE systems(<strong>SIKKE Web Wallet</strong>, <strong>SIKKE Web Wallet</strong>, <strong>SIKKE Client</strong>).With the created wallet, transaction can be made in all coin systems.</p>
<pre><code> createWalletAndSave ALIAS_NAME|LIMIT_HOURLY|LIMIT_DAILY|LIMIT_MAX_AMOUNT|DEFAULT : Value
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>ALIAS_NAME</td>
<td>SIKKE Wallet Alias Name</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_HOURLY</td>
<td>Hourly Transfer Limit</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_DAILY</td>
<td>Daily Transfer Limit</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_MAX_AMOUNT</td>
<td>Max Transfer Limit</td>
<td>optional</td>
</tr>
<tr>
<td>DEFAULT</td>
<td>Wallet Default</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usages(console):</p>
</blockquote>
<pre><code>&gt; createWalletAndSave  ALIAS_NAME:my_wallet_name
&gt; createWalletAndSave  DEFAULT:1
&gt; createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000
&gt; createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000 DEFAULT:1
</code></pre>
<h2 id="mergebalance">mergeBalance</h2>
<blockquote>
<p><em><strong>mergeBalances</strong></em>  function has two modes.</p>
</blockquote>
<p>In the first, this method does not accept any parameters. If no parameters are entered, all balances in all wallets except the default wallet are sent to the default wallet.</p>
<p>In the other mode, only the asset type, only the wallet address, or both can be entered. If only asset type is entered, the balances of all types of wallets other than the default wallet are sent to the default wallet. If only the wallet address is entered, all the balances of all the wallets except those entered wallets are sent to this wallet. If both are entered, the specified wallet is sent the specified type of balances of all other wallets.</p>
<p><strong>First Mode:</strong></p>
<pre><code>  &gt; mergeBalances
</code></pre>
<p><strong>Second Mode:</strong></p>
<pre><code> &gt; mergeBalances [address],[asset]
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>address</td>
<td>SIKKE Wallet Address</td>
<td>optional</td>
</tr>
<tr>
<td>asset</td>
<td>Asset Type</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usages(console):</p>
</blockquote>
<p><strong>First Mode:</strong></p>
<pre><code>&gt; mergeBalances
</code></pre>
<p><strong>Second Mode:</strong></p>
<pre><code> &gt; mergeBalances asset:SKK
 &gt; mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
 &gt; mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x asset:SKK
</code></pre>
<h2 id="getbalances">getBalances</h2>
<blockquote>
<p><em><strong>getBalances</strong></em> function accepts two optional parameters, one SIKKE wallet address and one asset. The command lists the balance information of the wallets found in the coin client according to the specified criteria.</p>
</blockquote>
<pre><code> &gt; getBalances sikkeWalletAddress |&amp; asset
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>sikkeWalletAddress</td>
<td>SIKKE Private Key Address</td>
<td>optional</td>
</tr>
<tr>
<td>asset</td>
<td>asset (SKK,XTG,UPC,OKO,…)</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usages(console):</p>
</blockquote>
<pre><code>&gt; getBalances
&gt; getBalances SKK 
&gt; getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
&gt; getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x SKK
&gt; getBalances SKK SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
</code></pre>
<h2 id="send">send</h2>
<blockquote>
<p><strong>send</strong> function takes two required parameters, one is the receiving address and the other is amount.<br>
This command sends the specified amount of coins to the selected recipient wallet address.<br>
If the sender wallet address is not entered, the default wallet in the system is set as sender wallet.<br>
If the asset is not selected, then the default asset is determined as SKK asset type.<br>
The transfer specified as “hidden” appears as a hidden transaction on the network.</p>
</blockquote>
<pre><code> send FROM|TO|ASSET|AMOUNT|DESC|HIDDEN : Value
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>FROM</td>
<td>Sender Wallet Address</td>
<td>optional</td>
</tr>
<tr>
<td>TO</td>
<td>Receiver Wallet Address</td>
<td>mandatory</td>
</tr>
<tr>
<td>ASSET</td>
<td>Asset(SKK,XTG,UPC,OKO,…)</td>
<td>optional</td>
</tr>
<tr>
<td>AMOUNT</td>
<td>Amount</td>
<td>mandatory</td>
</tr>
<tr>
<td>DESC</td>
<td>Description</td>
<td>optional</td>
</tr>
<tr>
<td>HIDDEN</td>
<td>Hidden Transactin</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p>Example Usages(console):</p>
</blockquote>
<pre><code>&gt;send TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
&gt;send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
&gt;send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC
&gt;send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC DESC:My_Transfer HIDDEN:1
</code></pre>
<h2 id="syncwallet">syncWallet</h2>
<blockquote></blockquote>
<blockquote>
<p><em><strong>syncWallet</strong></em> has two modes.</p>
<p>In the first of these modes, if any parameters are not entered, the system user’s <strong>SIKKE CLIENT</strong> is synchronized with all the wallets <strong>SIKKE API</strong> that are in the local database.<br>
In the second of these modes, a wallet address and information created with createWallet is synchronized with the SIKKE API.<br>
After that, the wallet that was previously only available in the local database becomes visible on the entire SIKKE system (<strong>SIKKE Web Wallet</strong>, <strong>SIKKE Mobile Wallet</strong>, <strong>SIKKE Client</strong>).</p>
</blockquote>
<h4 id="first-mod">First Mod:</h4>
<pre><code> syncWallet
</code></pre>
<blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt;syncWallet
</code></pre>
<h4 id="second-mod">Second Mod:</h4>
<pre><code>syncWallet ADDRESS|ALIAS_NAME|LIMIT_DAILY|LIMIT_HOURLY|LIMIT_MAX_AMOUNT:Value
</code></pre>

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Definition</th>
<th>Obligation</th>
</tr>
</thead>
<tbody>
<tr>
<td>ADDRESS</td>
<td>SIKKE Private Key Address</td>
<td>mandatory</td>
</tr>
<tr>
<td>ALIAS_NAME</td>
<td>Alias Name</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_DAILY</td>
<td>Daily Transfer Limit</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_HOURLY</td>
<td>Hourly Transfer Limit</td>
<td>optional</td>
</tr>
<tr>
<td>LIMIT_MAX_AMOUNT</td>
<td>Max Transfer Limit</td>
<td>optional</td>
</tr>
</tbody>
</table><blockquote>
<p><code>Example Usages(console):</code></p>
<p>syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x<br>
syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x LIMIT_DAILY:100 LIMIT_DAILY:1000<br>
syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x ALIAS_NAME:My_Wallet</p>
</blockquote>
<h2 id="importwallets">importWallets</h2>
<blockquote>
<p><em><strong>importWallets</strong></em> function imports the wallets in the file by reading the “wallets.skk” file in the directory where the jar file is located. If the file cannot be found, it returns a file not found error.</p>
</blockquote>
<pre><code> &gt; importWallets
</code></pre>
<blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt; importWallets
</code></pre>
<h2 id="exportwallets">exportWallets</h2>
<blockquote>
<p><em><strong>exportWallets</strong></em> function writes all the wallets of the user to the directory where the jar file is located, creating a file called “wallets.skk”. If the file cannot be found, it returns a file not found error.</p>
</blockquote>
<pre><code> &gt; exportWallets
</code></pre>
<blockquote>
<p>Example Usage(console):</p>
</blockquote>
<pre><code>&gt; exportWallets
</code></pre>
<h2 id="help">help</h2>
<blockquote>
<p><em><strong>help</strong></em> function shows Help Menu.<br>
With the help command you can quickly see how the methods work.</p>
</blockquote>

    </div>
  </div>
</body>

</html>
