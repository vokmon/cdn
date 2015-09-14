package helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Block;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The helper class for requesting file from the server.
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class CacheManager {

	/**
	 * The cache file table <file name, full path of the file to keep the cache>
	 */
	private Map<String, List<Block>> cacheFiles = new HashMap<String, List<Block>>();
	
	/**
	 * Comparator for sorting the block by index
	 * 
	 * @author Arnon Ruangthanawes arua663
	 */
	public class BlockComparator implements Comparator<Block> {

		@Override
		public int compare(Block o1, Block o2) {
			Integer p1 = o1.getIndex();
			Integer p2 = o2.getIndex();
			if (p1 > p2) {
				return 1;
			} else if (p1 < p2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Get the content of the file from the cache.
	 * The content needs to be reconstructed from the blocks
	 * 
	 * @param fileName the file name
	 * @return the content of the file
	 */
	public String getFileFromCache(String fileName) {
		List<Block> blocks = this.cacheFiles.get(fileName);
		if (blocks == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		for(Block block : blocks) {
			sb.append(block.getContentBlock()).append(System.lineSeparator());
		}
		
		return sb.toString();
	}
	
	/**
	 * Store the content from the server into list of {@link Block}
	 * 
	 * @param fileName the file name
	 * @param contentBlocks the content blocks
	 * @throws IOException
	 */
	public double storeFileToCache(String fileName, String contentBlocks, boolean isFileChanged) throws IOException {
		/**
		 * The number of percentage that the content is constructed from the cache
		 */
		double contructFromCache = 0;
		
		if (isFileChanged) {
			List<Block> existingBlocks = this.cacheFiles.get(fileName);
			ObjectMapper mapper = new ObjectMapper();
			List<Block> receivedBlocks = mapper.readValue(contentBlocks, new TypeReference<List<Block>>(){});
			Collections.sort(receivedBlocks, new BlockComparator());
			
			List<Block> blockToUpdate = null;
			if (existingBlocks == null) {
				blockToUpdate = receivedBlocks;
			}
			else {
				int sizeOfReceivedFile = 0;
				int sizeOfCache = 0;
				/* Fill the block with the cache */
				for (Block receivedBlock : receivedBlocks) {
					String contentFromReceivedBlock = receivedBlock.getContentBlock();
					if (contentFromReceivedBlock == null) {
						Block existingBlock = existingBlocks.get(existingBlocks.indexOf(receivedBlock));
						String content = existingBlock.getContentBlock();
						receivedBlock.setContentBlock(content);
						sizeOfCache = sizeOfCache + content.length();
						sizeOfReceivedFile = sizeOfReceivedFile + content.length();
					}
					else {
						sizeOfReceivedFile = sizeOfReceivedFile + contentFromReceivedBlock.length();
					}
				}
				blockToUpdate = receivedBlocks;
				
				contructFromCache = ((double)sizeOfCache / (double)sizeOfReceivedFile) * 100;
			}
			this.cacheFiles.put(fileName, blockToUpdate);
		}
		else {
			contructFromCache = 100;
		}
		
		return contructFromCache;
	}
	
	/**
	 * Clear the cache
	 */
	public void clearCache() {
		this.cacheFiles.clear();
		
	}
	
	public boolean isFileInCache(String fileName) {
		return this.cacheFiles.containsKey(fileName);
	}
	
	/**
	 * Return list of file name in the cache
	 * @return list of file name in the cache
	 */
	public List<String> getListOfCacheFiles() {
		List<String> fileList = new ArrayList<String>(cacheFiles.keySet());
		Collections.sort(fileList);
		return fileList;
	}
}
