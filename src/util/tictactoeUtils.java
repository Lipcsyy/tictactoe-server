package util;

public class tictactoeUtils {

    public static int GetPlayerState(int _state,int playerBits) {

        //System.out.println("Checking player state");

        //playerBits are here either 0b01/1 (X) or 0b10/2 (Y)

        //Player-state : 110011001
        int playerState = 0;

        for (int i = 0; i < 9; i++) {

            //Check if the current cell (represented by 2 bits) is occupied by X or O;

            if (((_state >> (i * 2) & 0b11) == playerBits)) {
                //System.out.println("Found matching playerbit");
                playerState = playerState | (1 << i);
            }
        }

        return playerState;

    }

}
