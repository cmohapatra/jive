package edu.buffalo.cse.jive.internal.ui.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.search.IJiveSearchPage;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;

/**
 * 
 * @author Jeffrey K Czyz
 * @author Dennis Patrone
 */
public class JiveSearchPageProxy extends DialogPage implements ISearchPage, ISelectionChangedListener {

	private ISearchPageContainer container;
	
	private TableViewer queryTableViewer;
	
	private List<JiveSearchPageDescriptor> descriptorList;
	
//	private List<IJiveSearchPage> queryPageList;
	
	private Composite searchPageArea;
	
	private StackLayout searchPageAreaLayout;
	
	private IJiveSearchPage currentSearchPage;

	public JiveSearchPageProxy() {
		// TODO Auto-generated constructor stub
	}

	public JiveSearchPageProxy(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public JiveSearchPageProxy(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new RowLayout());
		setControl(control);
		
		queryTableViewer = new TableViewer(control, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		queryTableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return descriptorList.toArray();
			}

			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		queryTableViewer.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				JiveSearchPageDescriptor descriptor = (JiveSearchPageDescriptor) element;
				return descriptor.getSearchQueryName();
			}
			
			@Override
			public Image getImage(Object element) {
				JiveSearchPageDescriptor descriptor = (JiveSearchPageDescriptor) element;
				return descriptor.getSearchQueryIcon();
			}
		});
		
		queryTableViewer.addPostSelectionChangedListener(this);
		
		searchPageArea = new Composite(control, SWT.NONE);
		searchPageAreaLayout = new StackLayout();
		searchPageArea.setLayout(searchPageAreaLayout);
		
		initializeSearchPages();
	}

	private void initializeSearchPages() {
		descriptorList = JiveUIPlugin.getDefault().getSearchPageDescriptors();
		queryTableViewer.setInput(descriptorList);
		
		for (JiveSearchPageDescriptor descriptor : descriptorList) {
			try {
				IJiveSearchPage page = descriptor.getSearchPage();
				page.createControl(searchPageArea);
				page.setContainer(getContainer());
			}
			catch (CoreException e) {
				JiveUIPlugin.log(e.getStatus());
			}
			catch (Exception e) {
				JiveUIPlugin.log(e);
			}
		}
		
//		queryPageList = new LinkedList<IJiveSearchPage>();
//		queryPageList.add(new VariableChangedQueryPage());
//		queryPageList.add(new LineExecutedQueryPage());
//		queryPageList.add(new MethodCalledQueryPage());
//		queryPageList.add(new MethodReturnedQueryPage());
//		queryPageList.add(new ObjectCreatedQueryPage());
//		queryPageList.add(new InvariantViolatedQueryPage());
//		
//		for (IJiveSearchPage queryPage : queryPageList) {
//			queryPage.createControl(queryPageArea);
//			queryPage.setContainer(getContainer());
//		}
//
//		queryTableViewer.setInput(queryPageList);
	}
	
	public void dispose() {
		queryTableViewer.removePostSelectionChangedListener(this);
		super.dispose();
	}
	
	public boolean performAction() {
		if (currentSearchPage.isInputValid()) {
			IJiveSearchQuery query = currentSearchPage.createSearchQuery();
			NewSearchUI.runQueryInBackground(query);
			return true;
		}
		else {
			return false;
		}
	}

	public void setContainer(ISearchPageContainer container) {
		this.container = container;
	}

	private ISearchPageContainer getContainer() {
		return container;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!descriptorList.isEmpty()) {
			JiveSearchPageDescriptor descriptor = descriptorList.get(0);
			changeToPageFor(descriptor);
			queryTableViewer.getTable().select(0);
		}	
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if (!event.getSelection().isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			JiveSearchPageDescriptor descriptor = (JiveSearchPageDescriptor) selection.getFirstElement();
			changeToPageFor(descriptor);
		}
	}
	
	private void changeToPageFor(JiveSearchPageDescriptor descriptor) {
		try {
			IJiveSearchPage searchPage = descriptor.getSearchPage();
			currentSearchPage = searchPage;
			searchPageAreaLayout.topControl = searchPage.getControl();
			searchPageArea.layout();
			
			searchPage.initializeInput(getContainer().getSelection());
			getContainer().setPerformActionEnabled(searchPage.isInputValid());
		}
		catch (CoreException e) {
			JiveUIPlugin.log(e.getStatus());
		}
		catch (Exception e) {
			JiveUIPlugin.log(e);
		}
	}
}
