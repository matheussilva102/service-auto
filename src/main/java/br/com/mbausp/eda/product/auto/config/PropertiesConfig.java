package br.com.mbausp.eda.product.auto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "runtime.props")
@RefreshScope
public class PropertiesConfig {

	private boolean unavailable;

	private long minLatencyInMilli;

	private long maxLatencyInMilli;

	public boolean isUnavailable() {
		return this.unavailable;
	}

	public void setUnavailable(boolean unavailable) {
		this.unavailable = unavailable;
	}

	public long getMinLatencyInMilli() {
		return this.minLatencyInMilli;
	}

	public void setMinLatencyInMilli(long minLatencyInMilli) {
		this.minLatencyInMilli = minLatencyInMilli;
	}

	public long getMaxLatencyInMilli() {
		return this.maxLatencyInMilli;
	}

	public void setMaxLatencyInMilli(long maxLatencyInMilli) {
		this.maxLatencyInMilli = maxLatencyInMilli;
	}

}
