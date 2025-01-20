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

public class CandidateList {
        /**Description - a Node reference to the first Node in the linked-list
         */
        Node head;
        
        /** Description - a Node reference to the last Node in the linked-list
         */
        Node tail;
        
        /**Description - an integer keeping track of the number of itemsets
         *              currently in the linked-list
         */
        int count;
        
        /**Description - the kth level of Apriori's level-wise approach. Each level
         * represents the size of large k-itemsets we are looking.
         */
        int k;
        
        /** Description - the default constructor for CandidateList class.
         * 
         * Input:  a call to instantiate object of this class.
         * Output: an instance of the class is fully constructed, the following
         *         object field are initialized as follows:
         * this.head = null
         * this.tail = null
         * this.count = 0
         * this.k = 0
         * 
         */
        CandidateList(){
            this(null, null, 0, 0);
        }
        
        /** Description - a partial constructor for CandidateList class with 
         *                initialization with k.
         * 
         * Input: a call to instantiate object of this class 
         * Output: an instance of the class is fully constructed, the following 
         *         additional object fields are initialized as follows:
         * this.head = null
         * this.tail = null
         * this.count = 0
         * 
         * @param _k - positive integer for the level-size of the itemsets 
         */
        CandidateList(int _k){
            this(null, null, 0, _k);
        }
        
        /** Description: the full constructor for CandidateList class
         * 
         * Input: a call to instantiate object of this class 
         * Output: an instance of this class fully constructed
         * 
         * @param _head - A Node reference or null
         * @param _tail - A Node reference or null
         * @param _count - integer for count of itemsets, initialized to zero
         * @param _k 
         */
        CandidateList(Node _head, Node _tail, int _count, int _k){
            this.head = _head;
            this.tail = _tail;
            this.count = _count;
            this.k = _k;
        }
        
    /** Description - increment the frequency of an itemset Node
     * 
     * Input: A non-null Node
     * Output: The frequency field of the input Node is incremented
     *
     * @param targNode - a reference to an itemset Node 
     */
    public void incFrequency(Node targNode){
            if(targNode != null)
                targNode.frequency++;
        }
        
    /** Description - insert an itemset Node at the end of the linked-list
     * 
     * Input: a populated integer array representing an itemset
     * Output: The linked-list is extended an additional itemset Node and the
     *         count field is incremented. The tail field points to it. The  
     *         header field may point to it as well if this is the only itemset 
     *         Node in the list.
     *
     * @param _itemset - 1-d integer array 
     */
    public void append(int[] _itemset){
            Node newNode = new Node(_itemset);
            if(this.isEmpty()){
                this.head = this.tail = newNode;
            }
            else{
                this.tail.nextNode = newNode;
                this.tail = newNode;
            }
            count++;
        }
        
    /** Description - remove all itemset Nodes who do not meet the minimum 
     *                support count
     * 
     * Input: This CandidateList ought to contain candidate k-itemsets to be 
     *        removed according to a non-zero support count minimum
     * Output: This CandidateList has itemset Nodes removed and the count field
     *         decremented accordingly.
     * 
     * @param _minSupCnt - an integer of the expected minimum support count
     */
    public void removeInfreqItemSets(int _minSupCnt){
            //save itemsets to remove
            int[][] saveItemsets = new int[this.count][];
            int count = 0;
            Node tempNode = null;
            if(!this.isEmpty()){
                tempNode = this.head;
                while(tempNode != null){
                    if(tempNode.frequency < _minSupCnt){
                        saveItemsets[count] = tempNode.itemset;
                        count++;
                    }
                    tempNode = tempNode.nextNode;
                }
                //itemsets
                int[] itemsetToRemove = null;
                for(int i = 0; i < count; i++){
                    itemsetToRemove = saveItemsets[i];
                    this.removeItemset(itemsetToRemove);
                }
            }
        }
        
    /**Description - remove all itemset Nodes who fail the prune test
     * 
     * Input: An instance of the Apriori class must be passed to access 
     *        subset methods. The prune filter uses subsets in addition with 
     *        a large itemset list to remove candidate k-itemsets Nodes.
     * Output: This CandidateList has itemset Nodes removed and the count field
     *         decremented accordingly. NOTE this CandidateList now contain a 
     *         list of all large k-itemsets
     *
     * @param _apriori - an instance of the Apriori class
     * @param prevLargeItemsets - 2-d integer array of large k-1 itemsets
     * @param _k - current size of the k-itemsets
     */
    public void prune(Apriori _apriori, 
                      int[][] prevLargeItemsets,  
                      int _k){
        //for each candidate apart of _Ck
        System.out.println("PASS-" + _k + " PRUNING");
        boolean unPrunable = false;
        Node tempNode = null;
        if(!this.isEmpty()){
            tempNode = this.head;
            while(tempNode != null){
                int[] candidateItemSet = tempNode.itemset;
                unPrunable = _apriori.pruneCandidates(candidateItemSet, this, _k,
                                                  prevLargeItemsets);
               if(!unPrunable){
                    System.out.println("During pruning on pass " + _k + " " +
                                        tempNode + " is removed");
                }
                tempNode = tempNode.nextNode;
                if(!unPrunable){
                    this.removeItemset(candidateItemSet);
                }
            }   
        }
    }

