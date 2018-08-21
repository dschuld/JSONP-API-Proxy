package de.davidschuld.jsonpapiproxy;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module providing the DI classes.
 * @author David Schuld (davidschuld@gmail.com) 
 *
 */
@Module
public class ApiCallModule {
	
	@Provides
	public JsonpApiCall provideCall() {
		return new JsonpApiCall();
	}

}
