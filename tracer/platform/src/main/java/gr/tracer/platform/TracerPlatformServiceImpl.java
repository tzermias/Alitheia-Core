package gr.tracer.platform;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import gr.tracer.common.security.TracerSecurityModel;
import gr.tracer.common.security.TracerSecurityModelImpl;

public class TracerPlatformServiceImpl implements TracerPlatformService {

	private BundleContext bc;
	private Logger logger;
	
	//TODO: create a singleton TracerPlatform class to hold instances of
	//TRACER related components, create an interface to be shared by tracer components
	//and add accessors for the tracer components to TracerPlatform
	
	public TracerPlatformServiceImpl() {
		try {
			AlitheiaCore core = AlitheiaCore.getInstance();
			
			if(core != null) {
				core.registerService(TracerPlatformService.class, getClass());
			}
			
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	
	@Override
	public boolean startUp() {
		
		SecurityManager sm = AlitheiaCore.getInstance().getSecurityManager();
		if(sm != null) {
			TracerSecurityModel tsm = new TracerSecurityModelImpl(sm, logger);
			tsm.initSecurityModel();
		}
		return true;
	}

	@Override
	public void shutDown() {
		logger.info("TracerPlatformService shutting down"); 
	}

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.bc = bc;
		this.logger = l;
	}

}
