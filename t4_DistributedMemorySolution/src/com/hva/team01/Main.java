package com.hva.team01;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

class ParallelSort {
    public static int[] Sort(int[] arraySequentialPart, int[] arrayReversedPart) throws InterruptedException {
        final AtomicInteger doneSorting = new AtomicInteger(0);

        // Create thread performing the sorting on the normal array
        Thread threadOne = new Thread(() -> {
            for (int i = 1; i < arraySequentialPart.length; ++i) {
                if(doneSorting.get() > 0) {
                    break;
                }
                int key = arraySequentialPart[i];
                int j = i - 1;
                while (j >= 0 && arraySequentialPart[j] > key) {
                    arraySequentialPart[j + 1] = arraySequentialPart[j];
                    j = j - 1;
                }
                arraySequentialPart[j + 1] = key;
            }

            if(doneSorting.get() < 1) {
                doneSorting.set(1);
            }
        });
        threadOne.start();

        // Create a thread for the sorting on the reversed array.
        Thread threadTwo = new Thread(() -> {
            for (int i = 1; i < arrayReversedPart.length; ++i) {
                if(doneSorting.get() > 0) {
                    break;
                }
                int key = arrayReversedPart[i];
                int j = i - 1;
                while (j >= 0 && arrayReversedPart[j] > key) {
                    arrayReversedPart[j + 1] = arrayReversedPart[j];
                    j = j - 1;
                }
                arrayReversedPart[j + 1] = key;
            }
            if(doneSorting.get() < 1) {
                doneSorting.set(2);
            }
        });
        threadTwo.start();

        threadTwo.join();
        threadOne.join();

        if(doneSorting.get() == 1) {
            return arraySequentialPart;
        } if(doneSorting.get() == 2) {
            return arrayReversedPart;
        } else {
            return new int[] {0};
        }
    }
}

class SequentialSort {

    public static int[] OriginalSort(int[] arr) {
        for (int i = 1; i < arr.length; ++i) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }

        return arr;
    }
}


class JMSProducer {
    private static String url = "vm://localhost:9000";
    public static javax.jms.ConnectionFactory connFactory;
    public static javax.jms.Connection connection;
    public static javax.jms.Session mqSession;
    public static javax.jms.Destination destination;
    public static javax.jms.MessageProducer producer;

    public static void main(String[] args) throws JMSException {
        System.out.println("Size");
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();

        System.out.println("Pieces");
        int nPieces = scanner.nextInt();

        connFactory = new ActiveMQConnectionFactory(url);
        connection = connFactory.createConnection("system","manager");
        connection.start();
        mqSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        destination = mqSession.createQueue("RealTimeData");
        producer = mqSession.createProducer(destination);
        producer.setTimeToLive(30000);

        TextMessage textMessage = mqSession.createTextMessage();
        ObjectMessage objectMessage = mqSession.createObjectMessage();

        int[] arr = new int[size];

        // fill the array to be worst case
        for (int k = 0; k < arr.length; k++) {
            arr[k] = arr.length - k;
        }

        textMessage.setText(String.valueOf(arr.length));
        producer.send(textMessage);

        System.out.println("sent_msg =>> "+ textMessage.getText());

        int perTask = arr.length / nPieces;

        for (int t = 0; t < nPieces; t++) {
            // calculate the piece of array
            int from = (perTask * t + 1);
            int to = ((t + 1) * perTask);

            // create normal and reversed array
            int[] array = new int[to - from + 1];
            int[] arrayReversed = new int[to - from + 1];

            // Fill arrays with values
            int k = 0;
            for (int i = from - 1; i < to; i++) {
                array[k] = arr[i];
                arrayReversed[to - i - 1] = arr[i];
                k++;
            }

            objectMessage.setObject(new int[][] {array, arrayReversed});
            producer.send(objectMessage);

            System.out.println("sent_msg =>> "+ Arrays.toString(array));
        }
    }
}

class JMSConsumer {
    private static String url = "vm://localhost:9001";
    public static javax.jms.ConnectionFactory connFactory;
    public static javax.jms.Connection connection;
    public static javax.jms.Session mqSession;
    public static javax.jms.Destination destination;
    public static ArrayList<javax.jms.MessageConsumer> consumer = new ArrayList<>();
    public static ArrayList<Integer> array = new ArrayList<>();
    public static int arrayLength = 0;
    public static int consumers = 0;

    public static void main(String[] args) {
        System.out.println("Consumers");
        Scanner scanner = new Scanner(System.in);
        consumers = scanner.nextInt();

        connFactory = new ActiveMQConnectionFactory(url);
        try {
            connection = connFactory.createConnection("system", "manager");
            connection.setClientID("0002");
            mqSession = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            destination = mqSession.createQueue("RealTimeData");
            for (int i = 0; i < consumers; i++) {
                consumer.add(mqSession.createConsumer(destination));
            }
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        boolean start = true;

        long startTimer = System.currentTimeMillis();

        while(array.size() < arrayLength || start) {
            for (MessageConsumer messageConsumer : consumer) {
                try {
                    Message message = messageConsumer.receive(1000);

                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        message.acknowledge();

                        arrayLength = Integer.parseInt(text);
                        start = false;
                    } else if (message instanceof ObjectMessage) {
                        Object object = ((ObjectMessage) message).getObject();
                        message.acknowledge();

                        int[][] request = (int[][]) object;

                        try {
                            int[] arr = ParallelSort.Sort(request[0], request[1]);

                            // Add the result to the arraylist.
                            for (int j : arr) {
                                array.add(j);
                            }
                        } catch (InterruptedException e) {
                            // Add the result to the arraylist.
                            for (int j : request[0]) {
                                array.add(j);
                            }
                        }
                    }
                } catch (JMSException e) {
                    System.out.println("Consumer failed");
                }
            }
        }

        SequentialSort.OriginalSort(array.stream().mapToInt(x -> x).toArray());

        long time = System.currentTimeMillis() - startTimer;
        File csvOutputFile = new File("file-" + consumers + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("Count, consumers, Time DMS");
            pw.printf("%d,%d,%d", arrayLength, consumers, time);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("file-" + consumers + ".csv Created!");
    }
}



