import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GUI extends JFrame {

    private JPanel contentPane;
    public static final Color WOOD = new Color(  186,  140, 99);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dice Poker");
        JButton roll = new JButton("Roll Dice");
        JLabel own1 = new JLabel();
        own1.setBounds(120, 450, 75, 75);
        JLabel own2 = new JLabel();
        own2.setBounds(210, 450, 75, 75);
        JLabel own3 = new JLabel();
        own3.setBounds(300, 450, 75, 75);
        JLabel own4 = new JLabel();
        own4.setBounds(390, 450, 75, 75);
        JLabel own5 = new JLabel();
        own5.setBounds(480, 450, 75, 75);
        JLabel opponent1 = new JLabel();
        opponent1.setBounds(120, 200, 75, 75);
        JLabel opponent2 = new JLabel();
        opponent2.setBounds(210, 200, 75, 75);
        JLabel opponent3 = new JLabel();
        opponent3.setBounds(300, 200, 75, 75);
        JLabel opponent4 = new JLabel();
        opponent4.setBounds(390, 200, 75, 75);
        JLabel opponent5 = new JLabel();
        opponent5.setBounds(480, 200, 75, 75);
        JTextField own = new JTextField();
        own.setBounds(75, 550, 500, 80);
        own.setHorizontalAlignment(JTextField.CENTER);
        own.setBackground(WOOD);
        own.setBorder(new LineBorder(WOOD, 2));
        own.setFont(new Font("sans serif", Font.BOLD, 35));
        own.setEditable(false);
        JTextField opponent = new JTextField();
        opponent.setBounds(75, 120, 600, 80);
        opponent.setHorizontalAlignment(JTextField.CENTER);
        opponent.setBackground(WOOD);
        opponent.setBorder(new LineBorder(WOOD, 2));
        opponent.setFont(new Font("sans serif", Font.BOLD, 35));        
        opponent.setEditable(false);
        JTextField winner = new JTextField();
        winner.setBounds(120, 325, 450, 80);
        winner.setHorizontalAlignment(JTextField.CENTER);
        winner.setBackground(WOOD);
        winner.setBorder(new LineBorder(WOOD, 2));
        winner.setFont(new Font("sans serif", Font.BOLD, 54));        
        winner.setEditable(false);
        roll.setBounds(630, 660, 95, 30);
        roll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer[] ownRoll = new Integer[5];
                Integer[] opponentRoll = new Integer[5];
                ImageIcon icon = new ImageIcon();
                int width = own1.getWidth();
                int height = own1.getHeight();
                for (int k = 0; k < 5; k++) {
                    ownRoll[k] = Dice.Dice_Roll();
                } 
                for (int k = 0; k < 5; k++) {
                    opponentRoll[k] = Dice.Dice_Roll();
                }
                icon = new ImageIcon(m(ownRoll[0], width, height));
                own1.setIcon(icon);
                icon = new ImageIcon(m(ownRoll[1], width, height));
                own2.setIcon(icon);
                icon = new ImageIcon(m(ownRoll[2], width, height));
                own3.setIcon(icon);
                icon = new ImageIcon(m(ownRoll[3], width, height));
                own4.setIcon(icon);
                icon = new ImageIcon(m(ownRoll[4], width, height));
                own5.setIcon(icon);
                icon = new ImageIcon(m(opponentRoll[0], width, height));
                opponent1.setIcon(icon);
                icon = new ImageIcon(m(opponentRoll[1], width, height));
                opponent2.setIcon(icon);
                icon = new ImageIcon(m(opponentRoll[2], width, height));
                opponent3.setIcon(icon);
                icon = new ImageIcon(m(opponentRoll[3], width, height));
                opponent4.setIcon(icon);
                icon = new ImageIcon(m(opponentRoll[4], width, height));
                opponent5.setIcon(icon);
                String ownHand = Dice.hands(ownRoll);
                own.setText("Your hand: " + ownHand);
                String opponentHand = Dice.hands(opponentRoll);
                opponent.setText("Opponent hand: " + opponentHand);
                String winningPlayer = Dice.winner(ownRoll, opponentRoll, ownHand, opponentHand);
                winner.setText(winningPlayer);
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(WOOD);
        frame.add(roll);
        frame.add(own1);
        frame.add(own2);
        frame.add(own3);
        frame.add(own4);
        frame.add(own5);
        frame.add(opponent1);
        frame.add(opponent2);
        frame.add(opponent3);
        frame.add(opponent4);
        frame.add(opponent5);
        frame.add(own);
        frame.add(opponent);
        frame.add(winner);
        frame.setSize(750, 750);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public static Image m(int x, int width, int height) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("../images/Dice_" + Integer.toString(x)
                    + ".png"));
        } catch (IOException e) {
            System.out.println("Error");
        }
        Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return dimg;
    }

}
