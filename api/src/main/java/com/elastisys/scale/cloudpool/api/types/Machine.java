package com.elastisys.scale.cloudpool.api.types;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.elastisys.scale.cloudpool.api.CloudPool;
import com.elastisys.scale.commons.util.time.UtcTime;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

/**
 * Represents a machine that is a member of a {@link MachinePool} managed by a
 * {@link CloudPool}.
 * <p/>
 * As explained in the <a
 * href="http://cloudpoolrestapi.readthedocs.org/en/latest/api.html">cloud pool
 * REST API</a>, a {@link Machine} has three different states of interest:
 * <ul>
 * <li>The <i>machine state</i>, being the execution state of the machine as
 * reported by the cloud API.</li>
 * <li>The <i>membership status</i>, indicating if the machine is to be given
 * special treatment in the pool. This includes protecting it from termination
 * and/or marking it in need of a replacement.</li>
 * <li>The <i>service state</i>, being the operational state of the service
 * running on the machine.</li>
 * </ul>
 *
 *
 * @see MachinePool
 * @see CloudPool
 */
public class Machine {

	/** The identifier of the {@link Machine} . */
	private final String id;
	/**
	 * The execution state of the {@link Machine} reported by the
	 * infrastructure.
	 */
	private MachineState machineState;
	/**
	 * The operational state of the service running on the machine. This is
	 * different from the {@link MachineState}, which is the execution state of
	 * the {@link Machine} reported by the infrastructure.
	 * <p/>
	 * If the {@link CloudPool} is not aware of the service state for a machine,
	 * this field is set to {@link ServiceState#UNKNOWN}.
	 */
	private final ServiceState serviceState;

	/**
	 * The membership status indicates if this pool member needs to be given
	 * special treatment. This includes protecting it from termination and/or
	 * marking it in need of a replacement.
	 */
	private final MembershipStatus membershipStatus;

	/**
	 * The launch time of the {@link Machine} if it has been launched. This
	 * attribute may be <code>null</code>, depending on the state of the
	 * {@link Machine}.
	 */
	private final DateTime launchTime;

	/**
	 * The request time of the {@link Machine}. This attribute shall be set
	 * immediately when a VM has been successfully requested from the cloud
	 * backend. It may be null, if the cloud pool has no way of determining the
	 * correct value, e.g., if it was started with a pool of VMs already
	 * allocated and there is no way to find out when they were requested.
	 */
	private final DateTime requestTime;

	/**
	 * The list of public IP addresses associated with this {@link Machine}.
	 * Depending on the state of the {@link Machine}, this list may be empty.
	 */
	private final List<String> publicIps;
	/**
	 * The list of private IP addresses associated with this {@link Machine}.
	 * Depending on the state of the {@link Machine}, this list may be empty.
	 */
	private final List<String> privateIps;

	/**
	 * Additional cloud provider-specific meta data about the {@link Machine}.
	 * This field is optional (may be <code>null</code>).
	 */
	private final JsonObject metadata;

	/**
	 * Constructs a new {@link Machine}.
	 *
	 * @param id
	 *            The identifier of the {@link Machine}.
	 * @param machineState
	 *            The execution state of the {@link Machine}.
	 * @param membershipStatus
	 *            The pool membership status of this {@link Machine}.
	 * @param serviceState
	 *            The operational state of the service running on the
	 *            {@link Machine}.
	 * @param requestTime
	 *            The request time of the {@link Machine}, if this time is
	 *            known. If the time when the machine was initially and
	 *            successfully requested is not known, this attribute shall be
	 *            null.
	 * @param launchTime
	 *            The launch time of the {@link Machine} if it has been
	 *            launched. This attribute may be <code>null</code>, depending
	 *            on the state of the {@link Machine}.
	 * @param publicIps
	 *            The list of public IP addresses associated with this
	 *            {@link Machine}. If the machine hasn't (yet) been assigned any
	 *            IP addresses, this attribute can be set to <code>null</code>
	 *            or an empty list.
	 * @param privateIps
	 *            The list of private IP addresses associated with this
	 *            {@link Machine}. If the machine hasn't (yet) been assigned any
	 *            IP addresses, this attribute can be set to <code>null</code>
	 *            or an empty list.
	 * @param metadata
	 *            Additional cloud provider-specific meta data about the
	 *            {@link Machine}. May be <code>null</code>.
	 */
	protected Machine(String id, MachineState machineState,
			MembershipStatus membershipStatus, ServiceState serviceState,
			DateTime requestTime, DateTime launchTime, List<String> publicIps,
			List<String> privateIps, JsonObject metadata) {
		checkNotNull(id, "missing id");
		checkNotNull(machineState, "missing machineState");
		checkNotNull(membershipStatus, "missing membershipStatus");
		checkNotNull(serviceState, "missing serviceState");

		this.id = id;
		this.machineState = machineState;
		this.membershipStatus = membershipStatus;
		this.serviceState = serviceState;
		this.requestTime = requestTime;
		this.launchTime = launchTime;
		this.publicIps = Optional.fromNullable(publicIps).or(
				new ArrayList<String>());
		this.privateIps = Optional.fromNullable(privateIps).or(
				new ArrayList<String>());
		this.metadata = metadata;
	}

