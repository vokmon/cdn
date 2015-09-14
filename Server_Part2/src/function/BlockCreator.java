package function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Block;
import model.BlockWrapper;

/**
 * The class for creating data blocks and computing finger print
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class BlockCreator {

	/**
	 * The finger print of the blocks by file name
	 */
	private Map<String, List<Block>> fingerPrintBlocks = new HashMap<String, List<Block>>();
	
	/**
	 * The {@link RabinHashFunction} to determine the block boundary
	 */
	private RabinHashFunction rf = new RabinHashFunction(3, 2048, 37, 1024, 2048);
	
	/**
	 * Create blocks from the file
	 * The fingerprint is processed based on MD5 algorithm
	 * 
	 * @param file the file to create block
	 * @param fileName the request name
	 * @param noCache if true, compute the finger print and blocks
	 * @return blocks from the file
	 */
	public BlockWrapper makeBlocks(File file, String fileName, boolean noCache) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    
		    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		    List<String> stringList = splitContent(sb.toString());
		    List<Block> blockList = new ArrayList<Block>();
		    int index = 0;
		    for(String str : stringList) {
		    	String fingerPrint = new String(messageDigest.digest(str.getBytes()));
		    	Block block = new Block();
		    	block.setIndex(index);
		    	block.setFingerPrint(fingerPrint);
		    	block.setContentBlock(str);
		    	blockList.add(block);
		    	index++;
		    }
		    
		    /* Compute the file change */
		    BlockWrapper wrapper = new BlockWrapper();
		    
		    List<Block> blockInCache = fingerPrintBlocks.get(fileName);
		    if (blockInCache == null || noCache) {
		    	/* New file is requested */
		    	wrapper.setBlocks(blockList);
			    wrapper.setFileChanged(true);
			    fingerPrintBlocks.put(fileName, blockList);
		    }
		    else {
		    	if (blockInCache.size() == blockList.size() &&
		    	    blockInCache.containsAll(blockList) && blockList.containsAll(blockInCache)) {
		    		/* The file is not changed at all */
		    		wrapper.setFileChanged(false);
		    	}
		    	else {
		    		/* only send the change back*/
		    		List<Block> blocksToSend = new ArrayList<Block>();
		    		int id = 0;
		    		for (Block block : blockList) {
		    			Block blockToSend = new Block();
		    			blockToSend.setFingerPrint(block.getFingerPrint());
		    			blockToSend.setIndex(id);
		    			if (!blockInCache.contains(block)) {
		    				blockToSend.setContentBlock(block.getContentBlock());
		    			}
		    			blocksToSend.add(blockToSend);
		    			id++;
		    		}
		    		wrapper.setBlocks(blocksToSend);
				    wrapper.setFileChanged(true);
				    fingerPrintBlocks.put(fileName, blockList);
		    	}
		    }
		    return wrapper;
		    
		} catch (Exception e){
			e.printStackTrace();
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	private List<String> splitContent(String content) {
		return rf.createChunk(content);
	}
}
