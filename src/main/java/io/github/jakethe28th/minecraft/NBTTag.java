package io.github.jakethe28th.minecraft;

public class NBTTag {

	/*
	 * This class will allow you to parse NBT data to a hashmap or quick access NBT data directly from binary.
	 * It will contain every Tag type in itself, so only one class is needed.
	 * It will also have functions to deal with Structure blocks, MCA files, and Schematics quickly.
	 */
	
	/*
	 * To parse NBT to a hashmap, first we will need to define a way of storing NBT data once read.
	 * I'll opt to use HashMap<String, NBTTag>, for the Name, and this class.
	 * 
	 * To store NBT data in this class, we'll use a set of variables.
	 * For example, if the data is a byte;
	 * the type will be set to byte, and the variable byteData will be set to the data.
	 * 
	 * What we will do is parse down the line;
	 * Move forward a byte, check it's type, check it's name if applicable,
	 * then use a switch statement on it's name to get it's data.
	 * 
	 * It will return an NBTTag, which we will add to our map under <Name, NBTTag>
	 * This also deals with Compound, List, and Array tags;
	 * they'll store their data in the NBTTag, so this function doesn't need to deal with them.
	 * 
	 * If you want to get the data of a particular NBTTag;
	 * You can get the type, and use that to choose which variable to get
	 * Or, you can use a function called getTagData, which will convert the data to it's Object counterpart, and return that.
	 * 
	 * If you're using read from binary mode, and don't care about storing in a hashmap;
	 * All tags outside of your String path will be ignored.
	 * The storing part will be skipped, and replaced with returning the data of the last-in-line.
	 */
}
