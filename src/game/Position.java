package game;

import static game.Direction.*;
import static java.lang.Math.abs;

public class Position {
    
    private static final int  MOVE_DOWN  =  0b100000,
        MOVE_UP    = -0b100000,
        MOVE_RIGHT =  0b000001,
        MOVE_LEFT  = -0b000001;
    
    public static final byte UNCLICKABLE = 127;
    
    protected int index;
    protected int x;
    protected int y;
    
    public boolean isValid() {
        return x >= 0 && x < 32 && y >= 0 && y < 32;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
        x = index & 0b11111;
        y = index >>> 5;
    }
    
    private void moveUp(){
        y--;
        index += MOVE_UP;
    }
    private void moveLeft(){
        x--;
        index += MOVE_LEFT;
    }
    private void moveDown(){
        y++;
        index += MOVE_DOWN;
    }
    private void moveRight(){
        x++;
        index += MOVE_RIGHT;
    }
    public Position move(Direction direction){
        Position p = clone();
        switch (direction){
            case UP:    p.moveUp(); break;
            case LEFT:  p.moveLeft(); break;
            case DOWN:  p.moveDown(); break;
            case RIGHT: p.moveRight(); break;
        }
        return p;
    }
    
    public Position add(int x, int y){
        return new Position(this.x + x, this.y + y);
    }
    
    public static Position screenPosition(Position chipPosition){
        int screenX, screenY;
        int chipX = chipPosition.getX();
        int chipY = chipPosition.getY();
        
        if (chipX < 5) screenX = 0;
        else if (chipX >= 27) screenX = 23;
        else screenX = chipX - 4;
        
        if (chipY < 5) screenY = 0;
        else if (chipY >= 27) screenY = 23;
        else screenY = chipY - 4;
        
        return new Position(screenX, screenY);
    }
    
    public static Position clickPosition(Position screenPosition, byte clickByte){
        int n = -clickByte - 1;
        return new Position(screenPosition.getX() + n % 9, screenPosition.getY() + n / 9);
    }
    
    public byte clickByte(Position chipPosition){
        Position screen = screenPosition(chipPosition);
        if (y - screen.getY() < 9 && y - screen.getY() >= 0 &&
            x - screen.getX() < 9 && x - screen.getX() >= 0){
            return (byte) -(9 * (y - screen.getY()) + (x - screen.getX()) + 1);
        }
        return UNCLICKABLE;
    }
    
    public Direction[] seek(Position seekedPosition){
        int verticalDifference = y - seekedPosition.y;
        int horizontalDifference = x - seekedPosition.x;
    
        Direction verticalDirection = null;
        if (verticalDifference > 0) verticalDirection = UP;
        else if (verticalDifference < 0) verticalDirection = DOWN;
    
        Direction horizontalDirection = null;
        if (horizontalDifference > 0) horizontalDirection = LEFT;
        else if (horizontalDifference < 0) horizontalDirection = RIGHT;
        
        if (abs(verticalDifference) >= abs(horizontalDifference))
            return new Direction[] {verticalDirection, horizontalDirection};
        else return new Direction[] {horizontalDirection, verticalDirection};
    }
    
    public Position(int x, int y){
        this.x = x;
        this.y = y;
        index = (y << 5) | x;
    }
    
    public Position(int index){
        setIndex(index);
    }
    
    public Position(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }
    
    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }
    
    @Override
    public Position clone(){
        return new Position(index);
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof Position && ((Position) o).equals(this);
    }
    
    public boolean equals(Position p){
        assert p != null; //100% null should never get passed as a position so you need to know if it does
        return index == p.index;
    }
    
}
