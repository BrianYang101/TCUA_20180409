package com.c9.handlers;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.c9.common.CommonFunctions;
import com.c9.dialogs.CreateC9boltDialog;
import com.teamcenter.rac.aif.AIFDesktop;

public class CreateBoltHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Frame frame = AIFDesktop.getActiveDesktop().getFrame();
		final CreateC9boltDialog dialog = new CreateC9boltDialog(frame, CommonFunctions.getTCSession(), null);
		//dialog.pack();
		dialog.setLocationRelativeTo(null);
		//dialog.setAlwaysOnTop(true);
		//dialog.setModal(true);
		dialog.setVisible(true);
		
		return null;
	}

}
