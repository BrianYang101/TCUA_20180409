package com.c9.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

public class CommonFunctions {
	public static TCSession getTCSession() {
		TCSession session = (TCSession)AIFDesktop.getActiveDesktop().getCurrentApplication().getSession();
		return session;
	}
	
	// 藉由物件名稱和資料夾型態來查詢General物件
	public static TCComponent[] searchComponents(TCSession imansession, String objName, String objType) {
		TCComponent result[] = null;
		String objName_check = objName.replace('/', '?');
		String as[] = { "Name", "Type" };

		String as1[] = { objName_check, objType };
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) imansession
					.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find("General...");
			result = imancomponentquery.execute(as, as1);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return result;
	}
	
	public static List<TCComponent> getSelectedComponents(ExecutionEvent event) {
		List<TCComponent> components = new ArrayList<TCComponent>();
		IStructuredSelection iselection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
		if(iselection.size() > 0) {
			Iterator it = iselection.iterator();
			while(it.hasNext())	{
				Object obj = it.next();
				if(obj instanceof AIFComponentContext) {
					AIFComponentContext compContext = (AIFComponentContext)obj;
					components.add((TCComponent)compContext.getComponent());
				} else if(obj instanceof TCComponent) {
					components.add((TCComponent)obj);
				}
			}
		}
		
		return components;
	}
}
