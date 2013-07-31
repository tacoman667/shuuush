package com.codingzen.Shuuush;

public class ProviderFactory {

	@SuppressWarnings("unchecked")
	public <T> T resolve(Class<T> type) {

		if (EventsProvider.class.isAssignableFrom(type)) {
			return (T) new EventsProvider();
		} else if (WhitelistProvider.class.isAssignableFrom(type)) {
			return (T) new WhitelistProvider();
		}

		return null;

	}

}
