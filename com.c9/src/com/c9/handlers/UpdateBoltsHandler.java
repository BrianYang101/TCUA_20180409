package com.c9.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.c9.common.CommonFunctions;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;

public class UpdateBoltsHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		List<TCComponent> components = CommonFunctions.getSelectedComponents(event);
		for(TCComponent c : components) {
			try {
				System.out.println(c.getStringProperty("object_name"));
				c.lock();
				//TCProperty desc = c.getTCProperty("object_desc");
				//desc.setStringValue("update by UpdateBoltRevisionHandler");
				//c.setStringProperty("object_desc", "update by UpdateBoltRevisionHandler");
				
				TCProperty props[] = new TCProperty[2];
				props[0] = c.getTCProperty("c9diameter");
				props[0].setDoubleValue(1);
				props[1] = c.getTCProperty("c9length");
				props[1].setDoubleValue(2);
				c.setTCProperties(props);

				c.save();
				c.unlock();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
