/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dmtermproj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 *
 * @author PV
 */
public class Apriori {

    /**Description - a 2-d integer array is used to store pointers to 
     * transactions represented with s 1-d integer arrays of integer items
     */
    public int[][] database;

    /**Description - integer of the total number of transactions in the database
     */
    public int databaseSize;

    /**Description - integer of the size of the largest transaction
     */
    public int maxTransWidth;

    /**Description - the largest integer value of any of the items 
     */
    public int maxItem;

    /**Description - the smallest integer value of any of the items
     */
    public int minItem;

    /**Description - A CandidateList implements a singly-linked list of integer 
     *               arrays for candidate itemset storage and manipulation
     */
    public CandidateList Ck;

    /**Description - the kth level of Apriori's level-wise approach. Each level
     * represents the size of large k-itemsets we are looking.
     */
    public int k;

    /**Description - a 3-d integer array to store pointers to 2-d large itemsets 
     *               lists of 1-d integer array transactions of integer items
     */
    public int[][][] saveLargeItemsets;

    /** Description - the name of the file storing the database transactions to
     *                be used.
     */
    public String fileName;

    /** Description - the percent of the database transactions a k-itemset 
     *                should be apart to be considered large.
     *
     */
    public int minSupport;

    /** Description - integer of the transaction count threshold of minimum 
     *                support for the itemset to be considered large
     *
     */
    public int minSupCount;

  
    
    /**Description - the default constructor
     * 
     * Input: a call to instantiate object of this class 
     * Output: an instance of the class fully constructed, the following object 
     *         fields are initialized as follows:
     * 
     * this.database = null;
     * this.numOfTrans = 0;
     * this.Ck = null,
     * this.fileName = null;
     * this.prevLargeItemsets = null
     * this.maxTransWidth = 0
     * this.maxItem = 0
     * this.minItem = 0
     * this.minSupCount = 0
     *
     */
    public Apriori(){
        this(null, 0, null, null, null, 0, 0, 0, 0);
    }
    
    
    /** Description - driver constructor for Apriori class, but not full 
     *                constructor 
     * 
     * Input: a call to instantiate object of this class 
     * OutPut: an instance of the class constructed with user parameters for 
     *         filename, number of transactions, min and max value of items,
     *         and minimum support count. The additional objects intialized are
     *         initialized as follows:
     * 
     * this.database = null;
     * this.numOfTrans = 0;
     * this.Ck = null,
     * this.prevLargeItemsets = null
     * this.maxTransWidth = 0
     * @param _fileName - a file with lines of spaced integers 
     *                      ended with a newline for each line
     * @param _numOfTrans - the prior known number of transactions 
     *                      of the dataset
     * @param _minItem - the smallest item value
     * @param _maxItem - the largest item value
     */
    public Apriori(String _fileName, 
                   int _numOfTrans, 
                   int _minItem,  
                   int _maxItem,
                   int _minSupCount){
        
        this(null, _numOfTrans ,null, _fileName, null, 0, _minItem, _maxItem,
             _minSupCount);
    }    
    
