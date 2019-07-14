//Caleb Chadderton ID: 1328518
//Josh Fellingham ID: 1319780

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class LZencode {

    public static void main(String[] args) {
        if (args.length == 1) {
			
            //Create a dictionary to help process the files.
            MultiTrie Dictionary = new MultiTrie((byte)0);
            Path file = Paths.get(args[0]);
			int pointerIndex, 
			currData, 
			LargestInt = 0, 
			LargestData = 0, 
			SmallestData = 0;
            MultiTrie prev, 
			curr, 
			LastWord = null;

            try {
				
                //set up the files to be read and written to.
                //get all the individual bytes from file
                byte[] Data = Files.readAllBytes(file);
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("encoded.txt")));
                
                int count = 0;
				
                //if this is a new char then add the next char aswell repeat till new string.
                for(int j = 0; j < Data.length; j += count){
                    curr = Dictionary;
                    prev = curr;
                    count = 0;

                    //find the current data, if it exists find until there is a data that is new
                    while((curr = curr.insert(Data[j + count])) != null){
						
						//this data already existed so move to next and increment count to make sure
						//we dont try to add that data again after this loop is executed.
                        count++;
                        prev = curr;

						//exit the loop if we have incremented to the last data.
                        if((j + count)  >= Data.length){
                            LastWord = curr;
                            break;
                        }
                    }
					
					//count should always go up by one for the initial data attempted insert.
                    count++;
                    if(LastWord == null) {
						
                        //get the actual index of the just added data
                        pointerIndex = prev.trieID;
						
						//keep track of the largest data to be stored.
                        if(prev.trieID > LargestInt){
                            LargestInt = prev.trieID;
                        }
                        if(prev.trieData > LargestData){
                            LargestData = prev.trieData;
                        }
                        if(prev.trieData < SmallestData){
                            SmallestData = prev.trieData;
                        }
						
                        //after the previous loop a new word should be ready to insert so insert it.
                        currData = prev.children.get(prev.children.size()-1).trieData;
                        writer.write(pointerIndex + " " + currData + "\n");
                    }
                }
                if(LastWord != null) {
                    writer.write(LastWord.trieID + " " + 0);
                }
				
				//empty the writer.
                writer.close();
				
				//-1 or any negative number needs 8 bits to be encoded so return a -1 (11111111) if the smalles number is less than zero.
                int missMatch = -1;
				
                //negative numbers need 8 bits to encode
                if(!(SmallestData < 0)){
                    missMatch = LargestData;
                }
                System.out.println("Largest Integer: " + LargestInt + " Largest Data: " + missMatch + "\nWrites to file: encoded.txt");
				
				//print the next command.
				System.out.println("Next command: java LZpack " + LargestInt + " " + missMatch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class MultiTrie {
    public final int trieID;
    public final byte trieData;
    private static MultiTrie root;
    public static int nextIndex = 0;
    public ArrayList<MultiTrie> children = new ArrayList<>();


    public MultiTrie(byte data) {
        trieData = data;
        trieID = nextIndex++;
        //if this is the first node with index 0 then mae it the root node
        //globally for every future node.
        if(this.trieID == 0){
            root = this;
        }
    }

    //method inserts a new array of data into the trie.
    public MultiTrie insert(byte data){
        MultiTrie current = this;
        int index;
        byte curr;

        curr = data;
        //if the current node has a reference to the first letter
        //then set Current to that child node
        if((index = current.childIndex(curr)) != -1){
            current = current.children.get(index);
        }
        else{
            current.children.add(new MultiTrie(curr));
            current = null;
        }
        return current;
    }

    //else will return -1
    public int childIndex(byte data){
        for(int i = 0; i< this.children.size(); i++){
            if(this.children.get(i).trieData == data){
                return i;
            }
        }
        return -1;
    }
}
