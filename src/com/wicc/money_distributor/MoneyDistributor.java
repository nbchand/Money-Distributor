package com.wicc.money_distributor;

import java.util.*;


/**
 * @author Narendra
 * @version 1.0
 * @since 1/30/2022
 */
public class MoneyDistributor {

    //entities involved in this process
    private static final int TOTALBILL = 5000;
    private static final int TOTALPEOPLE = 5;
    private static final int AVERAGEBILL = (TOTALBILL / TOTALPEOPLE);

    //stores the bill payers along with the amount they paid
    private static Map<String, Integer> payerList = new HashMap<>();
    //stores the extra-bill payers along with the amount they need to receive
    private static Map<String, Integer> receiverList = new HashMap<>();
    //stores the non-bill or less than average bill payers along with the amount they need to pay
    private static Map<String, Integer> nonPayerList = new HashMap<>();

    /**
     * Main method of the class
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        //records bill payers
        payerList.put("Messi", 100);
        payerList.put("Ronaldo", 200);
        payerList.put("Suarez", 300);
        payerList.put("Torres", 400);
        payerList.put("Zlatan", 4000);

        //calculates the sum of all the individual bill paid and store them
        Integer sum = payerList.values() //extracts the integer values from the map
                .stream()//converts the extracted integers into stream objects
                .mapToInt(Integer::intValue)//converts the stream object into stream of integers(calls intValue() of Integer class)
                .sum();//calculates the sum of all the intStreams

        //executes if total bill is paid
        if (sum == TOTALBILL) {

            System.out.println("Total bill is paid");
            System.out.println("==================================");
            //finds all the receivers
            generateReceiverList();
            //finds all the non-payers or less bill payers
            generateNonPayerList();

            //every person on the non-payers list pays due until the list becomes empty
            while (!nonPayerList.isEmpty()) {
                payDues();
            }
            System.out.println("==================================");

        }

        //executes if the total bill is not paid
        else if (sum < TOTALBILL) {
            System.out.println("You're short of Rs." + (TOTALBILL - sum));
        }

        //executes if bill is over-paid
        else {
            System.out.println("You've paid Rs." + (sum - TOTALBILL) + " more");
        }

    }

    /**
     * Records the people who paid more than average bill
     */
    public static void generateReceiverList() {
        //if a person paid more than average bill he/she is added to recieverList
        for (Map.Entry<String, Integer> payer : payerList.entrySet()) {
            if (payer.getValue() > AVERAGEBILL) {
                receiverList.put(payer.getKey(), (payer.getValue() - AVERAGEBILL));
            }
        }

    }

    /**
     * Records the people who didn't pay average bill
     */
    public static void generateNonPayerList() {
        //if a person paid less than average bill he/she is added to non-payers list
        for (Map.Entry<String, Integer> nonPayer : payerList.entrySet()) {
            if (nonPayer.getValue() < AVERAGEBILL) {
                nonPayerList.put(nonPayer.getKey(), (AVERAGEBILL - nonPayer.getValue()));
            }
        }

    }


    /**
     * Displays message regarding how much money one person is obliged to pay another
     */
    public static void payDues() {
        //temp variables to store information for later use because we can't delete a Map object while iterating over it
        List<String> duePayers = new ArrayList<>();
        Map<String, Integer> newNonPayers = new HashMap<>();

        /*
        iterates over all the non-payers, find out how much and who they need to pay
        add the non-payers to payers list and remove their name from non-payers list clearing their dues
         */
        for (Map.Entry<String, Integer> notPaid : nonPayerList.entrySet()) {
            //extracts a non-payer
            String person = notPaid.getKey();
            //extracts how much amount the person needs to pay
            int amount = notPaid.getValue();

            //identifies a receiver who needs to be paid
            String receiver = findReceiver();

            //if no receiver is found due clearing process is terminated
            if (receiver.equals("")) {
                return;
            }

            //prints the information regarding the non-payer along with the amount he/she needs to pay to the receiver
            System.out.println(person + " needs to pay Rs." + amount + " to " + receiver);
            //adds the non-payer to payer-list now
            payerList.put(person, amount + (payerList.get(person)));

            //if the receiver receives more amount than required then he/she is added to non-payers list
            // along with the amount he/she owes to others
            if (amount > receiverList.get(receiver)) {
                newNonPayers.put(receiver, amount - receiverList.get(receiver));
                receiverList.put(receiver, 0);
            }

            //if the receiver receives less than or equal to the sum he/she needed, the receiver list is just updated with new amount
            else {
                receiverList.put(receiver, receiverList.get(receiver) - amount);
            }
            //person paying due is added to due-payers list
            duePayers.add(person);
        }

        //removes the people from non-payers list once they've paid all their dues
        removeAfterPaying(duePayers);
        //puts receiver who received more than they needed on the non-payers list
        nonPayerList.putAll(newNonPayers);
    }

    /**
     *Identifies the receiver who paid more than average and needs to get paid by the non-payers
     * @return A String containing person name who needs to get paid for his extra payment
     */
    public static String findReceiver() {
        String receiver = "";
        //finds a receiver who needs to be paid something(i.e. more than 0)
        for (Map.Entry<String, Integer> person : receiverList.entrySet()) {
            if (person.getValue() != 0) {
                receiver = person.getKey();
                break;
            }
        }

        return receiver;
    }

    /**
     * Removes the people from non-payers list
     * @param payers List of String which consists the names of people that need to be removed from non-payers list
     */
    public static void removeAfterPaying(List<String> payers) {
        for (String payer : payers) {
            nonPayerList.remove(payer);
        }
    }
}
