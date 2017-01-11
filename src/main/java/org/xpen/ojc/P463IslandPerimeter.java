package org.xpen.ojc;


public class P463IslandPerimeter {
    
    public int islandPerimeter(int[][] grid) {
        int result = 0;
        int x = grid.length-1;
        int y = grid[0].length-1;
        for (int i = x; i >=0; i--) {
            for (int j = y; j >=0; j--) {
                if (grid[i][j]==1) {
                    if (i==0) {
                        result++;
                    } else if (grid[i-1][j]==0) {
                        result++;
                    }
                    if (i==x) {
                        result++;
                    } else if (grid[i+1][j]==0) {
                        result++;
                    }
                    if (j==0) {
                        result++;
                    } else if (grid[i][j-1]==0) {
                        result++;
                    }
                    if (j==y) {
                        result++;
                    } else if (grid[i][j+1]==0) {
                        result++;
                    }
                }
            }
            
        }
        return result;
    }
    
    public static void main(String[] args) {
        P463IslandPerimeter main = new P463IslandPerimeter();
        int[][] grid = {
         {0,1,0,0},
         {1,1,1,0},
         {0,1,0,0},
         {1,1,0,0}};
        
        System.out.println(main.islandPerimeter(grid));
    }

}

