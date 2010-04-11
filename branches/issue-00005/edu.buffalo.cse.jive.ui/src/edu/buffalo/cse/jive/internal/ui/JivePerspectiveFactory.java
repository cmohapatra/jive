package edu.buffalo.cse.jive.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A factory for creating the JIVE perspective.  The perspective contains views
 * for the current execution state and the execution history, as well as views
 * for other common debugging tasks.
 * 
 * @author Jeffrey K Czyz
 */
public class JivePerspectiveFactory implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		// Create and populate the execution state folder
		IFolderLayout executionStateFolderLayout = layout.createFolder(
				IInternalJiveUIConstants.EXECUTION_STATE_FOLDER_ID,
				IPageLayout.RIGHT,
				(float) 0.45,
				editorArea);
		setExecutionStateFolderViews(executionStateFolderLayout);
		
		// Create and populate the execution history folder
		IFolderLayout executionHistoryFolderLayout = layout.createFolder(
				IInternalJiveUIConstants.EXECUTION_HISTORY_FOLDER_ID,
				IPageLayout.BOTTOM,
				(float) 0.50,
				IInternalJiveUIConstants.EXECUTION_STATE_FOLDER_ID);
		setExecutionHistoryFolderViews(executionHistoryFolderLayout);
		
		// Create and populate the launch folder
		IFolderLayout launchFolderLayout = layout.createFolder(
				IInternalJiveUIConstants.LAUNCH_FOLDER_ID,
				IPageLayout.TOP,
				(float) 0.30,
				editorArea);
		setLaunchFolderViews(launchFolderLayout);
		
		// Create and populate the console folder
		IFolderLayout consoleFolderLayout = layout.createFolder(
				IInternalJiveUIConstants.CONSOLE_FOLDER_ID,
				IPageLayout.BOTTOM,
				(float) 0.67,
				editorArea);
		setConsoleFolderViews(consoleFolderLayout);
		
		// Populate the "Window -> Open Perspective" and "Window -> Show View" menus
		setPerspectiveShortcuts(layout);
		setViewShortcuts(layout);
	}
	
	/**
	 * Adds views and placeholders to the execution state folder.
	 * 
	 * @param layout the layout for the execution state folder
	 */
	protected void setExecutionStateFolderViews(IFolderLayout layout) {
		layout.addView(IJiveUIConstants.CONTOUR_DIAGRAM_VIEW_ID);
		layout.addView(IJiveUIConstants.CONTOUR_MODEL_VIEW_ID);
		layout.addPlaceholder(IDebugUIConstants.ID_VARIABLE_VIEW);
		layout.addPlaceholder(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		layout.addPlaceholder(IDebugUIConstants.ID_EXPRESSION_VIEW);
		layout.addPlaceholder(IDebugUIConstants.ID_REGISTER_VIEW);
	}
	
	/**
	 * Adds views and placeholders to the execution history folder.
	 * 
	 * @param layout the layout for the execution history folder
	 */
	protected void setExecutionHistoryFolderViews(IFolderLayout layout) {
		layout.addView(IJiveUIConstants.SEQUENCE_DIAGRAM_VIEW_ID);
		layout.addView(IJiveUIConstants.SEQUENCE_MODEL_VIEW_ID);
		layout.addPlaceholder(IJiveUIConstants.EVENT_LOG_VIEW_ID);
	}
	
	/**
	 * Adds views and placeholders to the launch folder.
	 * 
	 * @param layout the layout for the launch folder
	 */
	protected void setLaunchFolderViews(IFolderLayout layout) {
		layout.addView(IDebugUIConstants.ID_DEBUG_VIEW);
		layout.addView(JavaUI.ID_PACKAGES);
	}
	
	/**
	 * Adds views and placeholders to the console folder.
	 * 
	 * @param layout the layout for the console folder
	 */
	protected void setConsoleFolderViews(IFolderLayout layout) {
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addView(IPageLayout.ID_TASK_LIST);
		layout.addView(NewSearchUI.SEARCH_VIEW_ID);
		layout.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		layout.addPlaceholder(IPageLayout.ID_PROP_SHEET);
	}
	
	/**
	 * Adds perspectives to the "Window -> Open Perspective" menu to be
	 * displayed when the JIVE perspective is the current perspective.
	 * 
	 * @param layout the layout for the JIVE perspective
	 */
	protected void setPerspectiveShortcuts(IPageLayout layout) {
		layout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
		layout.addPerspectiveShortcut(JavaUI.ID_BROWSING_PERSPECTIVE);
		layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
	}
	
	/**
	 * Adds views to the "Window -> Show View" menu to be displayed when the
	 * JIVE perspective is the current perspective.
	 * 
	 * @param layout the layout for the JIVE perspective
	 */
	protected void setViewShortcuts(IPageLayout layout) {
		layout.addShowViewShortcut(IJiveUIConstants.EVENT_LOG_VIEW_ID);
		layout.addShowViewShortcut(IJiveUIConstants.CONTOUR_DIAGRAM_VIEW_ID);
		layout.addShowViewShortcut(IJiveUIConstants.CONTOUR_MODEL_VIEW_ID);
		layout.addShowViewShortcut(IJiveUIConstants.SEQUENCE_DIAGRAM_VIEW_ID);
		layout.addShowViewShortcut(IJiveUIConstants.SEQUENCE_MODEL_VIEW_ID);
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
	}
}
