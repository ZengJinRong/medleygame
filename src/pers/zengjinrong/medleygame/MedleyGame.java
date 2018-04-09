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
 * @version 1.0
 */
public class MedleyGame extends JFrame {
    private final String TITLE="拼图游戏";      //标题
    private final int ROWS = 4;                 //拼图行数
    private final int COLS = 4;                 //拼图列数
    private final String IMG_URL = "res/image/image.jpg";   //图片所在路径

    private JPanel imagesPanel;         //拼图面板
    private JLabel emptyImageLabel;     //空白拼图板块
    private Image image;                //用于拼图显示的完整图片
    private Image imageMini;            //完整图片的略缩图
    private Image[][] images;           //图片经等分切割生成的图片组，分别用于各个拼图板块的显示

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
        imageInit();

        windowInit();
        topPanelInit();
        ImagesPanelInit();
    }

    /**
     * 拼图图片初始化
     */
    private void imageInit() {
        File file = new File(IMG_URL);
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = ImageTailor.cutImageIntoSquare(image, this);
        image = image.getScaledInstance(384, 384, Image.SCALE_SMOOTH);
        imageMini = image.getScaledInstance(192, 192, Image.SCALE_SMOOTH);
        images = ImageTailor.divideImage(image, ROWS, COLS, this);
    }

    /**
     * 窗口初始化
     */
    private void windowInit() {
        setResizable(false);
        setTitle(TITLE);
        setSize(390, 615);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        final JLabel modelLabel = new JLabel();
        modelLabel.setIcon(new ImageIcon(imageMini));
        topPanel.add(modelLabel, BorderLayout.WEST);

        final JButton startButton = new JButton();
        startButton.setText("下一局");
        startButton.addActionListener(new StartButtonAction());
        topPanel.add(startButton, BorderLayout.CENTER);
    }

    /**
     * 拼图面板初始化
     */
    private void ImagesPanelInit() {
        imagesPanel = new JPanel();
        imagesPanel.setSize(384, 384);
        imagesPanel.setBounds(0, 192, 384, 384);
        imagesPanel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        imagesPanel.setLayout(new GridLayout(ROWS, COLS));
        getContentPane().add(imagesPanel);

        orderImageLabels();
    }

    /**
     * 对各个拼图板块显示的图片进行排序
     */
    private void orderImageLabels() {
        imagesPanel.removeAll();
        Image[][] rightOrder = images.clone();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                //循环遍历每一行每一列
                final JLabel label = new JLabel();
                label.setName(row + "" + col);
                label.addMouseListener(new ImageMouseAdapter());

                //设置显示的图片
                if (row == 0 && col == 0) {
                    //第0行第0列设置为空白图片
                    label.setIcon(null);
                    emptyImageLabel = label;
                } else {
                    while (true) {
                        int randomRow = (int) (Math.random() * ROWS);
                        int randomCol = (int) (Math.random() * COLS);

                        if (rightOrder[randomRow][randomCol] != null) {
                            //该图片还未被使用过
                            label.setIcon(new ImageIcon(rightOrder[randomRow][randomCol]));
                            rightOrder[randomRow][randomCol] = null;
                            break;
                        }
                    }
                }
                imagesPanel.add(label);
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
    class StartButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            orderImageLabels();
        }
    }
}