	/**
	 * Returns the identifier of the {@link Machine}.
	 *
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the execution state of the {@link Machine}.
	 *
	 * @return
	 */
	public MachineState getMachineState() {
		return this.machineState;
	}

	/**
	 * Sets the execution state of the {@link Machine}.
	 *
	 * @param machineState
	 */
	public void setMachineState(MachineState machineState) {
		this.machineState = machineState;
	}

	/**
	 * Returns the pool membership status of this {@link Machine}.
	 *
	 * @return
	 */
	public MembershipStatus getMembershipStatus() {
		return this.membershipStatus;
	}

	/**
	 * Returns the service state of the {@link Machine}.
	 *
	 * @return
	 */
	public ServiceState getServiceState() {
		return this.serviceState;
	}

	/**
	 * Returns the launch time of the {@link Machine} if it has been launched.
	 * This attribute may be <code>null</code>, depending on the state of the
	 * {@link Machine}.
	 *
	 * @return
	 */
	public DateTime getLaunchTime() {
		return this.launchTime;
	}

	/**
	 * Returns the request time of the {@link Machine}. This attribute shall be
	 * set immediately when a VM has been successfully requested from the cloud
	 * backend. It may be null, if the cloud pool has no way of determining the
	 * correct value, e.g., if it was started with a pool of VMs already
	 * allocated and there is no way to find out when they were requested.
	 *
	 * @return A {@link DateTime} object with the request time, if any. null
	 *         otherwise.
	 */
	public DateTime getRequestTime() {
		return this.requestTime;
	}

	/**
	 * Returns the list of public IP addresses associated with this
	 * {@link Machine}. Depending on the state of the {@link Machine}, this list
	 * may be empty.
	 *
	 * @return
	 */
	public List<String> getPublicIps() {
		return this.publicIps;
	}

	/**
	 * Returns the list of private IP addresses associated with this
	 * {@link Machine}. Depending on the state of the {@link Machine}, this list
	 * may be empty.
	 *
	 * @return
	 */
	public List<String> getPrivateIps() {
		return this.privateIps;
	}

