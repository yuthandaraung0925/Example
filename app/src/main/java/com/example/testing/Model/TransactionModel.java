package com.example.testing.Model;

import java.util.HashMap;

public class TransactionModel extends HashMap<String,String>{
    private String accountHolder;
    private String accountNo;
    private String amount;
    private String transactionType;

        public  TransactionModel(String accountHolder, String accountNo, String amount, String transactionType){
            this.accountHolder = accountHolder;
            this.accountNo = accountNo;
            this.amount = amount;
            this.transactionType = transactionType;
        }

    public  String getAccountHolder(){return  accountHolder;}
    public  void setAccountHolder(String accountHolder){this.accountHolder = accountHolder;}

    public  String getAccountNo(){return  accountNo;}
    public  void setAccountNo(String accountNo){this.accountNo = accountNo;}

    public  String getAmount(){return  amount;}
    public  void setAmount(String amount){this.amount = amount;}

    public  String getTransactionType(){return  transactionType;}
    public  void setTransactionType(String transactionType){this.transactionType = transactionType;}
}