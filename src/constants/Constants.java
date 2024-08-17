package constants;

public class Constants {

    public static final int[] WINNING_STATES = {
            0b000000111,  // Bottom row
            0b000111000,  // Middle row
            0b111000000,  // Top row
            0b001001001,  // Left column
            0b010010010,  // Middle column
            0b100100100,  // Right column
            0b100010001,  // Diagonal from top-left to bottom-right
            0b001010100   // Diagonal from top-right to bottom-left
    };

}