    /**Description - the full constructor for Apriori class
     *
     * Input: a call to instantiate object of this class 
     * Out: an instance of this class fully constructed;
     * 
     * @param _database - a 2-dimensional integer array of transactions
     * @param _numOfTrans - the prior known number of transactions 
     *                      of the dataset
     * @param _Ck - a CandidateList for processing of integer candidate itemsets 
     * @param _fileName - a file with lines of spaced integers 
     *                    ended with a newline for each line
     * @param _saveLargeItemsets - A 3-dimensional array that holds 2-dimensional  
     *                              lists large k-itemsets 
     * @param _maxTransWidth - the longest transaction in the database
     * @param _minItem - the largest item value
     * @param _maxItem - the smallest item value
     */
    public Apriori(int[][] _database, 
                   int _numOfTrans, 
                   CandidateList _Ck, 
                   String _fileName, 
                   int[][][] _saveLargeItemsets,
                   int _maxTransWidth, 
                   int _minItem, 
                   int _maxItem,
                   int _minSupCount){
        
        this.database = _database;
        this.databaseSize = _numOfTrans;
        this.maxTransWidth = _maxTransWidth;
        this.maxItem = _maxItem;
        this.minItem = _minItem;
        this.Ck = _Ck;
        this.saveLargeItemsets = _saveLargeItemsets;
        this.fileName = _fileName;
        this.minSupCount = _minSupCount;
    }
    
    
    /**Desciption - the main loop for the implemented Apriori algorithm to find
     *              all large itemsets in this object's transaction database.
     * 
     * Input: The database must be populated and CandidateList must be created 
     * Output: All large k-itemset lists are stored in the saveLargeItemsets 
     *         object field
     */
    public void findLargeItemsets(){
        this.initApriori();
        this.createL1(this.database, this.Ck, this.minItem, this.maxItem, 
                      this.k, this.minSupCount);
        boolean isEmpty  = this.createAndSaveLK(this.saveLargeItemsets, this.Ck, 
                           this.k);
        while(!isEmpty){
            this.k++;
            this.printLargeKItemset(this.saveLargeItemsets, this.k-1);
            this.aproriGen(this.saveLargeItemsets[this.k - 2], this.Ck, this.k);
            if(this.Ck.isEmpty() != true){
            this.subsetTransactionAndSupCount(this.database, this.Ck, this.k);
            System.out.println("C" + this.k + " after candidate support count C" 
                                + " is:" + '\n' + this.Ck);
            this.Ck.removeInfreqItemSets(this.minSupCount);
             System.out.println("C" + this.k + " after removing infrequent" 
                                + " itemsets is:" + '\n' + this.Ck);
            }
            isEmpty = this.createAndSaveLK(this.saveLargeItemsets, this.Ck, 
                                           this.k);
            
        }
        
        System.out.println("Finished counting all large itemsets in database");
        for(int i = 1; i < this.k; i++){
            this.printLargeKItemset(saveLargeItemsets, i);
        }
    }
    
    /**Description - initialize core data structures for large itemset generation
     * 
     * Input: an instance of this class
     * Output: the database is created from the file, and the saveLargeItemsets  
     *          and CandidateList data structures are instantiated;
     * 
     */
    public void initApriori(){
        this.createDatabase();
        this.saveLargeItemsets = new int[this.maxTransWidth][][];
        this.Ck = new CandidateList();
        this.k = 1;
    }
    
    /**Description - stored a large k-itemset list 
     * 
     * Input: the 3-d integer array of large itemset lists is used to store a 
     *        deep copy of the large k-itemsets finished and stored in the 
     *        CandidateList.
     * Ouput: the large k-itemset list is stored in the large itemset list, and
     *        the CandidateList is cleared all itemsets.
     *
     * @param _largeItemsetList - 3-d integer array used to stored 2-d arrays of
     *                            large k-itemsets
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer current size of the k-itemsets 
     * @return
     */
    public boolean createAndSaveLK(int[][][] _largeItemsetList, 
                                   CandidateList _Ck, 
                                   int _k){
        boolean isEmpty = true;
        if(!_Ck.isEmpty()){
            isEmpty = false;
            int[][] Lk = _Ck.toArray();
            int listSize = Lk.length;
            int itemsetSize  = this.k;
            _largeItemsetList[_k-1] = new int[listSize][itemsetSize];
            for(int i = 0; i < listSize; i++){
                for(int j = 0; j < itemsetSize; j++)
                    _largeItemsetList[_k-1][i][j] = Lk[i][j];
            }
            this.Ck.clear();
        }
        return isEmpty;
    }
    
