import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	BufferedReader reader;
	int taxID;
	String geneName;
	int geneOrOrg;
	Map<Integer,String> taxIDToName = new HashMap<Integer,String>();
	
	public CreateGraph(String organism) 
	{
		this.organismSelected=organism;
		try{
		Class.forName("com.mysql.jdbc.Driver");
		//Information should be the same; "EDP" should be changed to password of user you are using on your system
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","Edp1234");
		//Create MySQL query statement
		}
		catch(Exception e)
		{
			
		}
	}
	
	public CreateGraph(BufferedReader reader)
	{
		this.reader=reader;
	}
	
	public CreateGraph(int taxID, HashMap<String,Integer> nameToTaxID)
	{
		this.taxID=taxID;
		this.geneOrOrg=1;
		for(Map.Entry<String,Integer> entry: nameToTaxID.entrySet())
		{
			taxIDToName.put(entry.getValue(), entry.getKey());
		}
		
	}
	
	public CreateGraph(String geneName, HashMap<String,Integer> nameToTaxID)
	{
		this.geneName=geneName;
		this.geneOrOrg=0;
		for(Map.Entry<String,Integer> entry: nameToTaxID.entrySet())
		{
			taxIDToName.put(entry.getValue(), entry.getKey());
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

	 
	 //CREATE MUTABLE GRAPH FROM DATABASE
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
	static ArrayList<Integer> indexOfAll(Object obj, List list){
	    ArrayList<Integer> indexList = new ArrayList<Integer>();
	    for (int i = 0; i < list.size(); i++)
	    {
	        if(obj.equals(list.get(i))){
	            indexList.add(i);
	        }
	    }
	    return indexList;
	}
	
	//Test to see if there is one value of equal index (this would indicate the interaction has been recorded)
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
	
	
	
	
	
	//CREATE GRAPH FROM URL BASED INFORMATION AS WELL AS SET ATTRIBUTES
	public ExtendedGraph createMutableGraphURL()
	{
		
		ExtendedGraph testing = new ExtendedGraph();
		
		ArrayList<ArrayList> pubMedID = new ArrayList<ArrayList>();
		ArrayList<ArrayList> experimentalSystem = new ArrayList<ArrayList>();
		ArrayList<ArrayList> experimentalSystemType = new ArrayList<ArrayList>();
		ArrayList<ArrayList> author = new ArrayList<ArrayList>();
		
		ArrayList<String> geneNames = new ArrayList<String>();
		ArrayList<String> organismNames = new ArrayList<String>();
		
		try{
			
			//URL url = new URL("http://webservice.thebiogrid.org/interactions/?searchNames=true&geneList=MDM2&taxId=9606&includeInteractors=true&includeInteractorInteractions=true&accesskey=457329496765df827aec5829b6b6d26a");
		//	http://webservice.thebiogrid.org/interactions/?taxId=9606&includeInteractors=true&includeInteractorInteractions=true&accesskey=457329496765df827aec5829b6b6d26a
			URL url=null;
			if(geneOrOrg==1)
				url = new URL("http://webservice.thebiogrid.org/interactions/?includeInteractors=true&includeInteractorInteractions=true&taxId=" + Integer.toString(taxID) +"&accesskey=457329496765df827aec5829b6b6d26a");
			else if(geneOrOrg==0)
				url= new URL("http://webservice.thebiogrid.org/interactions/?searchNames=true&geneList=" + geneName + "&includeInteractors=true&includeInteractorInteractions=true&accesskey=457329496765df827aec5829b6b6d26a");
			System.out.println(url.toString());
			URLConnection urlConnect=url.openConnection();
			InputStreamReader in;
			String s;
			TabDelimitedReader tabRead = new TabDelimitedReader();
			ArrayList<String> tempValues = new ArrayList<String>();
			
			if(urlConnect!=null)
				urlConnect.setReadTimeout(60*1000);
			if(urlConnect!=null)
			{
				in = new InputStreamReader(urlConnect.getInputStream());
				BufferedReader buffRead = new BufferedReader(in);
			
				while((s=buffRead.readLine())!=null)
				{
					tempValues =tabRead.readLine(s);
					if(geneNames.contains(tempValues.get(7))!=true)
					{
						geneNames.add(tempValues.get(7));
						organismNames.add(tempValues.get(15));
					}
					
					if(geneNames.contains(tempValues.get(8))!=true)
					{
						geneNames.add(tempValues.get(8));
						organismNames.add(tempValues.get(16));
					}
				}
				
				String[] genes= new String[geneNames.size()];
				geneNames.toArray(genes);
				geneNames.clear();
				
			//	Arrays.sort(genes,String.CASE_INSENSITIVE_ORDER);
				List<String> gene1 = new ArrayList<String>(); //parallel arrays to represent edges
				List<String> gene2 = new ArrayList<String>();
				List<Integer> edgeWeights = new ArrayList<Integer>();
				
				//Sorted Array List and Adding edges
				int v[]= new int[genes.length];
				List<String> sortedGenes = new ArrayList<String>(Arrays.asList(genes));
				for(int i=0;i<genes.length;i++)
				{
					v[i]=testing.getGraph().AddVertex();
				}
				
				
				urlConnect=url.openConnection();
				in= new InputStreamReader(urlConnect.getInputStream());
				buffRead = new BufferedReader(in);
				int currentEdge=0;
				while((s=buffRead.readLine())!=null)
				{
					tempValues=tabRead.readLine(s);
					int indexOfValues;
					indexOfValues=CheckIndices(tempValues.get(7),tempValues.get(8), gene1,gene2);
					
					if(indexOfValues==-1)
					{
						gene1.add(tempValues.get(7));
						gene2.add(tempValues.get(8));
						edgeWeights.add(1);
						
						
						pubMedID.add(new ArrayList());
						pubMedID.get(currentEdge).add(tempValues.get(14));
						
						author.add(new ArrayList());
						author.get(currentEdge).add(tempValues.get(13));
						
						experimentalSystem.add(new ArrayList());
						experimentalSystem.get(currentEdge).add(tempValues.get(11));
						
						experimentalSystemType.add(new ArrayList());
						experimentalSystemType.get(currentEdge).add(tempValues.get(12));
						
					
						testing.getGraph().AddGraphEdge(v[sortedGenes.indexOf(tempValues.get(7))], v[sortedGenes.indexOf(tempValues.get(8))]);
						currentEdge++;
					}
					else
					{
				
						edgeWeights.set(indexOfValues,edgeWeights.get(indexOfValues)+1);
						
						
						pubMedID.get(indexOfValues).add(tempValues.get(14));
						author.get(indexOfValues).add(tempValues.get(13));
						experimentalSystem.get(indexOfValues).add(tempValues.get(11));
						experimentalSystemType.get(indexOfValues).add(tempValues.get(12));	
					}
				}
				
				vtkStringArray labels = new vtkStringArray();
				labels.SetNumberOfComponents(1);
				labels.SetName("labels");
				
				vtkStringArray organisms = new vtkStringArray();
				organisms.SetNumberOfComponents(1);
				organisms.SetName("organisms");
				
				for(int i=0; i<sortedGenes.size();i++)
				{
					labels.InsertNextValue(sortedGenes.get(i));
					organisms.InsertNextValue(organismNames.get(i));
				}
				
//				for(int i=0;i<genesSortedvtk.GetSize();i++)
//					System.out.println(genesSortedvtk.GetValue(i));
				
				testing.getGraph().GetVertexData().AddArray(labels);
				testing.getGraph().GetVertexData().AddArray(organisms);
				//testing.getGraph().GetVertexData().AddArray(organismVtk);


				vtkIntArray weights = new vtkIntArray();
				weights.SetNumberOfComponents(1);
				weights.SetName("weights");
				
				int totalInts=0;
	
				for(int i=0; i<edgeWeights.size();i++)
				{
					weights.InsertNextValue(edgeWeights.get(i));
					totalInts= totalInts + edgeWeights.get(i);
				}
				
				testing.getGraph().GetEdgeData().AddArray(weights);
				testing.setAuthor(author);
				testing.setPubMedID(pubMedID);
				testing.setSystem(experimentalSystem);
				testing.setSystemType(experimentalSystemType);
				
				
				System.out.println("Edges : " + totalInts);
				System.out.println("Vertices : " + sortedGenes.size());
			}
			
		}
		catch(Exception e)
		{ 
			System.out.println(e.getMessage());
		}

		return testing;
	}
	
	//CHECKS FOR COMMON VALUES AT THE SAME INDEX, THIS WILL INDICATE THAT THE INTERACTION HAS BEEN ADDED TO THE GRAPH
	public int CheckIndices(String gene1, String gene2, List<String> list1, List<String> list2)
	{
		
		int index=-1;
		List<Integer> gene1List1 = indexOfAll(gene1,list1);
		List<Integer> gene1List2= indexOfAll(gene1,list2);
		List<Integer> gene2List1= indexOfAll(gene2,list1);
		List<Integer> gene2List2= indexOfAll(gene2,list2);
		
		gene1List1.retainAll(gene2List2);
		gene2List1.retainAll(gene1List2);
		
		if(gene1List1.size()>0)
			index=gene1List1.get(0);
		else if(gene2List1.size()>0)
			index=gene2List1.get(0);
		return index;
	}
	
	
	//SET ATTRIBUTES OF GRAPH FROM DATABASE
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
		
		
		
		//System.out.println("LIST /n/n");
		while(iterator.HasNext())
		{
			edge=iterator.NextGraphEdge();
			gene1=edge.GetSource();
			gene2=edge.GetTarget();
			int edgeId=edge.GetId();
			
		//	System.out.println(idToGene.get(gene1) + " " + idToGene.get(gene2) + " " + edgeWeights.GetValue(edgeId) );
			//Query database for desired attributes
			statement = con.prepareStatement("SELECT Pubmed_ID,Experimental_System,Author,Experimental_System_Type from " + organismSelected + " WHERE (Official_Symbol_Interactor_A= '" + geneNames.GetValue(gene1) + "' AND Official_Symbol_Interactor_B='" + geneNames.GetValue(gene2)+ "') OR (Official_Symbol_Interactor_A= '" + geneNames.GetValue(gene2) + "' AND Official_Symbol_Interactor_B='" + geneNames.GetValue(gene1)+ "')");
			rs= statement.executeQuery();
			
			while(rs.next())
			{
				//System.out.println(rs.getInt(1) + " " +rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
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
		graph.setAuthor(author);
		graph.setSystem(experimentalSystem);
		graph.setPubMedID(pubMedID);
		graph.setSystemType(experimentalSystemType);
	}
	
	
	public ExtendedGraph ImportMutableGraph() throws Exception
	{
		ExtendedGraph graph = new ExtendedGraph();
		//text oder: Gene1,Gene2, Edge Weight, User Edge Weight, Author, System Type, System, PubmedID
		String s=reader.readLine();//first line is only headers
		
		
		String tab="\t";
		int index1=0;
		int index2=0;
		
		int tempVert1; //Holds vertex id for adding edges
		int tempVert2; //Holds vertex id for adding edges
		
		String gene1;
		String gene2;
		
		//label array
		vtkStringArray labels = new vtkStringArray();
		labels.SetNumberOfComponents(1);
		labels.SetName("labels");
		
		//organism name array
		vtkStringArray organisms = new vtkStringArray();
		organisms.SetNumberOfComponents(1);
		organisms.SetName("organisms");
		
		
		//edge weight array
		vtkIntArray weights = new vtkIntArray();
		weights.SetNumberOfComponents(1);
		weights.SetName("weights");
		
		geneToId= new HashMap(); //going from gene name to vertex id
		idToGene = new HashMap(); //vice versa
		int vertexNum=0; //increment for vertex id
		int edgeNum=0;
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		
		int edgeWeight;
		//Arrays to hold relevant data for each edge
		ArrayList<ArrayList> author = new ArrayList<ArrayList>();
		ArrayList<ArrayList> systemType= new ArrayList<ArrayList>();
		ArrayList<ArrayList> system = new ArrayList<ArrayList>();
		ArrayList<ArrayList> pubMedID = new ArrayList<ArrayList>();
		ArrayList<String> tempValues = new ArrayList<String>();
		
		//Arrays hold temporary data for a single edge
		ArrayList authorTemp = new ArrayList();
		ArrayList systemTypeTemp= new ArrayList();
		ArrayList systemTemp = new ArrayList();
		ArrayList pubMedIDTemp = new ArrayList();
		
		
		while((s=reader.readLine())!=null)
		{
			index2= s.indexOf(tab);
			while(index2!=-1)
			{
				tempValues.add(s.substring(index1, index2));
				s=s.substring(index2+1, s.length());
				index2= s.indexOf(tab);
			}
			
			if(idToGene.containsValue(tempValues.get(0))!=true)
			{
				idToGene.put(vertexNum, tempValues.get(0));//add vertex
				geneToId.put(tempValues.get(0), vertexNum);
				labels.InsertNextValue(tempValues.get(0));
				organisms.InsertNextValue(tempValues.get(2));
				vertices.add(graph.getGraph().AddVertex());
				vertexNum++;
			}
			if(idToGene.containsValue(tempValues.get(1))!=true)
			{
				idToGene.put(vertexNum, tempValues.get(1));//add Vertex
				geneToId.put(tempValues.get(1), vertexNum);
				labels.InsertNextValue(tempValues.get(1));
				organisms.InsertNextValue(tempValues.get(3));
				vertices.add(graph.getGraph().AddVertex());
				vertexNum++;
			}
			
			
			//Insert edge weight value into array
			edgeWeight=Integer.parseInt(tempValues.get(4));
			weights.InsertNextValue(edgeWeight);
			
		//	System.out.println("Test" + tempValues.get(0) + ": " + geneToId.get(tempValues.get(0))+ " "+ tempValues.get(1) + ": " + geneToId.get(tempValues.get(1)));
			
			tempVert1=(int)geneToId.get(tempValues.get(0));
			tempVert2=(int)geneToId.get(tempValues.get(1));
			
			graph.getGraph().AddGraphEdge(tempVert1,tempVert2); //Add edge based on vertex indices
			//graph.getGraph().AddGraphEdge(vertices.get((int)geneToId.get(tempValues.get(0))),vertices.get((int)geneToId.get(tempValues.get(1)))); //Add edge based on vertex indices
		//	System.out.println("Test");
			
			tempValues.clear();
			
			for(int i=0; i<edgeWeight;i++)
			{
				s=reader.readLine();
				//System.out.println(s);
				index2= s.indexOf(tab);
				//System.out.println(index2);
				while(index2!=-1)
				{
					tempValues.add(s.substring(0, index2));
					s=s.substring(index2+1, s.length());
					index2= s.indexOf(tab);
				}
			//	System.out.println("Size:" +tempValues.size());
				//Load temp values for single edge
				
				authorTemp.add(tempValues.get(5));
				systemTemp.add(tempValues.get(7));
				systemTypeTemp.add(tempValues.get(6));
				pubMedIDTemp.add(tempValues.get(8));
				tempValues.clear();
			}
			//Add temp values to the specified edge
			author.add(new ArrayList());
			system.add(new ArrayList());
			systemType.add(new ArrayList());
			pubMedID.add(new ArrayList());
			
			
			for(int i=0; i<edgeWeight; i++)
			{
				author.get(edgeNum).add(authorTemp.get(i));
				system.get(edgeNum).add(systemTemp.get(i));
				systemType.get(edgeNum).add(systemTypeTemp.get(i));
				pubMedID.get(edgeNum).add(pubMedIDTemp.get(i));
			}

			authorTemp.clear();
			systemTemp.clear();
			systemTypeTemp.clear();
			pubMedIDTemp.clear();
			edgeNum++;
		}
		

		//Set relevant information
		graph.getGraph().GetVertexData().AddArray(labels);
		graph.getGraph().GetVertexData().AddArray(organisms);
		graph.getGraph().GetEdgeData().AddArray(weights);
		
		graph.setAuthor(author);
		graph.setSystem(system);
		graph.setSystemType(systemType);
		graph.setPubMedID(pubMedID);
		return graph;
	}
}


