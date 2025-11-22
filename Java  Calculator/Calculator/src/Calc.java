import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

public class Calc {
    int boardWidth = 360;
    int boardHeight = 540;

    Color customLightGray = new Color(212, 212, 210);
    Color customDarkGray = new Color(80, 80, 80);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255, 149, 0);

    String[] buttonValues = {
            "AC", "+/-", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "√", "="
    };
    String[] rightSymbols = {"÷", "×", "-", "+", "="};
    String[] topSymbols = {"AC", "+/-", "%"};

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    // A+B, A-B, A*B, A/B
    String A = "0";
    String operator = null;
    String B = null;

    public Calc() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // display
        displayLabel.setBackground(customBlack);
        displayLabel.setForeground(Color.white);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setOpaque(true);

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(displayLabel, BorderLayout.CENTER);
        frame.add(displayPanel, BorderLayout.NORTH);

        // buttons grid
        buttonsPanel.setLayout(new GridLayout(5, 4, 6, 6)); // gaps so you see the round shape
        buttonsPanel.setBackground(customBlack);
        frame.add(buttonsPanel, BorderLayout.CENTER);

        // create buttons
        for (int i = 0; i < buttonValues.length; i++) {
            String buttonValue = buttonValues[i];

            RoundButton button = new RoundButton(buttonValue);
            button.setFont(new Font("Arial", Font.PLAIN, 28));
            button.setFocusable(false);
            button.setForeground(Color.white);

            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                button.setBackground(customLightGray);
                button.setForeground(customBlack);
            } else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                button.setBackground(customOrange);
                button.setForeground(Color.white);
            } else {
                button.setBackground(customDarkGray);
                button.setForeground(Color.white);
            }

            buttonsPanel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton btn = (JButton) e.getSource();
                    String value = btn.getText();

                    if (Arrays.asList(rightSymbols).contains(value)) {
                        // operators / =
                        if (value.equals("=")) {
                            if (A != null && operator != null) {
                                B = displayLabel.getText();
                                double numA = Double.parseDouble(A);
                                double numB = Double.parseDouble(B);

                                switch (operator) {
                                    case "+":
                                        displayLabel.setText(removeZeroDecimal(numA + numB));
                                        break;
                                    case "-":
                                        displayLabel.setText(removeZeroDecimal(numA - numB));
                                        break;
                                    case "×":
                                        displayLabel.setText(removeZeroDecimal(numA * numB));
                                        break;
                                    case "÷":
                                        displayLabel.setText(removeZeroDecimal(numA / numB));
                                        break;
                                }
                                clearAll();
                            }
                        } else if ("+-×÷".contains(value)) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                            }
                            operator = value;
                        }
                    } else if (Arrays.asList(topSymbols).contains(value)) {
                        // top row: AC, +/-, %
                        if (value.equals("AC")) {
                            clearAll();
                            displayLabel.setText("0");
                        } else if (value.equals("+/-")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay *= -1;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        } else if (value.equals("%")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay /= 100;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                    } else { // digits or .
                        if (value.equals(".")) {
                            if (!displayLabel.getText().contains(".")) {
                                displayLabel.setText(displayLabel.getText() + ".");
                            }
                        } else if ("0123456789".contains(value)) {
                            if (displayLabel.getText().equals("0")) {
                                displayLabel.setText(value);
                            } else {
                                displayLabel.setText(displayLabel.getText() + value);
                            }
                        }
                    }
                }
            });
        }

        frame.setVisible(true);
    }

    void clearAll() {
        A = "0";
        operator = null;
        B = null;
    }

    String removeZeroDecimal(double numDisplay) {
        if (numDisplay % 1 == 0) {
            return Integer.toString((int) numDisplay);
        }
        return Double.toString(numDisplay);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calc::new);
    }
}

/**
 * Rounded pill-style button.
 */
class RoundButton extends JButton {
    public RoundButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false); // we draw background
        setOpaque(false);            // no default background
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = Math.min(w, h); // circle / pill

        // background color changes slightly when pressed
        Color base = getBackground();
        if (getModel().isArmed() || getModel().isPressed()) {
            // darker when pressed
            base = base.darker();
        }
        g2.setColor(base);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // clip to the rounded shape so text / focus stays inside circle
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        // draw the text on top
        super.paintComponent(g2);

        g2.dispose();
    }

    @Override
    public void setContentAreaFilled(boolean b) {
        // ignore; we handle background ourselves
    }

    @Override
    protected void paintBorder(Graphics g) {
        // no border (optional: you could draw a round border here)
    }
}

