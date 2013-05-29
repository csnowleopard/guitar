import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/*
 *   Copyright (c) 2009-@year@. The GUITAR group at the University of
 *   Maryland.  Names of owners of this group may be obtained by sending an
 *   e-mail to atif@cs.umd.edu
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a
 *   copy of this software and associated  documentation files (the "Software"),
 *   to deal in the Software without restriction,  including without  limitation
 *   the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *   and or sell copies of the Software, and to permit persons to whom the
 *   Software is furnished to do so, subject to the following  conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial  portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT  LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO  EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR  THE USE OR OTHER
 *   DEALINGS IN THE SOFTWARE.
 */

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class TestPermutation {

	List<String> s1 = Arrays.asList("a", "b");
	List<String> s2 = Arrays.asList("c", "d");
	List<String> s3 = Arrays.asList("e", "f", "g");

	List<List<String>> set = Arrays.asList(s1, s2, s3);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestPermutation test = new TestPermutation();
		test.runTest();
		System.out.println("DONE");
	}

	public void runTest() {
		List<List<String>> result = new ArrayList<List<String>>();
		getPerm(set, new ArrayList<String>(), result);

		System.out.println("size: " + result.size());
		System.out.println("size: " + resultAll.size());
		for (List<String> alist : result) {
			System.out.println(alist);
		}
	}

	List<List<String>> resultAll = new ArrayList<List<String>>();

	/**
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	private void getPerm(List<List<String>> remainingSet,
			List<String> covertedList, List<List<String>> result) {
		if (remainingSet.size() == 0) {
			result.add(covertedList);
			resultAll.add(covertedList);

		} else {
			List<String> firstSet = remainingSet.get(0);

			for (String aFirstElement : firstSet) {
				List<String> extendedCovertedList = new ArrayList<String>();

				extendedCovertedList.addAll(covertedList);

				extendedCovertedList.add(aFirstElement);

				List<List<String>> extendedremainingSet = new ArrayList<List<String>>();
				extendedremainingSet.addAll(remainingSet);
				extendedremainingSet.remove(0);

				getPerm(extendedremainingSet, extendedCovertedList, result);
			}
		}

	}
}
