
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class DataAnalysis 
{
	public static HashMap<Integer, ArrayList<Integer>> records = new HashMap<Integer, ArrayList<Integer>>(); //A hashmap to store the records
	public static int[] daycount = new int[9];
	public static int totaldaycount=0;
	
	public static void updateRecord(int[] values)
	{
		ArrayList<Integer> temp= records.get(values[0]);
		if(values[1]==2)
			temp.set(0, temp.get(0)+1);
		else if (values[1]==1)
			temp.set(1, temp.get(1)+1);
		if (values[2]==1)
			temp.set(2, temp.get(2)+1);
		else if (values[2]==2 ||values[2]==3 )
			temp.set(3, temp.get(3)+1);
		else if (values[2]==4 ||values[2]==5 ||values[2]==6 )
			temp.set(4, temp.get(4)+1);
		records.put(values[0], temp);
	}
	
	public static void dayCount(int claimutil)
	{
		totaldaycount++;	
		if(claimutil>30)
			daycount[8]++;
		else if(claimutil>=11 && claimutil<=30)
			daycount[7]++;
		else if(claimutil>=6 && claimutil<=10)
			daycount[6]++;
		else daycount[claimutil]++;		
	}

	public static void writeclaimutil(String[] args) throws IOException
	{
		
		CSVWriter clwriter = new CSVWriter(new FileWriter(args[1], false));
	    String[] entries = "Utilization Range#Counts#Percentages".split("#");
	    clwriter.writeNext(entries);
	    int count=0;
	    String output = "";float percent;
		while (count < 6) {
			percent = (float) (daycount[count] * 100.0 / totaldaycount);
			output = count + "#" + daycount[count++] + "#" + percent + "%";
			String[] entry = output.split("#");
			clwriter.writeNext(entry);
		}
		percent = (float) (daycount[count] * 100.0 / totaldaycount);
		output = "6 to 10" + "#" + daycount[count++] + "#" + percent + "%";
		String[] entry = output.split("#");
		clwriter.writeNext(entry);

		percent = (float) (daycount[count] * 100.0 / totaldaycount);
		output = "11 to 30" + "#" + daycount[count++] + "#" + percent + "%";
		entry = output.split("#");
		clwriter.writeNext(entry);

		percent = (float) (daycount[count] * 100.0 / totaldaycount);
		output = ">30" + "#" + daycount[count] + "#" + percent + "%";
		entry = output.split("#");
		clwriter.writeNext(entry);
		
		clwriter.close();	
	}
	public static void writetofile(String[] args) throws IOException
	{
		CSVWriter writer = new CSVWriter(new FileWriter(args[0], false));
	    String[] entries = "State#Female#Male#Ages <65#Ages 65-74#Ages 75+".split("#");
	    writer.writeNext(entries);
		Iterator it = records.entrySet().iterator();
		String output = "";int count = 0;
		while (it.hasNext()) {
			count = 0;
			Map.Entry pair = (Map.Entry) it.next();
			output = pair.getKey() + "#";
			ArrayList<Integer> temp = (ArrayList<Integer>) pair.getValue();
			while (count < 5) {
				output = output + "" + temp.get(count++) + "#";
			}
			String[] entry = output.split("#");
			writer.writeNext(entry);
			it.remove();
		}
		System.out.println("Done");
		writer.close();
	}
	
	public static void main( String[] args ) throws IOException
    {
        String csvfile= "data.csv";                                           
        CSVReader reader = new CSVReader(new FileReader(csvfile));   
        String [] nextLine;
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null)                        
        {
        	int statecode=Integer.parseInt(nextLine[nextLine.length-2]);
        	int gender= Integer.parseInt(nextLine[nextLine.length-5]);
        	int age = Integer.parseInt(nextLine[nextLine.length-6]); 
        	int claimUtilDay= Integer.parseInt(nextLine[46]);
        	dayCount(claimUtilDay);
        	int[] values = {statecode, gender, age};
        	ArrayList<Integer> list = new ArrayList<Integer>();       	
        	for(int i=0;i<5;i++)
        		list.add(0);
        	if (records.get(statecode) == null)
            {   
        		records.put(statecode,list);
	            updateRecord(values);           
            }
        	else updateRecord(values);
        }
        writetofile(args);
        writeclaimutil(args);
    }
}
