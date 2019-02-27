/**
 * Copyright (C) 2010-2019 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.test.rest.common;


import com.jayway.restassured.RestAssured;
import org.structr.api.config.Settings;
import org.structr.common.SecurityContext;
import org.structr.core.Services;
import org.structr.core.app.StructrApp;
import org.structr.core.auth.SuperUserAuthenticator;
import org.structr.rest.DefaultResourceProvider;
import org.testng.annotations.BeforeClass;

/**
 * Base class for Structr GraphQL tests.
 * All tests are executed in superuser context.
 */
public abstract class StructrGraphQLTest extends StructrRestTestBase {

	@BeforeClass
	@Override
	public void setup() {

		final long timestamp = System.nanoTime();

		basePath = "/tmp/structr-test-" + timestamp;

		Settings.Services.setValue("NodeService SchemaService HttpService");

		setupNeo4jConnection();

		// example for new configuration setup
		Settings.BasePath.setValue(basePath);
		Settings.DatabasePath.setValue(basePath + "/db");
		Settings.FilesPath.setValue(basePath + "/files");

		Settings.RelationshipCacheSize.setValue(1000);
		Settings.NodeCacheSize.setValue(1000);

		Settings.SuperUserName.setValue("superadmin");
		Settings.SuperUserPassword.setValue("sehrgeheim");

		Settings.ApplicationTitle.setValue("structr unit test app" + timestamp);
		Settings.ApplicationHost.setValue(host);
		Settings.HttpPort.setValue(httpPort);

		Settings.Servlets.setValue("GraphQLServlet JsonRestServlet");
		Settings.GraphQLAuthenticator.setValue(SuperUserAuthenticator.class.getName());
		Settings.GraphQLResourceProvider.setValue(DefaultResourceProvider.class.getName());
		Settings.GraphQLServletPath.setValue("/structr/graphql");
		Settings.RestAuthenticator.setValue(SuperUserAuthenticator.class.getName());
		Settings.RestResourceProvider.setValue(DefaultResourceProvider.class.getName());
		Settings.RestServletPath.setValue("/structr/rest");

		final Services services = Services.getInstance();

		// wait for service layer to be initialized
		do {
			try { Thread.sleep(100); } catch (Throwable t) {}

		} while (!services.isInitialized());

		securityContext = SecurityContext.getSuperUserInstance();
		app             = StructrApp.getInstance(securityContext);

		// sleep again to wait for schema initialization
		try { Thread.sleep(2000); } catch (Throwable t) {}

		// configure RestAssured
		RestAssured.basePath = "/structr/graphql";
		RestAssured.baseURI  = "http://" + host + ":" + httpPort;
		RestAssured.port     = httpPort;
	}
}
