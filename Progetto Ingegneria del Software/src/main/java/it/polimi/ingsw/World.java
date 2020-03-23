package it.polimi.ingsw;

import java.awt.image.ImageProducer;

public class World {
        static Space[][] tab;
        tab = new Space[5][5];

        public World () {
            tab = new Space[5][5];
            for(int i=0;i<5;i++) {
                for(int j=0;j<5;j++) {
                    tab[i][j]=new Space(i,j);
                }
            }
        }
        public static Space getSpaces(int x, int y){
            return tab [x][y] ;
        }

        public static boolean isInTab(int y, int x)
        {
            return y > -1 && y < 5 && x > -1 && x < 5;
        }

        public static boolean isNext(int xattuale, int yattuale, int xmovimento, int ymovimento)
        {
            for (int i=yattuale-1;i<yattuale+2;i=i+1){
                for (int j=xattuale-1;i<xattuale+2;j=j+1){
                    if(i==ymovimento && j==xmovimento)
                        return true;
                }
            }
            return false;
        }

        public static int levelDifference(int xattuale, int yattuale, int xmovimento, int ymovimento){
            int k;
            k=getSpaces(xattuale, yattuale).getLevel()-getSpaces(xmovimento, ymovimento).getLevel();
            return k;
        }

        public static Boolean canMoveThere(int xattuale,int yattuale,int xmovimento,int ymovimento){
            int k = levelDifference(xattuale, yattuale, xmovimento, ymovimento);
            return ( isNext(xattuale,yattuale, xmovimento, ymovimento) && isInTab(ymovimento,xmovimento) && k>-2 && !getSpaces(xmovimento,ymovimento).isOccupied() );
        }
        public static Boolean canMoveThere(int xattuale,int yattuale,int xmovimento,int ymovimento)

        public static boolean canBuildThere(int xattuale, int yattuale, int xmovimento, int ymovimento) {
            return (isInTab(xmovimento,ymovimento) && isNext(xattuale,yattuale,xmovimento,ymovimento) && !getSpaces(xmovimento,ymovimento).isOccupied())
    }
}