	/**
	 * Returns any additional cloud provider-specific meta data about the
	 * {@link Machine} if set, otherwise <code>null</code>.
	 *
	 * @return
	 */
	public JsonObject getMetadata() {
		return this.metadata;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id, this.machineState,
				this.membershipStatus, this.serviceState, this.launchTime,
				this.requestTime, this.publicIps, this.privateIps,
				this.metadata);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Machine) {
			Machine that = (Machine) obj;
			final boolean launchtimesEqual;
			if (this.launchTime != null && that.launchTime != null) {
				launchtimesEqual = this.launchTime.isEqual(that.launchTime);
			} else if (this.launchTime == null && that.launchTime == null) {
				launchtimesEqual = true;
			} else {
				launchtimesEqual = false;
			}
			final boolean requesttimesEqual;
			if (this.requestTime != null && that.requestTime != null) {
				requesttimesEqual = this.requestTime.isEqual(that.requestTime);
			} else if (this.requestTime == null && that.requestTime == null) {
				requesttimesEqual = true;
			} else {
				requesttimesEqual = false;
			}
			return Objects.equal(this.id, that.id)
					&& Objects.equal(this.machineState, that.machineState)
					&& Objects.equal(this.membershipStatus,
							that.membershipStatus)
					&& Objects.equal(this.serviceState, that.serviceState)
					&& launchtimesEqual && requesttimesEqual
					&& Objects.equal(this.publicIps, that.publicIps)
					&& Objects.equal(this.privateIps, that.privateIps)
					&& Objects.equal(this.metadata, that.metadata);
		}
		return false;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", this.id)
				.add("machineState", this.machineState)
				.add("membershipStatus", this.membershipStatus)
				.add("serviceState", this.serviceState)
				.add("requestTime", this.requestTime)
				.add("launchTime", this.launchTime)
				.add("publicIps", this.publicIps)
				.add("privateIps", this.privateIps)
				.add("metadata", this.metadata).toString();
	}

	/**
	 * Creates a copy of this {@link Machine} with a different value for the
	 * metadata field.
	 *
	 * @param metadata
	 *            Metadata to set for the {@link Machine} copy, or
	 *            <code>null</code> if no metadata is desired.
	 * @return A copy
	 */
	public Machine withMetadata(JsonObject metadata) {
		return new Machine(this.id, this.machineState, this.membershipStatus,
				this.serviceState, this.requestTime, this.launchTime,
				this.publicIps, this.privateIps, metadata);
	}

	/**
	 * Returns a transformation {@link Function} that given a {@link Machine}
	 * extracts its {@link MachineState}.
	 *
	 * @return
	 */
	public static Function<? super Machine, MachineState> toState() {
		return new MachineStateExtractor();
	}

	/**
	 * Returns a transformation {@link Function} that given a {@link Machine}
	 * extracts its identifier.
	 *
	 * @return
	 */
	public static Function<? super Machine, String> toId() {
		return new MachineIdExtractor();
	}

	/**
	 * Returns a {@link Function} that given a {@link Machine} returns the
	 * remaining time (in seconds) to the start of its next hour.
	 *
	 * @return
	 */
	public static Function<? super Machine, Long> remainingInstanceHourTime() {
		return new RemainingInstanceHourTime();
	}

	/**
	 * Optionally returns how many milliseconds ago a request was made.
	 *
	 * @param now
	 *            The time to regard as the current time.
	 * @return A {@link Function} that given a {@link Machine} and the current
	 *         time returns how many milliseconds ago it was requested from the
	 *         underlying cloud infrastructure. Since not all clouds support
	 *         reporting how old a request is, the return value is optional.
	 */
	public static Function<? super Machine, Optional<Long>> requestAge(
			final DateTime now) {
		return new RequestAge(now);
	}

	/**
	 * Returns a {@link Function} that given a {@link Machine} (with a launch
	 * time set) returns the starting point of the {@link Machine}'s most
	 * recently started hour.
	 *
	 * @return
	 */
	public static Function<? super Machine, DateTime> instanceHourStart() {
		return new InstanceHourStart();
	}

	/**
	 * Returns a {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} in a given {@link MachineState}.
	 *
	 * @param state
	 *            The {@link MachineState} for which this {@link Predicate} will
	 *            return <code>true</code>.
	 * @return
	 */
	public static Predicate<? super Machine> inState(MachineState state) {
		return new MachineWithState(state);
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} in a given {@link MachineState}.
	 */
	public static class MachineWithState implements Predicate<Machine> {
		private final MachineState state;

		public MachineWithState(MachineState state) {
			checkNotNull(state);
			this.state = state;
		}

		@Override
		public boolean apply(Machine machine) {
			return machine.getMachineState() == this.state;
		}
	}

	/**
	 * A transformation {@link Function} that when applied to a {@link Machine}
	 * extracts the {@link Machine}'s state.
	 * <p/>
	 * Can be used to transform a collection of {@link Machine}s to a collection
	 * of {@link MachineState}. See
	 * {@link Iterables#transform(Iterable, Function)}.
	 *
	 * @see http://code.google.com/p/guava-libraries/wiki/FunctionalExplained
	 */
	public static class MachineStateExtractor implements
			Function<Machine, MachineState> {
		/**
		 * Extracts the state of a {@link Machine}.
		 *
		 * @see Function#apply(Object)
		 */
		@Override
		public MachineState apply(Machine machine) {
			return machine.getMachineState();
		}
	}

	/**
	 * A transformation {@link Function} that when applied to a {@link Machine}
	 * extracts the {@link Machine}'s id.
	 * <p/>
	 * Can be used to transform a collection of {@link Machine}s to a collection
	 * of {@link String}. See {@link Iterables#transform(Iterable, Function)}.
	 *
	 * @see http://code.google.com/p/guava-libraries/wiki/FunctionalExplained
	 *
	 *
	 */
	public static class MachineIdExtractor implements Function<Machine, String> {
		/**
		 * Extracts the id of a {@link Machine}.
		 *
		 * @see Function#apply(Object)
		 */
		@Override
		public String apply(Machine machine) {
			return machine.getId();
		}
	}

	/**
	 * Returns a {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that is an active pool member -- it has been allocated
	 * from the underlying infrastructure ({@link MachineState#REQUESTED},
	 * {@link MachineState#PENDING} or {@link MachineState#RUNNING}) and is an
	 * active pool member ({@link MembershipStatus#isActive()}).
	 *
	 * @return
	 */
	public static Predicate<Machine> isActiveMember() {
		return new ActiveMemberPredicate();
	}

	/**
	 * Returns a {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that has been allocated from the underlying
	 * infrastructure (machine state {@link MachineState#REQUESTED},
	 * {@link MachineState#PENDING} or {@link MachineState#RUNNING}).
	 *
	 * @return
	 */
	public static Predicate<Machine> isAllocated() {
		return new AllocatedMachinePredicate();
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that has been started by the underlying infrastructure
	 * (machine state is {@link MachineState#PENDING} or
	 * {@link MachineState#RUNNING}).
	 *
	 * @see MachineState
	 */
	public static Predicate<Machine> isStarted() {
		return new StartedMachinePredicate();
	}

	/**
	 * Returns a {@link Predicate} that returns <code>true</code> for any
	 * {@link Machine} with an evictable {@link MembershipStatus}.
	 *
	 * @return
	 */
	public static Predicate<Machine> isEvictable() {
		return new EvictableMemberPredicate();
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} with an evictable {@link MembershipStatus}.
	 */
	public static class EvictableMemberPredicate implements Predicate<Machine> {
		@Override
		public boolean apply(Machine machine) {
			return machine.getMembershipStatus().isEvictable();
		}
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that is an active pool member -- it has been allocated
	 * from the underlying infrastructure ({@link MachineState#REQUESTED},
	 * {@link MachineState#PENDING} or {@link MachineState#RUNNING}) and is an
	 * active pool member ({@link MembershipStatus#isActive()}).
	 */
	public static class ActiveMemberPredicate implements Predicate<Machine> {
		@Override
		public boolean apply(Machine machine) {
			return isAllocated().apply(machine)
					&& machine.getMembershipStatus().isActive();
		}
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that has been allocated from the underlying
	 * infrastructure (machine state {@link MachineState#REQUESTED},
	 * {@link MachineState#PENDING} or {@link MachineState#RUNNING}).
	 */
	public static class AllocatedMachinePredicate implements Predicate<Machine> {
		private static final Set<MachineState> allocatedStates = Sets
				.newHashSet(MachineState.REQUESTED, MachineState.PENDING,
						MachineState.RUNNING);

		@Override
		public boolean apply(Machine machine) {
			return allocatedStates.contains(machine.getMachineState());
		}
	}

	/**
	 * A {@link Predicate} that returns <code>true</code> when passed a
	 * {@link Machine} that has been started by the underlying infrastructure
	 * (machine state is {@link MachineState#PENDING} or
	 * {@link MachineState#RUNNING}).
	 *
	 * @see MachineState
	 */
	public static class StartedMachinePredicate implements Predicate<Machine> {
		private static final Set<MachineState> startedStates = Sets.newHashSet(
				MachineState.PENDING, MachineState.RUNNING);

		@Override
		public boolean apply(Machine machine) {
			return startedStates.contains(machine.getMachineState());
		}
	}

	/**
	 * A {@link Function} that for a given {@link Machine} calculates when the
	 * started its most recent hour.
	 * <p/>
	 * The {@link Machine} must have its launch time set.
	 */
	public static class InstanceHourStart implements
			Function<Machine, DateTime> {

		/**
		 * Calculates the starting point of the machine's current hour.
		 *
		 * @param machine
		 * @return
		 */
		@Override
		public DateTime apply(Machine machine) {
			checkArgument(machine != null, "null machine");
			checkArgument(machine.getLaunchTime() != null,
					"null launch time for machine");
			DateTime now = UtcTime.now();
			DateTime launchtime = machine.getLaunchTime();
			long secondsPerHour = TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);
			long millisPerSecond = TimeUnit.MILLISECONDS.convert(1,
					TimeUnit.SECONDS);
			long millisPerHour = secondsPerHour * millisPerSecond;

			// millis from epoch machine was launched
			long epochMillis = launchtime.getMillis();
			// millis into wall-clock hour machine was launched
			long wallclockhourOffset = epochMillis % millisPerHour;
			// apply the wallclock hour offset to the current hour
			DateTime currentHour = now.withMinuteOfHour(0)
					.withSecondOfMinute(0).withMillisOfSecond(0);
			DateTime HourStart = currentHour.plus(wallclockhourOffset);
			// if that results in a time instant that lies in the future, the
			// start of the current billing hour was in the previous
			// wall-clock hour
			if (now.isBefore(HourStart)) {
				HourStart = HourStart.minusHours(1);
			}
			return HourStart;
		}
	}

	/**
	 * A {@link Function} that optionally returns the number of milliseconds of
	 * how long ago the request to start a given {@link Machine} was made. Since
	 * not all cloud infrastructures support returning this value, the return
	 * value from the function is {@link Optional}.
	 */
	public static class RequestAge implements Function<Machine, Optional<Long>> {
		private final DateTime now;

		/**
		 * Creates a new instance that sets the current time to a particular
		 * value.
		 *
		 * @param now
		 *            The time to regard as the current time.
		 */
		public RequestAge(DateTime now) {
			this.now = now;
		}

		@Override
		public Optional<Long> apply(Machine machine) {
			if (machine.getRequestTime() != null) {
				return Optional.of(this.now.minus(
						machine.getRequestTime().getMillis()).getMillis());
			} else {
				return Optional.absent();
			}
		}
	}

	/**
	 * A {@link Function} that for a given {@link Machine} calculates the
	 * remaining time (in seconds) of the machine's current hour.
	 *
	 *
	 */
	public static class RemainingInstanceHourTime implements
			Function<Machine, Long> {

		/**
		 * Calculates the remaining time (in seconds) of the machine's last
		 * started billing hour.
		 *
		 * @param machine
		 * @return
		 */
		@Override
		public Long apply(Machine machine) {
			checkArgument(machine != null, "null machine");
			checkArgument(machine.getLaunchTime() != null,
					"null launch time for machine");

			DateTime HourStart = instanceHourStart().apply(machine);
			DateTime nextHourStart = HourStart.plusHours(1);

			long millisToNextHour = nextHourStart.getMillis()
					- UtcTime.now().getMillis();
			return millisToNextHour / 1000;
		}
	}

	/**
	 * Sorts a collection of {@link Machine}s according to the order prescribed
	 * by a certain {@link Comparator}.
	 *
	 * @param machines
	 * @param comparator
	 * @return
	 */
	public static List<Machine> sort(Collection<Machine> machines,
			Comparator<Machine> comparator) {
		List<Machine> list = Lists.newArrayList(machines);
		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * Factory method for the {@link ToShortMachineFormat} {@link Function}.
	 *
	 * @return
	 */
	public static Function<Machine, Machine> toShortFormat() {
		return new ToShortMachineFormat();
	}

	/**
	 * A {@link Function} that, for a given {@link Machine}, returns a clone of
	 * the {@link Machine} that excludes the meta data field of the original
	 * {@link Machine}.
	 */
	public static class ToShortMachineFormat implements
			Function<Machine, Machine> {

		@Override
		public Machine apply(Machine machine) {
			return machine.withMetadata(null);
		}
	}

	/**
	 * Factory method for the {@link ToShortMachineString} {@link Function}.
	 *
	 * @return
	 */
	public static Function<Machine, String> toShortString() {
		return new ToShortMachineString();
	}

	/**
	 * A {@link Function} that, for a given {@link Machine}, returns a string
	 * representation that excludes any {@link Machine} meta data (which can
	 * produce quite some log noise).
	 */
	public static class ToShortMachineString implements
			Function<Machine, String> {

		@Override
		public String apply(Machine machine) {
			return MoreObjects.toStringHelper("").add("id", machine.getId())
					.add("machineState", machine.getMachineState().name())
					.add("membershipStatus", machine.getMembershipStatus())
					.add("serviceState", machine.getServiceState().name())
					.add("requestTime", machine.getRequestTime())
					.add("launchTime", machine.getLaunchTime())
					.add("publicIps", machine.getPublicIps())
					.add("privateIps", machine.getPrivateIps()).toString();
		}
	}

	/**
	 * Creates a {@link Builder} object for generating {@link Machine}
	 * instances.
	 *
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * A builder for creating {@link Machine} instances.
	 */
	public static class Builder {
		private String id = null;
		private MachineState machineState = null;
		private ServiceState serviceState = ServiceState.UNKNOWN;
		private MembershipStatus membershipStatus = MembershipStatus
				.defaultStatus();
		private DateTime launchTime = null;
		private DateTime requestTime = null;
		private final List<String> publicIps = Lists.newArrayList();
		private final List<String> privateIps = Lists.newArrayList();
		private JsonObject metadata = null;

		private Builder() {
		}

		/**
		 * Creates a new {@link Machine} instance from the parameters passed
		 * thus far to the {@link Builder}.
		 *
		 * @return
		 */
		public Machine build() {
			checkArgument(this.id != null, "machine: no id given");
			checkArgument(this.machineState != null,
					"machine: no machineState given");
			if (this.requestTime != null && this.launchTime != null) {
				checkArgument(this.requestTime.isBefore(this.launchTime)
						|| this.requestTime.isEqual(this.launchTime),
						"requestTime cannot be later than launchTime");
			}
			return new Machine(this.id, this.machineState,
					this.membershipStatus, this.serviceState, this.requestTime,
					this.launchTime, this.publicIps, this.privateIps,
					this.metadata);
		}

		/**
		 * Sets the identifier for the {@link Machine} being built. Required
		 * attribute.
		 *
		 * @param id
		 * @return
		 */
		public Builder id(String id) {
			checkArgument(id != null, "id cannot be null");
			this.id = id;
			return this;
		}

		/**
		 * Sets the {@link MachineState} for the {@link Machine} being built.
		 * Required attribute.
		 *
		 * @param machineState
		 * @return
		 */
		public Builder machineState(MachineState machineState) {
			checkArgument(machineState != null, "machineState cannot be null");
			this.machineState = machineState;
			return this;
		}

		/**
		 * Sets the {@link ServiceState} for the {@link Machine} being built.
		 * Default: {@link ServiceState#UNKNOWN}.
		 *
		 * @param serviceState
		 * @return
		 */
		public Builder serviceState(ServiceState serviceState) {
			checkArgument(serviceState != null, "serviceState cannot be null");
			this.serviceState = serviceState;
			return this;
		}

		/**
		 * Sets the {@link MembershipStatus} for the {@link Machine} being
		 * built. Default: {@link MembershipStatus#defaultStatus()}.
		 *
		 * @param membershipStatus
		 * @return
		 */
		public Builder membershipStatus(MembershipStatus membershipStatus) {
			checkArgument(membershipStatus != null,
					"membershipStatus cannot be null");
			this.membershipStatus = membershipStatus;
			return this;
		}

		/**
		 * Sets the launchTime for the {@link Machine} being built. Default:
		 * <code>null</code>.
		 *
		 * @param launchTime
		 *            Launch time. May be <code>null</code>.
		 * @return
		 */
		public Builder launchTime(DateTime launchTime) {
			this.launchTime = launchTime;
			return this;
		}

		/**
		 * Sets the requestTime for the {@link Machine} being built. Default:
		 * <code>null</code>.
		 *
		 * @param requestTime
		 *            Request time. May be <code>null</code>.
		 * @return
		 */
		public Builder requestTime(DateTime requestTime) {
			this.requestTime = requestTime;
			return this;
		}

		/**
		 * Adds a single public IP address for the {@link Machine} being built.
		 *
		 * @param publicIp
		 * @return
		 */
		public Builder publicIp(String publicIp) {
			checkArgument(publicIp != null, "publicIp cannot be null");
			this.publicIps.add(publicIp);
			return this;
		}

		/**
		 * Adds several public IP address for the {@link Machine} being built.
		 *
		 * @param publicIps
		 * @return
		 */
		public Builder publicIps(List<String> publicIps) {
			checkArgument(publicIps != null, "publicIps cannot be null");
			this.publicIps.addAll(publicIps);
			return this;
		}

		/**
		 * Adds a single private IP address for the {@link Machine} being built.
		 *
		 * @param privateIp
		 * @return
		 */
		public Builder privateIp(String privateIp) {
			checkArgument(privateIp != null, "privateIp cannot be null");
			this.privateIps.add(privateIp);
			return this;
		}

		/**
		 * Adds several private IP address for the {@link Machine} being built.
		 *
		 * @param privateIps
		 * @return
		 */
		public Builder privateIps(List<String> privateIps) {
			checkArgument(privateIps != null, "privateIps cannot be null");
			this.privateIps.addAll(privateIps);
			return this;
		}

		/**
		 * Sets the meta data for the {@link Machine} being built. Default:
		 * <code>null</code>.
		 *
		 * @param metadata
		 *            Meta data. May be <code>null</code>.
		 * @return
		 */
		public Builder metadata(JsonObject metadata) {
			this.metadata = metadata;
			return this;
		}
	}
}
