public class Ball {

    private Player owner;
    private Position position;
    private boolean targetBall;

    public Ball(Player owner, Position position, boolean targetBall) {
        this.owner = owner;
        this.position = position;
        this.targetBall = targetBall;
    }

    public Player getOwner() {
        return owner;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isTargetBall() {
        return targetBall;
    }
}