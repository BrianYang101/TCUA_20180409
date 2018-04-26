package com.c9.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.c9.common.CommonFunctions;
import com.teamcenter.rac.aif.AIFClipboard;
import com.teamcenter.rac.aif.AIFPortal;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;



public class PasteReferenceHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		List<TCComponent> selectedComponents = CommonFunctions.getSelectedComponents(event);
		AIFClipboard clipboard = AIFPortal.getClipboard(); 
		Transferable transferable = clipboard.getContents(this);
		if (transferable != null) {
			List<TCComponent> pasteUsers = new ArrayList<TCComponent>();
        	try {
				Vector objs = (Vector)transferable.getTransferData(new DataFlavor(Vector.class, "AIF Vector"));
				for (Object obj : objs) {
					if(obj instanceof TCComponentDataset) {
						for(TCComponent c : selectedComponents) {
							c.add("IMAN_reference", (TCComponent) obj);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return null;
	}

}
