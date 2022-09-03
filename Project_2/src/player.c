/* vim: ai:sw=4:ts=4:sts:et */

/*H**********************************************************************
 *
 *    This is a skeleton to guide development of Othello engines that can be used
 *    with the Ingenious Framework and a Tournament Engine. 
 *    The communication with the referee is handled by an implementaiton of comms.h,
 *    All communication is performed at rank 0.
 *
 *    Board co-ordinates for moves start at the top left corner of the board i.e.
 *    if your engine wishes to place a piece at the top left corner, the "gen_move"
 *    function must return "00".
 *
 *    The match is played by making alternating calls to each engine's "gen_move"
 *    and "play_move" functions. The progression of a match is as follows:
 *        1. Call gen_move for black player
 *        2. Call play_move for white player, providing the black player's move
 *        3. Call gen move for white player
 *        4. Call play_move for black player, providing the white player's move
 *        .
 *        .
 *        .
 *        N. A player makes the final move and "game_over" is called for both players
 *    
 *    IMPORTANT NOTE:
 *        Any output that you would like to see (for debugging purposes) needs
 *        to be written to file. This can be done using file fp, and fprintf(),
 *        don't forget to flush the stream. 
 *        I would suggest writing a method to make this
 *        easier, I'll leave that to you.
 *        The file name is passed as argv[4], feel free to change to whatever suits you.
 *H***********************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <mpi.h>
#include <time.h>
#include <assert.h>
#include "comms.h"

const int OUTER = 3;
const int ALLDIRECTIONS[8] = {-11, -10, -9, -1, 1, 9, 10, 11};
const int BOARDSIZE = 100;

const int LEGALMOVSBUFSIZE = 65;
const char piecenames[4] = {'.','b','w','?'};

void gen_move(char *move);
void play_move(char *move, int *game_board);
void game_over();
void run_worker();
void initialise_board();
void free_board();

void legalmoves (int player, int *moves, int *game_board);
int legalp (int move, int player, int *game_board);
int validp (int move);
int wouldflip (int move, int dir, int player, int *game_board);
int opponent (int player);
int findbracketingpiece(int square, int dir, int player, int *game_board);
int randomstrategy();
int minimax(int *game_board, int current_player, int depth, int *alpha, int beta);
void makemove (int move, int player, int *game_board);
void makeflips (int move, int dir, int player, int *game_board);
int get_loc(char* movestring);
void get_move_string(int loc, char *ms);
void printboard();
char nameof(int piece);
int count (int player, int *game_board);
void copy_board(int *game_board, int *srcBoard);

int my_colour;
int time_limit;
int running;
int rank;
int size;
int *board;
FILE *fp;

int main(int argc , char *argv[]) {
    char cmd[CMDBUFSIZE]; 
    char opponent_move[MOVEBUFSIZE];
    char my_move[MOVEBUFSIZE];

    /* starts MPI */
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);    /* get current process id */
    MPI_Comm_size(MPI_COMM_WORLD, &size);    /* get number of processes */

    my_colour = EMPTY;
    initialise_board();
    // Rank 0 is responsible for handling communication with the server
    if (rank == 0 && argc == 3) {

        time_limit = atoi(argv[1]);
        fp = fopen(argv[2], "w");
        fprintf(fp, "This is an example of output written to file.\n");
		fflush(fp);
		printboard();

        if (comms_init(&my_colour) == FAILURE) return FAILURE;
        running = 1;

		while (running == 1) {
            if (comms_get_cmd(cmd, opponent_move) == FAILURE) {
                fprintf(fp,"Error getting cmd\n");
                fflush(fp);
                running = 0;        
                break;
            }

            if (strcmp(cmd, "game_over") == 0) {
                running = 0;
                fprintf(fp, "Game over\n");
                fflush(fp);
                break;

            // Rank 0 calls gen_move 
            } else if (strcmp(cmd, "gen_move") == 0) {
                memset(my_move, 0, MOVEBUFSIZE);
                gen_move(my_move);
                printboard();
                if (comms_send_move(my_move) == FAILURE) { 
                    running = 0;
                    fprintf(fp,"Move send failed\n");
                    fflush(fp);
                    break;
                }    

            // Add the opponent's move to my board 
            } else if (strcmp(cmd, "play_move") == 0) { 
                play_move(opponent_move, board);
				printboard();
            }
        }
    } else {
        // Rank i (i != 0) calls run_worker 
        run_worker(rank);
    }
    game_over();
}

/*
    Called at the start of execution on all ranks
 */
void initialise_board() {
    int i;
    running = 1;
    board = (int *)malloc(BOARDSIZE * sizeof(int));
    for (i = 0; i <= 9; i++) board[i]=OUTER;
    for (i = 10; i <= 89; i++) {
        if (i%10 >= 1 && i%10  <=  8) board[i]=EMPTY; else board[i]=OUTER;
    }
    for (i = 90; i <= 99; i++) board[i]=OUTER;
    board[44]=WHITE; board[45]=BLACK; board[54]=BLACK; board[55]=WHITE;
}

