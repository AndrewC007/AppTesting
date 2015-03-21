import java.util.ArrayList;
import java.util.List;

import vtk.*;

public class ExtendedGraph extends vtkMutableUndirectedGraph {
	private vtkMutableUndirectedGraph graph;
	private ArrayList<ArrayList> author;
	private ArrayList<ArrayList> systemType;
	private ArrayList<ArrayList> system;
	private ArrayList<ArrayList> pubMedID;
	
	public ExtendedGraph(){
		
		graph= new vtkMutableUndirectedGraph();
		
		author=new ArrayList<ArrayList>();
		systemType=new ArrayList<ArrayList>();
		system=new ArrayList<ArrayList>();
		pubMedID=new ArrayList<ArrayList>();	
	}
	
	
	//getter and setter methods
	public vtkMutableUndirectedGraph getGraph()
	{
		return graph;
	}
	
	public void setGraph(vtkMutableUndirectedGraph graph)
	{
		this.graph= graph;
	}
	
	public ArrayList<ArrayList> getAuthor()
	{
		return author;
	}
	
	public void setAuthor(ArrayList<ArrayList> author)
	{
		this.author = author;
	}
	
	public ArrayList<ArrayList> getSystem()
	{
		return system;
	}
	
	public void setSystem(ArrayList<ArrayList> system)
	{
		this.system = system;
	}
	
	public ArrayList<ArrayList> getSystemType()
	{
		return systemType;
	}
	
	public void setSystemType(ArrayList<ArrayList> systemType)
	{
		this.systemType = systemType;
	}
	
	public ArrayList<ArrayList> getPubMedID()
	{
		return pubMedID;
	}
	
	public void setPubMedID(ArrayList<ArrayList> pubMedID)
	{
		this.pubMedID = pubMedID;
	}
	
	public vtkIntArray getEdgeWeights()
	{
		return (vtkIntArray)this.graph.GetEdgeData().GetAbstractArray("weights");
	}
	
	public vtkStringArray getLabels()
	{
		return (vtkStringArray)this.graph.GetVertexData().GetAbstractArray("labels");
	}
}
