/*      
 
 
 * 
 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *      the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *      conditions:
 * 
 *      The above copyright notice and this permission notice shall be included in all copies or substantial 
 *      portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *      LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *      EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *      IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *      THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */





package edu.umd.cs.guitar.ripper.filter;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

//import edu.umd.cs.guitar.ripper.core.Ripper;
import edu.umd.cs.guitar.model.data.ComponentType;



/**
 *   
 * An interface for component filters. Component filters are used 
 * to change the ripper behavior for special components. For example,
 * some components need to be ignored or just captured the GUI information 
 * instead of trying to click on them (e.g., links to external web site).
 * 
 * <p>
 *   
 * @author     </a>
 *
 */
public abstract class GComponentFilter {
        
       // Ripper ripper;

        /*
         
         
        public void setRipper(Ripper ripper) {
                this.ripper = ripper;
        }
        */

    public abstract boolean isProcess(GComponent component, GWindow window );//{return false;}
        
    public abstract ComponentType ripComponent(GComponent component, GWindow window);//{return null;}

//      public void setRipper(cn.edu.nju.software.ripperCore.Ripper ripper) {
//              // TODO Auto-generated method stub
//              this.ripper = ripper;
//      }

}