void free_board() {
   free(board);
}

/*
   Rank i (i != 0) executes this code 
   ----------------------------------
   Called at the start of execution on all ranks except for rank 0.
   - run_worker should play minimax from its move(s) 
   - results should be send to Rank 0 for final selection of a move 
 */
void run_worker() {
	int score, move, opp;
	int alpha = -99999;
	int *alpha_p = &alpha;
	int *tempBoard = (int*) malloc (BOARDSIZE * sizeof(int));
	while (1) {
		MPI_Recv(&my_colour, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&move, 1, MPI_INT, 0, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(board, BOARDSIZE, MPI_INT, 0, 2, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(alpha_p, 1, MPI_INT, 0, 3, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		opp = opponent(my_colour);
		memset(tempBoard, 0, BOARDSIZE);
		copy_board(tempBoard, board);
		makemove(move, my_colour, tempBoard);
		score = minimax(tempBoard, opp, 1, alpha_p, 99999);
		MPI_Send(&score, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(alpha_p, 1, MPI_INT, 0, 1, MPI_COMM_WORLD);
	}
}

/*
   Rank 0 executes this code: 
   --------------------------
   Called at the start of execution on rank 0.
   - gen_move should play minimax from its move(s)
   - the ranks may communicate during execution 
   - final results should be gathered at rank 0 for final selection of a move 
   - rank 0 should return a string of the form "xy", where x and y represent 
     the row and column where your piece is placed, respectively.

   - play_move will not be called for your own player's moves, so you
     must apply the move generated here to any relevant data structures
     before returning.
 */
void gen_move(char *move) {
    int loc, best_score, score, test_alpha;
	int move_send = 0;
	int alpha = -99999;
	int *alpha_p;
	alpha_p = &alpha;
	int *tempBoard = (int*) malloc (BOARDSIZE * sizeof(int));
    if (my_colour == EMPTY) {
        my_colour = BLACK;
    }
	int opp = opponent(my_colour);
	int *moves = (int*) malloc (LEGALMOVSBUFSIZE * sizeof(int));
	memset(moves, 0, LEGALMOVSBUFSIZE);
	legalmoves(my_colour, moves, board);
	if (moves[0] == 0) {
		loc = -1;
	} else {
		best_score = -99999;
		loc = moves[1];
		int i = 1;
		while (i < moves[0]) {
			for (int j = 1; j < size; j++) {
				if (moves[i] != 0) {
					move_send = moves[i];
					MPI_Send(&my_colour, 1, MPI_INT, j, 0, MPI_COMM_WORLD);
					MPI_Send(&move_send, 1, MPI_INT, j, 1, MPI_COMM_WORLD);
					MPI_Send(board, BOARDSIZE, MPI_INT, j, 2, MPI_COMM_WORLD);
					MPI_Send(alpha_p, 1, MPI_INT, j, 3, MPI_COMM_WORLD);
					i++;
				}
			}
			memset(tempBoard, 0, BOARDSIZE);
			copy_board(tempBoard, board);
			makemove(moves[i], my_colour, tempBoard);
			score = minimax(tempBoard, opp, 1, alpha_p, 99999);
			if (score > best_score) {
				best_score = score;
				loc = moves[i];
			}
			for (int j = 1; j < size; j++) {
				MPI_Recv(&score, 1, MPI_INT, j, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
				MPI_Recv(&test_alpha, 1, MPI_INT, j, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
				if (score > best_score) {
					best_score = score;
					loc = moves[i - (size-1)];
				}
				if (test_alpha > *alpha_p) {
					*alpha_p = test_alpha;
				}
			}
			i++;
		}
	}
	free(moves);
	free(tempBoard);
    if (loc == -1) {
        strncpy(move, "pass\n", MOVEBUFSIZE);
    } else {
        get_move_string(loc, move);
        makemove(loc, my_colour, board);
    }
}

/*
    Called when the other engine has made a move. The move is given in a
    string parameter of the form "xy", where x and y represent the row
    and column where the opponent's piece is placed, respectively.
 */
void play_move(char *move, int *game_board) {
    int loc;
    if (my_colour == EMPTY) {
        my_colour = WHITE;
    }
    if (strcmp(move, "pass") == 0) {
        return;
    }
    loc = get_loc(move);
    makemove(loc, opponent(my_colour), game_board);
}

void game_over() {
    free_board();
    MPI_Abort(MPI_COMM_WORLD, MPI_SUCCESS);
	MPI_Finalize();
}

void get_move_string(int loc, char *ms) {
    int row, col, new_loc;
    new_loc = loc - (9 + 2 * (loc / 10));
    row = new_loc / 8;
    col = new_loc % 8;
    ms[0] = row + '0';
    ms[1] = col + '0';
    ms[2] = '\n';
    ms[3] = 0;
}

int get_loc(char* movestring) {
    int row, col;
    row = movestring[0] - '0';
    col = movestring[1] - '0';
    return (10 * (row + 1)) + col + 1;
}

void legalmoves (int player, int *moves, int *game_board) {
    int move, i;
    moves[0] = 0;
    i = 0;
    for (move=11; move <= 88; move++)
        if (legalp(move, player, game_board)) {
            i++;
            moves[i] = move;
        }
    moves[0] = i;
}

int legalp (int move, int player, int *game_board) {
    int i;
    if (!validp(move)) return 0;
    if (game_board[move] == EMPTY) {
        i = 0;
        while (i <=  7 && !wouldflip(move, ALLDIRECTIONS[i], player, game_board)) i++;
        if (i == 8) return 0; else return 1;
    }
    else return 0;
}

int validp (int move) {
    if ((move >= 11) && (move  <=  88) && (move%10 >= 1) && (move%10 <= 8))
        return 1;
    else return 0;
}

int wouldflip (int move, int dir, int player, int *game_board) {
    int c;
    c = move + dir;
    if (game_board[c] == opponent(player))
        return findbracketingpiece(c+dir, dir, player, game_board);
    else return 0;
}

int findbracketingpiece(int square, int dir, int player, int *game_board) {
    while (game_board[square] == opponent(player)) square = square + dir;
    if (game_board[square] == player) return square;
    else return 0;
}

int opponent (int player) {
    if (player == BLACK) return WHITE; 
    if (player == WHITE) return BLACK; 
    fprintf(fp, "illegal player\n"); return EMPTY;
}

int randomstrategy() {
    int r;
    int *moves = (int *) malloc(LEGALMOVSBUFSIZE * sizeof(int));
    memset(moves, 0, LEGALMOVSBUFSIZE);

    legalmoves(my_colour, moves, board);
    if (moves[0] == 0) {
        return -1;
    }
    srand (time(NULL));
    r = moves[(rand() % moves[0]) + 1];
	
    free(moves);
    return(r);
}

int minimax(int *game_board, int current_player, int depth, int *alpha, int beta) {
	int player_score, opponent_score, score, best_score;
	int opp = opponent(my_colour);
	int *tempBoard = (int*) malloc (BOARDSIZE * sizeof(int));
	if (depth == 4) {
		player_score = count(my_colour, game_board);
		opponent_score = count(opp, game_board);
		score = player_score - opponent_score;
		return score;
	}
	opp = opponent(current_player);
	int *moves = (int*) malloc (LEGALMOVSBUFSIZE * sizeof(int));
	memset(moves, 0, LEGALMOVSBUFSIZE);
	legalmoves(current_player, moves, game_board);
	if (moves[0] == 0) {
		return minimax(game_board, opp, depth+1, alpha, beta);
	}
	if (my_colour != current_player) {
		best_score = 99999;
	} else {
		best_score = -99999;
	}
	for (int i = 1; i < moves[0]; i++) {
		memset(tempBoard, 0, BOARDSIZE);
		copy_board(tempBoard, game_board);
		makemove(moves[i], current_player, tempBoard);
		score = minimax(tempBoard, opp, depth+1, alpha, beta);
		if (my_colour == current_player) {
			if (score > best_score) {
				best_score = score;
			}
			if (best_score > *alpha) {
				*alpha = best_score;
			}
			if (beta <= *alpha) {
				break;
			}
		} else {
			if (score < best_score) {
				best_score = score;
			}
			if (best_score < beta) {
				beta = best_score;
			}
			if (beta <= *alpha) {
				break;
			}
		}
	}
	free(moves);
	free(tempBoard);
	return best_score;
}

void makemove (int move, int player, int *game_board) {
    int i;
    game_board[move] = player;
    for (i = 0; i <= 7; i++) makeflips(move, ALLDIRECTIONS[i], player,
	game_board);
}

void makeflips (int move, int dir, int player, int *game_board) {
    int bracketer, c;
    bracketer = wouldflip(move, dir, player, game_board);
    if (bracketer) {
        c = move + dir;
        do {
            game_board[c] = player;
            c = c + dir;
        } while (c != bracketer);
    }
}

void printboard() {
    int row, col;
    fprintf(fp,"   1 2 3 4 5 6 7 8 [%c=%d %c=%d]\n",
            nameof(BLACK), count(BLACK, board), nameof(WHITE), count(WHITE, board));
    for (row = 1; row <= 8; row++) {
        fprintf(fp,"%d  ", row);
        for (col = 1; col <= 8; col++)
            fprintf(fp,"%c ", nameof(board[col + (10 * row)]));
        fprintf(fp,"\n");
    }
    fflush(fp);
}

char nameof (int piece) {
    assert(0  <=  piece && piece < 5);
    return(piecenames[piece]);
}

int count (int player, int *game_board) {
    int i, cnt;
    cnt = 0;
    for (i = 1; i <= 88; i++)
        if (game_board[i] == player) cnt++;
    return cnt;
}

void copy_board(int *game_board, int *srcBoard) {
	for (int i = 0; i < BOARDSIZE; i++) {
		game_board[i] = srcBoard[i];
	}
}
