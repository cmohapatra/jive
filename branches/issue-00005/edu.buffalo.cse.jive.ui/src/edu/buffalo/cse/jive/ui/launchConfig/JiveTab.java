package edu.buffalo.cse.jive.ui.launchConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.buffalo.cse.jive.core.IJiveCoreConstants;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.preferences.PreferenceInitializer;

/**
 * A configuration tab used to enable debugging with JIVE and to modify the
 * default JIVE settings for the corresponding launch.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveTab extends AbstractLaunchConfigurationTab {
	
	/**
	 * The name appearing on the tab.
	 */
	public static final String JIVE_TAB_NAME = "JIVE"; //$NON-NLS-1$
	
	/**
	 * The mode used to determine if JIVE is enabled if JIVE is enabled for the
	 * corresponding launch.
	 */
	public static final String JIVE_MODE = "jive"; //$NON-NLS-1$
	
	/**
	 * The set of modes relevant to JIVE.
	 * 
	 * @see JiveTab#getModes()
	 */
	protected Set<String> jiveModes;
	
	/**
	 * The check button used to enable JIVE.
	 */
	protected Button enableJiveButton;
	
	/**
	 * The text field used to add exclusion filters. 
	 */
	protected Text addFilterText;
	
	/**
	 * The button to add an exclusion filter to the filters list.
	 */	
	protected Button addFilterButton;
	
	/**
	 * The button to remove a filter from the filters list.
	 */
	protected Button removeFilterButton;
	
	/**
	 * The list to display the exclusion filters. 
	 */
	protected org.eclipse.swt.widgets.List filtersList; 
	
	/**
	 * Constructs the tab.
	 */
	public JiveTab() {
		jiveModes = new HashSet<String>();
		jiveModes.add(JIVE_MODE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, true));
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 1;
		c.setLayoutData(layoutData);
	    
		// Control for enabling JIVE
		enableJiveButton = new Button(c, SWT.CHECK);
		enableJiveButton.setText("Enable debugging with JIVE."); // TODO Add NLS support
		enableJiveButton.setLayoutData(new GridData());
		enableJiveButton.setSelection(false);
		enableJiveButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		Composite c1 = new Composite(c, SWT.NONE);
		c1.setLayout(new GridLayout(2, true));
		GridData layoutData1 = new GridData(GridData.FILL_BOTH);
		layoutData1.horizontalSpan = 1;
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
		// Control for entering exclusion filters
		addFilterText = new Text(c1,SWT.BORDER);
		addFilterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addFilterText.setToolTipText("Enter Exclusion Filter");
		addFilterText.setText("Enter Exclusion Filter");
		addFilterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				String data = addFilterText.getText();
				addFilterButton.setEnabled(data != null && !data.trim().isEmpty());
			}
		});
		addFilterText.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				if (addFilterText.getText().trim().equals("Enter Exclusion Filter")) {
					addFilterText.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				if (addFilterText.getText().trim().isEmpty()) {
					addFilterText.setText("Enter Exclusion Filter");
				}
			}
			
		});
		
		addFilterButton = new Button(c1, SWT.NONE);
		addFilterButton.setText("Add Filter");
		addFilterButton.setEnabled(false);
		addFilterButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				addFilter();
	    		addFilterButton.setEnabled(false);
	    		setDirty(true);
				updateLaunchConfigurationDialog();
	    	}
	    });
		
		filtersList = new org.eclipse.swt.widgets.List(c1, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		filtersList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		filtersList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				removeFilterButton.setEnabled(filtersList.getSelectionCount() > 0);
			}
		});
		
		removeFilterButton = new Button(c1, SWT.NONE);
		removeFilterButton.setText("Remove Filter");
		removeFilterButton.setEnabled(false);
	    removeFilterButton.addSelectionListener(new SelectionListener() {
	    	public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
	    		removeFilters();
	    		addFilterText.setText("Enter Exclusion Filter");
	    		// set it to false if done deleting.
	    		removeFilterButton.setEnabled(false);
	    		setDirty(true);
				updateLaunchConfigurationDialog();
	    	}
	    });
		
		// Set the controls for the tab
		setControl(c);
		
		// Mark the tab as clean
		setDirty(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return JIVE_TAB_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.get(IJiveUIConstants.ENABLED_JIVE_ICON_KEY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@SuppressWarnings("unchecked")
	public void initializeFrom(ILaunchConfiguration configuration) {
		// Initialize the controls based on the launch configuration
		try {
			// Initialize the enable JIVE checkbox
			Set<String> modes = configuration.getAttribute(
					// TODO(jkczyz):  Determine if this was made public in 3.4 or 3.5
					LaunchConfiguration.ATTR_LAUNCH_MODES,
					Collections.EMPTY_SET);
			if (modes.isEmpty()) {
				enableJiveButton.setSelection(false);
			}
			else {
				enableJiveButton.setSelection(modes.containsAll(jiveModes));
			}
			
			// Initialize the exclusion filters list
			List<String> exclusionFilters = configuration.getAttribute(
					IJiveCoreConstants.EXCLUSION_FILTERS_KEY, (List<String>) null);
			
			// Set the default filters for launch configurations created before exclusion
			// filters were supported
			if (exclusionFilters == null) {
				ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
				setDefaultFilters(workingCopy);
				exclusionFilters = workingCopy.getAttribute(
						IJiveCoreConstants.EXCLUSION_FILTERS_KEY,
						Collections.EMPTY_LIST);
				workingCopy.doSave();
			}
			
			if (filtersList.getItemCount() == 0) {
				for (String filter : exclusionFilters) {
					filtersList.add(filter);
				}
			}
		}
		catch(CoreException e) {
			JiveUIPlugin.log(e);
		}
		
		// Enable the controls only if launching in debug mode
		String mode = getLaunchConfigurationDialog().getMode();
		enableControls(mode.equals(ILaunchManager.DEBUG_MODE));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (isDirty()) {
			// Add or remove the relevant JIVE modes from the configuration
			boolean selected = enableJiveButton.getSelection();
			if (selected) {
				configuration.addModes(jiveModes);
			}
			else {
				configuration.removeModes(jiveModes);
			}
			
			// Add the exclusion filters to the configuration
			List<String> filters = new ArrayList<String>();
			filters.addAll(Arrays.asList(filtersList.getItems()));
			configuration.setAttribute(IJiveCoreConstants.EXCLUSION_FILTERS_KEY, filters);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		setDefaultFilters(configuration);
	}
	
	/**
	 * Sets the default event filters based on the launch configuration type.
	 * 
	 * @param configuration the launch configuration
	 */
	public void setDefaultFilters(ILaunchConfigurationWorkingCopy configuration) {
		try {
			String preferenceName = null;
			String launchTypeId = configuration.getType().getIdentifier();
			
			// Fill default filters based on the launch configuration type
			if (launchTypeId.equals(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION)) {
				preferenceName = IJiveUIConstants.PREF_JAVA_APPLICATION_FILTERS;
			}
			else if (launchTypeId.equals(IJavaLaunchConfigurationConstants.ID_JAVA_APPLET)) {
				preferenceName = IJiveUIConstants.PREF_JAVA_APPLET_FILTERS;
			}
			// TODO(jkczyz):  See if this was made public in 3.5
			else if (launchTypeId.equals(JUnitLaunchConfigurationConstants.ID_JUNIT_APPLICATION)) {
				preferenceName = IJiveUIConstants.PREF_JUNIT_APPLICATION_FILTERS;
			}
			else {
				JiveUIPlugin.log("Unsupported launch configuration type: " + launchTypeId);
				return;
			}
			
			Preferences pref = JiveUIPlugin.getDefault().getPluginPreferences();
			String javaPreferenceFilter = pref.getString(preferenceName);
			List<String> filterList = new ArrayList<String>();
			
			for (String filter : javaPreferenceFilter.split(PreferenceInitializer.EVENT_FILTER_DELIMITER)) {
				filter = filter.trim();
				if (!filter.isEmpty()) {
					filterList.add(filter);
				}
			}
			
			configuration.setAttribute(IJiveCoreConstants.EXCLUSION_FILTERS_KEY, filterList);
		}
		catch (CoreException e) {
			JiveUIPlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getTabId()
	 */
	public String getTabId() {
		return IJiveUIConstants.JIVE_TAB_ID;
	}
	
	/**
	 * Enables the tab's controls if the argument is <code>true</code>, and
	 * disables them otherwise.
	 * 
	 * @param enabled the new enabled state of each control
	 */
	protected void enableControls(boolean enabled) {
		enableJiveButton.setEnabled(enabled);
		addFilterText.setEnabled(enabled);
		addFilterButton.setEnabled(enabled && addFilterButton.isEnabled());
		removeFilterButton.setEnabled(enabled && removeFilterButton.isEnabled());
		filtersList.setEnabled(enabled);
	}
	
	/**
	 * Removes the selected filters from the filter list.
	 */
	private void removeFilters()	{
		int[] selectedFilters = filtersList.getSelectionIndices();
		filtersList.remove(selectedFilters);
	}
	
	/**
	 * Adds the user-provided filter to the filters list.
	 */
	private void addFilter() {
		String newFilter = addFilterText.getText().trim();
		if (!newFilter.isEmpty()) {
    		if (validateFilter(newFilter)) {
	    		for (String filter : filtersList.getItems()) {
	    			if (newFilter.equals(filter)) {
	    				return;
	    			}
	    		}
	    		
    			filtersList.add(newFilter);
    			addFilterText.setText("Enter Exclusion Filter");
    		}
    		else {
    			addFilterText.setText("Enter Exclusion Filter");
    		}
		}
		else {
			addFilterText.setText("Enter Exclusion Filter");
		}
	}
	
	/**
	 * Validates event filters entered by the user.
	 * 
	 * @param filter the event filter to validate 
	 * @return <code>true</code> if the filter is valid, otherwise <code>false</code>
	 */
	private boolean validateFilter(String filter) {	
		return !filter.contains(PreferenceInitializer.EVENT_FILTER_DELIMITER);
	}
}
