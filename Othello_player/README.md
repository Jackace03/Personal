To run just type make en then run ./runplayeronly.sh.

It is a parrallel minimax player. This is the lightweight version, as I could not get the server version to run.

It sends one of the possible moves to each of the available threads and does a move in the master thread as well, after every thread has done a move it compares the moves to the best move at the moment and also updates the alpha values.
