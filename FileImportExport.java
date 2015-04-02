import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;

import vtk.*;

import javax.swing.*;

public class FileImportExport implements ActionListener {
	JMenuItem importButton;
	JMenuItem exportButton;
	JFileChooser fc;
	ExtendedGraph graph;
	File selectedFile;
	JPanel contentPane;
	AppTesting2 appTest;
	
	public FileImportExport(JPanel contentPane,JMenuItem importButton, JMenuItem exportButton,ExtendedGraph graph,AppTesting2 appTest)
	{
		this.importButton=importButton;
		this.exportButton=exportButton;
		this.contentPane=contentPane;
		fc= new JFileChooser();
		if(graph!=null)
		{
			this.graph=graph;
		}
		
		this.appTest=appTest;
	}
	
	public void UpdateGraph(ExtendedGraph graph)
	{
		this.graph=graph;
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==importButton)
		{
			int returnVal= fc.showOpenDialog(contentPane);
			
			if(returnVal==JFileChooser.APPROVE_OPTION)
			{
				selectedFile=fc.getSelectedFile();
				try{
					this.ImportGraph(selectedFile, graph);
				}
				catch(Exception exc){
					System.out.println("Import not working" + exc.getMessage());
				}
			}
		}
		
		if(e.getSource() ==exportButton)
		{
			int returnVal= fc.showSaveDialog(contentPane);
			
			if(returnVal==JFileChooser.APPROVE_OPTION)
			{
				selectedFile=fc.getSelectedFile();
				try{
					this.ExportGraph(selectedFile, graph);
				}
				catch(Exception exc){
					System.out.println("IT DIDNT WORK!" + exc.getMessage());
				}
			}
		}
	}
	
	public void ExportGraph(File file,ExtendedGraph graph) throws Exception
	{
		String fileP = file.getAbsolutePath();
		PrintWriter writer = new PrintWriter(new FileWriter(fileP));
		
		writer.println("Gene1\tGene2\tEdge Weight\tUser Edge Weight\tAuthor\tSystem Type\tSystem\tPubMedID");
		
		vtkEdgeListIterator iterator = new vtkEdgeListIterator();
		
		
		vtkIntArray weights=graph.getEdgeWeights();
		vtkStringArray genes=graph.getLabels();
		ArrayList<ArrayList> authors = graph.getAuthor();
		ArrayList<ArrayList> pubMedID = graph.getPubMedID();
		ArrayList<ArrayList> system = graph.getSystem();
		ArrayList<ArrayList> systemType = graph.getSystemType();
		
		graph.getGraph().GetEdges(iterator);
		vtkGraphEdge edge;
		
		while(iterator.HasNext())
		{
			edge=iterator.NextGraphEdge();
			
			writer.println(genes.GetValue(edge.GetTarget())+ "\t" + genes.GetValue(edge.GetSource()) + "\t" 
			+ weights.GetValue(edge.GetId())+"\t-\t-\t-\t-\t-\t ");
			for(int i=0;i<weights.GetValue(edge.GetId());i++)
			{
				//formatted string
				writer.println("-\t-\t-\t-\t" + authors.get(edge.GetId()).get(i) +"\t"+
						systemType.get(edge.GetId()).get(i) + "\t" + system.get(edge.GetId()).get(i)
						+"\t"+pubMedID.get(edge.GetId()).get(i).toString()+ "\t ");
			}
		}
		writer.close();
	}
	
	public void ImportGraph(File file, ExtendedGraph graph) throws Exception
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		//String s=reader.readLine();
		CreateGraph createGraph= new CreateGraph(reader);
		graph=createGraph.ImportMutableGraph();
		appTest.InitializeRenderer(graph);
		//System.out.println(s);
	}
}

