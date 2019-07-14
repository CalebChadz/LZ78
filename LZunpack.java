//Caleb Chadderton ID: 1328518
//Josh Fellingham ID: 1319780

import java.io.*;
import java.util.ArrayList;

public class LZunpack {
    static int packSize;
    static int missMatchSize = 8;
    static int currentSize;
    static int bitsInBuffer = 0;
    static int[] buffer;
    static boolean newLine = false;

    public static void main(String[] args){
		
        ArrayList<String> output = new ArrayList<>();
        if(args.length ==2){
			
            // the number of bits used to pack in bit packer.
            packSize = Integer.parseInt(args[0]);
            missMatchSize = Integer.parseInt(args[1]);
            currentSize = packSize;
            buffer = new int[1];

            try {
                BufferedInputStream IStream = new BufferedInputStream(new FileInputStream(new File("BitPacked.txt")));
                BufferedWriter writer = new BufferedWriter(new FileWriter("UnPacked.txt"));
				System.out.println("Writes to file: UnPacked.txt");
				System.out.println("Next command: java LZdecode");
                int b;

                //get the read in byte and for every byte unpackit.
                while((b = IStream.read()) != -1){
                        unPack(b, output);
                }

                //close and flush out all remains
                flush(output);
				
				//for the last data read, since there can be additional zeors in file from a bad flush,
				//delete any of teh last zeors untill they are gone.
                while(output.get(output.size()-1).equals("0 ") || output.get(output.size()-1).equals("0") || output.get(output.size()-1).equals("0\n")){
                    output.remove(output.size()-1);
                }
				
				//the file should always end with one zero so add this back.
				if(output.size() % 2 != 0){
					output.add("0");
				}

				//write out all the data to th output file.
                for(String s : output){
                    writer.write(s);
                }
                writer.flush();
                writer.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
	
    ///this will take integer to unpack, it was a byte so only 8bits are needed
    public static void unPack(int toUnPack, ArrayList<String> output) {
        for(int i = 0; i < 8; i++){
			
            //10000000 -> 00000001, get the bits from left to right and write them out.
            int bit = (toUnPack >>> (7 - i)) & 0x00000001;
            writeInteger(bit, output);
        }
    }

    public static void writeInteger(int bit, ArrayList<String> output) {

        //if we have restored a full data then flush it.
        if(bitsInBuffer == currentSize){
			
			//if this data is not a missmatched bit.
            if(newLine == false){
                String out = Integer.toString(buffer[0]);
				
				//add a space after data and set the next data to be a missmatched bit.(new Line)
                output.add(out + " ");
                newLine = true;
				
				//set the size of the next data to be unpacked
                currentSize = missMatchSize;
            }
            else{
                byte b = (byte) buffer[0];
                String out = Integer.toString((int) b);
				
				//add a new line after the missmatch data. set the next data to be index data
                output.add(out + "\n");
                newLine = false;
				
				//set the next pack size to be of index pack size
                currentSize = packSize;
            }

            //clear the buffer and its current count
            buffer[0] = 0x00;
            bitsInBuffer = 0;
        }


        //write any 1 bits, if its a 0 then just leave the zero alone and move to next position.
        if(bit != 0){
            buffer[0] |= bit << ((currentSize-1) - bitsInBuffer);
        }
        bitsInBuffer ++;
    }

	///can be used to flush out any remaining data.
    public static void flush(ArrayList<String> output) throws IOException {
        String out = Integer.toString(buffer[0]);
        output.add(out);
    }
}



