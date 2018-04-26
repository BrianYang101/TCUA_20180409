package com.c9.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.C9boltRevisionPanel;
import com.teamcenter.rac.util.MessageBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreateC9boltDialog extends JDialog {

	//private final JPanel contentPanel = new JPanel();
	private C9boltRevisionPanel contentPanel;
	private TCComponent parentComponent;

	/**
	 * Create the dialog.
	 */
	public CreateC9boltDialog(Frame frame, TCSession session, TCComponent parentComponent) {
		super(frame);
		this.setTitle("Create Bolt");
		this.parentComponent = parentComponent;
		//setModalityType(ModalityType.APPLICATION_MODAL);
		try {
			TCComponentType typeComponent = (TCComponentType)session.getTypeComponent("C9boltRevision");
			this.contentPanel = new C9boltRevisionPanel(typeComponent);
			
			setBounds(100, 100, 450, 300);
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Job job = new Job("Waiting...") {

								@Override
								protected IStatus run(IProgressMonitor monitor) {
									contentPanel.createBolt();
									MessageBox.post("Done", "Success", MessageBox.INFORMATION);
									return Status.OK_STATUS;
								}
							};
							job.setUser(false);
							job.schedule();

							setVisible(false);
						}
					});
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setVisible(false);
						}
					});
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
