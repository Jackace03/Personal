
public class Dice {

    public static int Dice_Roll() {
        return StdRandom.uniform(1, 7);
    }

    public static String hands(Integer[] x) {
        String temp = "";
        String tempHigh = "";
        String xHigh = "";
        String xHighestHand = pairs(x);

        if (xHighestHand.startsWith("Pair")) {
            xHigh = xHighestHand.substring(5, 6);
        } else {
            if (xHighestHand.startsWith("Two pairs")) {
                xHigh = xHighestHand.substring(10, 11) + " " + xHighestHand
                        .substring(12, 13);
            }
        }

        temp = three_of_a_kind(x);
        if (temp.startsWith("Three of a kind")) {
            tempHigh = temp.substring(16, 17);
            if (xHighestHand.startsWith("Two pairs")) {
                if (tempHigh.equals(xHigh.substring(2, 3))) {
                    xHigh = tempHigh + " " + xHigh.substring(0, 1);
                }
                xHighestHand = "Full house " + xHigh;
            } else {
                xHighestHand = temp;
                xHigh = tempHigh;
            }
        }

        temp = Straight(x);
        if (temp.startsWith("Straight")) {
            xHighestHand = temp;
            xHigh = temp.substring(9, 10);
        }

        temp = four_of_a_kind(x);
        if (temp.startsWith("Four of a kind")) {
            xHighestHand = temp;
            xHigh = temp.substring(15, 16);
        }

        temp = five_of_a_kind(x);
        if (temp.startsWith("Five of a kind")) {
            xHighestHand = temp;
            xHigh = temp.substring(15, 16);
        }
        return xHighestHand;
    }

    public static String pairs(Integer[] x) {
        String pairNumber = "";
        String twopairNumber = "";
        boolean pair = false;
        boolean twopair = false;
        if (x[0] == x[1] || x[0] == x[2] || x[0] == x[3] || x[0] == x[4]) {
            pair = true;
            pairNumber = Integer.toString(x[0]);
        }
        if (x[1] == x[2] || x[1] == x[3] || x[1] == x[4]) {
            if (pair) {
                if (!(pairNumber.equals(Integer.toString(x[1])))) {
                    twopair = true;
                    twopairNumber = pairNumber + " " + Integer.toString(x[1]);
                }
            } else {
                pair = true;
                pairNumber = Integer.toString(x[1]);
            }
        }
        if (x[2] == x[3] || x[2] == x[4]) {
            if (pair) {
                if (!(pairNumber.equals(Integer.toString(x[2])))) {
                    twopair = true;
                    twopairNumber = pairNumber + " " + Integer.toString(x[2]);
                }
            } else {
                pair = true;
                pairNumber = Integer.toString(x[2]);
            }
        }
        if (x[3] == x[4]) {
            if (pair) {
                if (!(pairNumber.equals(Integer.toString(x[3])))) {
                    twopair = true;
                    twopairNumber = pairNumber + " " + Integer.toString(x[3]);
                }
            } else {
                pair = true;
                pairNumber = Integer.toString(x[3]);
            }
        }
        if (twopair) {
            return "Two pairs " + twopairNumber;
        } else {
            if (pair) {
                return "Pair " + pairNumber;
            } else {
                return "Nothing";
            }
        }
    }

    public static String three_of_a_kind(Integer[] x) {
        boolean threeOfAKind = false;
        String threeOfAKindNumber = "";
        if ((x[0] == x[1] && x[0] == x[2]) || (x[0] == x[1] && x[0] == x[3])
                || (x[0] == x[1] && x[0] == x[4]) || (x[0] == x[2]
                        && x[0] == x[3]) || (x[0] == x[2] && x[0] == x[4])
                || (x[0] == x[3] && x[0] == x[4])) {
            threeOfAKind = true;
            threeOfAKindNumber = Integer.toString(x[0]);
        } else {
            if ((x[1] == x[2] && x[1] == x[3]) || (x[1] == x[2] && x[1] == x[4])
                    || (x[1] == x[3] && x[1] == x[4])) {
                threeOfAKind = true;
                threeOfAKindNumber = Integer.toString(x[1]);
            } else {
                if ((x[2] == x[3] && x[2] == x[4])) {
                    threeOfAKind = true;
                    threeOfAKindNumber = Integer.toString(x[2]);
                }
            }
        }

        if (threeOfAKind) {
            return "Three of a kind " + threeOfAKindNumber;
        } else {
            return "Nothing";
        }
    }

