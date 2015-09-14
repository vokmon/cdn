package model;

import java.io.Serializable;

/**
 * The model object containing block data
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class Block implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5688425478189490804L;

	/**
	 * The index of the data block. It is used to determine the sequence of the block
	 */
	private int index;
	
	/**
	 * The fingerprint of the block
	 */
	private String fingerPrint;
	
	/**
	 * The content in the data block
	 */
	private String contentBlock;

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the fingerPrint
	 */
	public String getFingerPrint() {
		return fingerPrint;
	}

	/**
	 * @param fingerPrint the fingerPrint to set
	 */
	public void setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	/**
	 * @return the contentBlock
	 */
	public String getContentBlock() {
		return contentBlock;
	}

	/**
	 * @param contentBlock the contentBlock to set
	 */
	public void setContentBlock(String contentBlock) {
		this.contentBlock = contentBlock;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (!(obj instanceof Block))
	        return false;
	    Block other = (Block) obj;
	    return fingerPrint == null ? other.fingerPrint == null : fingerPrint.equals(other.fingerPrint);
	}
}
