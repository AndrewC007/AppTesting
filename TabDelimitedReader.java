import java.io.BufferedReader;
import java.util.ArrayList;


public class TabDelimitedReader {

	ArrayList<String> tempValues;
	
	//Breaks a tab delimited line into values and sends them back in an array
	public ArrayList<String> readLine(String s)
	{
		int index2;
		String tab= "\t";
		tempValues= new ArrayList<String>();
		
		index2= s.indexOf(tab);
		while(index2!=-1)
		{
			tempValues.add(s.substring(0, index2));
			s=s.substring(index2+1, s.length());
			index2= s.indexOf(tab);
		}
		if(index2==-1)
		{
			tempValues.add(s.substring(0, s.length()));//must account for values before \n 
		}
		
		return tempValues;
	}
}
