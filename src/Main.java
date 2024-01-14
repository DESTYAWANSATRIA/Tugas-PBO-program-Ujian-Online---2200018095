import java.awt.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main extends JFrame implements ActionListener {
    JButton[][] buttons;
    JButton start;
    JButton viewRanking;
    JLabel timerLabel;
    Timer timer;
    int secondsPassed;
    int currentLevel;
    int puzzleSize;
    private Clip backgroundMusic;
    private Clip buttonClickSound;
    private String playerName; // Variable to store player's name
    private long lastUpdateTime;
    private JTextArea rankingTextArea;
    private Clip winningSound;
    Main() {
        super("Puzzle Game - JavaTpoint");

        try {
            // Load and play background music
            File musicFile = new File("runamok.wav"); // Replace with your music file name
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Load winning sound
            File winningSoundFile = new File("winning_game.wav"); // Replace with your winning sound file name
            AudioInputStream winningSoundInput = AudioSystem.getAudioInputStream(winningSoundFile);
            winningSound = AudioSystem.getClip();
            winningSound.open(winningSoundInput);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle or display error to the user
        }

        JLabel background = new JLabel(new ImageIcon("background.jpg"));
        background.setBounds(0, 0, 1550, 1080);
        add(background);

        JLabel titleLabel = new JLabel("Puzzle Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(50, 30, 200, 40);
        background.add(titleLabel);

        rankingTextArea = new JTextArea();
        rankingTextArea.setEditable(false);
        rankingTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(rankingTextArea);
        scrollPane.setBounds(1100, 160, 300, 500);
        background.add(scrollPane);

        // Inisialisasi GUI
        setLayout(null);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Agar frame muncul di tengah layar
        setVisible(true);

        // Tampilkan ranking langsung setelah inisialisasi GUI
        showRanking();

        try {
            // Load button click sound
            File buttonClickFile = new File("tap-notification-180637.wav");
            AudioInputStream buttonClickAudioInput = AudioSystem.getAudioInputStream(buttonClickFile);
            buttonClickSound = AudioSystem.getClip();
            buttonClickSound.open(buttonClickAudioInput);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle or display error to the user
        }

        timerLabel = new JLabel("00:00");
        timerLabel.setBounds(725, 110, 100, 40);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        background.add(timerLabel);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Atur alignment horizontal ke CENTER
        timerLabel.setVerticalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBackground(new Color(70, 130, 180));
        timerLabel.setOpaque(true);


        start = new JButton("Start");
        start.setBounds(700, 30, 150, 60);
        start.setFont(new Font("Arial", Font.BOLD, 20));
        background.add(start);
        start.addActionListener(this);
        start.setBackground(new Color(255, 0, 0));
        start.setForeground(Color.WHITE);
        start.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 255, 255));

        start.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                start.setBackground(new Color(87, 24, 24)); // Warna hijau lebih gelap saat hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                start.setBackground(new Color(252, 7, 7)); // Kembali ke warna semula setelah keluar dari hover
            }
        });

        // Add window listener to handle resource cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (backgroundMusic != null && backgroundMusic.isOpen()) {
                    backgroundMusic.close();
                }
            }
        });
        // Initialize frame in EDT
        SwingUtilities.invokeLater(() -> {
            setLayout(null);
            setSize(400, 500);
            setVisible(true);
        });
    }



    private void initializeButtons() {
        puzzleSize = currentLevel + 3;
        buttons = new JButton[puzzleSize][puzzleSize];

        int buttonSize = 100;
        int startX = (getWidth() - buttonSize * puzzleSize) / 2;  // Hitung posisi x untuk menengahkan puzzle
        int startY = (getHeight() - buttonSize * puzzleSize) / 2; // Hitung posisi y untuk menengahkan puzzle

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= puzzleSize * puzzleSize - 1; i++) {
            numbers.add(i);
        }



        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setBounds(startX + j * buttonSize, startY + i * buttonSize, buttonSize, buttonSize);
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 26));
                buttons[i][j].setBackground(Color.ORANGE);
                buttons[i][j].setForeground(Color.BLACK);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(this);



                buttons[i][j].addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        JButton hoverButton = (JButton) evt.getSource();
                        if (!hoverButton.getText().isEmpty()) {
                            hoverButton.setBackground(new Color(252, 7, 7, 255)); // Warna hijau lebih gelap saat hover
                        }
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        JButton exitButton = (JButton) evt.getSource();
                        if (!exitButton.getText().isEmpty()) {
                            exitButton.setBackground(Color.ORANGE); // Kembali ke warna semula setelah keluar dari hover
                        }
                    }
                });

                add(buttons[i][j]);
            }
        }
    }

    private void cleanUpGame() {
        if (buttons != null) {
            for (int i = 0; i < puzzleSize; i++) {
                for (int j = 0; j < puzzleSize; j++) {
                    remove(buttons[i][j]);
                }
            }
        }

        if (timer != null) {
            timer.stop();
        }

        buttons = null;
    }

    private void startGame() {
        cleanUpGame();
        initializeButtons();
        resetPuzzle();
        timerLabel.setText("00:00");
        secondsPassed = 0;
        lastUpdateTime = System.currentTimeMillis();

        // Inisialisasi timer
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerText(); // Update timer text every second
            }
        });

        timer.start();
        start.setEnabled(false);

        // Start playing background music
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void resetPuzzle() {
        int maxCounter = (currentLevel + 3) * (currentLevel + 3);
        List<Integer> numbers = new ArrayList<>();

        for (int i = 1; i < maxCounter; i++) {
            numbers.add(i);
        }

        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (buttons[i][j] != buttons[puzzleSize - 1][puzzleSize - 1]) {
                    int randomIndex = (int) (Math.random() * numbers.size());
                    buttons[i][j].setText(String.valueOf(numbers.remove(randomIndex)));
                } else {
                    buttons[i][j].setText(""); // Empty space for the last button
                }
            }
        }
    }

    private void shufflePuzzle() {
        int maxCounter = (currentLevel + 3) * (currentLevel + 3);
        List<Integer> numbers = new ArrayList<>();

        for (int i = 1; i < maxCounter; i++) {
            numbers.add(i);
        }

        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (buttons[i][j] != buttons[puzzleSize - 1][puzzleSize - 1]) {
                    int randomIndex = (int) (Math.random() * numbers.size());
                    buttons[i][j].setText(String.valueOf(numbers.remove(randomIndex)));
                } else {
                    buttons[i][j].setText(""); // Empty space for the last button
                }
            }
        }
    }

    private JButton findEmptyButton() {
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return buttons[i][j];
                }
            }
        }
        return null;
    }

    private boolean isAdjacent(JButton button1, JButton button2) {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        Point pos1 = findButtonPosition(button1);
        Point pos2 = findButtonPosition(button2);

        for (int i = 0; i < 4; i++) {
            int newX = pos1.x + dx[i];
            int newY = pos1.y + dy[i];
            if (newX >= 0 && newX < puzzleSize && newY >= 0 && newY < puzzleSize && newX == pos2.x && newY == pos2.y) {
                return true;
            }
        }
        return false;
    }

    private Point findButtonPosition(JButton button) {
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (buttons[i][j] == button) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1, -1);
    }

    private void swapButtons(JButton button1, JButton button2) {
        String temp = button1.getText();
        button1.setText(button2.getText());
        button2.setText(temp);
    }

    private boolean isGameComplete() {
        int counter = 1;
        int maxCounter = puzzleSize * puzzleSize - 1;

        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (!buttons[i][j].getText().equals("") && Integer.parseInt(buttons[i][j].getText()) != counter) {
                    return false;
                }
                counter++;
                if (counter > maxCounter) {
                    counter = 1; // Reset hitungan untuk level 2 dan 3
                }
            }
        }

        // Jika sampai di sini, berarti permainan selesai
        return true;
    }

    private void notifyGameComplete() {
        // Stop background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        // Play winning sound
        if (winningSound != null) {
            winningSound.stop();
            winningSound.setFramePosition(0);
            winningSound.start();
        }

        JOptionPane.showMessageDialog(Main.this, "Level " + (currentLevel + 1) + " selesai! Waktu: " + timerLabel.getText());
        saveTimeToFile(playerName, timerLabel.getText());
        showRanking();
        start.setEnabled(true);
    }


    private void saveTimeToFile(String playerName, String timer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("time_record_level" + (currentLevel + 1) + ".txt", true))) {
            writer.println(playerName);
            writer.println(timer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showRanking() {
        List<String[]> rankingData = new ArrayList<>();
        FileToArray("time_record_level" + (currentLevel + 1) + ".txt", rankingData);
        SortingArray2D(rankingData);
        SortingArray2D(rankingData);
    }

    private void SortingArray2D(List<String[]> data) {
        data.sort(Comparator.comparing(arr -> arr[1]));

        StringBuilder result = new StringBuilder("Ranking :\n");

        int rank = 1;
        for (String[] row : data) {
            result.append(String.format("%d. %-20s %s\n", rank++, row[0], row[1]));
        }

        // Menampilkan hasil ke JTextArea
        rankingTextArea.setText(result.toString());

        // Menyesuaikan tata letak dan tampilan teks JTextArea
        rankingTextArea.setMargin(new Insets(10, 10, 10, 10));
        rankingTextArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        rankingTextArea.setForeground(Color.BLACK);
        rankingTextArea.setBackground(Color.YELLOW);
    }


    private void updateTimerText() {
        secondsPassed++;
        int minutes = secondsPassed / 60;
        int seconds = secondsPassed % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton == start) {
            playerName = JOptionPane.showInputDialog(this, "Masukkan Nickname Anda:");

            if (playerName != null && !playerName.isEmpty()) {
                cleanUpGame();
                String[] levels = {"Level 1", "Level 2", "Level 3"};
                String selectedLevel = (String) JOptionPane.showInputDialog(
                        this,
                        "Pilih Level",
                        "Pemilihan Level",
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        levels,
                        levels[0]
                );

                if (selectedLevel != null) {
                    currentLevel = Arrays.asList(levels).indexOf(selectedLevel);
                    startGame();
                } else {
                    JOptionPane.showMessageDialog(this, "Harap masukkan nama yang valid!");
                }
            }
        } else if (clickedButton == viewRanking) {
            // Hentikan timer sebelum menampilkan menu peringkat
            timer.stop();
            showRanking();
        } else {
            JButton emptyButton = findEmptyButton();
            if (emptyButton != null && isAdjacent(emptyButton, clickedButton)) {
                swapButtons(emptyButton, clickedButton);

                // Tambahkan pengecekan apakah permainan sudah selesai setelah swapButtons
                if (isGameComplete()) {
                    timer.stop();
                    notifyGameComplete();
                }
            }
            playButtonClickSound();
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= 1000) {
            updateTimerText();
            lastUpdateTime = currentTime;
        }
    }

    private void FileToArray(String filename, List<String[]> dataList) {
        File file = new File(filename);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] row = new String[2];
                row[0] = line; // Nama pemain
                line = bufferedReader.readLine(); // Baca baris berikutnya untuk waktu
                row[1] = line; // Waktu
                dataList.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.stop();
            buttonClickSound.setFramePosition(0);
            buttonClickSound.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }
}