    /**Description - find and get the reference to specified itemset's Node
     * 
     * Input: the target itemset
     * Output: The pointer to the target itemset Node or null if the itemset was 
     *         not found.
     *
     * @param _itemset - a 1-d integer array 
     * @return - if found return a Node reference, otherwise return null
     */
    public Node findItemset(int[] _itemset){
            Node tempNode = null; 
            if(!this.isEmpty()){
                tempNode = this.head;
                boolean found = false; 
                while(!found && tempNode != null){
                    if(tempNode.itemsetEquals(_itemset))
                        found = true;
                    else
                        tempNode = tempNode.nextNode;
                    }
            }
            return tempNode;
        }
        
    /**Description - find and get a pointer to specified itemset's Node from its
     *               previous Node
     * 
     * Input: the target itemset
     * Output: The pointer to the target itemset Node from it's previous Node, 
     *         or null if the itemset was not found.
     *
     * @param _itemset - a 1-d integer array 
     * @return - if found return a Node reference, otherwise return null
     */
    public Node findItemsetPrev(int[] _itemset){
            Node tempNode = null; 
            if(!this.isEmpty()){
                tempNode = this.head;
                boolean found = false;
                if(tempNode.itemsetEquals(_itemset))
                    found = true;
                while(!found && tempNode != null){
                    if(tempNode.nextNode != null  
                       && tempNode.nextNode.itemsetEquals(_itemset))
                        found = true;
                    else
                        tempNode = tempNode.nextNode;
                    }
            }
            return tempNode;
    }
        
    /**Description - remove a non-head or non-tail itemset Node
     * 
     * Input: the pointer to the target Node from its previous Node.
     * Output: the target Node is remove from the CandidateList
     *
     * @param previousToTarg - a reference to an itemset Node 
     */
    public void removeNode(Node previousToTarg){
            Node targetsNextNode = previousToTarg.nextNode.nextNode;
            previousToTarg.nextNode.itemset = null;
            previousToTarg.nextNode.nextNode = null;
            previousToTarg.nextNode = targetsNextNode; 
            
        }
                
    /**Description - remove the specified itemset from the CandidateList
     * 
     * Input: the target itemset
     * Output: the the target itemset's Node is removed from the CandidateList
     *         and the count field is decremented.
     *
     * @param _itemset - a 1-d integer array
     */
    public void removeItemset(int[] _itemset){
            if(!this.isEmpty()){
                Node tempNode = this.head;
                if(tempNode.itemsetEquals(_itemset))
                    this.removeHead();
                else{
                    boolean found = false;
                    tempNode = this.findItemsetPrev(_itemset);
                    if(tempNode != null)
                        found = true;
                    if(found){
                        if(tempNode.nextNode == this.tail){
                            this.tail.itemset = null;
                            this.tail = tempNode;
                            tempNode.nextNode = null;
                        }
                        else{  
                            removeNode(tempNode);
                        }
                        count--;
                    }//end found-if    
                }//end isEmpty-else
            }// end isEmpty-if   
        }//end removeItem
        
    /**Description - remove the head itemSet Node from the CandidateList
     * 
     * Input - only a CandidateList may make this call
     * Output - the CandidateList has its first itemset Node removed and the
     *          count field is decremented.
     */
    public void removeHead(){
            if(!this.isEmpty()){
                if(this.count == 1){
                    this.head.itemset = null;
                    this.head = this.tail = null;
                }
                else{
                    Node temp = null;
                    temp = this.head.nextNode;
                    this.head.itemset = null;
                    this.head.nextNode = null;
                    this.head = temp;
                }
            count--;
            }
            
        }//removeHead
        
    /**Description - check if the CandidateList currently has no itemset Nodes
     * 
     * Input: only a CandidateList may make this call
     * Output: a boolean value true if the CandidateList is empty, false 
     *         otherwise
     *
     * @return - boolean true if empty, false otherwise
     */
    public boolean isEmpty(){
            return count == 0;
        }
        
        //currentNode instead of temp

    /**Description - make the CandidateList empty by removing all itemsetNodes
     *
     * Input: only a CandidateList may make this call
     * Output: all itemsets from the Candidatelist are removed and the count 
     *        field will be zero.
     */
        public void clear(){
            if(!this.isEmpty()){
                Node tempNode = null;
                tempNode = this.head;
                while(tempNode != null){
                    this.removeHead();
                    tempNode = this.head;
                }
            }
        }
        
