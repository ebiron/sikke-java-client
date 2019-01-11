![enter image description here](https://wallet.sikke.com.tr/static/images/sikke_client_logo.jpg)
# Welcome to Sikke Client



**Sikke Platform** is an ecosystem in which transfer and other transactions related to Sikke (SKK) crypto coinand other crypto currencies created in the coin platform are made; smart contracts are created and approved; and future transactions can be made.


## Sikke Client Command Functions

  

> `| means OR  ,  & means AND`
> `[]  means Optional , () means Mandatory`, 
   ## register  

>   _**register**_ function accepts two required parameters. One of these is the email and the other is the password. If you are not registered in the system, you can register and perform wallet operations.

     register (email:Value), (password:Value)

|Parameter|Definition  |Obligation
|--|--|--|
|email   | New user email | mandatory
|password   | New user password | mandatory

> Example Usage(console):
> 

> 

       register email:test@gmail.com password:your_password

## login  

>  _**login**_ function accepts two required parameters to login operation, one user username(email) and one user password. You must be logged in to make wallet transactions at the SIKKE Client.

     login (email:Value), (password:Value)

|Parameter|Definition  |Obligation
|--|--|--|
|email   | New user email | mandatory
|password   | New user password | mandatory

> Example Usage(console):

    >login email:test@gmail.com password:your_password

   

## logout  

> _**logout**_ function allows the user to log out of the system. The user leaving the system must be logged in to perform the operation again.



    >logout

   



   ## makeDefault  

>   The ***makeDefault*** function receives the **SIKKE wallet address** starting with **SKK** as a mandatory parameter.
>    The wallet address is marked by default.

     makeDefault (address)

|Parameter|Definition  |Obligation
|--|--|--|
|address   | SIKKE wallet address | mandatory

> Example Usage(console):

    > makeDefault SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA


   
   

## importWallet
   >    ***importWallet*** function receives the **SIKKE private address** . 
   >     By importing the private key address of the existing  **SIKKE** wallet into the coin client system, a new wallet is created in this system.

     importWallet (privateKey)

|Parameter|Definition  |Obligation
|--|--|--|
|privateKey   | SIKKE Private Key Address | mandatory

    

> Example Usage(console):

    >importWallet 8o4taasJhgRaXJb5T1opıpnhzKHFfFGPSZ2HcWEznnvybz

   

## listWallets
   >    _***listWallets***_ function does not receive any parameters.  
This command lists the wallets and wallet balance information in the **SIKKE** client system of the logged-in user.

> Example Usage(console):
> 

    >listWallets

 

## getTransactions
   >  _**getTransactions**_ function runs in 2 different modes. First of all, no parameters are accepted. If no parameter is entered, the user's last 100 transaction records will be returned. In the other mode, only 1 parameter must be entered. The records are returned according to the selected parameter type.

> First Mode:

     > getTransactions
> Second Mode:

     > getTransactions ADDRESS|HASH|SEQ|BLOCK : Value 

|Parameter|Definition  |Obligation
|--|--|--|
|ADDRESS/HASH/SEQ/BLOCK : Value| Single Parameter | mandatory

>      Example Usages(console):
> 

**> First Mode:**
> 

    >getTransactions

**> Second Mode:**
> 
 
 > ***ADDRESS:Value***

    

    >getTransactions ADDRESS:SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA

> ***HASH:Value***

    >getTransactions HASH:8bce4ekkdl557678b9eb41f934654646595fd7b427032f2f48d9ddfgggfb7ea326791kk8dj  

> ***SEQ:Value***

    >getTransactions SEQ:10001
> ***BLOCK:Value***

    >getTransactions BLOCK:10000



   ## createWallet
   >   _**createWallet**_ function receives the optional wallet aliasName parameter.  
The command creates a **SIKKE** wallet starting with the SKK prefix in the SIKKE client local database.

     createWallet aliasName

|Parameter|Definition  |Obligation
|--|--|--|
|aliasName   | SIKKE Wallet Alias Name | optional

    

> Example Usage(console):
> 

    > createWallet 
    > createWallet your_sikke_wallet_alias_name

 

## createWalletAndSave

_**createWalletAndSave**_ function receives the optional parameters. The command creates **SIKKE** wallet in **SIKKE** client system. The created wallet is synchronized to the coin API network. When you create your **SIKKE** wallet, you can see this wallet on all SIKKE systems(**SIKKE Web Wallet**, **SIKKE Web Wallet**, **SIKKE Client**).With the created wallet, transaction can be made in all coin systems.

     createWalletAndSave ALIAS_NAME|LIMIT_HOURLY|LIMIT_DAILY|LIMIT_MAX_AMOUNT|DEFAULT : Value

|Parameter|Definition  |Obligation
|--|--|--|
|ALIAS_NAME| SIKKE Wallet Alias Name | optional
|LIMIT_HOURLY| Hourly Transfer Limit | optional
|LIMIT_DAILY|Daily Transfer Limit|optional
|LIMIT_MAX_AMOUNT| Max Transfer Limit | optional
|DEFAULT| Wallet Default | optional


> Example Usages(console):

    > createWalletAndSave  ALIAS_NAME:my_wallet_name
    > createWalletAndSave  DEFAULT:1
    > createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000
    > createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000 DEFAULT:1



   
   ## mergeBalance
   >    _**mergeBalances**_  function has two modes.

In the first, this method does not accept any parameters. If no parameters are entered, all balances in all wallets except the default wallet are sent to the default wallet.

In the other mode, only the asset type, only the wallet address, or both can be entered. If only asset type is entered, the balances of all types of wallets other than the default wallet are sent to the default wallet. If only the wallet address is entered, all the balances of all the wallets except those entered wallets are sent to this wallet. If both are entered, the specified wallet is sent the specified type of balances of all other wallets.

**First Mode:**

      > mergeBalances
   

**Second Mode:**
 

     > mergeBalances [address],[asset]

   

|Parameter|Definition  |Obligation
|--|--|--|
|address| SIKKE Wallet Address | optional
|asset| Asset Type | optional

> Example Usages(console):
> 
**First Mode:**

    > mergeBalances
    
**Second Mode:**

     > mergeBalances asset:SKK
     > mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
     > mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x asset:SKK

   ## getBalances
   >    _**getBalances**_ function accepts two optional parameters, one SIKKE wallet address and one asset. The command lists the balance information of the wallets found in the coin client according to the specified criteria.

     > getBalances sikkeWalletAddress |& asset

|Parameter|Definition  |Obligation
|--|--|--|
|sikkeWalletAddress| SIKKE Private Key Address | optional
|asset| asset (SKK,XTG,UPC,OKO,..) | optional


> Example Usages(console):

    > getBalances
    > getBalances SKK 
    > getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
    > getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x SKK
    > getBalances SKK SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x


 ## send
   >   **send** function takes two required parameters, one is the receiving address and the other is amount.  
This command sends the specified amount of coins to the selected recipient wallet address.  
If the sender wallet address is not entered, the default wallet in the system is set as sender wallet.  
If the asset is not selected, then the default asset is determined as SKK asset type.  
The transfer specified as “hidden” appears as a hidden transaction on the network.

     send FROM|TO|ASSET|AMOUNT|DESC|HIDDEN : Value

|Parameter|Definition  |Obligation
|--|--|--|
|FROM| Sender Wallet Address | optional
|TO| Receiver Wallet Address | mandatory
|ASSET| Asset(SKK,XTG,UPC,OKO,...) | optional
|AMOUNT| Amount | mandatory
|DESC| Description| optional
|HIDDEN| Hidden Transactin | optional


> Example Usages(console):

    >send TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC DESC:My_Transfer HIDDEN:1

   


   ## syncWallet
   >    


    

> ***syncWallet*** has two modes. 
> 
>In the first of these modes, if any parameters are not entered, the system user's **SIKKE CLIENT** is synchronized with all the wallets **SIKKE API** that are in the local database. 
>In the second of these modes, a wallet address and information created with createWallet is synchronized with the SIKKE API.
After that, the wallet that was previously only available in the local database becomes visible on the entire SIKKE system (**SIKKE Web Wallet**, **SIKKE Mobile Wallet**, **SIKKE Client**).
> 
> 


#### First Mod:

     syncWallet

> Example Usage(console):

    >syncWallet

#### Second Mod:

    syncWallet ADDRESS|ALIAS_NAME|LIMIT_DAILY|LIMIT_HOURLY|LIMIT_MAX_AMOUNT:Value

|Parameter|Definition  |Obligation
|--|--|--|
|ADDRESS| SIKKE Private Key Address | mandatory
|ALIAS_NAME| Alias Name | optional
|LIMIT_DAILY| Daily Transfer Limit | optional
|LIMIT_HOURLY| Hourly Transfer Limit | optional
|LIMIT_MAX_AMOUNT| Max Transfer Limit | optional

    

> `Example Usages(console):`
> 
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x 
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x LIMIT_DAILY:100 LIMIT_DAILY:1000
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x ALIAS_NAME:My_Wallet


 ## importWallets
   >    _**importWallets**_ function imports the wallets in the file by reading the "wallets.skk" file in the directory where the jar file is located. If the file cannot be found, it returns a file not found error.

     > importWallets



> Example Usage(console):

    > importWallets


 ##  exportWallets
   >    _**exportWallets**_ function writes all the wallets of the user to the directory where the jar file is located, creating a file called "wallets.skk". If the file cannot be found, it returns a file not found error.

     > exportWallets

> Example Usage(console):

    > exportWallets


## help
   >    ***help*** function shows Help Menu. 
   >     With the help command you can quickly see how the methods work.

 







