package edu.buffalo.cse.jive.internal.launching;

import java.io.File;
import java.net.URL;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.LibraryLocation;

/**
 * An implementation of an <code>IVMInstall</code> that delegates nearly all
 * requests to a subject <code>IVMInstall</code>.  The only exception is
 * <code>getVMRunner()</code>, which is overridden to return a specialized
 * <code>IVMRunner</code> needed to debug with JIVE enabled.
 * 
 * @see VMInstallProxy#getVMRunner(String)
 * @author Jeffrey K Czyz
 */
public class VMInstallProxy implements IVMInstall {

	/**
	 * The subject to which the proxy delegates requests.
	 */
	private IVMInstall subject;

	/**
	 * Constructs a proxy for the supplied <code>IVMInstall</code>.
	 * 
	 * @param subject the object used as a delegate
	 */
	public VMInstallProxy(IVMInstall subject) {
		this.subject = subject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getId()
	 */
	public String getId() {
		return subject.getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getInstallLocation()
	 */
	public File getInstallLocation() {
		return subject.getInstallLocation();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getJavadocLocation()
	 */
	public URL getJavadocLocation() {
		return subject.getJavadocLocation();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getLibraryLocations()
	 */
	public LibraryLocation[] getLibraryLocations() {
		return subject.getLibraryLocations();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getName()
	 */
	public String getName() {
		return subject.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getVMArguments()
	 */
	public String[] getVMArguments() {
		return subject.getVMArguments();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getVMInstallType()
	 */
	public IVMInstallType getVMInstallType() {
		return subject.getVMInstallType();
	}

	/**
	 * Returns a VM runner that runs this installed VM with JIVE debugging enabled
	 * if the given mode is <code>org.eclipse.debug.core.ILaunchManager.DEBUG_MODE</code>.
	 * Otherwise, an exception is throw.
	 * 
	 * @param mode the mode the VM should be launched in; only
	 *   <code>org.eclipse.debug.core.ILaunchManager.DEBUG_MODE</code> is applicable
	 * @return a VM runner used to debug with JIVE enabled
	 * @throws IllegalStateException if a mode other than
	 *   <code>org.eclipse.debug.core.ILaunchManager.DEBUG_MODE</code> is supplied
	 * @see org.eclipse.jdt.launching.IVMInstall#getVMRunner(java.lang.String)
	 * @see org.eclipse.debug.core.ILaunchManager.DEBUG_MODE
	 */
	public IVMRunner getVMRunner(String mode) {
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			return new JiveVMDebugger(this);
		}
		else {
			// TODO Add log message after internationalizing the above string literal
			//JiveLaunchingPlugin.log("JIVE can only be used when launching in debug mode.");
			throw new IllegalStateException("JIVE can only be used when launching in debug mode.");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setInstallLocation(java.io.File)
	 */
	public void setInstallLocation(File installLocation) {
		subject.setInstallLocation(installLocation);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setJavadocLocation(java.net.URL)
	 */
	public void setJavadocLocation(URL url) {
		subject.setJavadocLocation(url);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setLibraryLocations(org.eclipse.jdt.launching.LibraryLocation[])
	 */
	public void setLibraryLocations(LibraryLocation[] locations) {
		subject.setLibraryLocations(locations);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setName(java.lang.String)
	 */
	public void setName(String name) {
		subject.setName(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setVMArguments(java.lang.String[])
	 */
	public void setVMArguments(String[] vmArgs) {
		subject.setVMArguments(vmArgs);
	}

}
