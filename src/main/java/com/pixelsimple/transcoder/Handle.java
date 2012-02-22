/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder;


/**
 *
 * @author Akshay Sharma
 * Feb 20, 2012
 */
public class Handle {

	private String handleId;
	private String outputFileCreated;
	
	public Handle(String handleId) {
		this.handleId = handleId.toLowerCase();
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Handle))
			return false;
		
		Handle handle = (Handle) obj;
		
		if (handle == this)
			return true;

		if (handle.getHandleId().equalsIgnoreCase(this.getHandleId()))
			return true;
		
		return false;
	}
	
	@Override public int hashCode() {
		return this.getHandleId().hashCode();
	}

	/**
	 * @return the handleId
	 */
	public String getHandleId() {
		return this.handleId;
	}

	@Override public String toString() {
		return "handle id:" + this.handleId; 
	}

	/**
	 * @return the outputFileCreated
	 */
	public String getOutputFileCreated() {
		return outputFileCreated;
	}

	/**
	 * @param outputFileCreated the outputFileCreated to set
	 */
	protected Handle setOutputFileCreated(String outputFileCreated) {
		this.outputFileCreated = outputFileCreated;
		return this;
	}
	
}
