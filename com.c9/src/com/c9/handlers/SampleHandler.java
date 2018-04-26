package com.c9.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import com.c9.services.rac.lib.TrainingService;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;

import org.apache.log4j.Logger;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {


	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Logger logger = Logger.getLogger("com.teamcenter.rac");

		System.out.println("Hello Kitty");
		logger.info("Hello Kitty"); 
		logger.warn("Hello Kitty"); 
		logger.error("Hello Kitty"); 
		
		/*
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"C9",
				"Hello, Eclipse world");
		*/
		
		
		AbstractAIFApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
		TCSession session = (TCSession) app.getSession();		
		
		//TrainingService trainingService = TrainingService.getService(session);
		//trainingService.doHelloKitty();
		
		try {
			String serviceReturn = null;
			session = (TCSession) AIFDesktop.getActiveDesktop().getCurrentApplication().getSession();
			int n_input_args = 1;
			Object[] input_args = new Object[0];
			TCUserService userService = session.getUserService();
			serviceReturn = (String) userService.call("doHelloKitty", input_args);
			System.out.println("TCUserService: " + serviceReturn); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
