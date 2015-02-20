import vtk.*;

import java.sql.*;

public class geneInfo {

	public static void getGeneInfo(vtkMutableUndirectedGraph graph, int gene,String organism)
	{
		vtkStringArray geneNames = (vtkStringArray) graph.GetVertexData().GetAbstractArray("labels");
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/organism","root","EDP");
			PreparedStatement statement = con.prepareStatement("SELECT * from "+organism+" WHERE Official_Symbol_Interactor_A ='" + geneNames.GetValue(gene) +"' OR Official_Symbol_Interactor_B = '" + geneNames.GetValue(gene) +"'");
			ResultSet rs = statement.executeQuery();
			System.out.format("%55s",geneNames.GetValue(gene));
			System.out.format("\n");
			System.out.format("%10s%30s%35s%30s", "Gene", "Experimental System", "Experimental System Type", "Author");
			System.out.format("\n\n");
			while(rs.next())
			{
				if(!rs.getString("Official_Symbol_Interactor_A").equals(geneNames.GetValue(gene)))
					System.out.format("%10s%30s%35s%30s", rs.getString("Official_Symbol_Interactor_A"), rs.getString(12), rs.getString(13),rs.getString("Author"));
				else
					System.out.format("%10s%30s%35s%30s", rs.getString("Official_Symbol_Interactor_B"), rs.getString(12), rs.getString(13), rs.getString("Author"));
				System.out.format("\n");
			}
		}
		catch(Exception e)
		{
			e.getMessage();
		}
	}
	
}
