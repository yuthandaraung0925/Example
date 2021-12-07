Home Assignment

Features

1. Register Page
2. Login Page
3. Balance & Transaction History Page
4. Transfer Page

Assumption

1. Network connection (both internet and API connection) is OK.
2. Application View is only vertical.
3. Didn't click default buttons (Back, Home) of android phone.
4. Show latest 5 transactions for Transaction History.
5. Enter integer value for amount value in Transfer Page.

Flows

1. First show Login Page.
2. If users don't have account, need to register first.
   Go to Register Page from Login Page.
3. If users have account, show Balance & Transaction Page after login.
4. If users want to make transfer, go to Transfer Page from Balance & Transaction Page.
5. User can logout in that Balance & Transaction Page.

Projects

Activity
-RegisterActivity
-LoginActivity
-TransactionHistoryActivity
-TransferActivity

Model
- TransactionModel

Adapter
- TransactionListAdapter

Utility (use from all the other files)
- Constants
- Dialogs (show message)
- HttpUtils (connect api)
- Utils (encrypt data before store to preference and decrypt data)

Android Studio    4.1.1 
gradle            4.1.1
compileSdkVersion 31
buildToolsVersion 30.0.3
minSdkVersion     30
targetSdkVersion  31
url connection    com.loopj.android:android-async-http:1.4.9, com.android.volley:volley:1.1.1
Java version      1.8