    public static String four_of_a_kind(Integer[] x) {
        boolean fourOfAKind = false;
        String fourOfAKindNumber = "";
        if ((x[0] == x[1] && x[0] == x[2] && x[0] == x[3]) || (x[0] == x[1]
                && x[0] == x[2] && x[0] == x[4]) || (x[0] == x[1]
                        && x[0] == x[3] && x[0] == x[4]) || (x[0] == x[2]
                                && x[0] == x[3] && x[0] == x[4])) {
            fourOfAKind = true;
            fourOfAKindNumber = Integer.toString(x[0]);
        } else {
            if (x[1] == x[2] && x[1] == x[3] && x[1] == x[4]) {
                fourOfAKind = true;
                fourOfAKindNumber = Integer.toString(x[1]);
            }
        }
        if (fourOfAKind) {
            return "Four of a kind " + fourOfAKindNumber;
        } else {
            return "Nothing";
        }
    }

    public static String five_of_a_kind(Integer[] x) {
        boolean fiveOfAKind = false;
        if (x[0] == x[1] && x[0] == x[2] && x[0] == x[3] && x[0] == x[4]) {
            fiveOfAKind = true;
        }
        if (fiveOfAKind) {
            return "Five of a kind " + Integer.toString(x[0]);
        } else {
            return "Nothing";
        }
    }

    public static String Straight(Integer[] x) {
        boolean Straight = false;
        int min = Math.min(x[0], Math.min(x[1], Math.min(x[2], Math.min(x[3],
                x[4]))));
        int max = Math.max(x[0], Math.max(x[1], Math.max(x[2], Math.max(x[3],
                x[4]))));
        if (min > 2) {
            return "Nothing";
        } else {
            if (max != (min + 4)) {
                return "Nothing";
            }
        }
        if (min == 1) {
            if ((x[0] == 2) || (x[1] == 2) || (x[2] == 2) || (x[3] == 2)
                    || (x[4] == 2)) {
                if ((x[0] == 3) || (x[1] == 3) || (x[2] == 3) || (x[3] == 3)
                        || (x[4] == 3)) {
                    if ((x[0] == 4) || (x[1] == 4) || (x[2] == 4) || (x[3] == 4)
                            || (x[4] == 4)) {
                        if ((x[0] == 5) || (x[1] == 5) || (x[2] == 5)
                                || (x[3] == 5) || (x[4] == 5)) {
                            return "Straight 5";
                        }
                    }
                }
            }
        } else {
            if ((x[0] == 3) || (x[1] == 3) || (x[2] == 3) || (x[3] == 3)
                    || (x[4] == 3)) {
                if ((x[0] == 4) || (x[1] == 4) || (x[2] == 4) || (x[3] == 4)
                        || (x[4] == 4)) {
                    if ((x[0] == 5) || (x[1] == 5) || (x[2] == 5) || (x[3] == 5)
                            || (x[4] == 5)) {
                        if ((x[0] == 6) || (x[1] == 6) || (x[2] == 6)
                                || (x[3] == 6) || (x[4] == 6)) {
                            return "Straight 6";
                        }
                    }
                }
            }
        }
        return "Nothing";
    }

