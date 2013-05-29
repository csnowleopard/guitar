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

import edu.umd.cs.guitar.annotation.Initializer;
import edu.umd.cs.guitar.annotation.ReplayerPlugin;
import edu.umd.cs.guitar.annotation.RippingSpec;
import edu.umd.cs.guitar.crawljax.ripper.RippingSpecification;
import edu.umd.cs.guitar.model.PageLoadPlugin;
import edu.umd.cs.guitar.model.plugin.GPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for guitar specification object
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class GuitarSpecificationParser {

  private Class<?> guitarSpecification;

  /**
   * @param guitarSpecification specification for Guitar
   * @see RippingSpecification
   * @see GPlugin
   * @see PageLoadPlugin
   *
   */
  public GuitarSpecificationParser(final Class<?> guitarSpecification) {
    this.guitarSpecification = guitarSpecification;
  }


  public RippingSpecification getRippingSpecification() {
    List<Method> methodList = getMethodWithAnnotationAndReturnType(
        this.guitarSpecification, RippingSpec.class, RippingSpecification.class);

    if (methodList.size() > 1) {
      throw new IllegalArgumentException("Cannot provide mulitple Ripping specifications");
    }

    if (methodList.size() == 0) {
      return null;
    }

    Method method = methodList.get(0);
    return (RippingSpecification) getObjectFromGetter(guitarSpecification, method);
  }

  public List<GPlugin> getReplayerPluginList() {
    List<Method> methodList = getMethodWithAnnotationAndReturnType(
        this.guitarSpecification, ReplayerPlugin.class, GPlugin.class);

    List<GPlugin> pluginList = new ArrayList<GPlugin>();
    for (Method method : methodList) {
      Object t = getObjectFromGetter(guitarSpecification, method);
      if (!(t instanceof GPlugin)) {
        throw new IllegalArgumentException(
            "Plugin must has type GPlugin. It cannot be " + t.getClass().getName());
      }
      pluginList.add((GPlugin) t);
    }
    return pluginList;
  }

  public PageLoadPlugin getPageLoadPlugin() {
    List<Method> methodList = getMethodWithAnnotationAndReturnType(
        this.guitarSpecification, Initializer.class, PageLoadPlugin.class);

    if (methodList.size() > 1) {
      throw new IllegalArgumentException("Cannot have multiple page load plugin");
    } else if (methodList.size() == 0) {
      return null;
    }
    Method method = methodList.get(0);
    return (PageLoadPlugin) getObjectFromGetter(guitarSpecification, method);
  }


  private Object getObjectFromGetter(Class<?> guitarSpecification, Method method) {
    Object t;
    try {
      t = guitarSpecification.newInstance();
      return method.invoke(t);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException();
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException();
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException();
    }
  }

  private List<Method> getMethodWithAnnotationAndReturnType(
      Class<?> inputClass, Class<? extends Annotation> annotation, Class<?> returnType) {
    List<Method> methodList = new ArrayList<Method>();
    for (final Method method : inputClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(annotation)
        && returnType.isAssignableFrom(method.getReturnType())){ 
        methodList.add(method);
      }
    }
    return methodList;
  }
}
