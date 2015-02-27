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

public class CreateGraph {
	
	String organismSelected;
	Connection con;
	PreparedStatement statement;
	ResultSet rs;
	Map geneToId;
	Map idToGene;
	
	public CreateGraph(String organism) 
	{
		this.organismSelected=organism;
		try{
		Class.forName("com.mysql.jdbc.Driver");
		//Information should be the same; "EDP" should be changed to password of user you are using on your system
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","EDP");
		//Create MySQL query statement
		}
		catch(Exception e)
		{
			
		}
	}
	
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

	public ExtendedGraph createMutableGraph() 
	{
		ExtendedGraph testing = new ExtendedGraph();
	//	vtkMutableUndirectedGraph testing = new vtkMutableUndirectedGraph();
		try
		{
			statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B from " + organismSelected);
			rs = statement.executeQuery();
			
		
				//Create hashmaps so that the gene or id can be retrieved when the other is known
				geneToId = new HashMap();
				idToGene = new HashMap();
					
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
			
			//create labels for the corresponding genes
			
			for(int j=0; j<geneToId.size();j++)
			{
				v[j]=testing.getGraph().AddVertex();
				labels.InsertNextValue((String)idToGene.get(j));
			}
			
			//find repeat edges
			statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B, count(*) as NumDup from " + organismSelected + " GROUP BY Official_Symbol_Interactor_A, Official_Symbol_Interactor_B having NumDup>0");
			//statement = con.prepareStatement("SELECT ta.Official_Symbol_Interactor_A,ta.Official_Symbol_Interactor_B, COUNT(*) from " + organism + " ta WHERE (SELECT COUNT(*) FROM " + organism + " ta2 WHERE (ta.Official_Symbol_Interactor_A=ta2.Official_Symbol_Interactor_A AND ta.Official_Symbol_Interactor_B=ta2.Official_Symbol_Interactor_B) OR (ta.Official_Symbol_Interactor_A=ta2.Official_Symbol_Interactor_B AND ta.Official_Symbol_Interactor_B=ta2.Official_Symbol_Interactor_A) )>0 GROUP BY Official_Symbol_Interactor_A, Official_Symbol_Interactor_B");
			//statement = con.prepareStatement("SELECT Official_Symbol_Interactor_A,Official_Symbol_Interactor_B from " + organism);
			
			rs = statement.executeQuery();
			
			//Hold the interactions of genes with indexes representing interaction or edge number
			
			ArrayList<String> gene1 = new ArrayList<String>();
			ArrayList<String> gene2 = new ArrayList<String>();
			
			//Array list to hold edge weight values
			List<Integer> edgeWeights = new ArrayList<Integer>();
			
			int j =0;
			
			
		//similarValues returns -1 if the two genes have not been added and returns the index of the interactions if their interaction has been added
			while(rs.next())
			{
				ArrayList gene1Temp=CreateGraph.indexOfAll(rs.getString("Official_Symbol_Interactor_B"), gene1);
				ArrayList gene2Temp=CreateGraph.indexOfAll(rs.getString("Official_Symbol_Interactor_A"), gene2);
			//	System.out.println(rs.getString("Official_Symbol_Interactor_A") + " " + rs.getString("Official_Symbol_Interactor_B") + " " + rs.getInt(3));
				
				//System.out.println(createGraph.similarValues(gene1Temp, gene2Temp));
				if(CreateGraph.similarValues(gene1Temp,gene2Temp)<0)
				{
					gene1.add(rs.getString("Official_Symbol_Interactor_A"));
					gene2.add(rs.getString("Official_Symbol_Interactor_B"));
					testing.getGraph().AddGraphEdge(v[(int)geneToId.get(rs.getString("Official_Symbol_Interactor_A"))],v[(int)geneToId.get(rs.getString("Official_Symbol_Interactor_B"))]);
					edgeWeights.add(rs.getInt(3));
				}
				else if(CreateGraph.similarValues(gene1Temp,gene2Temp)>=0)
				{
					int index = CreateGraph.similarValues(gene1Temp,gene2Temp);
					edgeWeights.add(index, edgeWeights.get(index)+rs.getInt(3));
				}
			}
			
			//add label and edge weight arrays to graph
		
			for(int l=0;l<edgeWeights.size();l++)
			{
				weights.InsertNextValue(edgeWeights.get(l));
			}
			testing.getGraph().GetVertexData().AddArray(labels);
			testing.getGraph().GetEdgeData().AddArray(weights);
			this.setAttributes(testing);		
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
	
	public void setAttributes(ExtendedGraph graph) throws Exception
	{
		
		//Array lists to hold edge indices. Since the edges have been grouped in order to make
		//edge weights the data for each interaction between to genes must be maintained through a 2-d array list
		ArrayList<ArrayList> pubMedID = new ArrayList<ArrayList>();
		ArrayList<ArrayList> experimentalSystem = new ArrayList<ArrayList>();
		ArrayList<ArrayList> experimentalSystemType = new ArrayList<ArrayList>();
		ArrayList<ArrayList> author = new ArrayList<ArrayList>();
		vtkEdgeListIterator iterator = new vtkEdgeListIterator();
		
		//Edge list iterator
		graph.getGraph().GetEdges(iterator);
		vtkGraphEdge edge;
		int gene1;
		int gene2;
		
		
		//used for debugging purposes
		vtkStringArray geneNames = (vtkStringArray)graph.getGraph().GetVertexData().GetAbstractArray("labels");
		vtkIntArray edgeWeights = (vtkIntArray)graph.getGraph().GetEdgeData().GetAbstractArray("weights");
		
		//Temporary array lists to add to the array lists that hold the edge indices
		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<String> temp2 = new ArrayList<String>();
		ArrayList<String> temp3 = new ArrayList<String>();
		ArrayList<String> temp4 = new ArrayList<String>();
		
		
		
		System.out.println("LIST /n/n");
		while(iterator.HasNext())
		{
			edge=iterator.NextGraphEdge();
			gene1=edge.GetSource();
			gene2=edge.GetTarget();
			int edgeId=edge.GetId();
			
			System.out.println(idToGene.get(gene1) + " " + idToGene.get(gene2) + " " + edgeWeights.GetValue(edgeId) );
			//Query database for desired attributes
			statement = con.prepareStatement("SELECT Pubmed_ID,Experimental_System,Author,Experimental_System_Type from " + organismSelected + " WHERE (Official_Symbol_Interactor_A= '" + geneNames.GetValue(gene1) + "' AND Official_Symbol_Interactor_B='" + geneNames.GetValue(gene2)+ "') OR (Official_Symbol_Interactor_A= '" + geneNames.GetValue(gene2) + "' AND Official_Symbol_Interactor_B='" + geneNames.GetValue(gene1)+ "')");
			rs= statement.executeQuery();
			
			while(rs.next())
			{
				System.out.println(rs.getInt(1) + " " +rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
				temp.add(rs.getInt(1));
				temp2.add(rs.getString(2));
				temp3.add(rs.getString(3));
				temp4.add(rs.getString(4));
			}
			
			pubMedID.add(temp);
			experimentalSystem.add(temp2);
			author.add(temp3);
			experimentalSystemType.add(temp4);
			
		}
	}
	 
}


