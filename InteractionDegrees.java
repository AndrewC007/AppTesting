import java.util.ArrayList;
import java.util.List;

import vtk.*;

public class InteractionDegrees {
	vtkIntArray Path=new vtkIntArray();
	vtkIntArray ePath=new vtkIntArray();
	vtkBoostBreadthFirstSearch my_BFS=new vtkBoostBreadthFirstSearch();
	
	public static vtkSelection SelectDegrees(vtkMutableUndirectedGraph graph, int degrees, int vertexIndex)
	{
		vtkIdTypeArray selectedNodes = new vtkIdTypeArray();
		selectedNodes.InsertNextValue(vertexIndex);
		
		vtkBoostBreadthFirstSearch boost = new vtkBoostBreadthFirstSearch();
		
		boost.SetInputData(graph);
		boost.SetOriginVertex(vertexIndex);
		boost.Update();
		
		vtkIdTypeArray bfsNodes = new vtkIdTypeArray();
		vtkIntArray temp = (vtkIntArray)boost.GetOutput().GetVertexData().GetAbstractArray("BFS");
		
		for(int i=0;i<temp.GetNumberOfTuples();i++)
			bfsNodes.InsertNextValue(temp.GetValue(i));
		
		//POPULATING VERTEX LIST
		for(int i=1; i<=degrees;i++)
		{
			for(int j=0; j<bfsNodes.GetNumberOfTuples();j++)
			{
				if(bfsNodes.GetValue(j)==i)
					selectedNodes.InsertNextValue(j);
			}
		}
		
		List<Integer> selectedVertices = new ArrayList<Integer>();
		
		//COPYING TO ARRAY LIST FOR EASE OF SEARCH
		for(int i=0; i<selectedNodes.GetNumberOfTuples(); i++)
		{
			selectedVertices.add(selectedNodes.GetValue(i));
		}
		
		vtkIdTypeArray selectedEdges = new vtkIdTypeArray();
		
		
		//FINDING RELEVANT EDGES
		vtkEdgeListIterator edges=new vtkEdgeListIterator();
		graph.GetEdges(edges);
		while(edges.HasNext())
		{
			vtkGraphEdge e=edges.NextGraphEdge();
			if((selectedVertices.contains(e.GetTarget()) && selectedVertices.contains(e.GetSource())))
			{
				selectedEdges.InsertNextValue(e.GetId());
			}
			
		}
		
		vtkSelection sel = new vtkSelection(); 
		vtkSelectionNode node = new vtkSelectionNode(); //Vertex selectionNode
		node.SetContentType(4);
		node.SetFieldType(3);
		
		vtkSelectionNode edgeSelection = new vtkSelectionNode(); //edge selection
		edgeSelection.SetContentType(4);
		edgeSelection.SetFieldType(4);
				 
		
		edgeSelection.SetSelectionList(selectedEdges);
		node.SetSelectionList(selectedNodes);
		sel.AddNode(edgeSelection);
		sel.AddNode(node);
		return sel;
	}

}
