package model;

import java.util.List;

/**
 * The wrapper class that wrap the {@link Block}
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class BlockWrapper {

	/**
	 * whether the requested file is changed or not
	 */
	private boolean fileChanged;
	
	/**
	 * The list of {@link Block}
	 */
	private List<Block> blocks;

	/**
	 * @return the fileChanged
	 */
	public boolean isFileChanged() {
		return fileChanged;
	}

	/**
	 * @param fileChanged the fileChanged to set
	 */
	public void setFileChanged(boolean fileChanged) {
		this.fileChanged = fileChanged;
	}

	/**
	 * @return the blocks
	 */
	public List<Block> getBlocks() {
		return blocks;
	}

	/**
	 * @param blocks the blocks to set
	 */
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
}