    public static String winner(Integer[] x, Integer[] y, String xHand,
            String yHand) {
        String winner = "";
        int xHandNumber = 0;
        int yHandNumber = 0;
        int xNumber1 = 0;
        int xNumber2 = 0;
        int yNumber1 = 0;
        int yNumber2 = 0;
        if (xHand.startsWith("Five of a kind")) {
            xHandNumber = 8;
            xNumber1 = Integer.parseInt(xHand.substring(15, 16));
        } else {
            if (xHand.startsWith("Four of a kind")) {
                xHandNumber = 7;
                xNumber1 = Integer.parseInt(xHand.substring(15, 16));
            } else {
                if (xHand.startsWith("Straight")) {
                    xHandNumber = 6;
                    xNumber1 = Integer.parseInt(xHand.substring(9, 10));
                } else {
                    if (xHand.startsWith("Full house")) {
                        xHandNumber = 5;
                        xNumber1 = Integer.parseInt(xHand.substring(11, 12));
                        xNumber2 = Integer.parseInt(xHand.substring(13, 14));
                    } else {
                        if (xHand.startsWith("Three of a kind")) {
                            xHandNumber = 4;
                            xNumber1 = Integer.parseInt(xHand.substring(16,
                                    17));
                        } else {
                            if (xHand.startsWith("Two pairs")) {
                                xHandNumber = 3;
                                xNumber1 = Integer.parseInt(xHand.substring(9,
                                        10));
                                xNumber2 = Integer.parseInt(xHand.substring(11,
                                        12));
                            } else {
                                if (xHand.startsWith("Pair")) {
                                    xHandNumber = 2;
                                    xNumber1 = Integer.parseInt(xHand.substring(
                                            5, 6));
                                } else {
                                    if (xHand.startsWith("Nothing")) {
                                        xHandNumber = 1;
                                        xNumber1 = Math.max(x[0], Math.max(x[1],
                                                Math.max(x[2], Math.max(x[3],
                                                        x[4]))));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(xHandNumber);
        if (yHand.startsWith("Five of a kind")) {
            yHandNumber = 8;
            yNumber1 = Integer.parseInt(yHand.substring(15, 16));
        } else {
            if (yHand.startsWith("Four of a kind")) {
                yHandNumber = 7;
                yNumber1 = Integer.parseInt(yHand.substring(15, 16));
            } else {
                if (yHand.startsWith("Straight")) {
                    yHandNumber = 6;
                    yNumber1 = Integer.parseInt(yHand.substring(9, 10));
                } else {
                    if (yHand.startsWith("Full house")) {
                        yHandNumber = 5;
                        yNumber1 = Integer.parseInt(yHand.substring(11, 12));
                        yNumber2 = Integer.parseInt(yHand.substring(13, 14));
                    } else {
                        if (yHand.startsWith("Three of a kind")) {
                            yHandNumber = 4;
                            yNumber1 = Integer.parseInt(yHand.substring(16,
                                    17));
                        } else {
                            if (yHand.startsWith("Two pairs")) {
                                yHandNumber = 3;
                                yNumber1 = Integer.parseInt(yHand.substring(9,
                                        10));
                                yNumber2 = Integer.parseInt(yHand.substring(11,
                                        12));
                            } else {
                                if (yHand.startsWith("Pair")) {
                                    yHandNumber = 2;
                                    yNumber1 = Integer.parseInt(yHand.substring(
                                            5, 6));
                                } else {
                                    if (yHand.startsWith("Nothing")) {
                                        yHandNumber = 1;
                                        yNumber1 = Math.max(y[0], Math.max(y[1],
                                                Math.max(y[2], Math.max(y[3],
                                                        y[4]))));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Quick.sort(x);
        Quick.sort(y);
        if (xHandNumber > yHandNumber) {
            winner = "You Win";
        } else {
            if (xHandNumber < yHandNumber) {
                winner = "Opponent Win";
            } else {
                if (xNumber1 > yNumber1) {
                    winner = "You Win";
                } else {
                    if (xNumber1 < yNumber1) {
                        winner = "Opponent Win";
                    } else {
                        if (xHandNumber == 5 || xHandNumber == 3) {
                            if (xNumber2 > yNumber2) {
                                winner = "You Win";
                            } else {
                                if (xNumber2 < yNumber2) {
                                    winner = "Opponent Win";
                                } else {
                                    if (x[4] > y[4]) {
                                        winner = "You Win";
                                    } else {
                                        if (x[4] < y[4]) {
                                            winner = "Opponent Win";
                                        } else {
                                            if (x[3] > y[3]) {
                                                winner = "You Win";
                                            } else {
                                                if (x[3] < y[3]) {
                                                    winner = "Opponent Win";
                                                } else {
                                                    if (x[2] > y[2]) {
                                                        winner = "You Win";
                                                    } else {
                                                        if (x[2] < y[2]) {
                                                            winner = "Opponent Win";
                                                        } else {
                                                            if (x[1] > y[1]) {
                                                                winner = "You Win";
                                                            } else {
                                                                if (x[1] < y[1]) {
                                                                    winner = "Opponent Win";
                                                                } else {
                                                                    if (x[0] > y[0]) {
                                                                        winner = "You Win";
                                                                    } else {
                                                                        if (x[0] < y[0]) {
                                                                            winner = "Opponent Win";
                                                                        } else {
                                                                            winner = "Draw";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return winner;
    }
}
