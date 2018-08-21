package de.davidschuld.jsonpapiproxy;

import dagger.Component;

/**
 * A Dagger component providing the JSONP Proxy modules.
 * 
 * @author David Schuld (davidschuld@gmail.com) 
 *
 */
@Component(modules = ApiCallModule.class)
public interface ApiCallComponent {
	
	JsonpProxy buildConverter();

}
