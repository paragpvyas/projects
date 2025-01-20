/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dmtermproj;

/**
 *
 * @author PV
 */
public class DMTermProj {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        int numOfTrans = 100000;
        int minItem = 0;
        int maxItem = 999;
        
        Apriori testy = new Apriori("OGdata.txt", numOfTrans, minItem, maxItem, 
                                    20000);
        //testy.minSupCount = 2;
        testy.findLargeItemsets();
        //testy.printRowLargeItemset(testy.saveLargeItemsets, 1);
        
        //CandidateList test = new CandidateList();
        
        /*
        int numOfTrans = 9;
        int minItem = 1;
        int maxItem = 5;
        
        Apriori testy = new Apriori("testData2.txt", numOfTrans, minItem, maxItem);
        testy.minSupCount = 2;
        testy.findLargeItemsets();
        */
        /*
        int[] trans = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        16, 17, 18, 19, 20, 21, 22, 23, 24 ,25};
        int n = trans.length;
        int k = 25;
        int[][] destArr = null;
        
        destArr = testy.getSubsets(trans, n, k);
        
        System.out.println("numofSubsets: " + destArr.length);
        */
        
        /*
        for(int i = 0; i < destArr.length; i++){
            for(int j = 0; j < destArr[i].length; j++)
                System.out.print(destArr[i][j] + " ");
            System.out.println();
        }
        */
        
        /*
        Apriori test = new Apriori("testdata.txt", 4);
        test.initApriori();
        test.printDatabase();
        System.out.println("maxTransWidth: " + test.maxTransWidth);
        System.out.println("numOfTrans: " + test.databaseSize);
        */
        /*
        int[] arr = {1};
        int[] arr2 = {2};
        int[] arr3 = {3};
        int[] arr4 = {4};
        
        System.out.println(test);
        
        test.append(arr);
        test.append(arr2);
        test.append(arr3);
        test.append(arr4);
        
        System.out.println(test);
        
        test.removeTail();
        test.removeItemset(arr);
        
        System.out.println(test);
        test.removeTail();
        //test.removeItemset(arr4);
        
        System.out.println(test);
        
        //test.removeItemset(arr2);
        test.removeTail();
        test.removeHead();
        System.out.println(test);
    */
        
        
        
    }
    
}
