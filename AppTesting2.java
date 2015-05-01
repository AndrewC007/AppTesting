import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Desktop;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicTreeUI.MouseHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.Frame;

import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JToolBar;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import vtk.*;

public class AppTesting2 extends JFrame implements ActionListener,MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//GUI Declaration
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JPanel panel; //so initiliaze can add renderer after gui has been loaded
	private JPanel panel_2; //used for grey background
	private int degreeCount=0;
	private JMenuItem mntmImportCustomGraph;
	private JMenuItem mntmExportGraph;
	
	
	//For Find Shortest Path
	int vertex_ID=0;
	int vertex_ID2=0;
	
	//Image Icons
	ImageIcon circ = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Circle.png");
	ImageIcon simple = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Force.png");
	ImageIcon filt = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Filter.png");
	ImageIcon orig = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Original.png");
	ImageIcon path = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Path.png");
	ImageIcon search = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Search.png");
	ImageIcon inc = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Increase.png");
	ImageIcon dec = new ImageIcon("C:\\Users\\Andrew07\\workspace\\GeneMachineFinal\\src\\Decrease.png");
	
	//Graph Implementation
	 vtkGraphLayoutView view;
	 vtkRenderWindowPanel renderer = new vtkRenderWindowPanel();
	 vtkRenderedGraphRepresentation rep = new vtkRenderedGraphRepresentation();
     vtkGraphLayout graphLayout = new vtkGraphLayout();
     ExtendedGraph graph = new ExtendedGraph();
     ExtendedGraph origGraph = new ExtendedGraph();
     ExtendedGraph extractedGraph = new ExtendedGraph();
     vtkAnnotationLink link = new vtkAnnotationLink();
     vtkDataRepresentation dataRep = new vtkDataRepresentation();
     vtkIdTypeArray vertices;
     vtkIdTypeArray edges;
     static int numberOfEdges; // Used because graph path gives more edges than required and causes an exception
     int verticesNode; //Remembers which node the vertices are in
     int edgesNode; //Remeber which node the edges are in
     boolean original=true;
     String organism;
     JEditorPane editorPane = new JEditorPane();
     Popup popup;
     FileImportExport fileExchanger; //Used for importing and exporting files
     JMenuItem mntmImportBiogridInteractions; //menu for default biogrid graph creation
     JMenuItem importGeneGraph;
     JTabbedPane tabbedPane;
 
     
     int xCoord;
     int yCoord;
     
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		AppTesting2 test = new AppTesting2();
		test.setVisible(true);
	}

	//Used due to pathways bugs
	public static void updateVertexNumber(int numberOfVertices)
	{
		numberOfEdges = numberOfVertices-1;
	}
	/**
	 * Create the frame.
	 */
	//Initialize Renderer once the graph data has been loaded
	public void InitializeRenderer(ExtendedGraph graphTemp)
	{
		System.out.println("Rendering");
		this.origGraph=graphTemp;
	
//		this.extractedGraph =graph;
//		this.origGraph = graphTemp;
		
		
		
		//Theme
	    vtkViewTheme t = new vtkViewTheme();
	    vtkViewTheme theme = t.CreateNeonTheme();
	    theme.SetBackgroundColor(0, 0, 0);
	    theme.SetBackgroundColor2(0, 0, 0);
		
        this.view=new vtkGraphLayoutView();
        this.view.SetRenderWindow(this.renderer.GetRenderWindow());
        this.view.SetLayoutStrategyToSimple2D();
        this.view.SetVertexLabelVisibility(true);
        this.view.SetVertexLabelArrayName("labels");
        this.view.AddRepresentationFromInput(this.origGraph.getGraph());
        this.view.SetEnableVerticesByArray(Boolean.TRUE);
        this.view.ApplyViewTheme(theme);
        this.view.ResetCamera();
        
  
        this.link= this.view.GetRepresentation(0).GetAnnotationLink();
        GraphObserver obs = new GraphObserver();
        this.link.AddObserver("AnnotationChangedEvent", obs, "selectionCallback");
        
        if(renderer.getParent()==null)
        	panel.remove(panel_2);
        
  
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 3;
		gbc_panel_2.gridheight = 5;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		
		if(renderer.getParent()!=null)
		{
			tabbedPane.setSelectedIndex(1);
			tabbedPane.setSelectedIndex(0);
			
			//Import Export Action Listener
			fileExchanger.UpdateGraph(origGraph);
		}
		else
		{
			panel.add(renderer, gbc_panel_2);
			tabbedPane.setSelectedIndex(1);
			tabbedPane.setSelectedIndex(0);
			
			//Import Export Action Listener
			fileExchanger.UpdateGraph(origGraph);
		}
		
		System.gc();
		//Used to fix dropdown menu showing behind the render window
//		tabbedPane.setSelectedIndex(1);
//		tabbedPane.setSelectedIndex(0);
//		
//		//Import Export Action Listener
//		fileExchanger.UpdateGraph(origGraph);
	}
	
	
	public AppTesting2() {
		//Theme
	    vtkViewTheme t = new vtkViewTheme();
	    final vtkViewTheme theme = t.CreateNeonTheme();
	    theme.SetBackgroundColor(0, 0, 0);
	    theme.SetBackgroundColor2(0, 0, 0);
	    
        setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.addMouseListener(this);
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		
		JMenu mnImportGraph = new JMenu("Import Graph");
		mnFile.add(mnImportGraph);
		
		mntmExportGraph = new JMenuItem("Export Graph");
		mnFile.add(mntmExportGraph);
		
		
		mntmImportCustomGraph = new JMenuItem("Import Custom Graph");
		mnImportGraph.add(mntmImportCustomGraph);
		
		//Import Export Action Listener 
		fileExchanger=new FileImportExport(contentPane, mntmImportCustomGraph,mntmExportGraph,origGraph,this);
		mntmImportCustomGraph.addActionListener(fileExchanger);
		mntmExportGraph.addActionListener(fileExchanger);
		
		//Add action listener to Import 
		mntmImportBiogridInteractions = new JMenuItem("Import Default BioGRID Graph");
		mntmImportBiogridInteractions.addActionListener(this);
		
		importGeneGraph = new JMenuItem("Import Gene Based Graph");
		importGeneGraph.addActionListener(this);
			
		
		
		
		mnImportGraph.add(mntmImportBiogridInteractions);
		mnImportGraph.add(importGeneGraph);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addMouseListener(this);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);
		
		panel = new JPanel();
		panel.setBackground(Color.BLACK);
		tabbedPane.addTab("Graph View", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{780, 84, 164, 168, 0};
		//5th Value
		gbl_panel.rowHeights = new int[]{36, 0, 0, 34, 5, 37, 29, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 3;
		gbc_panel_2.gridheight = 5;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		
		
		JLabel lblNewLabel = new JLabel("Graph Layout Style:");
		lblNewLabel.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 3;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		//Circular View Button
		guiButton btnCircular = new guiButton(circ);
		btnCircular.setMinimumSize(new Dimension(120, 120));
		btnCircular.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				view.SetLayoutStrategyToCircular();
				view.ResetCamera();
				renderer.Render();
				
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
		});
		GridBagConstraints gbc_btnCircular = new GridBagConstraints();
		gbc_btnCircular.insets = new Insets(0, 0, 5, 0);
		gbc_btnCircular.gridx = 3;
		gbc_btnCircular.gridy = 1;
		panel.add(btnCircular, gbc_btnCircular);
		
		//Simple2D Button
		guiButton btnSimpled = new guiButton(simple);
		btnCircular.setMinimumSize(new Dimension(120, 120));
		btnSimpled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.SetLayoutStrategyToSimple2D();
				view.ResetCamera();
				renderer.Render();
				
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
		});
		GridBagConstraints gbc_btnSimpled = new GridBagConstraints();
		gbc_btnSimpled.insets = new Insets(0, 0, 5, 0);
		gbc_btnSimpled.gridx = 3;
		gbc_btnSimpled.gridy = 2;
		panel.add(btnSimpled, gbc_btnSimpled);
		
		JLabel lblGraphInteractions = new JLabel("Graph Interactions:");
		lblGraphInteractions.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 11));
		lblGraphInteractions.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblGraphInteractions = new GridBagConstraints();
		gbc_lblGraphInteractions.insets = new Insets(0, 0, 5, 0);
		gbc_lblGraphInteractions.gridx = 3;
		gbc_lblGraphInteractions.gridy = 3;
		panel.add(lblGraphInteractions, gbc_lblGraphInteractions);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.BLACK);
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 3;
		gbc_panel_4.gridy = 4;
		panel.add(panel_4, gbc_panel_4);
		
		
		//SHORTEST PATH!!
		guiButton btnFindShortestPath = new guiButton(path);
		btnFindShortestPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				System.out.println("Vertices");
				for(int i=0;i<vertices.GetNumberOfTuples();i++)
					System.out.println(vertices.GetValue(i));
				if(vertices.GetNumberOfTuples()==2){
					
					GraphPath gpath=new GraphPath();
					System.out.println("Vertices: " + vertices.GetValue(0) + " " + vertices.GetValue(1));
					//Used to be extended graph
					
					vtkIdTypeArray vertex_path;
					if(original==true)
						vertex_path=gpath.FindPath(vertices.GetValue(0), vertices.GetValue(1), origGraph.getGraph());
					else
						vertex_path=gpath.FindPath(vertices.GetValue(0), vertices.GetValue(1), extractedGraph.getGraph());
					
					//Find the Edges Between The Nodes
					//Used to be extended graph
					
					vtkIdTypeArray edgearray;
					if(original==true)
						edgearray=gpath.FindEdges(vertex_path, origGraph.getGraph());
					else
						edgearray=gpath.FindEdges(vertex_path, extractedGraph.getGraph());
									
					//Get The Selection Object				
					vtkSelection sel=new vtkSelection();
					
				//	vertex_path=AppTesting2.RemoveNegativeVertices(vertex_path);
				//	edgearray=AppTesting2.RemoveNegativeEdges(edgearray);
					
					sel=gpath.GetSelection(vertex_path, edgearray);
					
				
				    link.SetCurrentSelection(sel);
					link.Update();
					view.ZoomToSelection();
					view.ApplyViewTheme(theme);
					view.Render();
					
					//Used to fix dropdown menu showing behind the render window
					tabbedPane.setSelectedIndex(1);
					tabbedPane.setSelectedIndex(0);
					
					
					//NEED TO DO FOLLOWING CODE FOR GRAPH EXTRACTION PURPOSES
					
					vertices=vertex_path;
					edges=edgearray;
					vertices=AppTesting2.RemoveNegativeVertices(vertices);
					edges=AppTesting2.RemoveNegativeEdges(edges);
				}
			};
		});
		
		guiButton btnLoadUnfilteredGraph = new guiButton(orig);
		btnLoadUnfilteredGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphObserver obs = new GraphObserver();
				obs.OriginalGraph();
				view.ApplyViewTheme(theme);
				view.ResetCamera();
				renderer.Render();
				
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
		});
		
		guiButton btnFilterSelection = new guiButton(filt);
		btnFilterSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphObserver obs = new GraphObserver();
				obs.Exctraction();
				view.ApplyViewTheme(theme);
				view.ResetCamera();
				renderer.Render();
				
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
				
			}
		});
		
	//	JButton btnClearSelection = new JButton("Clear Selection");
		
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap(21, Short.MAX_VALUE)
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
					//	.addComponent(btnClearSelection)
						.addComponent(btnFindShortestPath)
						.addComponent(btnFilterSelection)
						.addComponent(btnLoadUnfilteredGraph))
					.addContainerGap())
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addComponent(btnFilterSelection)
					.addGap(10)
					.addComponent(btnLoadUnfilteredGraph)
					.addGap(32)
					.addComponent(btnFindShortestPath)
					.addGap(32)
			//		.addComponent(btnClearSelection)
					.addContainerGap(275, Short.MAX_VALUE))
		);
		panel_4.setLayout(gl_panel_4);
		
		JLabel lblSelectDegreesOf = new JLabel("Select Degrees Of Interaction");
		lblSelectDegreesOf.setForeground(Color.WHITE);
		lblSelectDegreesOf.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 11));
		GridBagConstraints gbc_lblSelectDegreesOf = new GridBagConstraints();
		gbc_lblSelectDegreesOf.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDegreesOf.gridx = 1;
		gbc_lblSelectDegreesOf.gridy = 5;
		panel.add(lblSelectDegreesOf, gbc_lblSelectDegreesOf);
		
		JLabel lblGeneName = new JLabel("Gene Name:");
		lblGeneName.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 11));
		lblGeneName.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblGeneName = new GridBagConstraints();
		gbc_lblGeneName.insets = new Insets(0, 0, 5, 5);
		gbc_lblGeneName.gridx = 2;
		gbc_lblGeneName.gridy = 5;
		panel.add(lblGeneName, gbc_lblGeneName);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.BLACK);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 6;
		panel.add(panel_3, gbc_panel_3);
		
		guiButton button = new guiButton(inc);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				degreeCount++;
				textField.setText(degreeCount + "");
			}
		});
		
		guiButton button_1 = new guiButton(dec);
		button_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(degreeCount!=0){
					degreeCount--;
					textField.setText(degreeCount + "");
				}
			}
		});
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setAlignmentX(Component.RIGHT_ALIGNMENT);
		textField.setColumns(10);
		textField.setText(degreeCount + "");
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGap(40)
					.addComponent(button)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button_1)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
					.addGap(29))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(button_1)
						.addComponent(button)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
					.addContainerGap())
		);
		panel_3.setLayout(gl_panel_3);
		
		textField_1 = new JTextField();
		textField_1.setMinimumSize(new Dimension(6, 25));
		textField_1.setPreferredSize(new Dimension(10, 30));
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.anchor = GridBagConstraints.NORTH;
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 6;
		panel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		guiButton btnSearch = new guiButton(search);
		//btnSearch.setMinimumSize(new Dimension(150, 150));
		btnSearch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String textFieldGene = textField_1.getText();
				String textFieldDegrees = textField.getText();
				GraphInteract interactor = new GraphInteract();
