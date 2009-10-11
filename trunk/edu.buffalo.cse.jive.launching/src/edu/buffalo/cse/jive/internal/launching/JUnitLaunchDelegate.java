package edu.buffalo.cse.jive.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;

/**
 * The launch delegate used to debug JUnit tests in JIVE.  When debugging with
 * JIVE is enabled for a launch configuration, Eclipse will re-target the launch
 * delegate to an object of this class.
 * 
 * @author Jeffrey K Czyz
 */
public class JUnitLaunchDelegate extends JUnitLaunchConfigurationDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#verifyVMInstall(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public IVMInstall verifyVMInstall(ILaunchConfiguration configuration) throws CoreException {
		IVMInstall subject = super.verifyVMInstall(configuration);
		return new VMInstallProxy(subject);
	}
}
