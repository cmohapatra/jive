package edu.buffalo.cse.jive.internal.ui.views.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class ThreadColorManager implements IThreadColorManager, IPropertyChangeListener, ILaunchListener {

	public static final int THREAD_COLOR_COUNT = 5;
	
	private List<IThreadColorListener> updateList;
	
	private List<Color> threadColors;
	
	private Map<IJiveDebugTarget, Map<ThreadID, Color>> targetToThreadColorsMap;
	
	public ThreadColorManager() {
		updateList = new LinkedList<IThreadColorListener>();
		threadColors = new ArrayList<Color>(THREAD_COLOR_COUNT);
		targetToThreadColorsMap = new HashMap<IJiveDebugTarget, Map<ThreadID,Color>>();
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(this);
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		for (ILaunch launch : manager.getLaunches()) {
			launchChanged(launch);
		}
		
		manager.addLaunchListener(this);
	}
	
	public void dispose() {
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.removePropertyChangeListener(this);
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		manager.removeLaunchListener(this);
		
		updateList.clear();
		targetToThreadColorsMap.clear();
		disposeThreadColors();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager#addThreadColorListener(edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorListener)
	 */
	public void addThreadColorListener(IThreadColorListener listener) {
		if (!updateList.contains(listener)) {
			updateList.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager#removeThreadColorListener(edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorListener)
	 */
	public void removeThreadColorListener(IThreadColorListener listener) {
		if (updateList.contains(listener)) {
			updateList.remove(listener);
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager#threadColor(edu.buffalo.cse.jive.core.IJiveDebugTarget, edu.bsu.cs.jive.util.ThreadID)
	 */
	public Color threadColor(IJiveDebugTarget target, ThreadID thread) {
		if (targetToThreadColorsMap.containsKey(target)) {
			if (threadColors.isEmpty()) {
				updateThreadColors();
			}
			
			Map<ThreadID, Color> threadToColorMap = targetToThreadColorsMap.get(target);
			if (!threadToColorMap.containsKey(thread)) {
				updateThreadToColorMap(target);
			}
			
			return threadToColorMap.get(thread);
		}
		else {
			throw new IllegalStateException("The supplied target no longer exists.");
		}
	}
	
	private void updateThreadToColorMap(IJiveDebugTarget target) {
		Map<ThreadID, Color> threadToColorMap = targetToThreadColorsMap.get(target);
		threadToColorMap.clear();
		
		MultiThreadedSequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			LinkedList<Color> temp = new LinkedList<Color>(threadColors);
			for (ThreadID thread : model.getThreads()) {
				if (temp.isEmpty()) {
					temp.addAll(threadColors);
				}
				
				threadToColorMap.put(thread, temp.removeFirst());
			}
		}
		finally {
			modelLock.unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.startsWith(IJiveUIConstants.PREF_THREAD_COLOR_PREFIX)) {
			disposeThreadColors();
			
			for (Map<ThreadID, Color> map : targetToThreadColorsMap.values()) {
				map.clear();
			}
			
			for (IJiveDebugTarget target : targetToThreadColorsMap.keySet()) {
				fireThreadColorsChanged(target);
			}
		}
	}
	
	private void updateThreadColors() {
		for (int i = 1; i <= THREAD_COLOR_COUNT; i++) {
			IPreferenceStore store = JiveUIPlugin.getDefault().getPreferenceStore();
			String name = IJiveUIConstants.PREF_THREAD_COLOR_PREFIX + i;
			RGB color = PreferenceConverter.getColor(store, name);
			threadColors.add(new Color(JiveUIPlugin.getStandardDisplay(), color));
		}
	}
	
	private void disposeThreadColors() {
		for (Color c : threadColors) {
			c.dispose();
		}
		
		threadColors.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
	 */
	public void launchAdded(ILaunch launch) {
		launchChanged(launch);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
	 */
	public void launchChanged(ILaunch launch) {
		IDebugTarget target = launch.getDebugTarget();
		if (target != null && target instanceof IJiveDebugTarget) {
			Map<ThreadID, Color> threadToColorMap = new HashMap<ThreadID, Color>();
			targetToThreadColorsMap.put((IJiveDebugTarget) target, threadToColorMap);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	public void launchRemoved(ILaunch launch) {
		IDebugTarget target = launch.getDebugTarget();
		if (target != null && target instanceof IJiveDebugTarget) {
			targetToThreadColorsMap.remove((IJiveDebugTarget) target);
		}
	}
	
	private void fireThreadColorsChanged(IJiveDebugTarget target) {
		for (IThreadColorListener listener : updateList) {
			listener.threadColorsChanged(target);
		}
	}

}