    /**Description - create the single-candidate list from all members of the 
     *                continuous integer range between the minItem and maxItem
     * 
     * Input: An instance of this class with it CandidateList structure to 
     *       create all 1-itemsets between user defined integer range
     * Output: This instance's CandidateList has stored all candidate 1-itemsets
     *
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _minItem - positive integer of the largest item value
     * @param _maxItem - positive integer of the smallest item value
     */
    public void createC1(CandidateList _Ck, 
                         int _minItem, 
                         int _maxItem){
        
        int itemRange = (_maxItem - _minItem) + 1;
        int item = 0;
        //elevate the range to a non-zero minItem, Ex Case: starting at 1
        if(_minItem > 0);
            item = _minItem;
        int[] itemset = null;
        for(int i = 0; i < itemRange; i++, item++){
            itemset = new int[1];
            itemset[0] = item;        
            //add candidate 1-itemsets to CandidateList structure
            _Ck.append(itemset);
            itemset = null;
        } 
    }
    
    /**Description - count support for all candidate 1-itemsets, then create the 
     *               Large 1-itemsets list by removing infrequent itemsets from 
     *               the CandidateList.
     * 
     * Input: The CandidateList and minItem and maxItem are used to create the 
     *        1-itemset candidate list. Which then have their support counts
     *        done then this instance's database. Finally the minimum support 
     *        count is used for removal of infrequent canddate 1-items;
     * Output: 
     *
     * @param database - a 2-dimensional integer array of transactions
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _minItem - positive integer of the largest item value
     * @param _maxItem - positive integer of the smallest item value
     * @param _k - positive integer of current size of the k-itemsets 
     * @param _minSup - the user defined min
     */
    public void createL1(int[][] database, 
                         CandidateList _Ck, 
                         int _minItem, 
                         int _maxItem, 
                         int _k, 
                         int _minSup){
        
        this.createC1(_Ck, _minItem, _maxItem);
        System.out.println("C1 is:" + '\n' + _Ck);
        this.subsetTransactionAndSupCount(database, _Ck, _k);
        System.out.println("C1 after support count is:" + '\n' + _Ck);
        _Ck.removeInfreqItemSets(_minSup);
        System.out.println("C1 after removing infrequent itemsets is:"  
                           + '\n' + _Ck);
        
    }
    
    /**Description - generate all candidate k-itemsets using the large 
     *               k-1 itemset list.
     * 
     * Input: the 2-d integer array of large k-1 itemsets must be populated,
     *        the CandidateList must be empty.
     * Output: after join and prune steps, all candidate k-itemsets have been 
     *         found and stored in the CandidateList.
     *
     * @param prevLargeItemsets - 2-d integer array of large k-1 itemsets
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets
     */
    public void aproriGen(int[][] prevLargeItemsets, 
                          CandidateList _Ck, 
                          int _k){
        
        this.join(prevLargeItemsets, _Ck, _k);
        System.out.println("After natural join, C" + _k + " is:" + '\n' + _Ck);
        if(_k != 2)
            _Ck.prune(this, prevLargeItemsets, _k);
        System.out.println();
        System.out.println("After candidate pruning, C" + this.k + " is:"
                            + '\n' + this.Ck);
       
    }
    
    /**Description - do all support counting of transactions for all candidate 
     *               k-itemsets in the CandidateList using database transactions
     * 
     * Input: The database must be populated with transactions. The 
     *        CandidateList must  already be storing all candidate k-itemsets 
     * Output: support count of all candidate k-itemsets stored in the Candidate
     *         list is done.
     *
     * @param _database - a 2-dimensional integer array of transactions
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets 
     */
    public void subsetTransactionAndSupCount(int[][] _database, 
                                             CandidateList _Ck, 
                                             int _k){
        System.out.println("PASS-" + _k + " SUPPORT COUNTING");
        for(int transNum= 0;  transNum < this.databaseSize; transNum++){
            this.subsetAndCountTransaction(_database[transNum], _Ck, _k);
        }
    }
    
