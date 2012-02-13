/**
 * © PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.command;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Akshay Sharma
 * Feb 13, 2012
 */
public class TranscodeCommandBuilderChain {
	
	// Maybe useful for traversal?
	private List<TranscodeCommandBuilder> chain = new ArrayList<TranscodeCommandBuilder>();
	
	public TranscodeCommandBuilderChain(TranscodeCommandBuilder chainStart) {
		this.chain.add(chainStart);
	}
	
	public TranscodeCommandBuilderChain addNextSuccessor(TranscodeCommandBuilder successor) {
		TranscodeCommandBuilder lastInChain = this.chain.get(chain.size() - 1);
		lastInChain.setSuccessor(successor);
		// Add it in the chain list as well. 
		this.chain.add(successor);
		return this;
	}
	
	public TranscodeCommandBuilder getChainStart() {
		return this.chain.get(0);
	}
}
