Caleb Chadderton
Josh Fellingham

LZencode:
To use LZencode you must pass it a filename as an argument, the file that you wish to encode with LZ78 encoding. 
You can give it any type of file for example .jpg/.BMP/.txt ...

Usage: java LZencode <file.txt>

It will print out to console the Largest integer Reference(first column of numbers) and the Largest number MissMatch(seccond column of numbers).
The MissMatch numbers can be negative so if a number is negative it will require 8 bits to store it without sign loss in our implementation. 
If the largest MissMatch number printed to console is '-1' this just means there are negative numbers in the encode that will need to be packed.
It will print to console the file that the encode was written to and also the next java command to invoke.

######################################################################################################################################################

LZpack:
To use LZpack it requires two arguments: Largest index(first column number) and Largest MissMatch(seccond column number)
If you have just run LZencode it will give you thewe two numbers printed to console.

Usage: java LZpack <Largest index> <Largest MissMatch>

This will pack the bits and print out to console the size pack used for the index(First Column) numbers and the MissMatch(Seccond Column) numbers.
It will print to console the filename where the bitpacked encode is stored and also the next java command to envoke.

######################################################################################################################################################

LZunpack:
To use LZunpack it requres two arguments. The packsize used to pack the index(First Column) numbers and the packsize used to pack the MissMatch
(Seccond Column) numbers.
If you have just run LZpack then these numbers will have been printed to console.

Usage: java LZunpack <Index Packsize> <MissMatch Packsize>

This will unpack the bits and print to the console the filename where the unpacked bits have been written to.
It will also print the next java command to envoke.

######################################################################################################################################################

LZdecode:
To use LZdecode you must have a LZ78 encode file named UnPacked.txt in the directory. This will be the case if all three of the previous programs
have been run.
There are no arguments for this to run.

Usage: java LZdecode

This will take the LZ78 encode and process it. The decoded file will be written to a file named decode(this has no file extension).


