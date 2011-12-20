package edu.ntu.mobile.smallelephant.ader;

import java.util.Comparator;

public class ComparatorImageAndText implements Comparator {

	public int compare(Object arg0, Object arg1) {
		ImageAndText item1 = (ImageAndText) arg0;
		ImageAndText item2 = (ImageAndText) arg1;
		
		int flag = item1.isOnline().compareTo( item2.isOnline());
		if( flag == 0){
			return item1.getText().compareTo( item2.getText());
		}
		return -1*flag;
	}
}