    /**Description - turn the list of all itemset Nodes into a 2-d integer array
     * 
     * Input: only a CandidateList may make this call
     * Output: 2-d integer array is populated with all itemsets of the Nodes
     *          currently stored in the CandidateList 
     *
     * @return - 2-d integer array of 1-d itemsets, otherwise null when 
     *           CandidateList empty  
     */
    public int[][] toArray(){
            int[][] arr = null;
            if(!this.isEmpty()){
                arr = new int[count][];
                int nloops = 0;
                Node tempNode = this.head;
                while(tempNode != null){
                    int itemsetSize = tempNode.itemset.length;
                    //int includeCountField = itemsetSize + 1;
                    arr[nloops] = new int[itemsetSize];
                    for(int i = 0; i < itemsetSize; i++)
                        arr[nloops][i] = tempNode.itemset[i];
                     //arr[nloops][itemsetSize + 1]= tempNode.frequency;
                    
                    nloops++;
                    tempNode = tempNode.nextNode;
                }
            }
            return arr;
        }

    /** Description - overriden toString for CandidateList creates a formatted
     *                String from all itemset Nodes currently stored or a String 
     *                "empty"  if CandidateList is empty.
     * 
     * Input: only a CandidateList may make this call
     * Output: a formatted string 
     *
     * @return - a String 
     */
    @Override
        public String toString() {
            String temp = "";
            if(!this.isEmpty()){
                Node tempNode = this.head;
                while(tempNode != null){
                    temp += tempNode.toString() + '\n';
                    tempNode = tempNode.nextNode;
                }
            }
            else
                temp += "empty";
            return temp;
        }
        
    /**Description - The Node class is used to store itemsets, their frequency,
     *               and serve as the atomic component of the CandidateList 
     *               singly linked-list data structure.
     *
     */
    public class Node{
            /**Description - a 1-d integer array stores the items of an itemset
             */
            int[] itemset;
            
            /**Description - an integer tracks the frequency of an itemset
             */
            long frequency;
            
            /** Description - a Node reference complete single-link to next Node
             *                in a linked list of Node objects.
             */
            Node nextNode;
            
            /**Description - default constructor
             * 
             * Input: an object creation using the class identifier
             * Output: an instance of the class is fully constructed, the  
             *         following object field are initialized as follows:
             * this.itemset = null;
             * this.nextNode = null;
             * this.frequency = 0;
             * 
             */
            Node(){
                this(null,null,0);
            }
            
            /**Description - a partial constructor initializes Node with 
             *               specified itemset.
             * 
             * Input: a call to instantiate object of this class 
             * Output: an instance of the class is fully constructed,  
             *         additional object field are initialized as follows:
             * this.nextNode = null;
             * this.frequency = 0;
             * 
             * @param _itemset 
             */
            Node(int[] _itemset){
                this(_itemset, null, 0);
            }


            /**Description - the full constructor for Node class
             * 
             * Input: a call to instantiate object of this class 
             * Output: a fully constructed instance of this class
             * 
             * @param _itemset - 1-d integer array
             * @param _nextNode - Node reference or null
             * @param _frequency - integer for frequency tracking
             */
            Node(int[] _itemset, 
                 Node _nextNode, 
                 int _frequency){
                
                if(_itemset == null)
                    this.itemset = _itemset;
                else{
                    this.itemset = new int[_itemset.length];
                    for(int i = 0; i < this.itemset.length; i++)
                        this.itemset[i] = _itemset[i];
                    }
                this.frequency = _frequency;
                this.nextNode = _nextNode;
            }

            /**Description - check if this Node's itemset the same items of the
             *               passed itemset.
             * 
             * Input: another itemset of equal length.
             * Output: boolean value true if this Node's itemset elements match
             *         all elements of the passed itemset.
             *
             * @param _otherItemset - 1-d integer array
             * @return - boolean true if itemsets are equal, false otherwise
             */
            public boolean itemsetEquals(int[] _otherItemset){
                boolean result = true;
                if(this.itemset.length == _otherItemset.length){
                    for(int i = 0; i < this.itemset.length; i++){
                        if(this.itemset[i] != _otherItemset[i]){
                            result = false;
                            break;
                        }
                    }//end for loop
                }//end first-if
                else
                    result = false;
                
                return result;
            }
            
            /** Description - Node's overridden toString creates a formatted
             *                String from the itemset this Node currently stores
             *                and the itemset's current frequency count.
             * 
             * Input: only a Node may make this call
             * Output: a string of the Node itemset and frequency
             * 
             * @return - a String
             */
            @Override
            public String toString() {
                String temp = "( ";
                for(int i = 0; i < this.itemset.length; i++)
                    temp += this.itemset[i] +  " ";
                temp += ") freq: " + this.frequency;
                //temp += ") -> ";
                //temp += this.nextNode;
                return temp;
            }
            
        }//end class Node
    }//end LinkedList class
