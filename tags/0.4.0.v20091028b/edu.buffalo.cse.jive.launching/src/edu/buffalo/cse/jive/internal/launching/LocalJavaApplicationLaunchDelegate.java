package edu.buffalo.cse.jive.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

/**
 * The launch delegate used to debug Java programs in JIVE.  When debugging with
 * JIVE is enabled for a launch configuration, Eclipse will re-target the launch
 * delegate to an object of this class.
 * 
 * @author Jeffrey K Czyz
 */
public class LocalJavaApplicationLaunchDelegate extends JavaLaunchDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#verifyVMInstall(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public IVMInstall verifyVMInstall(ILaunchConfiguration configuration) throws CoreException {
		IVMInstall subject = super.verifyVMInstall(configuration);
		return new VMInstallProxy(subject);
	}
}
