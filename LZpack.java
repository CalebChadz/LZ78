//Caleb Chadderton ID: 1328518
//Josh Fellingham ID: 1319780

import java.io.*;

public class LZpack {
    public static int intBufferSize, 
	bitsInBuffer = 0, 
	missMatchSize = 8;
    public static BitStreamWriter flusher = new BitStreamWriter();
	
    //buffer to store a packed int
    public static int[] buffer = new int[1];

    public static void main(String[] args) {
		
        //make sure there are the two numbers given as arguments
        if (args.length == 2) {
            String s;
            String[] line;
            int index, 
			code, 
			lineCount = 0;
            buffer[0] = 0x00;

            try {
                //create an output file to write to, create a reader to get the data. calculate the max bytes required to pack the data.
                BufferedOutputStream OStream = new BufferedOutputStream(new FileOutputStream("BitPacked.txt"));
                BufferedReader reader = new BufferedReader(new FileReader(new File("encoded.txt")));
                intBufferSize = calculateBitMax(Integer.parseInt(args[0]));
                missMatchSize = calculateBitMax(Integer.parseInt(args[1]));
                System.out.println("Index Pack Size: " + intBufferSize + " MissMatch Pack Size: " + missMatchSize);
				System.out.println("Writes to file: BitPacked.txt");
				System.out.println("Next command: java LZunpack " + intBufferSize + " " + missMatchSize);



                //we need to read in the encode as strings, then convert to integers, then pack the integers.
                while((s = reader.readLine()) != null){
					
                    //read in a line
                    line = s.split(" ");
					
                    //create two seperate integers.
                    index = Integer.parseInt(line[0]);
                    code = Integer.parseInt(line[1]);
					
                    //pack the data into packs of the calculated minimum pack size.
                    pack(index,intBufferSize,OStream);
                    pack(code,missMatchSize,OStream);
                }
				//make sure to empty out all the data.
                flush(OStream);
                OStream.flush();
                OStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.err.println("Usage error: java LZpack <Largest integer> <Smallest integer>");
        }
    }

    ///this method takes the data to be packed and will process it one bit at a time till it is written packed in an integer(32 bits).
    private static void pack(int toPack, int numBits, BufferedOutputStream OStream) throws IOException {

		//for each bit to pack get the bits from right to left.
        for(int i =0; i < numBits; i++){

            int bit = toPack << ((32 - numBits) + i);
            bit = (bit >>> 31) & 0x01;
			
			//write the respective bit.
            writeIntBuffer(bit, OStream);
        }
    }

	///instead of writing the 32bit int to the ousput stream this will use a bitstream writer to write the 32bits, one bit at a time to output.
    private static void writeIntBuffer(int bit, BufferedOutputStream OStream) throws IOException {
		
		//if the int buffer is fully packed then flush it.
        if(bitsInBuffer == 32){
            flusher.writeBits(buffer[0],32,OStream);
			
			//reset the buffer.
            bitsInBuffer = 0;
            buffer[0] = 0x00;
        }

        if(bit != 0){
            //or it with the bit in the correct position.
            buffer[0] |= (bit << (31 - bitsInBuffer));
        }
        bitsInBuffer++;
    }

	///if there is data in the buffer after reading through the file then flush the reminants out.
    private static void flush(BufferedOutputStream OStream) throws IOException{
        flusher.writeBits(buffer[0], 32,OStream);
        flusher.empty(OStream);
    }

	///this will calculate the minimum number of bits requred to pack the data based of teh largest and smallest values to be stored.
    private static int calculateBitMax(int largestInt){
        int bits = 1;
        int represent = 1;
		
        //if there is a negative then there needs to be 8 bits for encoding.
        if(largestInt == -1){
            return 8;
        }
        while(represent < largestInt){
            bits ++;
            represent *=2;
        }
		
        //you can store teh data in the number of bits one less than this calculation.
        return bits -1;
    }

}

//class that enables me to write out individualt bytes at a time rather than trying to write out integers at a time.
//I managed to get better results with this than trying to write an int of data at a time. Not sure why.
class BitStreamWriter{
	
    //keep track of the buffer volume
    private int bitsInBuffer1 = 0;
    private byte[] buf = new byte[1];

    public BitStreamWriter(){
        //clear buf
        buf[0] = 0x00;
    }

    ///the data to print out, given as the size being used to pack this particular data.
    public int writeBits(int toWrite, int numBits, BufferedOutputStream OStream) throws IOException{
        int bitsWritten = 0;
        for (int i = 0; i < numBits; i++){
			
            //get the current bit from the int
            int bit = (toWrite << i);
            bit = bit >>> (numBits-1);
			
            //add this bit by bit to the buffer and avery byte print it out.
            WriteToBuffer(bit, OStream);
        }
        return bitsWritten;
    }

	///writes to output the bytes of whatever int is passed to it.
    private void WriteToBuffer(int bit, BufferedOutputStream oStream) throws IOException {
        if(bitsInBuffer1 == 8){
            WriteFlush(oStream);
        }
		
        //only if there is a non zero bit then change the buff. otherwise it is already set to a zero
        if(bit != 0){
			
            //bits get written in from left to right
            buf[0] |= bit << (7 - bitsInBuffer1);
        }
		
        //keep track to see if it can print out this buffer yet.
        bitsInBuffer1++;
    }

    ///prints out the completed byte
    public void WriteFlush(BufferedOutputStream oStream) throws IOException{
        if(bitsInBuffer1 != 0){
            oStream.write(buf[0]);
        }
        buf[0] = 0x00;
        bitsInBuffer1 = 0;
    }
	
	///clear out the buffer.
    public void empty(BufferedOutputStream oStream) throws IOException{
        oStream.write(buf);
    }
}
