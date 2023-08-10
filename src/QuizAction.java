import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class QuizAction {
   public static Index index = new Index();
   public static final String[] good = new String[] { "すごいね！", "やったね！", "よくできました！", "スゴイ！", "エクセレント！", "いいね！" };
   public static boolean isRandomMode = false, isMute = false, isEnabledSupportMessage = true;
   public static final Color defaultColor = Color.white, focusColor = Color.cyan;
   public static final int delayMs = 100;
   public static List<String> keySet = null;
   public static JButton returnButton, exitButton;
   public static JFrame settingFrame;

   public static void addManyComponent(final Container f, final JComponent... component) {
      for (final JComponent c : component)
         f.add(c);
   }

   public static KeyAdapter keyHandle(final Index index, final AbstractButton... buttons) {
      return new KeyAdapter() {
         @Override
         public void keyPressed(final KeyEvent e) {
            final int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
               buttons[index.getIndex() % buttons.length].doClick();
            } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
               buttons[index.getIndex() % buttons.length].setBackground(defaultColor);
               index.setIndex((index.getIndex() + buttons.length + keyCode - 39) % buttons.length);
               buttons[index.getIndex() % buttons.length].setBackground(focusColor);
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
               exitButton.doClick();
            }
         }
      };
   }

   public static void setupComponent(final Font f, final Object... component) {
      for (final Object o : component)
         if (o instanceof JComponent) {
            final JComponent c = (JComponent) o;
            c.setFocusable(false);
            c.setFont(f);
            if (c instanceof AbstractButton) {
               c.setBackground(defaultColor);
               ((AbstractButton) c).setHorizontalAlignment(SwingConstants.CENTER);
            } else if (c instanceof JTextField) {
               final JTextField tmp = (JTextField) c;
               tmp.setEditable(false);
               tmp.setHorizontalAlignment(SwingConstants.CENTER);
            }
         }
   }

   public static void readFile(final String fileName, final List<String> list) {
      String line = null;
      try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
         while ((line = reader.readLine()) != null)
            list.add(line);
      } catch (final IOException e) {
         System.err.println("Error reading file: " + e.getMessage());
         System.exit(1);
      }
   }

   public static void setupFrame(final JFrame frame, final String title, final Dimension d) {
      frame.setTitle(title);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(d);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void playSound(final String file) {
      try {
         final Clip clip = AudioSystem.getClip();
         clip.open(AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile()));
         clip.start();
      } catch (final Exception e) {
         System.err.println("Error reading file: " + e.getMessage());
         System.exit(1);
      }
   }

   public static void setupMainFrame(final JFrame frame, final JButton returnBtn, final JButton exit, final Font f,
         final JPanel center, final JPanel bottom, final List<JButton> buttons, final Index index) {
      QuizAction.setupComponent(f, buttons.toArray());
      QuizAction.addManyComponent(bottom, returnBtn, exit);
      frame.add(center, BorderLayout.CENTER);
      frame.add(bottom, BorderLayout.SOUTH);
      QuizAction.setupComponent(f, returnBtn, exit);
      buttons.get(index.getIndex()).requestFocus();
      buttons.get(index.getIndex()).setBackground(QuizAction.focusColor);
      QuizAction.setupFrame(frame, "レッスン", new Dimension(900, 700));
   }

   public static void makeSettingFrame(final JFrame frame, final JFrame settingFrame, final JButton returnButton,
         final JButton exitButton, final Font f) {
      frame.dispose();
      QuizAction.settingFrame.setLayout(new BorderLayout());
      final JPanel mid = new JPanel(new GridLayout(3, 2)), bottomSetting = new JPanel(),
            randomPanel = new JPanel(new GridLayout(2, 1)), mutePanel = new JPanel(new GridLayout(2, 1)),
            messagePanel = new JPanel(new GridLayout(2, 1));
      final JRadioButton randomButton = new JRadioButton("ランダムして選ぶ", QuizAction.isRandomMode),
            noRandomButton = new JRadioButton("全部チェックする", !QuizAction.isRandomMode),
            muteButton = new JRadioButton("はい", QuizAction.isMute),
            noMuteButton = new JRadioButton("いいえ", !QuizAction.isMute),
            messageButton = new JRadioButton("はい", QuizAction.isEnabledSupportMessage),
            noMessageButton = new JRadioButton("いいえ", !QuizAction.isEnabledSupportMessage);
      final ButtonGroup randomGroup = new ButtonGroup(), muteGroup = new ButtonGroup(),
            messageGroup = new ButtonGroup();
      final JTextField mode = new JTextField("モード"), mute = new JTextField("無音"), message = new JTextField("応援メッセージ");
      randomButton.addActionListener(e1 -> QuizAction.isRandomMode = true);
      noRandomButton.addActionListener(e1 -> QuizAction.isRandomMode = false);
      muteButton.addActionListener(e1 -> QuizAction.isMute = true);
      noMuteButton.addActionListener(e1 -> QuizAction.isMute = false);
      messageButton.addActionListener(e1 -> QuizAction.isEnabledSupportMessage = true);
      noMessageButton.addActionListener(e1 -> QuizAction.isEnabledSupportMessage = false);
      randomGroup.add(randomButton);
      randomGroup.add(noRandomButton);
      muteGroup.add(muteButton);
      muteGroup.add(noMuteButton);
      messageGroup.add(messageButton);
      messageGroup.add(noMessageButton);
      QuizAction.addManyComponent(randomPanel, randomButton, noRandomButton);
      QuizAction.addManyComponent(mutePanel, muteButton, noMuteButton);
      QuizAction.addManyComponent(messagePanel, messageButton, noMessageButton);
      QuizAction.addManyComponent(bottomSetting, QuizAction.returnButton, QuizAction.exitButton);
      QuizAction.addManyComponent(mid, mode, randomPanel, mute, mutePanel, message, messagePanel);
      QuizAction.settingFrame.add(mid, BorderLayout.CENTER);
      QuizAction.settingFrame.add(bottomSetting, BorderLayout.SOUTH);
      final AbstractButton[] buttons = new AbstractButton[] { randomButton, noRandomButton, muteButton, noMuteButton,
            messageButton, noMessageButton, returnButton, exitButton };
      QuizAction.settingFrame.addKeyListener(QuizAction.keyHandle(index, buttons));
      QuizAction.setupComponent(f, mode, mute, message, randomButton, noRandomButton, muteButton, noMuteButton,
            messageButton, noMessageButton);
      buttons[index.getIndex()].requestFocus();
      buttons[index.getIndex()].setBackground(focusColor);
      QuizAction.setupFrame(QuizAction.settingFrame, "設定", new Dimension(900, 400));
   }
}