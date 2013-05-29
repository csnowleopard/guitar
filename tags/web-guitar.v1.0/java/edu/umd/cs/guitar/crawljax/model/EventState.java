/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.crawljax.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal object representing a set of event available 
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class EventState {
  Collection<String> eventSet;

  public EventState(Collection<String> eventSet) {
    super();
    this.eventSet = eventSet;
  }

  public Collection<String> getEventSet() {
    return eventSet;
  }

  /**
   * Check if event set contains an object 
   */
  public boolean contains(Object o) {
    return eventSet.contains(o);
  }


  @Override
  public String toString() {
    return eventSet.toString();
  }

  /**
   * Redefining the default hashcode
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((eventSet == null) ? 0 : eventSet.hashCode());
    return result;
  }

  /**
   * Redefining the default equals method
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    EventState other = (EventState) obj;
    if (eventSet == null) {
      if (other.eventSet != null) return false;
    } else {
      Set<String> thisEventSetCopy = new HashSet<String>();
      thisEventSetCopy.addAll(eventSet);
      Set<String> otherEventSetCopy = new HashSet<String>();
      otherEventSetCopy.addAll(other.eventSet);
      if (!thisEventSetCopy.equals(otherEventSetCopy)) return false;
    }
    return true;
  }
}
