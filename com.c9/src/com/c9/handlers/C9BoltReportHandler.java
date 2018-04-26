package com.c9.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.c9.common.CommonFunctions;
import com.c9.dialogs.BoltReportQueryDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PlatformHelper;

public class C9BoltReportHandler extends AbstractHandler {

	TCComponent[] getBolts(String name) {
		TCSession session = CommonFunctions.getTCSession();
		return CommonFunctions.searchComponents(session, name, "C9bolt");
	}
	
	void generateReport(String reportPath, TCComponent[] bolts) {
		File reportFile = new File(reportPath + "\\bolt.xlsx");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(reportFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Bolts");
		int ii=0;
		for(TCComponent c : bolts) {
			TCComponentItem bolt = (TCComponentItem)c;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				XSSFRow row = sheet.createRow(ii);
				XSSFCell cell = row.createCell(0);
				String objectName = c.getStringProperty("object_name");
				System.out.println("objectName = " + objectName);
				cell.setCellValue(objectName);
				
				cell = row.createCell(1);
				double diameter = bolt.getLatestItemRevision().getDoubleProperty("c9diameter");
				cell.setCellValue(diameter);
				
				TCComponentUser owner = (TCComponentUser)c.getReferenceProperty("owning_user");
				//String ownerName = owner.getStringProperty("...");
				cell = row.createCell(2);
				String ownerName = owner.getUserId();
				System.out.println("ownerName = " + ownerName);
				cell.setCellValue(ownerName);
				++ii;
				//Thread.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			workbook.write(fout);
			fout.close();
	        MessageBox.post("Done", "Success", MessageBox.INFORMATION);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		BoltReportQueryDialog dialog = new BoltReportQueryDialog(PlatformHelper.getCurrentShell());
		dialog.create();
		if(dialog.open() == Window.OK) {
			final String objectName = dialog.getObjectName();
			System.out.println(objectName);
			DirectoryDialog directoryDialog = 
					new DirectoryDialog(PlatformHelper.getCurrentShell());
			final String reportPath = directoryDialog.open();
			Job job = new Job("Waiting...") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					TCComponent[] bolts = getBolts(objectName);
					if(bolts != null && bolts.length > 0) {
						generateReport(reportPath, bolts);
					}
					System.out.println(bolts.length);
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

}
