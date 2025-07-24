package com.enzolfranco.hangman;

import com.enzolfranco.hangman.exceptions.GameIsFinishedException;
import com.enzolfranco.hangman.exceptions.LetterAlreadyInputtedException;
import com.enzolfranco.hangman.model.HangmanChar;
import com.enzolfranco.hangman.model.HangmanGame;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String... args) {
        String secretWord = getSecretWord(args);

        if (secretWord.isEmpty()) {
            System.out.println("Nenhuma palavra foi fornecida. Por favor, inicie o jogo com uma palavra.");
            return;
        }

        List<HangmanChar> hangmanChars = secretWord.chars()
                .mapToObj(c -> (char) c)
                .map(HangmanChar::new)
                .collect(Collectors.toList());

        HangmanGame hangmanGame = new HangmanGame(hangmanChars);
        playGame(hangmanGame);
    }

    private static String getSecretWord(String... args) {
        if (args.length > 0) {
            return args[0].toLowerCase();
        } else {
            System.out.print("Digite a palavra secreta (ela não será exibida): ");
            String word = scanner.nextLine().toLowerCase();
            return word;
        }
    }

    private static void playGame(HangmanGame hangmanGame) {
        while (true) {
            System.out.println("\n" + hangmanGame); // Adiciona uma quebra de linha para melhor visualização
            System.out.print("Digite uma letra: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                System.out.println("Por favor, digite uma letra válida.");
                continue;
            }

            char guessedLetter = input.toLowerCase().charAt(0);

            try {
                hangmanGame.inputLetter(guessedLetter);
            } catch (GameIsFinishedException e) {
                System.out.println(e.getMessage());
                System.out.println(hangmanGame); // Mostra o estado final do jogo
                break;
            } catch (LetterAlreadyInputtedException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Ocorreu um erro: " + e.getMessage());
                break;
            }
        }
        scanner.close();
    }
}