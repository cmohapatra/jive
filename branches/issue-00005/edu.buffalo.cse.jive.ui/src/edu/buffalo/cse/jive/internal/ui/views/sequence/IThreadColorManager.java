package edu.buffalo.cse.jive.internal.ui.views.sequence;

import org.eclipse.swt.graphics.Color;

import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;

public interface IThreadColorManager {

	public void addThreadColorListener(IThreadColorListener listener);
	
	public void removeThreadColorListener(IThreadColorListener listener);
	
	public Color threadColor(IJiveDebugTarget target, ThreadID thread);
}
