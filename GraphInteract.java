import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import vtk.*;

public class GraphInteract {
	
	public String articleInfo;
	
	//SINGLE GENE SEARCH WITHOUT DEGREES OF INTERACTION
	public void selectNode(vtkAnnotationLink link, String gene,vtkMutableUndirectedGraph graph)
	{
		
		try{
			vtkSelection sel = new vtkSelection(); 
			vtkSelectionNode node = new vtkSelectionNode(); //Vertex selectionNode
			node.SetContentType(4);
			node.SetFieldType(3);
			
			 // Required for SelectionCallback function to set the vertex selectionNode to node 1 in the selection
			vtkSelectionNode emptyEdgeNode = new vtkSelectionNode(); 
			
			vtkStringArray geneNames = (vtkStringArray)graph.GetVertexData().GetAbstractArray("labels");
	
			
			
			for(int i=0; i<geneNames.GetNumberOfTuples();i++)
			{
				if(gene.equals(geneNames.GetValue(i)))
				{
					
					vtkIdTypeArray temp= new vtkIdTypeArray();
					temp.InsertNextValue(i);
					
					node.SetSelectionList(temp);
					sel.AddNode(emptyEdgeNode);
					sel.AddNode(node);
					link.SetCurrentSelection(sel);
					break;
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception Thrown:");
			System.out.println(e.getMessage());
		}
		
	}
	
	//GENE SEARCH WITH DEGREES OF INTERACTION (USES AN EXTERNAL FUNCTION CREATED IN A SEPERATE CLASS)(Separate class created
	//for simplicity of merging the code
	public void selectNode(vtkAnnotationLink link, String gene, String degrees,vtkMutableUndirectedGraph graph)
	{
		if(Integer.parseInt(degrees)==0)
		{
			System.out.println("TEST 0");
			try{
				vtkSelection sel = new vtkSelection(); 
				vtkSelectionNode node = new vtkSelectionNode(); //Vertex selectionNode
				node.SetContentType(4);
				node.SetFieldType(3);
				
				 // Required for SelectionCallback function to set the vertex selectionNode to node 1 in the selection
				vtkSelectionNode emptyEdgeNode = new vtkSelectionNode(); 
				
				vtkStringArray geneNames = (vtkStringArray)graph.GetVertexData().GetAbstractArray("labels");
		
				
				
				for(int i=0; i<geneNames.GetNumberOfTuples();i++)
				{
					if(gene.equals(geneNames.GetValue(i)))
					{
						
						vtkIdTypeArray temp= new vtkIdTypeArray();
						temp.InsertNextValue(i);
						
						node.SetSelectionList(temp);
						sel.AddNode(emptyEdgeNode);
						sel.AddNode(node);
						link.SetCurrentSelection(sel);
						break;
					}
				}
			}
			catch(Exception e)
			{
				System.out.println("Exception Thrown:");
				System.out.println(e.getMessage());
			}
		}
		else if(Integer.parseInt(degrees)>=1)
		{
			System.out.println("TEST");
			vtkStringArray geneNames = (vtkStringArray)graph.GetVertexData().GetAbstractArray("labels");
			for(int i=0; i<geneNames.GetNumberOfTuples();i++)
			{
				if(gene.equals(geneNames.GetValue(i)))
				{
					InteractionDegrees.SelectDegrees(graph, Integer.parseInt(degrees), i, link);
					break;
					
				}
			}
			
		}
		
	}
	
	
	public vtkMutableUndirectedGraph extract(vtkIdTypeArray vertices, vtkMutableUndirectedGraph originalGraph )
	{
		vtkMutableUndirectedGraph extractedGraph = new vtkMutableUndirectedGraph();
		
		vtkSelection sel = new vtkSelection();
		vtkSelectionNode node = new vtkSelectionNode();
		node.SetContentType(4);
		node.SetFieldType(3);
		
		
		node.SetSelectionList(vertices);
		sel.AddNode(node);
		vtkExtractSelectedGraph extract = new vtkExtractSelectedGraph();
		
		extract.SetInputData(0, originalGraph);
		extract.SetInputData(1, sel);
		extract.Update();
		
		extractedGraph.DeepCopy(extract.GetOutputDataObject(0));
		return extractedGraph;
	}
	
	
		 
/// READING AN XML FILE		
	   public String articleParser(URL pubmedURL, String infoWanted) {
	 
	    try {
	    
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
	 
		
	     
	 
	    // System.out.println(pubmedURL.toString());
	     URLConnection conn = pubmedURL.openConnection ();
	     HandlerOverride handler = new HandlerOverride(infoWanted);
	     saxParser.parse(conn.getInputStream(), handler);
	     //System.out.println(articleInfo);

	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	    
	    	 return articleInfo;
	   
	   }
	   
	   public class HandlerOverride extends DefaultHandler {
			 
			boolean abstractTest=false;
			boolean title=false;
			boolean journal=false;
			String infoWanted;
			
			public HandlerOverride(String info)
			{
				this.infoWanted=info;
				articleInfo="";
			}
			
			public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
				
				if(qName.equalsIgnoreCase("abstractText"))
				{
					abstractTest=true;
				}
				if(qName.equalsIgnoreCase("Title"))
				{
					title=true;
				}
				if(qName.equalsIgnoreCase("journal"))
				{
					journal=true;
				}
			}
		 
			public void endElement(String uri, String localName,
					String qName) throws SAXException {
			 
					if(qName.equalsIgnoreCase("journal"))
					{
						journal = false;
					}
					if(qName.equalsIgnoreCase("abstractText"))
					{
						abstractTest=false;
					}
					if(qName.equalsIgnoreCase("Title"))
					{
						title=false;
					}
			 
				}

		 
			public void characters(char ch[], int start, int length) throws SAXException {
		 
				if(infoWanted.equals("Abstract"))
				{
					if(abstractTest)
					{
						String s = new String(ch, start, length);
						articleInfo+=s;
					}
				}
				else if(infoWanted.equals("Title"))
				{
					if(title)
					{
						if(!journal)
						{
							String s = new String(ch, start, length);
							articleInfo+=s;
						}
					}
				}

		 
			}
		 
		 }
	   
	   //READING AN XML FILE DONE
}