//				GraphObserver obs = new GraphObserver();
//				obs.OriginalGraph();
				
				//PSUEDO SELECT
				if(Integer.parseInt(textFieldDegrees)==0)
				{
					if(original==true)
					{
						vtkSelection sel = new vtkSelection();
						System.out.println("PSEUDO NON-EXTRACTED");
						sel=interactor.selectNode(textFieldGene,new String("1"),origGraph.getGraph());
						link.SetCurrentSelection(sel);
						
						view.ResetCamera();
						view.ZoomToSelection();
						view.ApplyViewTheme(theme);
						
						sel = interactor.selectNode(textFieldGene,new String("0"),origGraph.getGraph());
						link.SetCurrentSelection(sel);
					}
					else 
					{
						vtkSelection sel = new vtkSelection();
						System.out.println("PSEUDO EXTRACTED");
						sel=interactor.selectNode(textFieldGene,new String("1"),extractedGraph.getGraph());
						link.SetCurrentSelection(sel);
						
						view.ResetCamera();
						view.ZoomToSelection();
						view.ApplyViewTheme(theme);
						
						sel=interactor.selectNode(textFieldGene,new String("0"),extractedGraph.getGraph());
						link.SetCurrentSelection(sel);
					}
				}
				//REGULAR SELECT
				else if(Integer.parseInt(textFieldDegrees)>0)
				{
					vtkSelection sel = new vtkSelection();
					if(original==true)
					{
						sel=interactor.selectNode(textFieldGene,textFieldDegrees,origGraph.getGraph());
						link.SetCurrentSelection(sel);
						link.Update();
						view.ResetCamera();
						view.ZoomToSelection();
						view.ApplyViewTheme(theme);
					}
					else 
					{
						sel=interactor.selectNode(textFieldGene,textFieldDegrees,extractedGraph.getGraph());
						link.SetCurrentSelection(sel);
						link.Update();
						view.ResetCamera();
						view.ZoomToSelection();
						view.ApplyViewTheme(theme);
					}
				}
				view.Render();
				
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
		});
		//83,27
		//btnSearch.setMinimumSize(new Dimension(83, 27));
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.anchor = GridBagConstraints.NORTH;
		gbc_btnSearch.weighty = 1.0;
		gbc_btnSearch.gridx = 3;
		gbc_btnSearch.gridy = 6;
		panel.add(btnSearch, gbc_btnSearch);
		
