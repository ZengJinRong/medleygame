package pers.zengjinrong.medleygame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * 基于JAVA的移动拼图游戏实现
 *
 * @author ZengJinRong
 * @version 2.1
 */
public class MedleyGame extends JFrame {
    private final String TITLE = "拼图游戏";      //标题
    private final int ROWS = 4;                 //拼图行数
    private final int COLS = 4;                 //拼图列数
    private final String IMG_URL = "res/image/image.jpg";   //默认图片所在路径

    private JPanel imagesPanel;         //拼图面板
    private JLabel emptyImageLabel;     //空白拼图板块
    private JLabel modelLabel;          //略缩图展示板块
    private Image image;                //用于拼图显示的完整图片
    private Image imageMini;            //完整图片的略缩图
    private Image[][] images;           //图片经等分切割生成的图片组，分别用于各个拼图板块的显示
    private JLabel[][] labels = new JLabel[ROWS][COLS];

    /**
     * 主函数入口
     */
    public static void main(String[] args) {
        MedleyGame medleyGame = new MedleyGame();
        medleyGame.setVisible(true);

    }

    /**
     * 拼图游戏构造函数
     */
    private MedleyGame() {
        super();
        lookAndFeelInit();
        windowInit();
        menuBarInit();
        topPanelInit();
        imagesPanelInit();
        imageInit(IMG_URL);
    }

    /**
     * UI风格初始化
     */
    private void lookAndFeelInit(){
        //设置UI风格为当前系统风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * 窗口初始化
     */
    private void windowInit() {
        setResizable(false);
        setTitle(TITLE);
        setSize(390, 635);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * 菜单栏初始化
     */
    private void menuBarInit() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menuFile = new JMenu("文件");
        final JMenuItem menuItemOpen = new JMenuItem("打开");
        menuFile.add(menuItemOpen);
        menuItemOpen.addActionListener(new MenuItemOpenAction());
        menuBar.add(menuFile);
        setJMenuBar(menuBar);
    }

    /**
     * 顶部面板初始化
     */
    private void topPanelInit() {
        final JPanel topPanel = new JPanel();
        topPanel.setSize(384, 192);
        topPanel.setBounds(0, 0, 384, 192);
        topPanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        topPanel.setLayout(new BorderLayout());
        getContentPane().add(topPanel);

        modelLabel = new JLabel();
        topPanel.add(modelLabel, BorderLayout.WEST);

        final JButton startButton = new JButton();
        startButton.setText("下一局");
        startButton.addActionListener(new ReStartButtonAction());
        topPanel.add(startButton, BorderLayout.CENTER);
    }

    /**
     * 拼图面板初始化
     */
    private void imagesPanelInit() {
        imagesPanel = new JPanel();
        imagesPanel.setSize(384, 384);
        imagesPanel.setBounds(0, 192, 384, 384);
        imagesPanel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        imagesPanel.setLayout(new GridLayout(ROWS, COLS));
        getContentPane().add(imagesPanel);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                //循环遍历每一行每一列
                labels[row][col] = new JLabel();
                JLabel label = labels[row][col];
                label.setName(row + "" + col);
                label.addMouseListener(new ImageMouseAdapter());
                imagesPanel.add(label);
            }
        }
    }

    /**
     * 拼图图片初始化
     */
    private void imageInit(String url) {
        File file = new File(url);
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "图片加载错误！",
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        if (image != null) {
            image = ImageTailor.cutImageIntoSquare(image, this);
            image = image.getScaledInstance(384, 384, Image.SCALE_SMOOTH);
            imageMini = image.getScaledInstance(192, 192, Image.SCALE_SMOOTH);
            images = ImageTailor.divideImage(image, ROWS, COLS, this);
            modelLabel.setIcon(new ImageIcon(imageMini));
            orderImageLabels();
        } else {
            JOptionPane.showMessageDialog(null, "图片加载错误！",
                    "错误", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * 对各个拼图板块显示的图片进行排序
     */
    private void orderImageLabels() {
        Image[][] rightOrder = new Image[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.arraycopy(images[row], 0, rightOrder[row], 0, images[row].length);
            }
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                //循环遍历每一行每一列

                //设置显示的图片
                if (row == 0 && col == 0) {
                    //第0行第0列设置为空白图片
                    labels[row][col].setIcon(null);
                    emptyImageLabel = labels[row][col];
                } else {
                    while (true) {
                        int randomRow = (int) (Math.random() * ROWS);
                        int randomCol = (int) (Math.random() * COLS);

                        if (rightOrder[randomRow][randomCol] != null) {
                            //该图片还未被使用过
                            labels[row][col].setIcon(new ImageIcon(rightOrder[randomRow][randomCol]));
                            rightOrder[randomRow][randomCol] = null;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 拼图板块点击监听器
     */
    class ImageMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            String emptyName = emptyImageLabel.getName();
            char emptyRow = emptyName.charAt(0);
            char emptyCol = emptyName.charAt(1);
            JLabel clickImageLabel = (JLabel) event.getSource();
            String clickName = clickImageLabel.getName();
            char clickRow = clickName.charAt(0);
            char clickCol = clickName.charAt(1);

            if (Math.abs(clickRow - emptyRow) + Math.abs(clickCol - emptyCol) == 1) {
                emptyImageLabel.setIcon(clickImageLabel.getIcon());
                clickImageLabel.setIcon(null);
                emptyImageLabel = clickImageLabel;
            }
        }
    }

    /**
     * 开始键监听器
     */
    class ReStartButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            orderImageLabels();
        }
    }

    /**
     * 文件菜单项[打开]监听器
     */
    class MenuItemOpenAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileDialog fileDialog = new FileDialog(new Frame(), "选择图片", FileDialog.LOAD);
            fileDialog.setVisible(true);
            if (fileDialog.getFile()!=null&&fileDialog.getDirectory()!=null){
                String fileURL = fileDialog.getDirectory() + fileDialog.getFile();
                imageInit(fileURL);
            }
        }
    }

}
