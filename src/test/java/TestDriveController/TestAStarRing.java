package TestDriveController;

import com.robotino.drive.AStar;
import com.robotino.drive.Spot;

import java.util.List;

public class TestAStarRing {


    public TestAStarRing() {
        List<Spot> blocked = new AStar().getRingOfNeighbor(new Spot(50, 50));
        for(Spot spot : blocked) {
            System.out.println(spot.toString());
        }
        System.out.println(blocked.size());

        String [][] grid = new String[100][100];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if(isFree(blocked, j, i)) {
                    grid[i][j] = "  .  ";
                }else{
                    grid[i][j] = "  X  ";
                }
            }
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++){
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }

    }

    public boolean isFree(List<Spot> blocked , int i, int j){
        for(Spot block : blocked) {
            if(i == block.x && j == block.y){
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        new TestAStarRing();
    }
}