    /**description - generate and save all k-subsets of the transaction, then
     *               use them with the CandidateList to do support counting for
     *               candidate k-itemsets
     * 
     * Input: a 1-d integer array populated with all items of a transaction, and
     *        a CandidateList for support counting of its stored candidate 
     *        k-itemsets.
     * Output: support count of all candidate k-itemsets stored in the Candidate
     *         list is done.
     *
     * @param _transaction - 1-d integer array of integers in ascending order
     * @param _Ck - A CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets 
     */
    public void subsetAndCountTransaction(int[] _transaction, 
                                          CandidateList _Ck, 
                                          int _k){
            int transSize = _transaction.length;
            System.out.print(_k + "-subsets of Transaction ( ");
            for(int i = 0; i < transSize; i++){
                System.out.print(_transaction[i] + " ");
            }
            System.out.println(" ) are: ");
            
            int[][] subsetList = null;
            int numOfSubsets = 0;
            subsetList = this.getSubsets(_transaction, transSize, _k);
            
            if(subsetList != null){
                for(int i = 0; i < subsetList.length; i++){
                    System.out.print("( ");
                    for(int j = 0; j < subsetList[i].length; j++){
                        System.out.print(subsetList[i][j] + " ");
                    }
                    System.out.println(" )");
                }
                System.out.println();
            }
            else
                System.out.println("No " + _k + "-subsets found" + '\n');
            
            if(subsetList != null){
                numOfSubsets = subsetList.length;
                for(int j = 0; j < numOfSubsets; j++){
                    Ck.incFrequency(Ck.findItemset(subsetList[j]));
                }
            }
    }

    /**Description - check whether a candidate k-itemset should be pruned
     *               
     * Input: A candidate k-itemset populated with integers, a populated large,
     *        k-1 itemset list, and a CandidateList storing all candidate 
     *        k-itemsets
     * Output: Indicate whether this candidate k-itemset is NOT prunable
     *
     * @param _candItemset
     * @param _Ck -A CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets 
     * @param prevLargeItemsets - 2-d integer array of large k-1 itemsets
     * @return boolean true if all k-1 subsets are large, false otherwise
     */
    public boolean pruneCandidates(int[] _candItemset, 
                                   CandidateList _Ck, 
                                   int _k, 
                                   int[][]prevLargeItemsets){
        
            int candidateSize = _candItemset.length;
            System.out.print((_k-1) + "-subsets of Candidate ( ");
            for(int i = 0; i < candidateSize; i++){
                System.out.print(_candItemset[i] + " ");
            }
            System.out.println(" ) are: ");
            
            int[][] subsetList = null;
            int numOfSubsets = 0;
            subsetList = this.getSubsets(_candItemset, candidateSize, _k-1);
            if(subsetList != null){
                for(int i = 0; i < subsetList.length; i++){
                    System.out.print("( ");
                    for(int j = 0; j < subsetList[i].length; j++){
                        System.out.print(subsetList[i][j] + " ");
                    }
                    System.out.println(" )");
                }
                System.out.println();
            }
            else
                System.out.println("No " + _k + "-subsets found" + '\n');
            
            boolean unPrunable = true;
            if(subsetList != null){
                numOfSubsets = subsetList.length;
                for(int i = 0; i < numOfSubsets; i++){
                    unPrunable = unPrunable & checkCandidateSubsetsToPrevLK(subsetList[i], 
                                                            prevLargeItemsets,
                                                            _Ck, _k);
                }
            }
            return unPrunable;
    }
    
    /**Description - check if all k-1 subsets of a candidate k-itemset are large
     * 
     * Input: A candidate k-itemset populated with integers, a populated large 
     *        k-1 itemset list.
     * Output: A boolean indicated whether or the candidate k-itemset should be
     *         pruned
     *
     * @param candidateSubset - 1-d integer array 
     * @param prevLargeItemsets - 2-d integer array of large k-1 itemsets
     * @param _CK - a CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets 
     * @return - boolean true if all k-1 subsets are large, false otherwise
     */
    public boolean checkCandidateSubsetsToPrevLK(int[] candidateSubset, 
                                                 int[][]prevLargeItemsets,
                                                 CandidateList _CK, 
                                                 int _k){
              boolean unPrunable = true;
              int prevLargeItemListLen = prevLargeItemsets.length;
              int prevLargeItemSize = prevLargeItemsets[0].length;
              boolean foundMatch = false;
              boolean setEqual = true;
              for(int i = 0; i < prevLargeItemListLen; i++){
                  for(int j = 0; j < prevLargeItemSize; j++){
                      setEqual = (setEqual & candidateSubset[j] == prevLargeItemsets[i][j]);
                  }
                  foundMatch = (foundMatch | setEqual);
                  setEqual = true;
              } 
              unPrunable = (unPrunable & foundMatch);
              return unPrunable;
    }

