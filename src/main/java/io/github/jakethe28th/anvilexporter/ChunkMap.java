package io.github.jakethe28th.anvilexporter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.engine.math.Vector3f;
import net.querz.mca.Chunk;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.nbt.tag.LongArrayTag;

public class ChunkMap {

	
	boolean isValid = true;
	MCAFile mca = null;
	Sprite spr = null;
	
	Map<String, Integer> chunkMap = new HashMap<String, Integer>();
	
	ChunkMap(int x, int z) throws IOException {
		String mcaName = x + "." + z; 
		
		String fName = "region_testing/r." + mcaName + ".mca";
		
		if ((new File(fName)).exists()) 
			{ this.mca = MCAUtil.read(fName); } 
				else { 
					System.out.println("MCA " + mcaName + " is UNABLE TO LOAD");
					this.isValid = false; }
		
		this.spr = new Sprite(1024, 1024);
		System.out.println("MCA " + mcaName + " is GOOD TO GO");
	}
	
	public Chunk getChunk(int x, int z) {
		return this.mca.getChunk(x, z);
		}
	
	public int getChunkSprite(int x, int z) {
		if (isValid) {
		if (chunkMap.get(x + ":" + z) == null) {
			Chunk chunk = getChunk(x, z);
			if (chunk != null) {
				int id = GuiPreview.genChunkPreview((LongArrayTag) chunk.getHeightMaps().get("WORLD_SURFACE"), this.spr);
				chunkMap.put(x + ":" + z, id);
			} else chunkMap.put(x + ":" + z, -1);
		} } else { chunkMap.put(x + ":" + z, -2); }

		int chunk_spr = chunkMap.get(x + ":" + z);
		//System.out.println("CHUNK" + x + " " + z + " is " + chunk_spr);
		return chunk_spr;
	}
	
	public boolean isChunkProcessed(int x, int z) {
		if (isValid) {
			if (chunkMap.get(x + ":" + z) != null) {
				return true;
			}}
		
		return false;
		}
	
	public Sprite getSprite() { return this.spr; }

}
