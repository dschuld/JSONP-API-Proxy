package de.davidschuld.jsonpapiproxy;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiCallModule {
	
	@Provides
	public JsonpApiCall provideCall() {
		return new JsonpApiCall();
	}

}
