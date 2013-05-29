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
 * Class
 *
 */
public class Class {

	/**
	 * Name
	 */
	private String name;

	/**
	 * Super name
	 */
	private String superName;

	/**
	 * Interfaces
	 */
	private String[] interfaces;

	/**
	 * Methods
	 */
	private Map<String, Method> methods = new HashMap<String, Method>();

	/**
	 * Fields
	 */
	private Map<String, Field> fields = new HashMap<String, Field>();

	/**
	 * Declared
	 */
	private boolean declared = false;

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
	 * Returns the super name
	 * @return Super name
	 */
	public String getSuperName() {
		return superName;
	}

	/**
	 * Assigns the super name
	 * @param superName Super name
	 */
	public void setSuperName(String superName) {
		this.superName = superName;
	}

	/**
	 * Returns the interfaces
	 * @return Interfaces
	 */
	public String[] getInterfaces() {
		return interfaces;
	}

	/**
	 * Assigns the interfaces
	 * @param interfaces Interfaces
	 */
	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * Returns an existing method
	 * @param name Name of the method
	 * @return Method
	 */
	public Method getMethod(String name) {
		Method m = methods.get(name);
		return m;
	}

	/**
	 * Adds a new method
	 * @param m Method
	 */
	public void addMethod(Method m) {
		methods.put(m.getName(), m);
		m.setOwner(this);
	}

	/**
	 * Returns an existing field
	 * @param name Name of the field
	 * @return Field
	 */
	public Field getField(String name) {
		return fields.get(name);
	}

	/**
	 * Adds a new field
	 * @param field Field
	 */
	public void addField(Field field) {
		this.fields.put(field.getName(), field);
		field.setOwner(this);
	}

	/**
	 * Returns the flag "declared"
	 * @return true = is declared
	 */
	public boolean isDeclared() {
		return declared;
	}

	/**
	 * Assigns the flag "declared"
	 * @param declared
	 */
	public void setDeclared(boolean declared) {
		this.declared = declared;
	}

	@Override
	public String toString() {
		return name;
	}

}
