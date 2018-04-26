package com.c9.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.c9.common.CommonFunctions;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateOut;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

public class CreateBoltsHandler extends AbstractHandler {
	
	public TCComponentItem createBolt(String objectName, double length) throws Exception
	{
		DataManagementService dmService = DataManagementService.getService(CommonFunctions.getTCSession());
		
		CreateIn itemDef = new CreateIn();
		CreateInput itemRevisionDef = new CreateInput();

		itemDef.data.boName = "C9bolt";
		itemDef.data.stringProps.put("object_name", objectName);

		itemRevisionDef.boName = "C9boltRevision";
		itemRevisionDef.doubleProps.put("c9diameter", length);
		itemDef.data.compoundCreateInput.put("revision", new CreateInput[]{ itemRevisionDef });

		TCComponentItem item = null;
		TCComponentItemRevision itemRev = null;
		TCComponentForm form = null;
		
		try {
			CreateResponse createObjResponse = dmService.createObjects(new CreateIn[]{ itemDef });
			if(!ServiceDataError(createObjResponse.serviceData)) {
				for(CreateOut out : createObjResponse.output) {
					for(TCComponent obj : out.objects) {
						if(obj instanceof TCComponentItem) {
							item = (TCComponentItem) obj;
						}
						else if(obj instanceof TCComponentItemRevision) {
							itemRev = (TCComponentItemRevision) obj;
						}
						else if(obj instanceof TCComponentForm) {
							form = (TCComponentForm) obj;
						}
					}
				}
			}
		}
		catch (ServiceException e) 
		{
			e.printStackTrace();
		}
		
		if(item != null) {
			TCComponentFolder newstuffFolder = CommonFunctions.getTCSession().getUser().getNewStuffFolder();
			// paste tccomponentitem to the folder
			newstuffFolder.add("contents", item);
		}
		
		return item;
	}

	protected boolean ServiceDataError(final ServiceData data)
	{
		if (data.sizeOfPartialErrors() > 0)	{
			for (int i = 0; i < data.sizeOfPartialErrors(); i++) {
				for (String msg : data.getPartialError(i).getMessages()) {
					System.out.println("ServiceDataError: " + msg);
				}
			}
			return true;
		}
		return false;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			createBolt("123", 123);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
