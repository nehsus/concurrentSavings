package edu.uic.shen.hw2;

import java.security.SecureRandom;
import java.util.Scanner;

/**
 * Main class
 * Author: Sushen Kumar
 */
public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // Random
        SecureRandom rand = new SecureRandom();

        System.out.println("\nEnter the number of threads.");

        // Specifies the number of threads to spawn
        int n = in.nextInt();

        // Array for random events based on enum
        TransactionType []events = new TransactionType[n];

        for(int i = 0; i < n; i ++) {

            // Create random events for threads
            events[i] = randomEnum(TransactionType.class, rand);
        }

        // Create the threads
        accountMaker(n, events, rand);

    }

    public static void accountMaker(int n, TransactionType[] events, SecureRandom rand) {

        // Initial accounts
        Account first = new Account(rand.nextInt(500));
        Account second = new Account(rand.nextInt(500));
        Thread []threads = new Thread[n];

        System.out.println("Making threads..");
        // System.out.println("Events are: " + events);

        for(int i = 0; i < n; i ++) {
            int current = i;
            threads[current] = new Thread(() -> {

                // Threads are spawned with random transactions.
                switch (events[current]) {
                    case Deposit:
                        System.out.println("Making a deposit.");
                        first.deposit(rand.nextInt(500));
                        break;
                    case Preferred:
                        System.out.println("Making a preferred withdrawal.");
                        first.withdraw(rand.nextInt(500), TransactionType.Preferred);
                        break;
                    case Ordinary:
                        System.out.println("Making a ordinary withdrawal.");
                        first.withdraw(rand.nextInt(500), TransactionType.Ordinary);
                        break;
                    case Transfer:
                        System.out.println("Making a transfer.");
                        first.transfer(rand.nextInt(500), second);
                        break;
                    default:
                        System.out.println("Didn't provide a transaction!");
                        break;
                }
            });
        }

        // Start threads
        for(Thread i : threads) {
            i.start();
        }
    }

    /**
     * Helper method to generate a random Enum from TransactionType
     * @param enu
     * @param rand
     * @param <T>
     * @return
     */
    public static <T extends Enum<?>> T randomEnum(Class<T> enu, SecureRandom rand){
        int x = rand.nextInt(enu.getEnumConstants().length);
        return enu.getEnumConstants()[x];
    }
}
