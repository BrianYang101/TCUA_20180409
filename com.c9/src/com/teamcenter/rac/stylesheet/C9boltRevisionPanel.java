package com.teamcenter.rac.stylesheet;

import java.awt.Color;

import javax.swing.JPanel;

import com.c9.common.CommonFunctions;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.teamcenter.rac.common.lov.view.components.LOVDisplayer;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.stylesheet.PropertyNameLabel;
import com.teamcenter.rac.stylesheet.PropertyTextField;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateOut;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class C9boltRevisionPanel extends JPanel {
	private PropertyNameLabel objectNameLabel;
	private PropertyTextField objectNameField;
	private PropertyNameLabel materialLabel;
	private PropertyLOVDisplayer materialField;

	TCComponent component;
	TCComponentType typeComponent;

	/**
	 * Create the panel.
	 */
	public C9boltRevisionPanel(TCComponent c) {
		System.out.println("C9boltRevisionPanel");
		this.component = c;
		init();
		try {
			this.objectNameLabel.load(c);
			this.objectNameField.load(c);
			this.materialLabel.load(c);
			this.materialField.load(c);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public C9boltRevisionPanel(TCComponentType t) {
		System.out.println("C9boltRevisionPanel");
		this.typeComponent = t;
		init();
		try {
			this.objectNameLabel.load(t);
			this.objectNameField.load(t);
			this.materialLabel.load(t);
			this.materialField.load(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void init() {
		System.out.println("C9boltRevisionPanel:init");
		this.setBackground(Color.WHITE);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("10dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		objectNameLabel = new PropertyNameLabel();
		add(objectNameLabel, "2, 2, right, default");
		objectNameLabel.setProperty("object_name");
		
		objectNameField = new PropertyTextField();
		add(objectNameField, "4, 2, fill, default");
		//objectNameField.setColumns(10);
		objectNameField.setProperty("object_name");
		
		materialLabel = new PropertyNameLabel();
		add(materialLabel, "2, 4, right, default");
		materialLabel.setProperty("c9material");
		
		materialField = new PropertyLOVDisplayer();
		add(materialField, "4, 4, fill, default");
		materialField.setProperty("c9material");

		
		/*
		JLabel lblNewLabel_1 = new JLabel("New label");
		add(lblNewLabel_1, "2, 4, right, default");
		
		textField_1 = new JTextField();
		add(textField_1, "4, 4, fill, default");
		textField_1.setColumns(10);
		*/
	}
	
	public boolean isRenderingModified() {
		try {
			//if(this.objectNameField.isPropertyModified(component)) {
			String oldObjectName = component.getStringProperty("object_name");
			if(!oldObjectName.equals(this.getObjectName())) {
				System.out.println("C9boltRevisionPanel:isRenderingModified:true");
				return true;
			}
			
			String oldMaterialName = component.getStringProperty("c9material");
			if(!oldMaterialName.equals(this.getMaterialName())) {
				System.out.println("C9boltRevisionPanel:isRenderingModified:true");
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("C9boltRevisionPanel:isRenderingModified:false");
		return false;
	}
	
	public String getObjectName() {
		return this.objectNameField.getText();
	}
	
	public String getMaterialName() {
		return this.materialField.getSelectedDisplayValue();
	}
	
	public TCComponent createBolt() {
		DataManagementService dmService = DataManagementService.getService(CommonFunctions.getTCSession());
		
		CreateIn itemDef = new CreateIn();
		CreateInput itemRevisionDef = new CreateInput();

		itemDef.data.boName = "C9bolt";
		itemDef.data.stringProps.put("object_name", this.getObjectName());

		itemRevisionDef.boName = "C9boltRevision";
		itemRevisionDef.stringProps.put("c9material", this.getMaterialName());
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
			TCComponentFolder newstuffFolder;
			try {
				newstuffFolder = CommonFunctions.getTCSession().getUser().getNewStuffFolder();
				newstuffFolder.add("contents", item);
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
