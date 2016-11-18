/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.util;

/**
 * Processes template source substituting placeholders with values
 */
public class AjscTemplateEngine {

    /**
     * Parse the text document looking for {{...}} and replace with values in map
     * 
     * @param input
     * @param map
     * @return String with properties replaced
     * @throws IOException
     */
    def static propertyReplace(String input, Map map) throws IOException {
		StringReader reader = new StringReader(input)
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader)
        }
        StringWriter sw = new StringWriter()
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '{') {
                reader.mark(1)
                c = reader.read()
                if (c != '{') {
                    sw.write('{')
                    reader.reset()
                } else {
                    reader.mark(1)
                    processProperty(reader, sw, map)
                }
                continue
            }
            // Handle raw new line characters.
            if (c == '\n' || c == '\r') {
                if (c == '\r') { // on Windows, "\r\n" is a new line.
                    reader.mark(1)
                    c = reader.read()
                    if (c != '\n') {
                        reader.reset()
                    }
                }
                sw.write("\n")
                continue
            }
            sw.write(c)
        }
        return sw.toString()
    }

    /**
     * Read property until terminator and replace with corresponding value from map
     * 
     * @param reader
     * @param sw
     * @param map
     * @return
     * @throws IOException
     */
    private static processProperty(Reader reader, StringWriter sw, Map map) throws IOException {
        int c;
		def propertyWriter = new StringWriter()
        while ((c = reader.read()) != -1) {
            if (c == '}') {
				reader.mark(1)
				c = reader.read()
				if (c != '}') {
					sw.write('}')
					propertyWriter.write('}')
					reader.reset()
				} else {
					break
				}
            } else {
	            if (c != '\n' && c != '\r') {
					propertyWriter.write(c)
	            }
			}
        }
		def property = propertyWriter.toString()
		if (map.containsKey(property)) {
			sw.write(map.get(property))
		} else {
			sw.write("{{" + property + "}}")
		}
    }
}

