//Caleb Chadderton ID: 1328518
//Josh Fellingham ID: 1319780

import java.io.*;
import java.util.*;

///LZ78 decode class
public class LZdecode {
    public static void main(String[] args) throws IOException {
        ///Name of File that output is sent to. No file extension as encoded file could be of any type.
        FileOutputStream writer = new FileOutputStream(("decode"));
        ///File that is read in from the LZUnpack
        BufferedReader reader = new BufferedReader(new FileReader("UnPacked.txt"));
        ///Creates array to keep a track of dictionary.
        ArrayList<int[]> dictionary = new ArrayList<int[]>();
        ArrayList<byte[]> output = new ArrayList<>();
        System.out.println("Decoding...");
        //Variables to use
        String line;
        String[] split;

        ///Variables to store each split line of text as a byte <21 55>
        byte b0, b1;
        //Used to store the next value in the dictionary.
        int[] next;
        ///Uses to store the symbols(as bytes)
        List<Byte> listByteSymbols = new ArrayList<Byte>();
        ///Buffer used to write the symbols to.
        byte[] buffer = new byte[4096];


        ///Loop while there are still lines to be read.
        while (((line = reader.readLine()) != null)) {
            ///Split read line.
            split = line.split(" ");
            int i = Integer.parseInt(split[0]);
            int j = Integer.parseInt(split[1]);
            //Cast to byte
            b0 = (byte) i;
            b1 = (byte)j;
            //Store new values as Int's in array.
            int[] arr = new int[2];
            arr[0] =  i;
            arr[1] = j;
            //Creates a temp list of all symbols for each tuple in dictionary.
            ArrayList<Integer> temp_list= new ArrayList<Integer>();
            dictionary.add(arr);

            //Check if first time seeing symbol.
            if (arr[0] == 0) {
                listByteSymbols.add(b1);

            } else {
                //set next array to new array
                next = arr;
                ///Loop while not end.
                while (next[0] != 0) {
                    temp_list.add(next[1]);
                    next = dictionary.get(next[0] - 1);
                }
                ///If at end add to list.
                if (next[0] == 0) {
                    temp_list.add(next[1]);
                }
                //Loop through temp list adding the symbols to list1 in the correct order.
                for(int l = temp_list.size()-1 ; l>=0; l--){
                    int temp = temp_list.get(l);
                    byte by = (byte)temp;
                    listByteSymbols.add(by);
                }
            }
        }
        reader.close();
        System.out.println("Writing");

        ///This loop writes out the symbols bytes to the file. Uses a buffer to speed up writing
        //Process.
        //Variables for list as well as symbols as well as checking buffer size.
        int count = 0;
        int bufferSize = 0;
        ///Loop till at the end of the list.
        while (count < listByteSymbols.size()) {
            ///if buffer isn't full
            if(bufferSize<4096) {
                ///Store in buffer.
                buffer[bufferSize] = listByteSymbols.get(count);
                count++;
                bufferSize++;
            }
            //If buffer is full reset count, create new buffer
            else if(bufferSize == 4097){bufferSize = 0; buffer = new byte[4096];  }
            ///Write buffer to file.
            else{
                //writer.write(buffer);
                output.add(buffer);
                ///Add one more so that on next loop
                bufferSize++;}

        }
        ///If looped through list and items still remain in buffer write out to file.
        if(bufferSize<4096) {
            ///Need to slice to avoid writing null to file.
            byte[] slice = Arrays.copyOfRange(buffer, 0, bufferSize);
            //writer.write(slice);
            output.add(slice);
        }
        //remove the additional byte.
        byte[] by = new byte[bufferSize-1];
        System.arraycopy(output.get(output.size()-1),0, by, 0,bufferSize-1);
        output.set(output.size()-1, by);

        for(byte[] b : output){
            writer.write(b);
        }
        ///Flush and close.
        writer.flush();
        writer.close();
        System.out.println("Done");
        System.exit(1);
    }
}
