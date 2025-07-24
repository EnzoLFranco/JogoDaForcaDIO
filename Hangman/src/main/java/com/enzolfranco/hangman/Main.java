package com.enzolfranco.hangman;

import com.enzolfranco.hangman.exceptions.GameIsFinishedException;
import com.enzolfranco.hangman.exceptions.LetterAlreadyInputtedException;
import com.enzolfranco.hangman.model.HangmanChar;
import com.enzolfranco.hangman.model.HangmanGame;

import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String... args){
        var letter = Stream.of(args)
                .map(l -> l.toLowerCase().charAt(0))
                .map(HangmanChar::new)
                .toList();
        var hangmanGame = new HangmanGame(letter);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println(hangmanGame);
                System.out.print("Digite uma letra: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;
                char ch = input.toLowerCase().charAt(0);
                hangmanGame.inputLetter(ch);
            } catch (GameIsFinishedException e) {
                System.out.println(e.getMessage());
                break;
            }catch (LetterAlreadyInputtedException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
    }
}

