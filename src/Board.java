import java.util.List;

public class Board {

    public static final int SIZE = 13;

    public boolean isInside(Position position) {
        return position.getX() >= 1
                && position.getX() <= SIZE
                && position.getY() >= 1
                && position.getY() <= SIZE;
    }

    public boolean isOuterField(Position position) {
        return position.getX() == 1
                || position.getX() == SIZE
                || position.getY() == 1
                || position.getY() == SIZE;
    }

    public void printBoard(Ball targetBall, List<Ball> balls) {

        // X-Koordinaten oben anzeigen
        System.out.print("    ");

        for (int x = 1; x <= SIZE; x++) {
            System.out.printf("%3d", x);
        }

        System.out.println();

        // Spielfeld Zeile für Zeile ausgeben
        for (int y = 1; y <= SIZE; y++) {

            // Y-Koordinate links anzeigen
            System.out.printf("%3d ", y);

            for (int x = 1; x <= SIZE; x++) {

                String symbol = ".";

                // Prüfen, ob sich hier die Zielkugel befindet
                if (targetBall.getPosition().getX() == x
                        && targetBall.getPosition().getY() == y) {

                    symbol = "Z";

                } else {

                    // Prüfen, ob sich hier eine Spielerkugel befindet
                    for (Ball ball : balls) {

                        if (ball.getPosition().getX() == x
                                && ball.getPosition().getY() == y) {

                            if (ball.getOwner() == null) {
                                symbol = "?";
                            } else if (ball.getOwner().getName().equals("Ridvan")) {
                                symbol = "R";
                            } else if (ball.getOwner().getName().equals("Jenny")) {
                                symbol = "J";
                            }

                            break;
                        }
                    }
                }

                System.out.printf("%3s", symbol);
            }

            System.out.println();
        }
    }
}