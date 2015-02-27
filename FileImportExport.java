import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import vtk.*;

import javax.swing.*;

public class FileImportExport implements ActionListener {
	JMenuItem importButton;
	JMenuItem exportButton;
	JFileChooser fc;
	File selectedFile;
	JPanel contentPane;
	
	public FileImportExport(JPanel contentPane,JMenuItem importButton, JMenuItem exportButton)
	{
		this.importButton=importButton;
		this.exportButton=exportButton;
		this.contentPane=contentPane;
		fc= new JFileChooser();
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==importButton)
		{
			int returnVal= fc.showOpenDialog(contentPane);
			
			if(returnVal==JFileChooser.APPROVE_OPTION)
			{
				selectedFile=fc.getSelectedFile();
			}
		}
	}
	
	public void ExportGraph(File file,ExtendedGraph graph)
	{
		
	}
}