    /**Description - generate and store candidate k-itemsets by performing the 
     *               natural join operation on the large k-1 itemset list.
     *
     * Input: a populated large k-1 itemset list, and the CandidateList must be
     *        empty.
     * Output: the CandidateList stores all candidate k-itemsets
     * 
     * @param prevLargeItemsets - 2-d integer array of large k-1 itemsets
     * @param _Ck - a CandidateList instance to process candidate k-itemsets
     * @param _k - positive integer of current size of the k-itemsets 
     */
    public void join(int[][] prevLargeItemsets, 
                     CandidateList _Ck, 
                     int _k){
    System.out.println("PASS-" + _k + " NATURAL JOIN");    
    int numOfRows = prevLargeItemsets.length;
    int numOfClms = prevLargeItemsets[0].length;
    int lastClm = numOfClms - 1;
    boolean isJoinable = true; 
    for(int pRow = 0; pRow < numOfRows; pRow++){
        for(int qRow = pRow + 1; qRow < numOfRows; qRow++){
            for(int clm = 0; isJoinable && clm < numOfClms; clm++){
                if(clm < lastClm){
                    if(prevLargeItemsets[pRow][clm] != prevLargeItemsets[qRow][clm])
                        isJoinable = false;
                }
                else{
                    if(!(prevLargeItemsets[pRow][clm] < prevLargeItemsets[qRow][clm]))
                        isJoinable = false;
                }
            }//end nested-nested-for
            if(isJoinable){
                int size = numOfClms + 1;
                int[] newCandidate = new int[size];
                for(int nloops = 0; nloops < numOfClms; nloops++){
                    newCandidate[nloops] = prevLargeItemsets[pRow][nloops];
                }
                newCandidate[size-1] = prevLargeItemsets[qRow][lastClm];
                _Ck.append(newCandidate);
            }
            isJoinable = true;
        }//end nested-for
    }//end first-for
}
    
    /**Description - create a list of all k-subsets for an itemset
     * 
     * Input: a single itemset, the itemset size and the length of
     *        subsets to be found.
     * Output: A 2-d array of 1-d integers arrays comprising of all k-subsets  
     *         in ascending order from the itemset. 
     * 
     * @param _itemset - 1-d integer array
     * @param _listSize - positive integer of number of elements in the itemset
     * @param _subsetSize - positive integer of the size of the subsets to be found
     * @return
     */
    public int[][] getSubsets(int[] _itemset, 
                              int _listSize, 
                              int _subsetSize){
        
        int[][] subsetList = null;
        if(_listSize >= _subsetSize){
            int[][] subsetIndexArr = null;
            int defaultSize = 100;
            int numOfSubsets = 0;        
            boolean tooManySubsets = true;
            while(tooManySubsets){
                tooManySubsets = false;
                try{
                    subsetIndexArr = new int[defaultSize][_subsetSize];
                    numOfSubsets = getSubsetIndices(_listSize, _subsetSize, subsetIndexArr);
                }catch (ArrayIndexOutOfBoundsException ex) 
                {System.out.println(ex + " -> subset memory grows");
                defaultSize = defaultSize * 100;
                tooManySubsets = true;
                numOfSubsets = 0;
                subsetIndexArr = null;}
            }//end while
            subsetList = new int[numOfSubsets][_subsetSize];
            int correctIndex;
            for(int i = 0; i < numOfSubsets; i++){
                for(int j = 0; j < _subsetSize; j++){
                    correctIndex = subsetIndexArr[i][j] - 1;
                    subsetList[i][j] = _itemset[correctIndex];
                }
            }//end for
        }//end first-if
        return subsetList;
    }
    
