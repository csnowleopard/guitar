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

import java.util.HashSet;
import java.util.Set;

/**
 * MethodDescriptor
 *
 */
public class MethodDescriptor {

	/**
	 * Method
	 */
	private Method method;

	/**
	 * Desc
	 */
	private String desc;

	/**
	 * Signature
	 */
	private String signature;

	/**
	 * Exceptions
	 */
	private String[] exceptions;

	/**
	 * Empty
	 */
	private boolean empty = true;

	/**
	 * Sharable
	 */
	private boolean sharable = true;

	/**
	 * Invokes
	 */
	private Set<MethodDescriptor> invokes = new HashSet<MethodDescriptor>();

	/**
	 * InvokedBy
	 */
	private Set<MethodDescriptor> invokedBy = new HashSet<MethodDescriptor>();

	/**
	 * Reads
	 */
	private Set<Field> read = new HashSet<Field>();

	/**
	 * Writes
	 */
	private Set<Field> write = new HashSet<Field>();

	/**
	 * Condition Reads
	 */
	private Set<Field> conditionReads = new HashSet<Field>();
	
	/**
	 * Condition Writes
	 */
	private Set<Field> conditionWrites = new HashSet<Field>();

	/**
	 * Returns the method
	 * @return Method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Assigns the method
	 * @param method Method
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Returns the desc
	 * @return Desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Assigns the desc
	 * @param desc Desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * Returns the signature
	 * @return Signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Assigns the signature
	 * @param signature Signature
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Returns the exceptions
	 * @return Exceptions
	 */
	public String[] getExceptions() {
		return exceptions;
	}

	/**
	 * Assigns the exceptions
	 * @param exceptions Exceptions
	 */
	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
	}

	/**
	 * Returns the flag "empty"
	 * @return Empty
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Assigns the flag "empty"
	 * @param empty Empty
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	/**
	 * Returns the flag "sharable"
	 * @return Sharable
	 */
	public boolean isSharable() {
		return sharable;
	}

	/**
	 * Assigns the flag "sharable"
	 * @param sharable Sharable
	 */
	public void setSharable(boolean sharable) {
		this.sharable = sharable;
	}

	/**
	 * Returns the invokes
	 * @return Invokes
	 */
	public Set<MethodDescriptor> getInvokes() {
		return invokes;
	}

	/**
	 * Adds a new invoke
	 * @param m MethodDescriptor
	 */
	public void addInvokes(MethodDescriptor m){
		invokes.add(m);
		m.invokedBy.add(this);
	}

	/**
	 * Returns all field reads
	 * @return Fields
	 */
	public Set<Field> getRead() {
		return read;
	}

	/**
	 * Determines if the method reads a given field
	 * @param f Field
	 * @return true = method reads a given field
	 */
	public boolean hasRead(Field f) {
		return read.contains(f);
	}

	/**
	 * Adds a new field read
	 * @param f Field
	 */
	public void addRead(Field f){
		read.add(f);
		f.readBy(this);
	}

	/**
	 * Returns all field writes
	 * @return Fields
	 */
	public Set<Field> getWrite() {
		return write;
	}

	/**
	 * Determines if the method writes a given field
	 * @param f Field
	 * @return true = method writes a given field
	 */
	public boolean hasWrite(Field f) {
		return write.contains(f);
	}

	/**
	 * Adds a new field write
	 * @param f Field
	 */
	public void addWrite(Field f){
		write.add(f);
		f.writeBy(this);
	}

	/**
	 * Returns the invoked by
	 * @return InvokedBy
	 */
	public Set<MethodDescriptor> getInvokedBy() {
		return invokedBy;
	}

	/**
	 * Returns the condition reads
	 * @return Condition reads
	 */
	public Set<Field> getConditionReads() {
		return conditionReads;
	}
	
	/**
	 * Returns the condition writes
	 * @return Condition writes
	 */
	public Set<Field> getConditionWrites() {
		return conditionWrites;
	}	

	@Override
	public String toString(){
		return method + desc;
	}

}
