import vtk.vtkBoostBreadthFirstSearch;
import vtk.vtkBoostConnectedComponents;
import vtk.vtkEdgeListIterator;
import vtk.vtkGraphEdge;
import vtk.vtkIdTypeArray;
import vtk.vtkIntArray;
import vtk.vtkMutableUndirectedGraph;
import vtk.vtkSelection;
import vtk.vtkSelectionNode;


public class GraphPath {
	
//Finds the shortest path between two nodes	
public vtkIdTypeArray FindPath(int vertex_ID1, int vertex_ID2,vtkMutableUndirectedGraph g) {		
		
		System.out.println("Looking for path....");
			
		vtkIdTypeArray cpath=new vtkIdTypeArray(); //current path
		vtkIntArray cbfs=new vtkIntArray();   //current bfs values
		vtkIntArray steps=new vtkIntArray();  //holds the nodes that are connected to current bfs origin
		vtkBoostBreadthFirstSearch my_BFS=new vtkBoostBreadthFirstSearch(); //current BFS object
		boolean Found=false;     //boolean to signal when path is found
		int cindex=0;            //current step node
		int steps_left;          //step or hops left to the next node
		
		//If they are the same vertex
		if(vertex_ID1==vertex_ID2){
			
			Found=true;
			
		}
		

		try{
			//check if path exists or not
		
			//This class takes the input graph and creates an array 
			//each element of the array corresponds to a specific vertex
			//if two vertices have the same value they are connected
			vtkBoostConnectedComponents components=new vtkBoostConnectedComponents();
		
			//set input graph
			components.SetInputData(g);
			components.Update();
			
			vtkIntArray component_array=new vtkIntArray();
		    component_array=(vtkIntArray)components.GetOutput().GetVertexData().GetArray("component");
		    
			if(component_array.GetValue(vertex_ID1) == component_array.GetValue(vertex_ID2)){
								
				System.out.println("A path exists");
			}
			else{
				
				throw new Exception("No path exists");
								
			}	
		
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return cpath;
						
		}
		
		//begin the path with the first vertex
		cpath.InsertNextValue(vertex_ID1);
		
		//bfs the first vertex
		my_BFS.SetInputData(g);
		my_BFS.SetOriginVertex(vertex_ID1);
		my_BFS.Update();
		
		//Get initial vertex array
		cbfs=(vtkIntArray)my_BFS.GetOutput().GetVertexData().GetArray("BFS");
		
		//find the possible next nodes
		for(int i=0;i<cbfs.GetNumberOfTuples();i++){
						
			if(cbfs.GetValue(i)==1){
				
				steps.InsertNextValue(i);
				
			}
						
		}
		
		//total steps in path minus 1
		steps_left=cbfs.GetValue(vertex_ID2)-1;
//		AppTesting2.updateVertexNumber(steps_left+1);
		
		//Find the path
		while(!Found){
			
			//set origin to next possible step			
			my_BFS.SetOriginVertex(steps.GetValue(cindex));
			my_BFS.Update();
			
			cbfs=(vtkIntArray)my_BFS.GetOutput().GetVertexData().GetArray("BFS");
			
			if(cbfs.GetValue(vertex_ID2)==steps_left){
				//We are on the right path if the steps left are 1 less the original
				
				cpath.InsertNextValue(steps.GetValue(cindex));
								
				//Found the path
				if(steps.GetValue(cindex)==vertex_ID2){
					Found=true;
				}
				
				//find the next possible steps
				
				for(int i=0;i<cbfs.GetNumberOfTuples();i++){
					
					if(cbfs.GetValue(i)==1){
					steps.InsertNextValue(i);
					}
				}
				
				//reset the index and decrement the steps needed 
				cindex=0;
				steps_left=steps_left-1;
								
			}
			else{
				// check the next step
				cindex=cindex+1;
			}
					
		}
			
		System.out.println("Size of path: " + cpath.GetSize());
		return cpath;
	}

//Find the edges between nodes
public vtkIdTypeArray FindEdges(vtkIdTypeArray vertex_list, vtkMutableUndirectedGraph g){
				
		vtkEdgeListIterator edges=new vtkEdgeListIterator();
		vtkIdTypeArray edgearray=new vtkIdTypeArray();
		
		for(int j=0;j<vertex_list.GetSize()-1;j++){
						
			g.GetEdges(edges);
			while(edges.HasNext()){
			
				vtkGraphEdge e=edges.NextGraphEdge();
						
				if((e.GetSource()==vertex_list.GetValue(j) && e.GetTarget()==vertex_list.GetValue(j+1))||(e.GetSource()==vertex_list.GetValue(j+1) && e.GetTarget()==vertex_list.GetValue(j))){
					edgearray.InsertNextValue(e.GetId());
				}
			}
		}		
	
		System.out.println("Number of Edges in path: " + edgearray.GetSize());
		return edgearray;			
}

	
//Given vertex and edge list this method outputs the selection
public vtkSelection GetSelection(vtkIdTypeArray vertex_list,vtkIdTypeArray edge_list){
	
	vtkSelection sel=new vtkSelection();
	
	vtkSelectionNode vertex_sel=new vtkSelectionNode();
	vtkSelectionNode edge_sel=new vtkSelectionNode();
					
	vertex_sel.SetFieldType(3);
	vertex_sel.SetContentType(4);
	
	edge_sel.SetFieldType(4);
	edge_sel.SetContentType(4);
	
	vertex_sel.SetSelectionList(vertex_list);
    edge_sel.SetSelectionList(edge_list);
    
   
	sel.AddNode(edge_sel);
	sel.AddNode(vertex_sel);
	
	return sel;	
	
	
}

public boolean DoesPathExist(int vertex_ID1, int vertex_ID2,vtkMutableUndirectedGraph g){

	
		//check if path exists or not
	
		//This class takes the input graph and creates an array 
		//each element of the array corresponds to a specific vertex
		//if two vertices have the same value they are connected
		vtkBoostConnectedComponents components=new vtkBoostConnectedComponents();
	
		//set input graph
		components.SetInputData(g);
		components.Update();
		
		vtkIntArray component_array=new vtkIntArray();
	    component_array=(vtkIntArray)components.GetOutput().GetVertexData().GetArray("component");
	    
		if(component_array.GetValue(vertex_ID1) == component_array.GetValue(vertex_ID2)){
							
			System.out.println("A path exists");
			return true;
		}
		else{
			
			return false;
							
		}	
	
	}
	
	
	
	
	
}

