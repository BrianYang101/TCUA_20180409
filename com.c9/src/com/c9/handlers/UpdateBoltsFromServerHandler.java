package com.c9.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.c9.common.CommonFunctions;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;

public class UpdateBoltsFromServerHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {            
			TCSession session = CommonFunctions.getTCSession();
			TCUserService userService = session.getUserService();

			List<TCComponent> selectedObjects = CommonFunctions.getSelectedComponents(event);
			Object [] inputArgs = new Object[1];
			inputArgs[0] = selectedObjects.toArray(new TCComponent[selectedObjects.size()]);       
			userService.call("C9BoltLib_register_update_bolts_method", inputArgs);
		}
		catch(TCException ex) {
			MessageBox.post(ex);
		}

		return null;
	}

}
