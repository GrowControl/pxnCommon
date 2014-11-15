package com.poixson.commonapp.plugin;

import com.poixson.commonjava.xLogger.xLog;


public abstract class xJavaPlugin {

	private volatile xPluginManager manager = null;
	private volatile xPluginYML yml = null;

	private enum INIT_STATE {FRESH, INITED, UNLOADED}
	private volatile INIT_STATE inited = INIT_STATE.FRESH;
	private volatile boolean enabled = false;



	protected void doInit(final xPluginManager pluginManager, final xPluginYML yaml) {
		if(pluginManager == null) throw new NullPointerException();
		if(yaml          == null) throw new NullPointerException();
		if(this.inited.equals(INIT_STATE.INITED))   throw new IllegalStateException("Plugin already inited!");
		if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot init plugin, already unloaded!");
		synchronized(this.inited) {
			if(this.inited.equals(INIT_STATE.INITED))   throw new IllegalStateException("Plugin already inited!");
			if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot init plugin, already unloaded!");
			this.manager = pluginManager;
			this.yml = yaml;
			onInit();
			this.inited = INIT_STATE.INITED;
		}
	}
	protected void doUnload() {
		if(!this.inited.equals(INIT_STATE.INITED)) return;
		if(isEnabled())
			doDisable();
		synchronized(this.inited) {
			if(!this.inited.equals(INIT_STATE.INITED)) return;
			this.inited = INIT_STATE.UNLOADED;
		}
	}
	protected void doEnable() {
		if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot enable plugin, already unloaded!");
		if(!this.inited.equals(INIT_STATE.INITED))  throw new IllegalStateException("Cannot enable plugin, not inited!");
		if(this.enabled) return;
		synchronized(this.inited) {
			if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot enable plugin, already unloaded!");
			if(!this.inited.equals(INIT_STATE.INITED))  throw new IllegalStateException("Cannot enable plugin, not inited!");
			if(this.enabled) return;
			onEnable();
			this.enabled = true;
		}
	}
	protected void doDisable() {
		if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot enable plugin, already unloaded!");
		if(!this.inited.equals(INIT_STATE.INITED))  throw new IllegalStateException("Cannot enable plugin, not inited!");
		if(!this.enabled) return;
		synchronized(this.inited){
			if(this.inited.equals(INIT_STATE.UNLOADED)) throw new IllegalStateException("Cannot enable plugin, already unloaded!");
			if(!this.inited.equals(INIT_STATE.INITED))  throw new IllegalStateException("Cannot enable plugin, not inited!");
			if(!this.enabled) return;
			this.enabled = false;
			onDisable();
		} 
	}



	public boolean isEnabled() {
		if(!this.inited.equals(INIT_STATE.INITED))
			return false;
		return this.enabled;
	}



	protected void onInit() {}
	protected void onUnload() {}
	protected abstract void onEnable();
	protected abstract void onDisable();



	public xPluginManager getPluginManager() {
		return this.manager;
	}
	public xPluginYML getPluginYML() {
		return this.yml;
	}



	public String getPluginName() {
		return getPluginYML().getPluginName();
	}
	public String getPluginVersion() {
		return getPluginYML().getPluginVersion();
	}
	public String getPluginAuthor() {
		return getPluginYML().getPluginAuthor();
	}
	public String getPluginWebsite() {
		return getPluginYML().getPluginWebsite();
	}



	// logger
	private volatile xLog _log = null;
	public xLog log() {
		if(this._log == null)
			this._log = xLog.getRoot(getPluginName());
		return this._log;
	}



}
