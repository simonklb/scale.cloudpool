package com.elastisys.scale.cloudadapters.api.types;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.elastisys.scale.cloudadapers.api.types.Machine;
import com.elastisys.scale.cloudadapers.api.types.MachinePool;
import com.elastisys.scale.cloudadapers.api.types.MachineState;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Convenience methods used by {@link MachinePool}-related tests.
 * 
 * 
 * 
 */
public class TestUtils {

	/**
	 * Convenience method for creating a {@link MachinePool}.
	 * 
	 * @param timestamp
	 * @param machines
	 * @return
	 */
	public static MachinePool pool(DateTime timestamp, Machine... machines) {
		List<Machine> machineList = Lists.newArrayList(machines);
		return new MachinePool(machineList, timestamp);
	}

	/**
	 * Convenience method for creating a {@link Machine} without IP addresses.
	 * 
	 * @param id
	 * @param state
	 * @param launchtime
	 * @param metadata
	 * @return
	 */
	public static Machine machineNoIp(String id, MachineState state,
			DateTime launchtime, JsonElement metadata) {
		return new Machine(id, state, launchtime, null, null, metadata);
	}

	/**
	 * Convenience method for creating a {@link Machine} with a given launch
	 * time.
	 * 
	 * @param id
	 * @param launchtime
	 * @return
	 */
	public static Machine machine(String id, DateTime launchtime) {
		return new Machine(id, MachineState.RUNNING, launchtime, null, null,
				new JsonObject());
	}

	/**
	 * Convenience method for creating a {@link Machine} with public and/or
	 * private IP address(es).
	 * 
	 * @param id
	 * @param state
	 * @param launchtime
	 * @param publicIps
	 * @param privateIps
	 * @param metadata
	 * @return
	 */
	public static Machine machine(String id, MachineState state,
			DateTime launchtime, List<String> publicIps,
			List<String> privateIps, JsonElement metadata) {
		return new Machine(id, state, launchtime, publicIps, privateIps,
				metadata);
	}

	public static List<String> ips(String... ipAddresses) {
		return Arrays.asList(ipAddresses);
	}

	public static long secondsBetween(DateTime start, DateTime end) {
		return Seconds.secondsBetween(start, end).getSeconds();
	}
}