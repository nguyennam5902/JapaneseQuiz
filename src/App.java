import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class App extends JFrame {
    private final JButton[] answerButtons = new JButton[4];
    private final List<String> wordList = new ArrayList<>();
    private final ArrayList<String>[] lists = new ArrayList[] { new ArrayList<>(), new ArrayList<>() };
    private final Timer timer = new Timer();
    private final JTextField timerField = new JTextField("0.0", 5);
    private final String mode, level;
    private int currentIndex = 0, correct = 0, meanOrReadIndex = 0;

    public App(final String mode, final String wordFolder) {
        this.mode = mode.equals("goi") ? "語彙" : "カタカナ";
        this.level = wordFolder;
        final Random r = new Random();
        final Container cp = getContentPane();
        final JTextField questionPane = new JTextField(), correctLeft = new JTextField("正解:"),
                correctRight = new JTextField(correct + "", 10), indexField = new JTextField(),
                timerText = new JTextField("Time:");
        final JButton settingButton = new JButton("設定");
        final Font f = new Font(questionPane.getFont().getName(), 0, 25);
        final JPanel top = new JPanel(), center = new JPanel(new GridLayout(5, 1));
        cp.setLayout(new BorderLayout());
        settingButton.addActionListener(e -> {
            QuizAction.makeSettingFrame(this, QuizAction.settingFrame, QuizAction.returnButton, QuizAction.exitButton,
                    f);
        });
        QuizAction.exitButton.addActionListener(e -> System.exit(0));
        center.add(questionPane);
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton();
            QuizAction.setupComponent(f, answerButtons[i]);
            answerButtons[i].setBackground(QuizAction.defaultColor);
            center.add(answerButtons[i]);
        }
        cp.add(top, BorderLayout.NORTH);
        cp.add(center, BorderLayout.CENTER);
        addKeyListener(QuizAction.keyHandle(QuizAction.index, answerButtons));
        if (mode.equals("goi")) {
            QuizAction.readFile("src/goi/" + wordFolder + "/goi", wordList);
            QuizAction.readFile("src/goi/" + wordFolder + "/imi", lists[0]);
            QuizAction.readFile("src/goi/" + wordFolder + "/yomikata", lists[1]);
            final Map<String, String>[] maps = new HashMap[] { new HashMap<>(), new HashMap<>() };
            for (int i = 0; i < wordList.size(); i++) {
                // 0 index: goi --> imi
                maps[0].put(wordList.get(i), lists[0].get(i));
                // 1 index: goi --> yomikata
                maps[1].put(wordList.get(i), lists[1].get(i));
            }
            if (QuizAction.isRandomMode)
                updateProblemSet(mode, r, wordList, questionPane);
            else {
                meanOrReadIndex = r.nextInt(2);
                updateProblemSet(r, questionPane, maps[meanOrReadIndex], meanOrReadIndex);
            }
            indexField.setText("問題: " + currentIndex + (!QuizAction.isRandomMode ? " / " + maps[0].size() : ""));
            for (int i = 0; i < 4; i++) {
                final int tmpI = i;
                answerButtons[i].addActionListener(e -> {
                    final String correctAnswer = maps[meanOrReadIndex].get(questionPane.getText());
                    final JLabel label = new JLabel();
                    label.setFont(f);
                    if (answerButtons[tmpI].getText().equals(correctAnswer)) {
                        if (!QuizAction.isMute)
                            QuizAction.playSound("src/audio/true.wav");
                        correctRight.setText(++correct + "");
                        label.setText(questionPane.getText() + " | " + maps[1].get(questionPane.getText()) + " --> "
                                + maps[0].get(questionPane.getText()));
                    } else {
                        if (!QuizAction.isMute)
                            QuizAction.playSound("src/audio/false.wav");
                        label.setText("正解例: " + correctAnswer);
                    }
                    if (QuizAction.isEnabledSupportMessage)
                        JOptionPane.showMessageDialog(null, label);
                    if (QuizAction.isRandomMode)
                        updateProblemSet(mode, r, wordList, questionPane);
                    else {
                        meanOrReadIndex = r.nextInt(2);
                        updateProblemSet(r, questionPane, maps[meanOrReadIndex], meanOrReadIndex);
                    }
                    indexField.setText("問題: " + currentIndex + (QuizAction.isRandomMode ? "" : " / " + maps[0].size()));
                });
            }
        } else if (mode.equals("katakana")) {
            QuizAction.readFile("src/katakana/" + wordFolder + "/eigo", wordList);
            QuizAction.readFile("src/katakana/" + wordFolder + "/katakanago", lists[0]);
            final Map<String, String> maps = new HashMap<>();
            for (int i = 0; i < wordList.size(); i++)
                maps.put(wordList.get(i), lists[0].get(i));
            if (QuizAction.isRandomMode)
                updateProblemSet(mode, r, wordList, questionPane);
            else
                updateProblemSet(r, questionPane, maps, 0);
            indexField.setText("問題: " + currentIndex + (!QuizAction.isRandomMode ? " / " + maps.size() : ""));
            for (int i = 0; i < 4; i++) {
                final int tmpI = i;
                answerButtons[i].addActionListener(e -> {
                    final String correctAnswer = maps.get(questionPane.getText());
                    final JLabel label = new JLabel();
                    label.setFont(f);
                    if (answerButtons[tmpI].getText().equals(correctAnswer)) {
                        if (!QuizAction.isMute)
                            QuizAction.playSound("src/audio/true.wav");
                        correctRight.setText(++correct + "");
                        label.setText(QuizAction.good[r.nextInt(QuizAction.good.length)]);
                    } else {
                        if (!QuizAction.isMute)
                            QuizAction.playSound("src/audio/false.wav");
                        label.setText("正解例: " + correctAnswer);
                    }
                    if (QuizAction.isEnabledSupportMessage)
                        JOptionPane.showMessageDialog(null, label);
                    if (QuizAction.isRandomMode)
                        updateProblemSet(mode, r, wordList, questionPane);
                    else
                        updateProblemSet(r, questionPane, maps, 0);
                    indexField.setText("問題: " + currentIndex + (!QuizAction.isRandomMode ? (" / " + maps.size()) : ""));
                });
            }
        }
        answerButtons[0].requestFocus();
        answerButtons[0].setBackground(QuizAction.focusColor);
        QuizAction.setupComponent(f, questionPane, correctRight, correctLeft, settingButton, QuizAction.exitButton,
                indexField, timerField, timerText);
        QuizAction.addManyComponent(top, indexField, correctLeft, correctRight, timerText, timerField, settingButton,
                QuizAction.exitButton);
        QuizAction.exitButton.setBackground(Color.red);
        setFocusable(true);
        QuizAction.setupFrame(this, "模擬試験", Toolkit.getDefaultToolkit().getScreenSize());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerField.setText(
                        String.format("%.1f", Double.parseDouble(timerField.getText()) + QuizAction.delayMs / 1000.0));
            }
        }, 0, QuizAction.delayMs);
    }

    private void updateProblemSet(final Random r, final JTextField qPane, final Map<String, String> map,
            final int mapIndex) {
        if (currentIndex == map.size()) {
            timer.cancel();
            final String result = String.format("%.1f", correct * 100.0 / map.size());
            final JLabel label = new JLabel("<html>モード : <font color = \"red\">" + this.mode + " (" + this.level
                    + ")</font>" + "<br>正解: <font color = \"blue\">" + correct + " / " + map.size()
                    + "</font><br>パーセント: <font color = \"green\">" + result
                    + "</font>%<br>時間: <font color = \"ff33ff\">" + timerField.getText()
                    + "</font> 秒<br>もう一度しますか?</html>");
            label.setFont(new Font("Dialog", 0, 20));
            if (JOptionPane.showConfirmDialog(null, label, "完全!", 0) != 0)
                System.exit(0);
            else {
                dispose();
                QuizAction.keySet = null;
                currentIndex = 0;
                SwingUtilities.invokeLater(() -> new LessonGUI());
            }
        } else {
            if (QuizAction.keySet == null) {
                final List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
                Collections.shuffle(list);
                QuizAction.keySet = new ArrayList<>(map.keySet());
            }
            qPane.setText(QuizAction.keySet.get(currentIndex));
            final List<Integer> nums = new ArrayList<>();
            nums.add(currentIndex);
            while (nums.size() < 4) {
                final int num = r.nextInt(QuizAction.keySet.size());
                if (!nums.contains(num))
                    nums.add(num);
            }
            Collections.shuffle(nums);
            for (int i = 0; i < 4; i++)
                answerButtons[i].setText(map.get(QuizAction.keySet.get(nums.get(i))));
            currentIndex++;
        }
    }

    private void updateProblemSet(final String mode, final Random r, final List<String> qList, final JTextField qPane) {
        try {
            currentIndex++;
            final int questionIndex = r.nextInt(qList.size());
            qPane.setText(qList.get(questionIndex));
            final List<Integer> nums = new ArrayList<>();
            nums.add(questionIndex);
            while (nums.size() < 4) {
                final int num = r.nextInt(qList.size());
                if (!nums.contains(num))
                    nums.add(num);
            }
            Collections.shuffle(nums);
            meanOrReadIndex = mode.equals("katakana") ? 0 : r.nextInt(2);
            for (int i = 0; i < 4; i++)
                answerButtons[i].setText(lists[meanOrReadIndex].get(nums.get(i)));
        } catch (final IllegalArgumentException e) {
            System.out.println("Size: " + qList.size());
            System.exit(1);
        }

    }
}