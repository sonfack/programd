/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.predicates;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * A <code>PredicateValue</code> is, naturally, the value of a predicate. It can either have a single String value, or a
 * list of values.
 * 
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 */
public class PredicateValue {

  /** The single value (if assigned). */
  private String singleValue;

  /** The list of values (if assigned). */
  private ArrayList<String> valueList;

  /** Whether this PredicateValue has multiple values. */
  private boolean multiValued;

  private static Logger LOGGER = Logger.getLogger("programd");

  /**
   * Creates a new <code>PredicateValue</code> with the given list of values.
   * 
   * @param values the list of values to assign
   */
  public PredicateValue(ArrayList<String> values) {
    this.valueList = values;
    this.multiValued = true;
  }

  /**
   * Creates a new <code>PredicateValue</code> with the given single value.
   * 
   * @param value the single value to assign
   */
  public PredicateValue(String value) {
    this.singleValue = value;
    this.multiValued = false;
  }

  /**
   * Adds the given value into the value list at the given index.
   * 
   * @param index the index at which to add a value
   * @param value the new value
   */
  public void add(int index, String value) {
    if (!this.multiValued) {
      this.becomeMultiValued();
    }
    try {
      this.valueList.add(index - 1, value);
    }
    catch (IndexOutOfBoundsException e) {
      this.valueList.add(0, value);
    }
  }

  /**
   * Adds the given value. In all cases, this means the <code>PredicateValue</code> becomes multi-valued.
   * 
   * @param value the value to add
   */
  public void add(String value) {
    this.multiValued = true;
    this.singleValue = null;
    if (this.valueList == null) {
      this.valueList = new ArrayList<String>(PredicateManager.MAX_INDEX);
    }
    this.valueList.add(value);
  }

  /**
   * If this <code>PredicateValue</code> is multi-valued, returns itself. If it is single-valued, converts itself to
   * multivalued and then returns itself.
   * 
   * @return the list of values (or only value)
   */
  public PredicateValue becomeMultiValued() {
    if (this.multiValued) {
      return this;
    }
    // otherwise...
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Converting predicate value to multi-valued.");
    }
    this.multiValued = true;
    this.valueList = new ArrayList<String>(1);
    this.valueList.add(this.singleValue);
    this.singleValue = null;
    return this;
  }

  /**
   * @param index the index whose value is wanted
   * @return the value at the given index
   */
  public String get(int index) {
    if (!this.multiValued) {
      if (index == 1) {
        return this.singleValue;
      }
      throw new IndexOutOfBoundsException();
    }
    return this.valueList.get(index - 1);
  }

  /**
   * If this <code>PredicateValue</code> is single-valued, simply returns the value. If it is multi-valued, returns the
   * first in the list.
   * 
   * @return the first or only value
   */
  public String getFirstValue() {
    if (this.multiValued) {
      return this.valueList.get(0);
    }
    // otherwise...
    return this.singleValue;
  }

  /**
   * @return whether this <code>PredicateValue</code> is multi-valued
   */
  public boolean isMultiValued() {
    return this.multiValued;
  }

  /**
   * Pushes a value onto the front of a list, and pops off any values at the end of the list so that the list size is no
   * more than {@link PredicateManager#MAX_INDEX}.
   * 
   * @param value the value to push
   */
  public void push(String value) {
    if (!this.multiValued) {
      this.becomeMultiValued();
    }
    this.valueList.add(0, value);
    while (this.valueList.size() > PredicateManager.MAX_INDEX) {
      this.valueList.remove(this.valueList.size() - 1);
    }
  }

  /**
   * @return the number of values stored
   */
  public int size() {
    if (!this.multiValued) {
      return 1;
    }
    // otherwise...
    return this.valueList.size();
  }
}
