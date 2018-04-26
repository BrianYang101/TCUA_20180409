package com.teamcenter.rac.stylesheet;

import java.awt.BorderLayout;

import javax.swing.JTextField;

import com.c9.common.CommonFunctions;
import com.teamcenter.rac.common.SoaPropertyHelper;
import com.teamcenter.rac.kernel.Markpoint;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.stylesheet.AbstractRendering;

public class C9boltRevisionForm extends AbstractRendering {
	C9boltRevisionPanel formPanel;
	
	public C9boltRevisionForm(TCComponent c) throws Exception {
		super(c);
		// TODO Auto-generated constructor stub
		System.out.println("C9boltRevisionForm");
		loadRendering();
	}

	@Override
	public void loadRendering() throws TCException {
		// TODO Auto-generated method stub
		System.out.println("C9boltRevisionForm:loadRendering");
		setLayout(new BorderLayout());
		this.formPanel = new C9boltRevisionPanel(component);
		add(formPanel, BorderLayout.CENTER);
		//add(new JTextField(), BorderLayout.SOUTH);
	}

	@Override
	public void saveRendering() {
		// TODO Auto-generated method stub
		System.out.println("C9boltRevisionForm:saveRendering");
		try {
			TCProperty properties[] = component.getTCProperties(new String[] { "object_name", "c9material" });
			properties[0].setStringValueData(formPanel.getObjectName());
			properties[1].setStringValueData(formPanel.getMaterialName());
			SoaPropertyHelper.setPropertiesService(component, properties);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateRendering() {
		// TODO Auto-generated method stub
		System.out.println("C9boltRevisionForm:updateRendering");
		if(isRenderingModified()) {
			//this.formPanel.load();
		}
		super.updateRendering();
		//this.formPanel.load();
	}
	
	@Override
	public boolean isRenderingModified() {
		System.out.println("C9boltRevisionForm:isRenderingModified");
		return this.formPanel.isRenderingModified();
	}
}
