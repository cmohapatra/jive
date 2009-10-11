package edu.buffalo.cse.jive.internal.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.search.IJiveSearchPage;

public class JiveSearchPageDescriptor implements IPluginContribution {

	public static final String PAGE_TAG = "page";
	
	public static final String ID_ATTRIBUTE = "id";
	
	public static final String NAME_ATTRIBUTE = "name";
	
	public static final String CLASS_ATTRIBUTE = "class";
	
	public static final String ICON_ATTRIBUTE = "icon";

	private IConfigurationElement pageElement;
	
	private IJiveSearchPage searchPage;
	
	public JiveSearchPageDescriptor(IConfigurationElement element) {
		pageElement = element;
	}
	
	public String getLocalId() {
		return pageElement.getAttribute(ID_ATTRIBUTE);
	}

	public String getPluginId() {
		return pageElement.getContributor().getName();
	}
	
	public String getSearchQueryName() {
		return pageElement.getAttribute(NAME_ATTRIBUTE);
	}
	
	public Image getSearchQueryIcon() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		String key = getPluginId() + "/" + getLocalId();
		Image icon = registry.get(key);
		
		if (icon == null) {
			try {
				ImageDescriptor descriptor =
					AbstractUIPlugin.imageDescriptorFromPlugin(getPluginId(), getIconPath());
				registry.put(key, descriptor);
				icon = registry.get(key);
			}
			// TODO Catch the right exception and log accordingly.
			// Also, handle the case when no icon is given better. 
			catch (Exception e) {
//				JiveUIPlugin.log(e);
				return null;
			}
		}
		
		return icon;
	}
	
	public IJiveSearchPage getSearchPage() throws CoreException {
		if (searchPage == null) {
			searchPage = (IJiveSearchPage) pageElement.createExecutableExtension(CLASS_ATTRIBUTE);
		}
		
		return searchPage;
	}
	
	private String getIconPath() {
		return pageElement.getAttribute(ICON_ATTRIBUTE);
	}
	
}
