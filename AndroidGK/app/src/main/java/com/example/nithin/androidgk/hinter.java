package com.example.nithin.androidgk;

import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.lang.*;
import java.util.TreeMap;

/**
 * Created by nithin on 4/5/2018.
 */

public class hinter {
    static ArrayList<String> words;

    public hinter()
    {
        words = new ArrayList<>();
    }
    public static ArrayList<String> load_english_dict(File dictfile)
    {
        Scanner dictReader = null;
        try {
            dictReader = new Scanner(dictfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(dictReader.hasNextLine())
        {
            words.add(dictReader.nextLine());
        }
        Log.d("dict words", words.toString());
        return words;
    }
    public static ArrayList<String> compatible_words(String word, int limit)
    {
        ArrayList<String> output = new ArrayList<>();
        int word_count = 0;
        for(String i:words)
        {
            if(i.startsWith(word))
            {
                output.add(i);
                word_count++;
            }
            if(word_count>=limit)
                break;
        }
        return output;
    }
    public static ArrayList<Character> next_letters(String word)
    {
        ArrayList<String> temp = compatible_words(word,100);
        ArrayList<Character> letters = new ArrayList<>();
        for(String i:temp)
        {
            if(i.length()>word.length())
            {
              char letter = i.charAt(word.length());
              if(!letters.contains(letter))
                  letters.add(letter);
            }

        }
        return letters;
    }
    public boolean does_word_exists(String word)
    {
        if(words.contains(word))
            return true;
        return false;
    }

    public static char most_probable_letter(File outputFile, String word)
    {
        Scanner opReader = null;
        String[] classes_String;
        Character[] classes = new Character[128];
        try {
            opReader = new Scanner(outputFile);
            Log.d("Scanner opReader Ready", "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(word.matches(""))
        {
            Log.d("Matched with '\0'", "");
            return '\0';
        }
        Log.d("Didn't match proceeding", "");
        double[] probabilities = new double[128];
        if(opReader.hasNextLine())
        {
            opReader.next();
            classes_String = opReader.nextLine().split(" ");
            for(int i=1;i<classes_String.length;i++)
                classes[i-1] =(char) Byte.parseByte(classes_String[i]);
            opReader.nextInt();
            String[] prob = opReader.nextLine().split(" ");
            for(int i=1;i<prob.length;i++) {
                probabilities[i-1] = Math.log(Float.parseFloat(prob[i]));
            }
        }
        //Arrays.sort(classes);
        TreeMap<Double,Character> values = new TreeMap<>(Collections.reverseOrder());
        for(int i=0;i<probabilities.length;i++)
        {
            values.put(probabilities[i],classes[i]);
        }
        ArrayList<Character> possible_letters = next_letters(word);
        System.out.println(word + " : " + possible_letters);
        for (Character value : values.values()) {
            if(possible_letters.contains(value))
                return value;
        }
        return '\0';
    }
}