//		JPanel panel_1 = new JPanel();
//		tabbedPane.addTab("Edge Information", null, panel_1, null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(this);
		tabbedPane.addTab("Node Information", null, scrollPane, null);
		
		
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                	if(hle.getURL().toString().length() > new String("http://www.ncbi.nlm.nih.gov/pubmed/123456789123456789").length())
                	{
	                	xCoord= (int)MouseInfo.getPointerInfo().getLocation().getX();
	                	yCoord= (int)MouseInfo.getPointerInfo().getLocation().getY();
	                	
	                    JScrollPane popupPanel = new JScrollPane();
	            		JTextArea textArea = new JTextArea(20,30);
	            		textArea.setLineWrap(true);
	            		textArea.setWrapStyleWord(true);
	            		
	            		
	            		
	            		GraphInteract interactor = new GraphInteract();
	            		String infoTitle = interactor.articleParser(hle.getURL(), "Title");
	            		
	                    String info = interactor.articleParser(hle.getURL(), "Abstract");
	            		textArea.setText("Title: \n" + infoTitle + "\n\n" + "Abstract: \n" +info);
	            		
	            		popupPanel.setViewportView(textArea);
	            		textArea.setCaretPosition(0);
	            		PopupFactory popupFactory = new PopupFactory();
	            		
	            		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	            		
	            		if(xCoord>=(dimension.getWidth()-textArea.getPreferredSize().getWidth())){
	            			xCoord=xCoord - (int)textArea.getPreferredSize().getWidth();
	            		}
	            		if((yCoord+textArea.getPreferredSize().getHeight())>=dimension.getHeight()){
	            			yCoord=yCoord - (int)textArea.getPreferredSize().getHeight();
	            		}
	            		popup = popupFactory.getPopup(null, popupPanel, xCoord,yCoord);
	            		popup.show();
                	}
                	else
                	{
                		try{
	                		if(Desktop.isDesktopSupported())
	                		{
	                			Desktop.getDesktop().browse(new URI(hle.getURL().toString()));
	                		}
                		}
                		catch(Exception uriExc)
                		{
                			System.out.println(uriExc.getMessage());
                		}
                	}
                }
            }
        });
		editorPane.addMouseListener(this);
		contentPane.addMouseListener(this);
		scrollPane.setViewportView(editorPane);
	}

	
	private class GraphObserver{
		void OriginalGraph(){
			original=true;
		//	extractedGraph = null;
			view.RemoveAllRepresentations();
			view.AddRepresentationFromInput(origGraph.getGraph());
			dataRep = view.GetRepresentation(0);
			
			link.RemoveAllObservers();
			link = dataRep.GetAnnotationLink();
			link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
		    view.SetVertexLabelVisibility(true);
		    
		    view.SetVertexLabelArrayName("labels");
			view.SetLayoutStrategyToSimple2D();
			view.ResetCamera();
			renderer.Render();
			
			//Used to fix dropdown menu showing behind the render window
			tabbedPane.setSelectedIndex(1);
			tabbedPane.setSelectedIndex(0);
		}
		
		
		void Exctraction(){
			GraphInteract graphInteractor = new GraphInteract();
			if(vertices.GetNumberOfTuples()>1 && original==true)
			{
				System.out.println("Original Extract");
				original=false;
//				rep = view.GetRepresentation(0);
//				
//				link = rep.GetAnnotationLink();
//				link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
//             
				
				System.out.println("Num of Edges: "  +numberOfEdges);
				edges=AppTesting2.RemoveNegativeEdges(edges);
				
				System.out.println("Vertices");
				System.out.println("Number of Vertices: " + vertices.GetSize());
				vertices=AppTesting2.RemoveNegativeVertices(vertices);
				
				//Assign values to next object	
				extractedGraph.setGraph(graphInteractor.extract(vertices,edges,origGraph.getGraph()));
				extractedGraph.setAuthor(origGraph.getAuthor(),edges, numberOfEdges);
				extractedGraph.setPubMedID(origGraph.getPubMedID(),edges, numberOfEdges);
				extractedGraph.setSystem(origGraph.getSystem(),edges, numberOfEdges);
				extractedGraph.setSystemType(origGraph.getSystemType(),edges, numberOfEdges);
				
				view.RemoveAllRepresentations();
				view.AddRepresentationFromInput(extractedGraph.getGraph());
				
				
				link.RemoveAllObservers();
				link = view.GetRepresentation(0).GetAnnotationLink();
				link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
				
			    view.SetVertexLabelVisibility(true);
			    
			    view.SetVertexLabelArrayName("labels");
			    view.SetLayoutStrategyToSimple2D();
			    view.ResetCamera();
				renderer.Render();
				
				fileExchanger.UpdateGraph(extractedGraph);
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
			else if(vertices.GetNumberOfTuples()>1  && original==false)
			{
				System.out.println("Extracted Extract");
				original=false;
//				rep = view.GetRepresentation(0);
//				
//				link = rep.GetAnnotationLink();
//				link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
//				
				
			
				System.out.println("Num of Edges: "  +numberOfEdges);
				edges=AppTesting2.RemoveNegativeEdges(edges);
				
				System.out.println("Vertices");
				System.out.println("Number of Vertices: " + vertices.GetSize());
				vertices=AppTesting2.RemoveNegativeVertices(vertices);
				
				//Set values for object
				extractedGraph.setGraph(graphInteractor.extract(vertices,edges,extractedGraph.getGraph()));
				extractedGraph.setAuthor(extractedGraph.getAuthor(),edges, numberOfEdges);
				extractedGraph.setPubMedID(extractedGraph.getPubMedID(),edges, numberOfEdges);
				extractedGraph.setSystem(extractedGraph.getSystem(),edges, numberOfEdges);
				extractedGraph.setSystemType(extractedGraph.getSystemType(),edges, numberOfEdges);
				
				
				view.RemoveAllRepresentations();
				view.AddRepresentationFromInput(extractedGraph.getGraph());
				
				
				link.RemoveAllObservers();
				link = view.GetRepresentation(0).GetAnnotationLink();
				link.AddObserver("AnnotationChangedEvent",this,  "selectionCallback");
				
			    view.SetVertexLabelVisibility(true);
			    
			    view.SetVertexLabelArrayName("labels");
			    view.SetLayoutStrategyToSimple2D();
				view.ResetCamera();
				renderer.Render();
				
				
				fileExchanger.UpdateGraph(extractedGraph);
				//Used to fix dropdown menu showing behind the render window
				tabbedPane.setSelectedIndex(1);
				tabbedPane.setSelectedIndex(0);
			}
		}
		
		void selectionCallback(){
			System.out.println("HERE");
			vtkSelection sel = link.GetCurrentSelection();
			vtkSelectionNode node1 = sel.GetNode(1);
			vtkSelectionNode node0 = sel.GetNode(0);
			int node1_field_type=-1;
			if(node1!=null)
				node1_field_type = node1.GetFieldType();
			int node0_field_type=-1;
			if(node0!=null)
				node0_field_type = node0.GetFieldType();
			
			verticesNode=-1;
			if(node1_field_type==3)
			{
				verticesNode=1;
				if(node0_field_type!=-1)
					edgesNode=0;
				else 
					edgesNode=-1;
			}
			else if(node0_field_type==3)
			{
				verticesNode=0;
				if(node1_field_type!=-1)
					edgesNode=1;
				else
					edgesNode=-1;
			}
			
			if(verticesNode!=-1)
				vertices=(vtkIdTypeArray)(link.GetCurrentSelection().GetNode(verticesNode).GetSelectionList());
			if(edgesNode!=-1)
				edges=(vtkIdTypeArray)(link.GetCurrentSelection().GetNode(edgesNode).GetSelectionList());
			
//			System.out.println("Vertices");
//			for(int i=0;i<vertices.GetSize();i++)
//				System.out.println(vertices.GetValue(i));
			//System.out.println("Vertices: " + vertices.GetValue(0));
			if(edges!=null)
			{
				numberOfEdges=edges.GetSize();
//				for(int i=0;i<edges.GetSize();i++)
//					System.out.println(edges.GetValue(i));		
			}
			
			
			DatabaseConnector connect = new DatabaseConnector();


			if(vertices.GetNumberOfTuples() ==1  && original==true )
			{
				connect.getGeneInfo(origGraph, vertices.GetValue(0),editorPane);
			}
			else if(vertices.GetNumberOfTuples() ==1  && original==false )
			{
				connect.getGeneInfo(extractedGraph,vertices.GetValue(0),editorPane);
			}
		}
		
	}
	
	public void actionPerformed(ActionEvent e){
			
			ExtendedGraph graphTemp = new ExtendedGraph();
			DatabaseConnector connect = new DatabaseConnector();
			HashMap<String,Integer> NameToTaxID = new HashMap<String,Integer>();
			try{
				if(e.getSource()==mntmImportBiogridInteractions)
				{
					String[] organismList;
					organismList= connect.getOrganismListURLDriven(NameToTaxID);
					Arrays.sort(organismList);
					organism = (String)JOptionPane.showInputDialog(
		                contentPane,
		                "Pick an Organism:",
		                "Organism Selection",
		                JOptionPane.PLAIN_MESSAGE,null,
		                organismList,organismList[0]);
				
					int taxID= NameToTaxID.get(organism);
						
					CreateGraph graphCreator = new CreateGraph(taxID,NameToTaxID);
					graphTemp=graphCreator.createMutableGraphURL();
					this.InitializeRenderer(graphTemp);
				}
				else if(e.getSource()==importGeneGraph)
				{
					String geneName = JOptionPane.showInputDialog(
							"Please enter the gene name:");
					CreateGraph graphCreator = new CreateGraph(geneName,NameToTaxID);
					graphTemp=graphCreator.createMutableGraphURL();
					this.InitializeRenderer(graphTemp);
				}
				this.renderer.Render();
				this.view.GetInteractor().Start();
			}
			catch(Exception exc){
				System.out.println(exc.getMessage());
			}
	}

	public static vtkIdTypeArray RemoveNegativeVertices(vtkIdTypeArray vertices)
	{
		//REMOVE IRRELEVANT VERTICES
		int numberOfVertices= vertices.GetSize();
		int newNumberOfVertices=numberOfVertices;
		for(int i=0; i<numberOfVertices;i++)
		{
			if(vertices.GetValue(i)<0)
				newNumberOfVertices--;
		}
		int[] verticesTemp2 = new int[newNumberOfVertices];
		
		int verticesCount=0;
		for(int i=0; i<numberOfVertices; i++)
		{
			if(vertices.GetValue(i)>=0)
			{
				verticesTemp2[verticesCount]=vertices.GetValue(i);
				verticesCount++;
			}
		}
		
		numberOfVertices=newNumberOfVertices;
		Arrays.sort(verticesTemp2);
		
		System.out.println("Vertices");
		for(int i=0; i<numberOfVertices; i++)
		{
			vertices.InsertValue(i, verticesTemp2[i]);
			System.out.println(vertices.GetValue(i));
		}
		
		return vertices;
		//DONE REMOVING IRRELEVANT VERTICES
	}
	
	public static vtkIdTypeArray RemoveNegativeEdges(vtkIdTypeArray edges)
	{

		//REMOVE IRRELEVANT EDGES
		int newNumberOfEdges=numberOfEdges;
		for(int i=0; i<numberOfEdges;i++)
		{
			if(edges.GetValue(i)<0)
				newNumberOfEdges--;
		}
		
		int[] edgesTemp2 = new int[newNumberOfEdges];
		int edgesCount=0;
		for(int i=0; i<numberOfEdges; i++)
		{
			if(edges.GetValue(i)>=0)
			{
				edgesTemp2[edgesCount] = edges.GetValue(i);
				edgesCount++;
			}
		}
		numberOfEdges=newNumberOfEdges;
		Arrays.sort(edgesTemp2);
		
		System.out.println("Edges");
		for(int i=0; i<numberOfEdges; i++)
		{
			edges.InsertValue(i, edgesTemp2[i]);
			System.out.println(edges.GetValue(i));
		}
		return edges;
		//DONE REMOVING IRRELEVANT EDGES
	}
	public void PopupShow()
	{
		JPanel popupPanel = new JPanel();
		JTextArea textArea = new JTextArea();
		popupPanel.add(textArea);
		PopupFactory popupFactory = new PopupFactory();
        Popup popup = popupFactory.getPopup(null, popupPanel, 0, 0);
        popup.show();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(popup!=null){
			popup.hide();
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
