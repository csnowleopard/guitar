/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and or sell copies of the Software, and to permit persons to whom the Software is
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
package edu.umd.cs.guitar.runner;

import edu.umd.cs.guitar.exception.ReplayerStateException;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * {@link org.junit.rules.TestRule} that implements Guitar logging logic.
 *
 *  It collects the logging trace during test execution and append it to the noticing message when
 * the test case failed.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class LogCollectionRule extends TestWatcher {

  private static final String LOGGING_SCOPE = "edu.umd.cs.guitar";
  private WriterAppender appender;
  Writer writer;
  private static Logger guitarLogger = Logger.getLogger(LOGGING_SCOPE);

  public LogCollectionRule() {
    super();
    if (guitarLogger.getLevel().isGreaterOrEqual(Level.WARN)) {
      throw new IllegalArgumentException(
          "Need to setup logging level of " + LOGGING_SCOPE + " package to at least Level.INFO " +
          		"to enable collecting execution trace.");
    }
    writer = new StringWriter();
    Layout layout = new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN);
    appender = new WriterAppender(layout, writer);
    guitarLogger.addAppender(appender);

  }

  @Override
  protected void failed(Throwable e, Description description) {
    try {
      writer.flush();
    } catch (IOException e1) {
      String errorMsg = "The test is failed. " + e.getMessage()
          + "However, there is some problem in recording log information";
      throw new ReplayerStateException(errorMsg, e1);
    }
    StringBuffer loggingMsg = new StringBuffer();
    loggingMsg.append(writer.toString());
    loggingMsg.append(e.getMessage());

    if (e instanceof AssertionError) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      sw.flush();

      StringBuffer msg = new StringBuffer();
      msg.append(e.getMessage() + "\n");
      msg.append(loggingMsg + "\n");
      msg.append(sw.toString());

      throw new AssertionError(msg.toString());
    }

    throw new ReplayerStateException(loggingMsg.toString(), e);
  }
}
