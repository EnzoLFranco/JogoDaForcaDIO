package com.enzolfranco.hangman.model;

import com.enzolfranco.hangman.exceptions.GameIsFinishedException;
import com.enzolfranco.hangman.exceptions.LetterAlreadyInputtedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.enzolfranco.hangman.model.HangmanGameStatus.*;

public class HangmanGame {

    private final static int HANGMAN_INITIAL_LINE_LENGTH = 9;
    private final static int HANGMAN_INITIAL_LINE_LENGHT_WITH_LINE_SEPARATOR = 10;

    private final int lineSize;
    private final int hangmanInitialSize;
    private final List<HangmanChar> hangmanCharsPaths;
    private final List<HangmanChar> hangmanChars;
    private final List<Character> failedLetters = new ArrayList<>();
    private String hangman;
    private HangmanGameStatus hangmanGameStatus;

    public HangmanGame(final List<HangmanChar> hangmanChars) {
        var whiteSpaces = " ".repeat(hangmanChars.size());
        var characterSpace = "- ".repeat(hangmanChars.size());
        this.lineSize = HANGMAN_INITIAL_LINE_LENGHT_WITH_LINE_SEPARATOR + whiteSpaces.length();
        this.hangmanGameStatus = PENDING;
        this.hangmanCharsPaths = buildHangmanPathsPosition();
        buildHangmanDesign(whiteSpaces, characterSpace);
        this.hangmanChars = setLetterSpacesPositionInGame(hangmanChars);
        this.hangmanInitialSize = this.hangman.length();
    }

    private List<HangmanChar> setLetterSpacesPositionInGame(final List<HangmanChar> hangmanChars) {
        final int LINE_LETTER = 9;
        for (int i = 0; i < hangmanChars.size(); i++) {
            hangmanChars.get(i).setPosition(this.lineSize + LINE_LETTER + HANGMAN_INITIAL_LINE_LENGTH + i);
        }
        return hangmanChars;
    }

    private ArrayList<HangmanChar> getHangmanCharsDrawn(int HEAD_LINE, int BODY_LINE, int LEGS_LINE) {
        return new ArrayList<>(List.of(
                new HangmanChar('O', this.lineSize * HEAD_LINE + 10),
                new HangmanChar('|', this.lineSize * BODY_LINE + 15),
                new HangmanChar('/', this.lineSize * BODY_LINE + 14),
                new HangmanChar('\\', this.lineSize * BODY_LINE + 16),
                new HangmanChar('/', this.lineSize * LEGS_LINE + 19),
                new HangmanChar('\\', this.lineSize * LEGS_LINE + 21))
        );
    }

    private List<HangmanChar> buildHangmanPathsPosition() {
        final int HEAD_LINE = 4;
        final int BODY_LINE = 5;
        final int LEGS_LINE = 6;

        return getHangmanCharsDrawn(HEAD_LINE, BODY_LINE, LEGS_LINE);
    }

    public void inputLetter(final char letter) {
        if (this.hangmanGameStatus != PENDING) {
            String message = this.hangmanGameStatus == WIN ? "You won!" : "You lost! Try again.";
            throw new GameIsFinishedException(message);
        }
        var found = this.hangmanChars.stream()
                .filter(hangmanChar -> hangmanChar.getLetter() == letter)
                .toList();

        if (this.failedLetters.contains(letter)) {
            throw new LetterAlreadyInputtedException("You already tried this letter: " + letter);
        }

        if (found.isEmpty()) {
            this.failedLetters.add(letter);
            if (failedLetters.size() >= 6) {
                finishGame();
                this.hangmanGameStatus = LOSE;
                throw new GameIsFinishedException("You lost! Try again.");
            }
            rebuildHangman(this.hangmanCharsPaths.remove(0));
            return;
        }

        if (found.getFirst().isVisible()) {
            throw new LetterAlreadyInputtedException("You already tried this letter: " + letter);
        }

        this.hangmanChars.forEach(c -> {
            if (c.getLetter() == found.getFirst().getLetter())
                c.setVisible(true);
        });

        if (this.hangmanChars.stream().allMatch(HangmanChar::isVisible)) {
            this.hangmanGameStatus = WIN;
            throw new GameIsFinishedException("You won!");
        }
        rebuildHangman(found.toArray(HangmanChar[]::new));
    }

    private void rebuildHangman(final HangmanChar... hangmanChars) {
        StringBuilder hangmanBuilder = new StringBuilder(this.hangman);
        Stream.of(hangmanChars).forEach(h ->
                hangmanBuilder.setCharAt(h.getPosition(), h.getLetter()));
        String failMessage = this.failedLetters.isEmpty() ? "" : String.join(", ", failedLetters.stream().map(String::valueOf).toList());
        this.hangman = hangmanBuilder.substring(0, hangmanInitialSize) + failMessage;
    }

    private void buildHangmanDesign(final String whiteSpaces, final String characterSpace) {
        this.hangman = " __________ " + whiteSpaces + System.lineSeparator() +
                " |         | " + whiteSpaces + System.lineSeparator() +
                " |         | " + whiteSpaces + System.lineSeparator() +
                " |           " + whiteSpaces + System.lineSeparator() +
                " |           " + whiteSpaces + System.lineSeparator() +
                " |           " + whiteSpaces + System.lineSeparator() +
                " |           " + whiteSpaces + System.lineSeparator() +
                "=============" + characterSpace + System.lineSeparator();
    }

    private void finishGame() {
        System.out.println("GAME OVER!");
        System.out.println("* __________  ");
        System.out.println("  |         | ");
        System.out.println("  |         | ");
        System.out.println("  |         O ");
        System.out.println("  |        /|\\");
        System.out.println("  |        / \\ ");
        System.out.println("=============");
    }

    @Override
    public String toString() {
        return new StringBuilder("*").append(this.hangman).toString();
    }
}
