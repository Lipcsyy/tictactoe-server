import constants.Constants;
import util.tictactoeUtils;

import java.util.ArrayList;
import java.util.List;


public class Opponent {

    // 0b01 is X and 0b10 is O
    //X is maxing and O is minimizing


    //We add alfa-beta pruning to it. Basically all it does, is that it cuts off the branches that are not going to be useful.
    //We don't need to explore a specific part of the tree, if we know, that we can win a better way.
    //Alfa is for the maximizing player and beta is for the minimizing player
    public int Minimax( int state, int alfa, int beta ){

        //First we need to check whether it's the end of the game or not --> If it's the end of the game we can just evaluate that position
        if ( IsStateTerminal(state) ) {
            return EvaluateState(state);
        }

        List<Integer> possibleStates = PossibleStates(state);

        //Now we need to check how is coming, if X is coming we are maximizing
        if (IsXPlayersTurn(state)) {
            //System.out.println("X is maximizing");

            int value = Integer.MIN_VALUE;
            for (int possibleState : possibleStates) {

                value = Math.max(value, Minimax(possibleState, alfa, beta));
                alfa = Math.max(alfa, value);
                //What it does, is it keeps track of the other players best move and if it's worse than the current best move, it cuts off the branch
                if ( beta <= alfa )
                    break;

            }
            return value;

        }
        else {
            //System.out.println("O is minimizing");
            int value = Integer.MAX_VALUE;
            for (int possibleState : possibleStates) {
                value = Math.min(value, Minimax(possibleState, alfa, beta));
                beta = Math.min(beta, value);
                if ( beta <= alfa )
                    break;
            }
            return value;
        }

    }

    //Somebody won or it's draw
    public boolean IsStateTerminal(int state) {

        int[] playerBits = {0b01, 0b10};

        for ( int playerBit : playerBits ) {
            for (int winningState : Constants.WINNING_STATES) {

                if ((winningState & tictactoeUtils.GetPlayerState(state,playerBit)) == winningState) {
                    return true;
                }
            }
        }

        return PossibleStates(state).isEmpty();

    }

    public int EvaluateState(int state) {

        //X is maxing so if X wins we'll get 1 and O is minimizing so if O wins we'll get -1

        int[] playerBits = {0b01, 0b10};

        for ( int playerBit : playerBits ) {
            for (int winningState : Constants.WINNING_STATES) {
                if ((winningState & tictactoeUtils.GetPlayerState(state,playerBit)) == winningState) {
                    return playerBit == 1 ? 1 : -1;
                }
            }
        }

        return 0;

    }

    public boolean IsXPlayersTurn ( int state ) {
        //Only based on the board state we can determine whose turn it is, not using the model
        //The 19th bit is the player bit

        int XState = tictactoeUtils.GetPlayerState(state, 0b01);
        int OState = tictactoeUtils.GetPlayerState(state, 0b10);


        int stateTogether = XState | OState;

        return Integer.bitCount(stateTogether) % 2 == 0;

    }

    private List<Integer> PossibleStates(int state) {
        List<Integer> possibleStates = new ArrayList<>();
        int playerBit = IsXPlayersTurn(state) ? 0b01 : 0b10;

        for (int i = 0; i < 9; i++) {
            if ((state >> (i * 2) & 0b11) == 0) {
                int newState = state | (playerBit << (i * 2));
                possibleStates.add(newState);
            }
        }

        return possibleStates;
    }

    public int[] FindBestMove(int state) {
        List<Integer> possibleStates = PossibleStates(state);

        boolean isXTurn = IsXPlayersTurn(state);
        int bestValue = isXTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestMove = -1;

        for (int possibleState : possibleStates) {
            int value = Minimax(possibleState, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if ((isXTurn && value > bestValue) || (!isXTurn && value < bestValue)) {
                bestValue = value;
                bestMove = possibleState;
            }
        }

        return convertStateToMove(state, bestMove);
    }

    private int[] convertStateToMove(int originalState, int newState) {
        for (int i = 0; i < 9; i++) {
            if (((originalState >> (i * 2)) & 0b11) == 0 && ((newState >> (i * 2)) & 0b11) != 0) {
                return new int[]{i / 3, i % 3};
            }
        }
        return new int[]{-1, -1};  // Invalid move
    }

}

