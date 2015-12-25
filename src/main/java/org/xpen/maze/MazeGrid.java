package org.xpen.maze;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 * 迷宫的每个格子
 *
 */
@SuppressWarnings("serial")
public class MazeGrid extends Canvas {

    private boolean mark;// 标记是否是通路，TRUE为通路，FALSE为不通
    private boolean isVisited;// 标记是否是访问过的,这是在生成迷宫的时候判断的。
    private boolean isPersonCome;// 标记是否已经走过
    private boolean isStart;// 判断是否为入口
    private boolean isEnd;// 判断是否为出口

    public MazeGrid() {
        this.setBackground(new Color(120, 0, 0));
        this.setSize(25, 25);
    }

    public MazeGrid(boolean mark, int width, int height) {
        this.mark = mark;
        this.setSize(width, height);
        if (mark) {
            this.setBackground(new Color(255, 255, 255));
        } else {
            this.setBackground(new Color(120, 0, 0));
        }
    }

    public void paint(Graphics g) {
        if (mark) {
            if (isStart || isEnd) {
                this.setBackground(new Color(255,0,0));
            } else
                this.setBackground(new Color(255, 255, 255));
        } else {
            this.setBackground(new Color(120, 0, 0));
        }
        if (isPersonCome) {
            g.setColor(Color.BLACK);
            g.fillOval(2, 2, 15, 15);
        }

    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public boolean isPersonCome() {
        return isPersonCome;
    }

    public void setPersonCome(boolean isPersonCome) {
        this.isPersonCome = isPersonCome;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
}
