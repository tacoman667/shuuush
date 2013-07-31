package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.content.Context;

public class WhitelistItem {

	public long Id;
	public String Name;
	public String PhoneNumber;

	public long save(Context context) {
		return new ProviderFactory().resolve(WhitelistProvider.class).save(this);
	}

	public static ArrayList<WhitelistItem> all(Context context) {
		return new ProviderFactory().resolve(WhitelistProvider.class).all();
	}

	public static void removeAll(Context context) {
		new ProviderFactory().resolve(WhitelistProvider.class).removeAll();
	}

	public static int count(Context context) {
		return new ProviderFactory().resolve(WhitelistProvider.class).count();
	}

	public int delete() {
		return new ProviderFactory().resolve(WhitelistProvider.class).remove(this);
	}

	public static WhitelistItem find(long _Id) {
		return new ProviderFactory().resolve(WhitelistProvider.class).find(_Id);
	}
}
