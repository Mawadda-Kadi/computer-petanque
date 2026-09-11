import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

    private Board board;
    private Player player1;
    private Player player2;
    private Ball targetBall;
    private Player currentPlayer;
    private int throwDx;
    private int throwDy;
    private boolean draw = false;
    private Random random = new Random();
    private List<Ball> balls = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private static final int NUMBER_OF_ROUNDS = 5;

    public Game() {
        board = new Board();
        player1 = new Player("Ridvan");
        player2 = new Player("Jenny");

        currentPlayer = player1;
    }

    public void start() {

        // Spielstart anzeigen
        System.out.println("Computer-Pétanque gestartet!");

        // Zielkugel zufällig auf dem Spielfeld platzieren
        placeTargetBall();

        // Position der Zielkugel ausgeben
        System.out.println(
                "Position der Zielkugel: "
                        + targetBall.getPosition()
        );

        // Spielfeld vor dem ersten Wurf anzeigen
        board.printBoard(targetBall, balls);

        // Festgelegte Anzahl von Spielrunden durchführen
        for (int round = 1; round <= NUMBER_OF_ROUNDS; round++) {

            for (int turn = 1; turn <= 2; turn++) {

                System.out.println();
                System.out.println("Runde " + round);
                System.out.println(
                        "Aktueller Spieler: "
                                + currentPlayer.getName()
                );

                // Startposition auswählen
                Position startPosition =
                        chooseStartPosition(currentPlayer);

                // Landeposition berechnen
                Position landingPosition =
                        calculateLandingPosition(startPosition);

                System.out.println(
                        currentPlayer.getName()
                                + " wirft von "
                                + startPosition
                                + " nach "
                                + landingPosition
                );

                // Kugel platzieren und gegebenenfalls andere Kugeln verschieben
                placePlayerBall(
                        currentPlayer,
                        landingPosition
                );

                // Zielkugel wurde aus dem Spielfeld geschoben
                if (draw) {
                    System.out.println();
                    System.out.println(
                            "Die Zielkugel wurde aus dem Spielfeld geschoben."
                    );
                    System.out.println(
                            "Das Spiel endet unentschieden!"
                    );

                    break;
                }

                // Aktuelles Spielfeld anzeigen
                board.printBoard(targetBall, balls);

                // Spieler wechseln
                switchPlayer();
            }

            // Äußere Schleife ebenfalls beenden
            if (draw) {
                break;
            }
        }

        // Gewinner bestimmen, falls das Spiel nicht bereits
        // durch das Herausfallen der Zielkugel unentschieden endete
        if (!draw) {
            determineWinner();
        }

        // Scanner nach Spielende schließen
        scanner.close();
    }

    private void placeTargetBall() {

        int x = random.nextInt(Board.SIZE) + 1;
        int y = random.nextInt(Board.SIZE) + 1;

        Position position = new Position(x, y);

        targetBall = new Ball(null, position, true);
    }

    private Position chooseStartPosition(Player player) {

        while (true) {

            System.out.println(
                    player.getName()
                            + ", wähle eine Startposition am Spielfeldrand."
            );

            int x = readNumber("x: ");
            int y = readNumber("y: ");

            Position position = new Position(x, y);

            // Prüfen, ob die Position innerhalb des Spielfelds liegt
            if (!board.isInside(position)) {
                System.out.println(
                        "Die Koordinaten müssen zwischen 1 und 13 liegen."
                );
                continue;
            }

            // Prüfen, ob die Position am äußeren Rand liegt
            if (!board.isOuterField(position)) {
                System.out.println(
                        "Die Startposition muss am äußeren Rand liegen."
                );
                continue;
            }

            // Startposition darf nicht direkt auf der Zielkugel liegen
            if (position.getX() == targetBall.getPosition().getX()
                    && position.getY() == targetBall.getPosition().getY()) {

                System.out.println(
                        "Die Startposition darf nicht auf der Zielkugel liegen."
                );
                continue;
            }

            return position;
        }
    }

    private int readNumber(String message) {

        while (true) {

            System.out.print(message);

            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }

            System.out.println(
                    "Ungültige Eingabe. Bitte eine Zahl eingeben."
            );

            scanner.next();
        }
    }

    private Position calculateLandingPosition(Position startPosition) {

        int startX = startPosition.getX();
        int startY = startPosition.getY();

        int targetX = targetBall.getPosition().getX();
        int targetY = targetBall.getPosition().getY();

        int x = startX;
        int y = startY;

        int differenceX = targetX - startX;
        int differenceY = targetY - startY;

        // Zufällige Wurfweite zwischen 0 und 5 Feldern
        int distance = random.nextInt(6);

        boolean moveHorizontal;

        // Ziel liegt direkt über oder unter dem Startfeld
        if (differenceX == 0) {

            moveHorizontal = false;

        // Ziel liegt direkt links oder rechts vom Startfeld
        } else if (differenceY == 0) {

            moveHorizontal = true;

        // Ziel unterscheidet sich in X- und Y-Richtung
        // -> zufällig horizontale oder vertikale Wurfrichtung wählen
        } else {

            moveHorizontal = random.nextBoolean();
        }

        if (moveHorizontal) {

            // Wurfrichtung: links oder rechts in Richtung Zielkugel
            throwDx = Integer.signum(differenceX);
            throwDy = 0;

            x += throwDx * distance;

            // Zufällige orthogonale Abweichung: 0 oder 1 Feld
            if (random.nextBoolean()) {

                int deviation = random.nextBoolean() ? 1 : -1;

                int newY = y + deviation;

                // Abweichung nur verwenden,
                // wenn das Feld innerhalb des Spielfelds liegt
                if (newY >= 1 && newY <= Board.SIZE) {
                    y = newY;
                }
            }

        } else {

            // Wurfrichtung: oben oder unten in Richtung Zielkugel
            throwDx = 0;
            throwDy = Integer.signum(differenceY);

            y += throwDy * distance;

            // Zufällige orthogonale Abweichung: 0 oder 1 Feld
            if (random.nextBoolean()) {

                int deviation = random.nextBoolean() ? 1 : -1;

                int newX = x + deviation;

                // Abweichung nur verwenden,
                // wenn das Feld innerhalb des Spielfelds liegt
                if (newX >= 1 && newX <= Board.SIZE) {
                    x = newX;
                }
            }
        }

        return new Position(x, y);
    }

    private void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }
    
    private Ball getBallAt(Position position) {

        // Zuerst prüfen, ob sich hier die Zielkugel befindet
        if (targetBall.getPosition().getX() == position.getX()
                && targetBall.getPosition().getY() == position.getY()) {

            return targetBall;
        }

        // Danach nach Spielerkugeln suchen
        for (Ball ball : balls) {

            if (ball.getPosition().getX() == position.getX()
                    && ball.getPosition().getY() == position.getY()) {

                return ball;
            }
        }

        return null;
    }

    private void pushBall(Ball ball) {

        Position oldPosition = ball.getPosition();

        Position newPosition = new Position(
                oldPosition.getX() + throwDx,
                oldPosition.getY() + throwDy
        );

        // Kugel würde das Spielfeld verlassen
        if (!board.isInside(newPosition)) {

            // Zielkugel verlässt das Spielfeld
            // -> Spiel endet sofort unentschieden
            if (ball.isTargetBall()) {
                draw = true;
                return;
            }

            // Normale Spielerkugel wird aus dem Spiel entfernt
            balls.remove(ball);
            return;
        }

        // Prüfen, ob das nächste Feld ebenfalls belegt ist
        Ball nextBall = getBallAt(newPosition);

        if (nextBall != null) {
            pushBall(nextBall);
        }

        // Falls die Zielkugel herausgeschoben wurde,
        // muss nichts mehr verschoben werden
        if (draw) {
            return;
        }

        // Kugel auf das neue Feld verschieben
        ball.setPosition(newPosition);
    }

    private void placePlayerBall(
            Player player,
            Position landingPosition) {

        // Prüfen, ob das Zielfeld bereits belegt ist
        Ball existingBall = getBallAt(landingPosition);

        // Vorhandene Kugel verschieben
        if (existingBall != null) {
            pushBall(existingBall);
        }

        // Wenn die Zielkugel aus dem Spielfeld geschoben wurde,
        // endet das Spiel sofort
        if (draw) {
            return;
        }

        // Neue Spielerkugel auf dem Zielfeld platzieren
        Ball newBall = new Ball(
                player,
                landingPosition,
                false
        );

        balls.add(newBall);
    }

    private boolean isNextToTarget(Ball ball) {

        int ballX = ball.getPosition().getX();
        int ballY = ball.getPosition().getY();

        int targetX = targetBall.getPosition().getX();
        int targetY = targetBall.getPosition().getY();

        int differenceX = Math.abs(ballX - targetX);
        int differenceY = Math.abs(ballY - targetY);

        return differenceX <= 1
                && differenceY <= 1
                && !(differenceX == 0 && differenceY == 0);
    }

    private void determineWinner() {

        int player1Points = 0;
        int player2Points = 0;

        for (Ball ball : balls) {

            if (isNextToTarget(ball)) {

                if (ball.getOwner() == player1) {
                    player1Points++;
                } else if (ball.getOwner() == player2) {
                    player2Points++;
                }
            }
        }

        System.out.println();
        System.out.println("Spiel beendet!");
        System.out.println(
                player1.getName() + ": " + player1Points + " Kugeln neben der Zielkugel"
        );
        System.out.println(
                player2.getName() + ": " + player2Points + " Kugeln neben der Zielkugel"
        );

        if (player1Points > player2Points) {
            System.out.println(player1.getName() + " gewinnt!");
        } else if (player2Points > player1Points) {
            System.out.println(player2.getName() + " gewinnt!");
        } else {
            System.out.println("Unentschieden!");
        }
    }
}

