/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.testcase.plugin.edg;

import java.util.HashMap;
import java.util.Map;

/**
 * Method
 *
 */
public class Method {

	/**
	 * Owner
	 */
	private Class owner;

	/**
	 * Name
	 */
	private String name;

	/**
	 * Descs
	 */
	private Map<String, MethodDescriptor> descs = new HashMap<String, MethodDescriptor>();

	/**
	 * Returns the owner
	 * @return Owner
	 */
	public Class getOwner() {
		return owner;
	}

	/**
	 * Assigns the owner
	 * @param owner Owner
	 */
	public void setOwner(Class owner) {
		this.owner = owner;
	}

	/**
	 * Returns the name
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns the name
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the descriptor of the given ID
	 * @param id ID
	 * @return Descriptor
	 */
	public MethodDescriptor getDescriptor(String id){
		return descs.get(id);
	}

	/**
	 * Adds a new descriptor
	 * @param md Descriptor
	 */
	public void addDescriptor(MethodDescriptor md){
		descs.put(md.getDesc(), md);
		md.setMethod(this);
	}

	@Override
	public String toString(){
		return owner +"#" + name;
	}

}
