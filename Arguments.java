/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This is a parsing class.  Originally written by Roland Wunderlich.
used with his permission.  All this class does is help with parsing
command line parameters.
*/

import java.util.*;

//This is a class used for parsing...  it was originally writen by Roland Wunderlich.
//I've always used it with his permission.
//Also, since I did not write this it is formated slightly different

public class Arguments {

        public static final String ARGUMENT_MODIFIER = "-";

        Map modified = new TreeMap();
        List unmodified = new ArrayList();

        public Arguments(String[] args) {
                String modifier = null;
                for (int i = 0; i < args.length; i++) {
                        if (args[i].startsWith(ARGUMENT_MODIFIER)) {
                                if (modifier != null)
                                        modified.put(modifier, null);
                                modifier = args[i].substring(ARGUMENT_MODIFIER.length()).toLowerCase();
                        } else if (modifier != null) {
                                modified.put(modifier, args[i]);
                                modifier = null;
                        } else {
                                unmodified.add(args[i]);
                        }
                }
                if (modifier != null)
                        modified.put(modifier, null);

                modified = Collections.unmodifiableMap(modified);
                unmodified = Collections.unmodifiableList(unmodified);
        }

        public Map getModified() {
                return modified;
        }

        public boolean isSpecified(String modifier) {
                return modified.containsKey(modifier.toLowerCase());
        }

        public String getModified(String modifier) {
                return (String) modified.get(modifier.toLowerCase());
        }

        public int getModifiedInt(String modifier) throws NumberFormatException {
                return Integer.parseInt(getModified(modifier));
        }

        public List getUnmodified() {
                return unmodified;
        }

        public String toString() {
                return "Modified: " + modified + " Unmodified: " + unmodified;
        }

}
