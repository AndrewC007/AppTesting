import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.MouseInfo;

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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;






import vtk.*;

public class AppTesting extends JFrame implements ActionListener,MouseListener{

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JPanel panel; //so initiliaze can add renderer after gui has been loaded
	private JPanel panel_2; //used for grey background
	private int degreeCount=0;
	
	 vtkGraphLayoutView view;
	 vtkRenderWindowPanel renderer = new vtkRenderWindowPanel();
	 vtkRenderedGraphRepresentation rep = new vtkRenderedGraphRepresentation();
     vtkGraphLayout graphLayout = new vtkGraphLayout();
     ExtendedGraph graph = new ExtendedGraph();
     ExtendedGraph extractedGraph = new ExtendedGraph();
     vtkAnnotationLink link = new vtkAnnotationLink();
     vtkDataRepresentation dataRep = new vtkDataRepresentation();
     vtkIdTypeArray vertices;
     int verticesNode; //Remembers which node the vertices are in
     boolean original=true;
     String organism;
     JEditorPane editorPane = new JEditorPane();
     Popup popup;
     FileImportExport fileExchanger; //Used for importing and exporting files
     
     int xCoord;
     int yCoord;
     
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		AppTesting test = new AppTesting();
		test.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	//Initialize Renderer once the graph data has been loaded
	public void InitializeRenderer()
	{
		CreateGraph graphCreator = new CreateGraph(organism);
		this.graph=graphCreator.createMutableGraph();
        view=new vtkGraphLayoutView();
        this.view.SetRenderWindow(this.renderer.GetRenderWindow());
    //  this.view.ColorVerticesOn();
   //   this.view.DisplayHoverTextOn();
        this.view.SetLayoutStrategyToCircular();
        this.view.SetVertexLabelVisibility(true);
        this.view.SetVertexLabelArrayName("labels");
        this.view.AddRepresentationFromInput(this.graph.getGraph());
        this.view.SetEnableVerticesByArray(Boolean.TRUE);
        this.view.ResetCamera();
        
  
        this.link= this.view.GetRepresentation(0).GetAnnotationLink();
        GraphObserver obs = new GraphObserver();
        this.link.AddObserver("AnnotationChangedEvent", obs, "selectionCallback");
        
        panel.remove(panel_2);
        
    	JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 3;
		gbc_panel_2.gridheight = 5;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel.add(renderer, gbc_panel_2);
	}
	
	
	public AppTesting() {
        setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnImportGraph = new JMenu("Import Graph");
		mnFile.add(mnImportGraph);
		
		JMenuItem mntmExportGraph = new JMenuItem("Export Graph");
		mnFile.add(mntmExportGraph);
		
		
		JMenuItem mntmImportCustomGraph = new JMenuItem("Import Custom Graph");
		fileExchanger=new FileImportExport(contentPane, mntmImportCustomGraph,mntmExportGraph);
		mntmImportCustomGraph.addActionListener(fileExchanger);
		mnImportGraph.add(mntmImportCustomGraph);
		
		//Add action listener to Import 
		JMenuItem mntmImportBiogridInteractions = new JMenuItem("Import Default BioGRID Graph");
		mntmImportBiogridInteractions.addActionListener(this);
			
		mnImportGraph.add(mntmImportBiogridInteractions);
		
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);
		
		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		tabbedPane.addTab("Graph View", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{780, 84, 164, 168, 0};
		gbl_panel.rowHeights = new int[]{36, 0, 0, 34, 446, 37, 29, 0};
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
		panel.add(panel_2, gbc_panel_2);
		
		JLabel lblNewLabel = new JLabel("Graph Layout Style:");
		lblNewLabel.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 3;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		JButton btnCircular = new JButton("Circular");
		btnCircular.setMinimumSize(new Dimension(83, 23));
		GridBagConstraints gbc_btnCircular = new GridBagConstraints();
		gbc_btnCircular.insets = new Insets(0, 0, 5, 0);
		gbc_btnCircular.gridx = 3;
		gbc_btnCircular.gridy = 1;
		panel.add(btnCircular, gbc_btnCircular);
		
		JButton btnSimpled = new JButton("Simple 2-D");
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
		panel_4.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 3;
		gbc_panel_4.gridy = 4;
		panel.add(panel_4, gbc_panel_4);
		
		JButton btnFindShortestPath = new JButton("Find Shortest Path");
		
		JButton btnLoadUnfilteredGraph = new JButton("Load Unfiltered Graph");
		
		JButton btnFilterSelection = new JButton("Filter Selection");
		
		JButton btnClearSelection = new JButton("Clear Selection");
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap(21, Short.MAX_VALUE)
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(btnClearSelection)
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
					.addComponent(btnClearSelection)
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
		panel_3.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 6;
		panel.add(panel_3, gbc_panel_3);
		
		JButton button = new JButton("+");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				degreeCount++;
				textField.setText(degreeCount + "");
			}
		});
		
		JButton button_1 = new JButton("-");
		button_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(degreeCount!=0){
					degreeCount--;
					textField.setText(degreeCount + "");
				}
			}
		});
		
		textField = new JTextField();
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
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setMinimumSize(new Dimension(83, 27));
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.anchor = GridBagConstraints.NORTH;
		gbc_btnSearch.weighty = 1.0;
		gbc_btnSearch.gridx = 3;
		gbc_btnSearch.gridy = 6;
		panel.add(btnSearch, gbc_btnSearch);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Edge Information", null, panel_1, null);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Node Information", null, scrollPane, null);
		
		
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                	
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
            		System.out.println("Location: " +MouseInfo.getPointerInfo().getLocation().getX() + " " + MouseInfo.getPointerInfo().getLocation().getY());
            		System.out.println("Screen Size: " + dimension.getWidth() + " " + dimension.getHeight());
            		System.out.println("Preferred Size: " + textArea.getPreferredSize().getWidth() + " " + textArea.getPreferredSize().getHeight());
            		System.out.println("Calculated Location: " + xCoord + " " + yCoord);
            		popup.show();
                }
            }
        });
		editorPane.addMouseListener(this);
		scrollPane.setViewportView(editorPane);
	}

	
	private class GraphObserver{
		void OriginalGraph(){
			original=true;
			extractedGraph=null;
			view.RemoveAllRepresentations();
			view.AddRepresentationFromInput(graph.getGraph());
			dataRep = view.GetRepresentation(0);
			
			link.RemoveAllObservers();
			link = dataRep.GetAnnotationLink();
			link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
		    view.SetVertexLabelVisibility(true);
		    
		    view.SetVertexLabelArrayName("labels");
			view.SetLayoutStrategyToSimple2D();
			view.ResetCamera();
			renderer.Render();
		}
		
		
		void Exctraction(){
			GraphInteract graphInteractor = new GraphInteract();
			if(vertices.GetNumberOfTuples()>1 && original==true)
			{
				original=false;
//				rep = view.GetRepresentation(0);
//				
//				link = rep.GetAnnotationLink();
//				link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
//               
				extractedGraph.setGraph(graphInteractor.extract(vertices,graph.getGraph()));
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
			}
			else if(vertices.GetNumberOfTuples()>1  && original==false)
			{
				original=false;
//				rep = view.GetRepresentation(0);
//				
//				link = rep.GetAnnotationLink();
//				link.AddObserver("AnnotationChangedEvent", this,  "selectionCallback");
//				
				extractedGraph.setGraph(graphInteractor.extract(vertices,extractedGraph.getGraph()));
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
			}
		}
		
		void selectionCallback(){
			vtkSelection sel = link.GetCurrentSelection();
			vtkSelectionNode node1 = sel.GetNode(1);
			vtkSelectionNode node0 = sel.GetNode(0);
			int node1_field_type = node1.GetFieldType();
			int node0_field_type = node0.GetFieldType();
			
			if(node1_field_type==3)
			{
				verticesNode=1;
			}
			else if(node0_field_type==3)
			{
				verticesNode=0;
			}
			
			
			
			vertices=(vtkIdTypeArray)(link.GetCurrentSelection().GetNode(verticesNode).GetSelectionList());
			DatabaseConnector connect = new DatabaseConnector();
			if(vertices.GetNumberOfTuples() ==1  && original==true)
			{
				connect.getGeneInfo(graph.getGraph(), vertices.GetValue(0),organism,editorPane);
			}
//			if (vertices.GetNumberOfTuples() > 0) {
//				if(vertices.GetNumberOfTuples() ==1  && original==true)
//				{
//					connector.getGeneInfo(graph, vertices.GetValue(0),organismSelected);
//				}
//				else if(vertices.GetNumberOfTuples() ==1 && original==false)
//				{
//					connector.getGeneInfo(extractedGraph, vertices.GetValue(0),organismSelected);
//				}
//			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
			DatabaseConnector connect = new DatabaseConnector();
			String[] organismList= connect.getOrganismList();
			organism = (String)JOptionPane.showInputDialog(
	                contentPane,
	                "Pick an Organism:",
	                "Organism Selection",
	                JOptionPane.PLAIN_MESSAGE,null,
	                organismList,organismList[0]);
					this.InitializeRenderer();
					this.renderer.Render();
					this.view.GetInteractor().Start();
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
