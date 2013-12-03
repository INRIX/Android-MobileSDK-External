package com.inrix.sdk.client;

import com.inrix.sdk.client.interfaces.IClient;

/**
 * A factory for creating Client objects.
 */
public class ClientFactory {
	private static ClientFactory instance;
	private IClient localAgent;
	
	/**
	 * Instantiates a new client factory.
	 */
	private ClientFactory() {

	}

	/**
	 * Gets the single instance of ClientFactory.
	 * 
	 * @return single instance of ClientFactory
	 */
	public static ClientFactory getInstance() {
		if (instance == null) {
			instance = new ClientFactory();
		}

		return instance;
	}

	/**
	 * Gets the client.
	 * 
	 * @return the client
	 */
	public IClient getClient() {
		if (this.localAgent == null) {
			this.localAgent = this.getClientInternal();
		}

		return this.localAgent;
	}

	/**
	 * Gets the client internal. Add ability to create IPC Client
	 * 
	 * @return the client internal
	 */
	private IClient getClientInternal() {
		return new InrixLocalClient();
	}
}
