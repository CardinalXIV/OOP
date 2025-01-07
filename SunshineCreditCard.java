package com.sunshine;

import bank.CreditCard;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// CustomerID in CreditCard CLass. = Account ID in sushine
/**
 * The SunshineCreditCard class represents a credit card account in the Sunshine Bank.
 * It extends the CreditCard class and provides additional functionality specific to Sunshine Bank.
 */
public class SunshineCreditCard extends CreditCard {

    /**
     * Constructs a SunshineCreditCard object with the specified customer ID.
     *
     * @param custId the customer ID associated with the credit card account
     * @throws IOException if an I/O error occurs
     * @throws Exception if an error occurs
     */
    public SunshineCreditCard(String custId) throws IOException, Exception {
        super(custId); // Call the superclass constructor
    }

    /**
     * Adds a credit card to the SunshineCreditCard account with the specified card type ID, salary, and age.
     *
     * @param cardTypeId the card type ID of the credit card
     * @param salary the salary of the customer
     * @param age the age of the customer
     * @throws IOException if an I/O error occurs
     * @throws Exception if an error occurs
     */
    public void addCreditCard(String cardTypeId, double salary, int age) throws IOException, Exception {
        super.createCardAccount(cardTypeId, salary, age); // Use the superclass method to create a card account
    }


    /**
     * Retrieves all customer credit card account information.
     *
     * @return a HashMap containing the customer credit card account information
     */
    public HashMap<String, String[]> getAllCustCCAccInfo() {
        // Call the super method to get the HashMap
        return super.getAllCustCCAccInfo();
         
    }
}