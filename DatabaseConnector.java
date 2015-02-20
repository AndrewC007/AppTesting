import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import vtk.*;

public class DatabaseConnector 
{	
	
	   public static final char LF = '\n';

	   public static final char CR = '\r';
	
		public String[] getOrganismList()
		{
			List<String> organisms = new ArrayList();
			Connection con;
			DatabaseMetaData m;
			ResultSet tables;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				//Information should be the same; "EDP" should be changed to password of user you are using on your system
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","EDP");
				m = con.getMetaData();
				tables = m.getTables(null, null, null,null);
				while(tables.next())
				{
					organisms.add(tables.getString(3));
				}
				
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
			
		
			
				
			String[] organismList= new String[organisms.size()];
			organisms.toArray(organismList);
			return organismList;
			
		
		}
		
		
		public void getGeneInfo(vtkMutableUndirectedGraph graph, int gene,String organism, JEditorPane editorPane)
		{
			vtkStringArray geneNames = (vtkStringArray) graph.GetVertexData().GetAbstractArray("labels");
			
		//	styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
			
			
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","EDP");
				PreparedStatement statement = con.prepareStatement("SELECT * from "+organism+" WHERE BINARY Official_Symbol_Interactor_A ='" + geneNames.GetValue(gene) +"' OR BINARY Official_Symbol_Interactor_B = '" + geneNames.GetValue(gene) +"'");
				ResultSet rs = statement.executeQuery();
				String s = "";
				s+="<pre>";
				s+="<b>";
				s += String.format("%55s",geneNames.GetValue(gene));
				s+="</b>";
				s+="</pre>";
				s += "<br>";
				s+="<pre>";
				s+="<u>";
				s += String.format("%-20s%-30s%-40s%-40s%-20s", "Gene", "Experimental System", "Experimental System Type", "Author","Link");
				s+="</u>";
				s+="</pre>";
				s += "<br>";
				while(rs.next())
				{
					if(!rs.getString("Official_Symbol_Interactor_A").equals(geneNames.GetValue(gene)))
					{
						s+="<pre>";
						s += String.format("%-20s%-30s%-40s%-40s", rs.getString("Official_Symbol_Interactor_A"), rs.getString(12), rs.getString(13),rs.getString("Author"));
						s += "<a href='" + "http://www.ebi.ac.uk/europepmc/webservices/rest/search/resulttype=core&query=ext_id:"+rs.getString(15) + "'>Link</a>";
						s+="</pre>";
					}
					else
					{
						s+="<pre>";
						s += String.format("%-20s%-30s%-40s%-40s", rs.getString("Official_Symbol_Interactor_B"), rs.getString(12), rs.getString(13), rs.getString("Author"));
						s += "<a href='" + "http://www.ebi.ac.uk/europepmc/webservices/rest/search/resulttype=core&query=ext_id:"+rs.getString(15) + "'>Link</a>";
						s+="</pre>";
					}
					s += "<br>";
				}
				
				
					try {
						HTMLEditorKit htmlKit = new HTMLEditorKit();
						editorPane.setEditorKit(htmlKit);
						StyleSheet styleSheet = htmlKit.getStyleSheet();
						styleSheet.addRule("pre { display: inline;}");
						styleSheet.addRule("a {display:inline; }");
					      Document doc = htmlKit.createDefaultDocument();
					      editorPane.setDocument(doc);
					      editorPane.setText(s);
					     
					} catch(Exception exc) {
					      exc.getMessage();
					}
				
			}
			catch(Exception e)
			{
				e.getMessage();
			}
			
			
		}
		
	
}
