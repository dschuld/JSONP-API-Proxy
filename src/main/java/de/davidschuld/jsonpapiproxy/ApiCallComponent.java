package de.davidschuld.jsonpapiproxy;

import dagger.Component;

@Component(modules = ApiCallModule.class)
public interface ApiCallComponent {
	
	Jsonp2Json buildConverter();

}
