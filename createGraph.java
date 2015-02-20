import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

import vtk.*;

public class createGraph {
	
	 static {
	        if (!vtkNativeLibrary.LoadAllNativeLibraries()) {
	            for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
	                if (!lib.IsLoaded()) {
	                    System.out.println(lib.GetLibraryName() + " not loaded");
	                }
	            }
	        }
	        vtkNativeLibrary.DisableOutputWindow(null);
	     }

	public static vtkMutableUndirectedGraph createMutableGraph(String organism) 
	{
		vtkMutableUndirectedGraph testing = new vtkMutableUndirectedGraph();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			//Information should be the same; "EDP" should be changed to password of user you are using on your system
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","EDP");
			//Create MySQL query statement
			PreparedStatement statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B from " + organism);
			ResultSet rs = statement.executeQuery();
			
		
				//Create hashmaps so that the gene or id can be retrieved when the other is known
				Map geneToId = new HashMap();
				Map idToGene = new HashMap();
					
				int i=0;
				int k=0;
					
				//iterate through first column and create hashmaps(gene 1 in interaction)
				while(rs.next())
				{
					if(!geneToId.containsKey((rs.getString("Official_Symbol_Interactor_A"))))
					{
						geneToId.put(rs.getString("Official_Symbol_Interactor_A"),i);
						i++;
					}
					if(!idToGene.containsValue((rs.getString("Official_Symbol_Interactor_A"))))
					{
						idToGene.put(k, rs.getString("Official_Symbol_Interactor_A"));
						k++;
					}
				}
				
				//iterate through second column and create hashmaps(gene 2 in interaction)
				rs.beforeFirst();
				while(rs.next())
				{
					if(!geneToId.containsKey(rs.getString("Official_Symbol_Interactor_B")))
					{
						geneToId.put(rs.getString("Official_Symbol_Interactor_B"),i);
						i++;
					}
					if(!idToGene.containsValue((rs.getString("Official_Symbol_Interactor_B"))))
					{
						idToGene.put(k, rs.getString("Official_Symbol_Interactor_B"));
						k++;
					}
				}
					
				
			
			
			int v[] = new int[geneToId.size()];
			
			//label array
			vtkStringArray labels = new vtkStringArray();
			labels.SetNumberOfComponents(1);
			labels.SetName("labels");
			
			//edge weight array
			vtkIntArray weights = new vtkIntArray();
			weights.SetNumberOfComponents(1);
			weights.SetName("weights");
			
			vtkIntArray pubMedId = new vtkIntArray();
			weights.SetNumberOfComponents(1);
			weights.SetName("pubMedId");
			
			vtkStringArray author = new vtkStringArray();
			weights.SetNumberOfComponents(1);
			weights.SetName("author");
			
			vtkStringArray systemType = new vtkStringArray();
			weights.SetNumberOfComponents(1);
			weights.SetName("systemType");
			
			vtkStringArray system = new vtkStringArray();
			weights.SetNumberOfComponents(1);
			weights.SetName("system");
			//create labels for the corresponding genes
			
			for(int j=0; j<geneToId.size();j++)
			{
				v[j]=testing.AddVertex();
				labels.InsertNextValue((String)idToGene.get(j));
			}
			
			//find repeat edges
			statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B, count(*) as NumDup from " + organism + " GROUP BY Official_Symbol_Interactor_A, Official_Symbol_Interactor_B having NumDup>0");
			//statement = con.prepareStatement("SELECT ta.Official_Symbol_Interactor_A,ta.Official_Symbol_Interactor_B, COUNT(*) from " + organism + " ta WHERE (SELECT COUNT(*) FROM " + organism + " ta2 WHERE (ta.Official_Symbol_Interactor_A=ta2.Official_Symbol_Interactor_A AND ta.Official_Symbol_Interactor_B=ta2.Official_Symbol_Interactor_B) OR (ta.Official_Symbol_Interactor_A=ta2.Official_Symbol_Interactor_B AND ta.Official_Symbol_Interactor_B=ta2.Official_Symbol_Interactor_A) )>0 GROUP BY Official_Symbol_Interactor_A, Official_Symbol_Interactor_B");
			//statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B from " + organism);
			
			rs = statement.executeQuery();
			
			//Hold the interactions of genes with indexes representing interaction or edge number
			
			ArrayList<String> gene1 = new ArrayList<String>();
			ArrayList<String> gene2 = new ArrayList<String>();
			
			//Array list to hold edge weight values
			List<Integer> edgeWeights = new ArrayList<Integer>();
			
//			ArrayList<Integer> test1 = new ArrayList<Integer>();
//			ArrayList<Integer> test2 = new ArrayList<Integer>();
//			
//			test1.add(2);
//			test1.add(3);
//			test1.add(4);
//			
//			test2.add(3);
//			
//			System.out.println(createGraph.similarValues(test1, test2));
			//must account for repeat edges where the genes are not in the same columns. (ie. Gene A is now Gene B and Gene B is now Gene A) this is not registered as a repeat.
			//rs.beforeFirst();
			int j =0;
			
			
		//similarValues returns -1 if the two genes have not been added and returns the index of the interactions if their interaction has been added
			while(rs.next())
			{
				ArrayList gene1Temp=createGraph.indexOfAll(rs.getString("Official_Symbol_Interactor_B"), gene1);
				ArrayList gene2Temp=createGraph.indexOfAll(rs.getString("Official_Symbol_Interactor_A"), gene2);
				
				//System.out.println(createGraph.similarValues(gene1Temp, gene2Temp));
				if(createGraph.similarValues(gene1Temp,gene2Temp)<0)
				{
					gene1.add(rs.getString("Official_Symbol_Interactor_A"));
					gene2.add(rs.getString("Official_Symbol_Interactor_B"));
					testing.AddGraphEdge(v[(int)geneToId.get(rs.getString("Official_Symbol_Interactor_A"))],v[(int)geneToId.get(rs.getString("Official_Symbol_Interactor_B"))]);
					edgeWeights.add(rs.getInt(3));
					//System.out.println(rs.getString("Official_Symbol_Interactor_A") + " " + rs.getString("Official_Symbol_Interactor_B") + " " + rs.getInt(3));
				}
				else if(createGraph.similarValues(gene1Temp,gene2Temp)>0)
				{
					int index = createGraph.similarValues(gene1Temp,gene2Temp);
					edgeWeights.add(index, edgeWeights.get(index)+rs.getInt(3));
				}
			}
			
			//add label and edge weight arrays to graph
		
			for(int l=0;l<edgeWeights.size();l++)
			{
				weights.InsertNextValue(edgeWeights.get(l));
			}
			testing.GetVertexData().AddArray(labels);
			testing.GetEdgeData().AddArray(weights);
	
			System.out.println(testing.GetNumberOfEdges() + " " + geneToId.size());
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return testing;
	}
	
	//Find all the indexes that contain the object obj
	static ArrayList<Integer> indexOfAll(Object obj, ArrayList list){
	    ArrayList<Integer> indexList = new ArrayList<Integer>();
	    for (int i = 0; i < list.size(); i++)
	    {
	        if(obj.equals(list.get(i))){
	            indexList.add(i);
	        }
	    }
	    return indexList;
	}
	
	//Test to see if there is one value of equal index (this would indicate the interaction has been recorded, but in opposite order)
	static int similarValues(ArrayList gene1Temp, ArrayList gene2Temp)
	{
		int value=-1;
		//System.out.println(gene1Temp.size());
		for(int i=0;i<gene1Temp.size();i++)
		{
			if(gene2Temp.contains(gene1Temp.get(i)))
			{
				value= (int)gene1Temp.get(i);
			}
		}
		return value;
	}
	 
}


