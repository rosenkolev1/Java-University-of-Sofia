public class DataCenter {

    public static int getCommunicatingServersCount(int[][] map){

        int serversCommunicating = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {

                int currentCell = map[i][j];
                if(currentCell == 1 || currentCell == 2){

                    int oldServersCommunicating = serversCommunicating;
                    boolean currentServerIsCommunicating = false;

                    //Check column
                    for (int row = 0; row < map.length; row++) {
                        if(map[row][j] == 1 && row != i) {
                            map[row][j] = 2;
                            serversCommunicating++;
                            currentServerIsCommunicating = true;
                        }
                        else if(map[row][j] == 2 && row != i){
                            currentServerIsCommunicating = true;
                        }
                    }

                    //Check row
                    for (int col = 0; col < map[i].length; col++) {
                        if(map[i][col] == 1 && col != j){
                            map[i][col] = 2;
                            serversCommunicating++;
                            currentServerIsCommunicating = true;
                        }
                        else if(map[i][col] == 2 && col != j){
                            currentServerIsCommunicating = true;
                        }
                    }

                    //Add the server we are checking from itself
                    if(currentCell == 1 && currentServerIsCommunicating) {
                        serversCommunicating++;
                        map[i][j] = 2;
                    }
                }

            }
        }

        //Return the array to normal
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if(map[i][j] == 2) map[i][j] = 1;
            }
        }
        
        return serversCommunicating;
    }
}
