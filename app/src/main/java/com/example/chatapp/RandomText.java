package com.example.chatapp;

import java.util.Random;

public class RandomText {
    private static final String CHARACTERS = "abcdefghi jklmnopq rstuvwxyzAB CDEFGHI JKLMNOPQR STUVWXYZ 012 3456 789";

    public static void main(String[] args) {
        String randomText = generateRandomText(50);
        System.out.println(randomText);
    }

    public static String generateRandomText(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
