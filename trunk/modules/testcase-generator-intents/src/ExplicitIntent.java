import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


public class ExplicitIntent {

	public static void main(String args[]) throws IOException{ //args: gives filename.java

		try {
			//BufferedWriter out = new BufferedWriter(new FileWriter("../../../IntentOutputs/out.txt"));
			BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
			out.write("{\"intent\": [");
			out.close();
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		String commaIntent = "";
		for(String filename: args){
			String className = "unfound class name";
			String packageName = "unfound package name";
			HashSet<String> nameOfIntents = new HashSet<String>();
			HashMap<String,String> intentAttributes = new HashMap<String, String>();

			String statement[] = getLinesRemovedComment(filename).split(";");

			for(String s: statement){
				s = s.trim();
				// Since the statements got split using ";" and "{" or "}",
				// then extract the "return;" statement for cases like: "if(true) return;"
				while((s.contains("while") && s.indexOf("while") == 0) || (s.contains("if") && s.indexOf("if") == 0) 
						|| (s.contains("for") && s.indexOf("for") == 0)){
					if(s.contains("if else") && s.indexOf("if else") == 0)
						s = controlStatement("if else", s);
					else if(s.contains("if") && s.indexOf("if") == 0)
						s = controlStatement("if", s);
					else if(s.contains("while") && s.indexOf("while") == 0)
						s = controlStatement("while", s);
					else if(s.contains("for") && s.indexOf("for") == 0)
						s = controlStatement("for", s);
				}
				if(s.equals("false")) //if it's only if,if else, while, or for statement, skip
					continue;
				if((s.contains("public class") && s.indexOf("public class") == 0) || (s.contains("public final class") && s.indexOf("public final class") == 0)){
					if (s.contains("public class") && s.indexOf("public class") == 0)
						className = s.split(" ")[2];
					else 
						className = s.split(" ")[3];
					continue; // this statement declares public class, skip other checks
				}
				if(s.contains("package")){
					packageName = s.split(" ")[1];
					continue; // this statement declares package, skip other checks
				}
				//if it's a case control, then split the statement "case a: words" to only get "words"
				while(s.split(" ").length >= 1 && s.split(" ")[0].equals("case")){
					if(s.split(" ").length >= 2)
						s = s.substring(s.split(" ")[0].length() + s.split(" ")[1].length() + 1).trim();
					else break;
				}
				// catches all everything with startActivity(..) line
				if(s.contains("startActivity(")){
					String intent = s.substring(s.indexOf("(")+1, s.length()-1);
					nameOfIntents.add(intent);
					if(intentAttributes.containsKey(intent)){
						String attributes = intentAttributes.get(intent);
						if(attributes == null)
							attributes = "";
						attributes += ";\n" + s;
						intentAttributes.put(intent, attributes);

					} else
						intentAttributes.put(intent, s);
				}
				// catches intent declaration ie Intent i
				if(s.contains("Intent") && s.indexOf("Intent") == 0){
					String temp = s.replace("=", " ");
					if(temp.length() >=2){
						if(temp.split(" ").length >= 2) {
							nameOfIntents.add(temp.split(" ")[1]);
							if(!intentAttributes.containsKey(temp.split(" ")[1]))
								intentAttributes.put(temp.split(" ")[1], "");
						}
					}
				}
				if(s.contains("new Intent")){
					String target = "new Intent" + s.split("new Intent")[1];
					String temp[] = s.split(" ");
					if(temp.length >= 1){
						if(temp[0].equals("Intent")){
							if(temp.length >= 2){
								nameOfIntents.add(temp[1]);
								if(!intentAttributes.containsKey(temp[1]))
									intentAttributes.put(temp[1], "");
								if(!target.equals("()")){
									String attributes = intentAttributes.get(temp[1]);
									if(attributes != null && !attributes.contains(target)){
										attributes += ";\n"+ target;
										intentAttributes.put(temp[1], attributes);
									}
								}
							}
						} else if(intentAttributes.containsKey(temp[0])){
							if(!target.equals("()")){
								String attributes = intentAttributes.get(temp[0]);
								if(!attributes.contains(target)){
									attributes += ";\n"+ target;
									intentAttributes.put(temp[0], attributes);
								}
							}
						}
					}

				}
				for(String intent: nameOfIntents){
					if(s.contains("=") && s.replace("=", " = ").contains(" "+intent+" ")){
						String attributes = intentAttributes.get(intent);
						if(s.split(intent+"=").length >= 2){
							attributes += ";\n"+ "=" + s.split(intent+"=")[1].trim();
							intentAttributes.put(intent, attributes);
						}
					} else if(s.contains(intent+".set") && s.indexOf(intent+".set") == 0){
						String attributes = intentAttributes.get(intent);
						if(s.split(intent+".set").length >= 2){
							attributes += ";\n"+ ".set" + s.split(intent+".set")[1];
							intentAttributes.put(intent, attributes);
						}
					} else if(s.contains(intent+".add") && s.indexOf(intent+".add") == 0){
						String attributes = intentAttributes.get(intent);
						if(s.split(intent+".add").length >= 2){
							attributes += ";\n"+ ".add" + s.split(intent+".add")[1];
							intentAttributes.put(intent, attributes);
						}
					} else if(s.contains(intent+".putExtra") && s.indexOf(intent+".putExtra") == 0){
						String attributes = intentAttributes.get(intent);
						if(s.split(intent+".put").length >= 2){
							attributes += ";\n" + ".put" + s.split(intent+".put")[1];
							intentAttributes.put(intent, attributes);
						}
					}
				}

			}

			// append to file
			try {
				//FileWriter fstream = new FileWriter("../../../IntentOutputs/out.txt",true);
				FileWriter fstream = new FileWriter("out.txt",true);
				BufferedWriter out = new BufferedWriter(fstream);

				// append package and class information 
				for(String intent: intentAttributes.keySet()){
					if(intentAttributes.get(intent).length() > 0){
						if(packageName.equals("unfound package name"))
							packageName = "";
						out.write(commaIntent + "\n\n" + "{\"activity\":\"" +className +"\",\"packageName\":\"" + packageName + "\",\n");
						String toProcess[] = intentAttributes.get(intent).split(";");
						String declaration = "[\"";
						int countStartActivity = 0;
						for(int i = 0; i < toProcess.length; i++){
							String s = toProcess[i].trim();
							if(s.equals(""))
								continue;
							s = s.replace("\"", "\\\"");
							declaration += s+ ";"; 
							if(s.contains("startActivity(") && (s.indexOf("startActivity(") == 0) && i != 0){
								declaration = declaration.substring(0, declaration.length()-1) + "\",\"";	
								countStartActivity++;
							} 
						}
						if(countStartActivity > 0){
							declaration = declaration.substring(0, declaration.length() - 2) + "]";
						} else {
							declaration = declaration.substring(0, declaration.length() - 1) + "\"]";
						}

						// append information to file out.txt
						out.write("\"declaration\": " + declaration + "\n");
						out.write("}"); 
						commaIntent = ",";

					} else {
						// TODO: prints out empty intents (intents with no attributes)
					}

				}
				out.close();
			} catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			//FileWriter fstream = new FileWriter("../../../IntentOutputs/out.txt",true);
			FileWriter fstream = new FileWriter("out.txt",true);
			BufferedWriter out = new BufferedWriter(fstream);

			//out.write("}");
			out.close();
		}
		//FileWriter fstream = new FileWriter("../../../IntentOutputs/out.txt",true);
		FileWriter fstream = new FileWriter("out.txt",true);
		BufferedWriter out = new BufferedWriter(fstream);

		// append package and class information
		out.write("\n]\n}");
		out.close();
	}

	//this method evaluates one line if, while, else if, else
	//returns "false" if does not have other statement after control statement
	//returns the statement otherwise
	public static String controlStatement(String control, String line){
		int openBracket = 0;
		String ret = "false";
		line = line.replaceFirst(control, "").trim();
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '(')
				openBracket++;
			else if(line.charAt(i) == ')')
				openBracket--;
			if(openBracket == 0){
				ret = line.substring(i+1, line.length());
				break;
			}
		}
		return ret;
	}


	//this method returns everything in the source code, with removed "//" comment lines
	//and, it also replaces "{" or "}"
	public static String getLinesRemovedComment(String filename){
		String linesRemovedComment = "";
		FileReader f;
		try {
			f = new FileReader(new File(filename));
			BufferedReader b = new BufferedReader(f);
			String line;
			try {
				line = b.readLine();

				while(line != null){
					line = line.trim();
					String tempLine = new String(line);

					if(line.contains("//") && line.contains("\"")){
						boolean inQuote = false;
						boolean comment = false;
						for(int i = 0; i < tempLine.length(); i++){
							if(tempLine.charAt(i) == '\"')
								inQuote = !(inQuote);
							else if((!inQuote) && tempLine.charAt(i) == '/'){
								if (comment) {tempLine = tempLine.substring(0, i); break;}
								else comment = true;
							} else 
								comment = false;
						}
					} else if(line.contains("//")){
						tempLine = tempLine.substring(0, tempLine.indexOf("//"));
					} 
					linesRemovedComment += tempLine + " ";
					line = b.readLine();
				}
				b.close();

				//remove all /* */ comments
				linesRemovedComment = linesRemovedComment.replaceAll("/\\*([^*]|[\r\n]|(\\*+([^*/]|[\r\n])))*\\*+/","");
				linesRemovedComment = linesRemovedComment.replaceAll("\n", " ");
				//gives space in between = so that a word won't include "="
				linesRemovedComment = linesRemovedComment.replaceAll("=", " = ");

				// remove tabs, replace "{" and "}" with ";" (to be distincted as different statement"
				// split through statement, ";"
				linesRemovedComment = linesRemovedComment.replace("\t", "");
				linesRemovedComment = linesRemovedComment.replace("{", ";");
				linesRemovedComment = linesRemovedComment.replace("}", ";");
				return linesRemovedComment;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}
}