    /**Description - populate a destination array with the non-zeroed indices 
     *               used to create all k-subsets of an itemset
     * 
     * Input: The itemset and subset size are used to seed the generation 
     * of the subsets, the destination array is used save each subset.
     * Output: the 2-d the destination array has accommodated all subsets, and
     *         the total number of subsets created is returned.
     * 
     *
     * @param _listSize - positive integer length of the itemset
     * @param _subsetSize - positive integer indicating subset size wanted
     * @param destArr - a reference to a 2-d array used to store each the of  
     *                  subset indices
     * @return - integer number of total subsets found
     * @throws ArrayIndexOutOfBoundsException - the subsets found may exceed the
     * number of rows of the 2-d destination array possesses
     */
    public int getSubsetIndices(int _listSize, 
                                int _subsetSize, 
                                int[][] destArr) 
                                throws ArrayIndexOutOfBoundsException{
        
        int level = 0;
        int inputEdge = 0;
        int index = 0;
        int[] fixedList = new int[_subsetSize];
        int lastIndex = subsetIndices(_listSize, _subsetSize, level, inputEdge, 
                              index, destArr, fixedList);
       return lastIndex;
    }
    
    /**Description - generate indices for all k-subsets of an itemset by 
     *               recursively traversing a prefix tree
     * 
     * Input: The initial input edges and level must be zero. The fixedList
     *        must match the size of the k-subsets being generated of the
     *        destination array.
     * Output: The destination array is populated by each of the indices for 
     *         all k-sized subsets of an itemset of n-size
     * 
     * @param _nSize - positive integer length of itemset
     * @param _kDepth - positive integer size of subset to be generated
     * @param _currLevel - positive integer of the level of prefix tree being recursed
     * @param _inputEdge - positive integer index to be used by the next level of the 
     *                     prefix tree being recursed
     * @param _rowIndexEdge - positive integer points to next spot in destination array, 
     *                        and thus accumulating the number of subsets found
     * @param destArr - 2-d integer array used to store all subset indices
     * @param fixedList - 1-d integer array that allows recall of prior subset
     *                    members while subset branches are recursively searched
     * @return - integer number of total subsets found 
     * @throws ArrayIndexOutOfBoundsException - the subsets found may exceed the
     * number of rows of the 2-d destination array possesses
     */
    public int subsetIndices(int _nSize, 
                             int _kDepth, 
                             int _currLevel, 
                             int _inputEdge, 
                             int _rowIndexEdge, 
                             int[][]destArr, 
                             int[]fixedList) 
                             throws ArrayIndexOutOfBoundsException{
        
        if(_currLevel == _kDepth){           
           fixedList[_currLevel-1] = _inputEdge;
           for(int nloops = 0; nloops < fixedList.length; nloops++)
                destArr[_rowIndexEdge][nloops] = fixedList[nloops];            
           return _rowIndexEdge + 1;
        }
        else{
            if(_currLevel > 0){
                fixedList[_currLevel-1] = _inputEdge;
            }
            int start = _inputEdge;
            int nextNum = start;
            int end = (_nSize - _kDepth) + _currLevel;
            while(start <= end){
                nextNum++;
                _rowIndexEdge = subsetIndices(_nSize, _kDepth, (_currLevel + 1),  
                                              nextNum, _rowIndexEdge, destArr, 
                                              fixedList);
                start++;
            }
        }
       return _rowIndexEdge;
    }

    /**Description - read and create the database of transactions from the 
     *               file input
     * 
     * Input: The filename used must exist and the databaseSize must already be
     *        initialized with the know total number of transactions.
     * Output: This instance has its database populated with transactions stored
     *         in the file
     *          
     */
    public void createDatabase(){
        this.database = this.readDatabase(this.fileName, this.databaseSize);
    }
    
