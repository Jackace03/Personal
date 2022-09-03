This is a project that I had to do for my 3rd year and is a parrallel minmax othello player.

To run just type make en then run ./runplayeronly.sh.

It sends one of the possible moves to each of the available threads and does a move in the master thread as well, after every thread has done a move it compares the moves to the best move at the moment and also updates the alpha values.

The game that is played is outputted to the black.txt file
