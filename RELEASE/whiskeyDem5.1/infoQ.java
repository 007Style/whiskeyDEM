/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
Helper class that contains Quotes for Quote.java for message passing for ipKchat.
*/

import java.lang.*;
import java.io.Serializable;

public class infoQ implements Serializable 
{
        String symbol = null;
        String name = null;
        String price = null;
        String change = null;
        String volume = null;
}