    /**Description - make a deep copy of each transactions from the file input 
     *               for this instances 2-d database array pointers
     *
     * Input: The filename used must exist and the total number of transactions
     *        known before hand.
     * Output: All integer tokens are read into this instance's 2-d integer
     *         database array
     * 
     * @param filename - String of the file used
     * @param numOfTrans - integer number of total transactions know to be in 
     *                   the file
     * @return a reference to the 
     */
    public int[][] readDatabase(String filename, 
                                int numOfTrans){
        
        int[][] dbase = null;
        try{
            int[] saveTransWidth = new int[numOfTrans];
            int _maxTransWidth = 0;
            int transWidth = 0; 
            int transactionCount = 0;
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StreamTokenizer inTok = new StreamTokenizer(bufferedReader);
            inTok.eolIsSignificant(true);
            inTok.nextToken();
            while(inTok.ttype != inTok.TT_EOF){        
                if(inTok.ttype != inTok.TT_EOL){      
                    transWidth++;
                }
                else{
                    saveTransWidth[transactionCount] = transWidth;
                    transactionCount++;
                    if(transWidth > _maxTransWidth)
                        _maxTransWidth = transWidth;
                    transWidth = 0;
                }
                inTok.nextToken();
            }
            
            this.maxTransWidth = _maxTransWidth;
            
            bufferedReader.close();
            bufferedReader = null;
            fileReader.close();
            fileReader = null;
            inTok = null;
            
            fileReader = new FileReader(filename);
            bufferedReader = new BufferedReader(fileReader);
            inTok = new StreamTokenizer(bufferedReader);

            dbase = new int[numOfTrans][];
            int item = 0;
            transactionCount = 0;
            inTok.eolIsSignificant(true);
            inTok.nextToken();
            while(inTok.ttype != inTok.TT_EOF){        
                if(inTok.ttype != inTok.TT_EOL){
                    dbase[transactionCount] = new int[saveTransWidth[transactionCount]];
                    while(inTok.ttype != inTok.TT_EOL){
                        dbase[transactionCount][item] = (int)inTok.nval;
                        item++;   
                        inTok.nextToken();
                    }
                    transactionCount++;
                    item = 0;
                }
                inTok.nextToken();
            }
        }catch(FileNotFoundException ex) { System.out.println(ex); }
         catch(IOException ex) { ex.printStackTrace(); }    
        
        return dbase;
    }

    /**Description - print all transactions in the database
     * 
     * Input: an instance of 2-d integer database array is used to print
     * Output: each row of the 2-d integer database array is printed
     * 
     */
    public void printDatabase(){
        for(int transNum = 0; transNum < this.database.length; transNum++){
            for(int item = 0; item < this.database[transNum].length; item++)
                System.out.print(this.database[transNum][item] + " ");
            System.out.println();
        }
    }
    
    /**Description - print a row-specified large itemset list
     *
     * Input: an instance of a 2-d integer database array is used to print
     * Output: the specified large itemset list is printed
     * 
     * @param _largeItemsets - 3-d integer array used to stored 2-d arrays of
     *                         large k-itemsets
     * @param _row - the row of the large itemset array to print
     */
    public void printRowLargeItemset(int[][][] _largeItemsets, int _row){
        System.out.println("Print Row - " + _row + " of LargeItemsets");
        for(int i = 0; i < _largeItemsets[_row].length; i++){
            for(int j = 0; j < _largeItemsets[_row][i].length; j++)
                System.out.print(_largeItemsets[_row][i][j] + " ");
            System.out.println();
        }
    }

    /**Description - print the kth large itemset list
     * 
     * Input: an instance of a 2-d integer database array is used to print
     * Output: the kth large itemset list is printed
     *
     * @param _largeItemsets -  3-d integer array used to stored 2-d arrays of
     *                          large k-itemsets
     * @param _k - positive integer of row of the large itemset array to print
     */
    public void printLargeKItemset(int[][][] _largeItemsets, int _k){
        System.out.println("Print Large " + _k + "-Itemsets");
        for(int i = 0; i < _largeItemsets[_k-1].length; i++){
            System.out.print("( ");
            for(int j = 0; j < _largeItemsets[_k-1][i].length; j++)
                System.out.print(_largeItemsets[_k-1][i][j] + " ");
            System.out.println(")");
        }
        System.out.println();
    }
    
    
    
}//end Apriori class
