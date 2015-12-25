package org.xpen.maze;

/****************************************************************************/
/**Author: Sailor                                                          */
/**Vision: Maze1.00                                                       */
/**Date: 2010-04-15                                                      */
/**E-Mail:zpsailor@yahoo.com.cn                                         */  
/**QQ:251396377                                                        */
/**********************************************************************/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 迷宫
 * 出处：http://zpsailor.iteye.com/blog/651141
 *
 */
@SuppressWarnings("serial")
public class Maze extends JFrame implements ActionListener {

    private JPanel panel;
    private JPanel northPanel;
    private JPanel centerPanel;
    private MazeGrid grid[][];
    private JButton restart;
    private JButton dostart;
    private JButton genDemo;
    private int rows;// rows 和cols目前暂定只能是奇数
    private int cols;
    private List<String> willVisit;
    private List<String> visited;
    private LinkedList<String> comed;
    private long startTime;
    private long endTime;
    private int visitX;
    private int visitY;
    

    public Maze() {
        rows = 25;
        cols = 25;
        willVisit = new ArrayList<String>();
        visited = new ArrayList<String>();
        comed = new LinkedList<String>();
        init();
        this.setTitle("回溯法--走迷宫");
        this.add(panel);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void init() {
        panel = new JPanel();
        northPanel = new JPanel();
        centerPanel = new JPanel();
        panel.setLayout(new BorderLayout());
        restart = new JButton("重新生成迷宫");
        genDemo = new JButton("一步步生成迷宫");
        dostart = new JButton("开始走迷宫");
        grid = new MazeGrid[rows][cols];

        centerPanel.setLayout(new GridLayout(rows, cols, 1, 1));
        centerPanel.setBackground(new Color(0, 0, 0));
        northPanel.add(restart);
        northPanel.add(genDemo);
        northPanel.add(dostart);

        dostart.addActionListener(this);
        genDemo.addActionListener(this);
        restart.addActionListener(this);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (j % 2 == 0 && i % 2 == 0) {
                    grid[i][j] = new MazeGrid(true, 20, 20);
                } else {
                    grid[i][j] = new MazeGrid(false, 20, 20);
                }
            }
        }
        grid[0][0].setVisited(true);
        grid[0][0].setPersonCome(true);
        grid[0][0].setStart(true);
        visited.add("0#0");
        grid[rows - 1][cols - 1].setEnd(true);
        //从左上角开始走，深度优先遍历来生成迷宫
        //grid = createMap(grid, 0, 0);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].repaint();
                centerPanel.add(grid[i][j]);
            }
        }

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 生成迷宫
     * 
     * @param x
     * @param y
     * @return
     */
    public void createMap(int x, int y) {
        //int visitX = 0;
        //int visitY = 0;
        //左2
        if (x - 2 >= 0) {
            if (!grid[x - 2][y].isVisited()) {
                willVisit.add((x - 2) + "#" + y);
            }
        }
        //右2
        if (x + 2 < cols) {
            if (!grid[x + 2][y].isVisited()) {
                willVisit.add((x + 2) + "#" + y);
            }
        }
        //上2
        if (y - 2 >= 0) {
            if (!grid[x][y - 2].isVisited()) {
                willVisit.add(x + "#" + (y - 2));
            }
        }
        //下2
        if (y + 2 < rows) {
            if (!grid[x][y + 2].isVisited()) {
                willVisit.add(x + "#" + (y + 2));
            }
        }
        if (!willVisit.isEmpty()) {
            //随机选一个方向
            int visit = (int) (Math.random() * willVisit.size());
            String id = willVisit.get(visit);
            visitX = Integer.parseInt(id.split("#")[0]);
            visitY = Integer.parseInt(id.split("#")[1]);
            //中间通道打开
            grid[(visitX + x) / 2][(visitY + y) / 2].setMark(true);
            grid[(visitX + x) / 2][(visitY + y) / 2].repaint();

            grid[visitX][visitY].setVisited(true);
            if (!visited.contains(id)) {// 将这个点加到已访问中去
                visited.add(id);
            }
            willVisit.clear();
            //递归
            //createMap(visitX, visitY);
        } else {
            //无路可走
            if (!visited.isEmpty()) {
                //回溯
                String id = visited.remove(visited.size() - 1);// 取出最后一个元素
                visitX = Integer.parseInt(id.split("#")[0]);
                visitY = Integer.parseInt(id.split("#")[1]);
                grid[visitX][visitY].setVisited(true);
                createMap(visitX, visitY);
            } else {
                JOptionPane.showMessageDialog(null, "已经生成迷宫"
                        ,"消息提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * 走迷宫
     * 
     * @param mazeGrid
     * @param x
     * @param y
     */
    public String goMaze(MazeGrid mazeGrid[][], int x, int y) {
        int comeX = 0;
        int comeY = 0;
        // left
        if (x - 1 >= 0) {
            if (mazeGrid[x - 1][y].isMark()) {
                if (!comed.contains((x - 1) + "#" + y))
                    willVisit.add((x - 1) + "#" + y);
            }
        }
        // right
        if (x + 1 < cols) {
            if (mazeGrid[x + 1][y].isMark()) {
                if (!comed.contains((x + 1) + "#" + y))
                    willVisit.add((x + 1) + "#" + y);
            }
        }
        // up
        if (y - 1 >= 0) {
            if (mazeGrid[x][y - 1].isMark()) {
                if (!comed.contains(x + "#" + (y - 1)))
                    willVisit.add(x + "#" + (y - 1));
            }
        }
        // down
        if (y + 1 < rows) {
            if (mazeGrid[x][y + 1].isMark()) {
                if (!comed.contains(x + "#" + (y + 1)))
                    willVisit.add(x + "#" + (y + 1));
            }
        }
        if (!willVisit.isEmpty()) {
            int visit = (int) (Math.random() * willVisit.size());
            String id = willVisit.get(visit);
            comeX = Integer.parseInt(id.split("#")[0]);
            comeY = Integer.parseInt(id.split("#")[1]);
            mazeGrid[x][y].setPersonCome(false);
            mazeGrid[comeX][comeY].setPersonCome(true);
            mazeGrid[x][y].repaint();
            mazeGrid[comeX][comeY].repaint();
            willVisit.clear();
            comed.add(x + "#" + y);
        } else {
            if (!comed.isEmpty()) {
                String id = comed.removeLast();
                comeX = Integer.parseInt(id.split("#")[0]);
                comeY = Integer.parseInt(id.split("#")[1]);
                mazeGrid[x][y].setPersonCome(false);
                mazeGrid[comeX][comeY].setPersonCome(true);
                mazeGrid[x][y].repaint();
                mazeGrid[comeX][comeY].repaint();
                comed.addFirst(x + "#" + y);
            }
        }
        return comeX + "#" + comeY;
    }

    int comeX = 0;
    int comeY = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("重新生成迷宫")) {
            refreshMap(grid);
        } else if (e.getActionCommand().equals("一步步生成迷宫")) {
            createMap(visitX, visitY);
        } else if (e.getActionCommand().equals("开始走迷宫")) {
            startTime = System.currentTimeMillis();
            dostart.setVisible(false);
            restart.setText("禁止刷新");
            int delay = 1000;
            int period = 500;// 循环间隔
            java.util.Timer timer = new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (grid[rows - 1][cols - 1].isPersonCome()) {
                        endTime = System.currentTimeMillis();
                        JOptionPane.showMessageDialog(null, "已经走出迷宫，耗时"
                                + (endTime - startTime) / 1000 + "秒", "消息提示",
                                JOptionPane.ERROR_MESSAGE);
                        this.cancel();
                        restart.setText("重新生成迷宫");
                    } else {
                        String id = goMaze(grid, comeX, comeY);
                        comeX = Integer.parseInt(id.split("#")[0]);
                        comeY = Integer.parseInt(id.split("#")[1]);
                    }
                }
            }, delay, period);
        }
    }

    /**
     * 刷新地图
     */
    public void refreshMap(MazeGrid mazeGrid[][]) {
        comeX = 0;
        comeY = 0;
        visitX = 0;
        visitY = 0;
        willVisit.clear();
        visited.clear();
        comed.clear();
        this.remove(panel);
        init();
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String args[]) {
        long start = System.currentTimeMillis();
        new Maze();
        long end = System.currentTimeMillis();
        System.out.println("使用ArrayList生成迷宫耗时：" + (end - start) + "毫秒");
    }
}
