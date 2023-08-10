import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LessonGUI {
   public static void main(final String[] args) {
      SwingUtilities.invokeLater(() -> new LessonGUI());
   }

   public LessonGUI() {
      QuizAction.settingFrame = new JFrame("設定");
      QuizAction.returnButton = new JButton("戻る");
      QuizAction.exitButton = new JButton("終了");
      final JFrame frame = new JFrame("語彙/カタカナ"), goiFrame = new JFrame("レッスン"), levelFrame = new JFrame("レベル");
      final JTextField welcome = new JTextField("選んでください");
      final JButton goiButton = new JButton("語彙"), katakanaButton = new JButton("カタカナ"),
            settingButton = new JButton("設定");
      final JButton[] buttons = new JButton[] { goiButton, katakanaButton, settingButton, QuizAction.exitButton };
      final JPanel center = new JPanel(new GridLayout(7, 1)), bottom = new JPanel();
      final Font f = new Font("Dialog", 0, 30);
      frame.setLayout(new GridLayout(5, 1));
      frame.addKeyListener(QuizAction.keyHandle(QuizAction.index, buttons));
      settingButton.addActionListener(e -> QuizAction.makeSettingFrame(frame, QuizAction.settingFrame,
            QuizAction.returnButton, QuizAction.exitButton, f));
      QuizAction.exitButton.addActionListener(e -> System.exit(0));
      QuizAction.returnButton.addActionListener(e1 -> {
         QuizAction.keySet = null;
         frame.dispose();
         QuizAction.settingFrame.dispose();
         levelFrame.dispose();
         goiFrame.dispose();
         SwingUtilities.invokeLater(() -> new LessonGUI());
      });
      goiButton.addActionListener(e -> {
         frame.dispose();
         goiFrame.setLayout(new BorderLayout());
         center.add(welcome);
         final List<JButton> goiButtons = new ArrayList<>();
         for (final File subFolder : new File("src/goi").listFiles()) {
            final JButton tmpBtn = new JButton(subFolder.getName());
            tmpBtn.addActionListener(e1 -> {
               if (tmpBtn.getText().contains("AI")) {
                  goiFrame.dispose();
                  SwingUtilities.invokeLater(() -> new App("goi", tmpBtn.getText()));
               } else {
                  final List<JButton> levelButtons = new ArrayList<>();
                  for (final File level : new File("src/goi/" + tmpBtn.getText()).listFiles()) {
                     final JButton levelBtn = new JButton(level.getName());
                     levelBtn.addActionListener(e2 -> {
                        levelFrame.dispose();
                        SwingUtilities.invokeLater(() -> new App("goi", tmpBtn.getText() + "/" + levelBtn.getText()));
                     });
                     levelButtons.add(levelBtn);
                  }
                  goiFrame.dispose();
                  levelFrame.setLayout(new BorderLayout());
                  final JPanel levelCenter = new JPanel(new GridLayout(levelButtons.size() + 1, 1)),
                        levelBottom = new JPanel(new FlowLayout());
                  levelCenter.add(welcome);
                  levelButtons.forEach(b -> levelCenter.add(b));
                  QuizAction.addManyComponent(levelBottom, QuizAction.returnButton, QuizAction.exitButton);
                  levelFrame.add(levelCenter, BorderLayout.CENTER);
                  levelFrame.add(levelBottom, BorderLayout.SOUTH);
                  levelButtons.add(QuizAction.returnButton);
                  levelButtons.add(QuizAction.exitButton);
                  levelFrame.addKeyListener(
                        QuizAction.keyHandle(QuizAction.index, levelButtons.toArray(new JButton[levelButtons.size()])));
                  QuizAction.setupMainFrame(levelFrame, QuizAction.returnButton, QuizAction.exitButton, f, levelCenter,
                        levelBottom, levelButtons, QuizAction.index);
               }
            });
            goiButtons.add(tmpBtn);
            center.add(tmpBtn);
         }
         goiButtons.add(QuizAction.returnButton);
         goiButtons.add(QuizAction.exitButton);
         goiFrame.addKeyListener(
               QuizAction.keyHandle(QuizAction.index, goiButtons.toArray(new JButton[goiButtons.size()])));
         QuizAction.setupMainFrame(goiFrame, QuizAction.returnButton, QuizAction.exitButton, f, center, bottom,
               goiButtons, QuizAction.index);
      });
      katakanaButton.addActionListener(e -> {
         frame.dispose();
         goiFrame.setLayout(new BorderLayout());
         center.setLayout(new GridLayout(7, 1));
         center.add(welcome);
         final List<JButton> goiButtons = new ArrayList<>();
         for (final File subFolder : new File("src/katakana").listFiles()) {
            final JButton tmpBtn = new JButton(subFolder.getName());
            tmpBtn.addActionListener(e1 -> {
               goiFrame.dispose();
               SwingUtilities.invokeLater(() -> new App("katakana", tmpBtn.getText()));
            });
            goiButtons.add(tmpBtn);
            center.add(tmpBtn);
         }
         goiButtons.add(QuizAction.returnButton);
         goiButtons.add(QuizAction.exitButton);
         goiFrame.addKeyListener(
               QuizAction.keyHandle(QuizAction.index, goiButtons.toArray(new JButton[goiButtons.size()])));
         QuizAction.setupMainFrame(goiFrame, QuizAction.returnButton, QuizAction.exitButton, f, center, bottom,
               goiButtons, QuizAction.index);
      });
      QuizAction.setupComponent(f, welcome, goiButton, katakanaButton, QuizAction.exitButton, QuizAction.returnButton,
            settingButton);
      buttons[QuizAction.index.getIndex() % buttons.length].setBackground(QuizAction.focusColor);
      QuizAction.addManyComponent(frame, welcome, goiButton, katakanaButton, settingButton, QuizAction.exitButton);
      QuizAction.setupFrame(frame, "語彙/カタカナ", new Dimension(900, 700));
   }
}