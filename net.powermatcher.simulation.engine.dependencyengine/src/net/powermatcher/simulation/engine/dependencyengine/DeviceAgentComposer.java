package net.powermatcher.simulation.engine.dependencyengine;

import java.util.concurrent.ScheduledExecutorService;

import net.powermatcher.core.agent.framework.Agent;
import net.powermatcher.core.agent.framework.service.AgentService;
import net.powermatcher.core.agent.framework.service.MatcherService;
import net.powermatcher.core.agent.framework.task.BidUpdateTask;
import net.powermatcher.simulation.configuration.NodeDescriptor;
import net.powermatcher.simulation.engine.ComponentCreationException;
import net.powermatcher.simulation.engine.ComponentManager;

public class DeviceAgentComposer extends AgentComposer<Agent> {

	private Activity bidUpdateActivity;

	private Activity priceUpdateActivity;

	public DeviceAgentComposer(NodeDescriptor nodeDescriptor, long startTime) {
		super(nodeDescriptor, startTime);
	}

	@Override
	public void attachIncommingLink(Link link, Class<?> iface) {
		if (AgentService.class.equals(iface)) {
			this.priceUpdateActivity.setDependentOn(link);
		} else {
			throw new IllegalArgumentException("unsupported interface");
		}
	}

	@Override
	public void attachOutgoingLink(Link link, Class<?> iface) {
		if (MatcherService.class.equals(iface)) {
			link.setDependentOn(this.bidUpdateActivity);
		} else {
			throw new IllegalArgumentException("unsupported interface");
		}
	}

	@Override
	public Agent createNode(ComponentManager componentManager) throws ComponentCreationException {
		Agent agent = super.createNode(componentManager);

		this.bidUpdateActivity = new Activity("bid-update " + agent.getName());
		this.priceUpdateActivity = new Activity("price-update " + agent.getName());

		return agent;
	}

	@Override
	public Activity[] getActivities() {
		return new Activity[] { this.bidUpdateActivity, this.priceUpdateActivity };
	}

	@Override
	protected ScheduledExecutorService getScheduledExecutorService() {
		return new DelegatingScheduledExecutorService() {
			@Override
			protected ScheduledExecutorService getDelegateForCommand(Object command) {
				if (command instanceof BidUpdateTask) {
					return bidUpdateActivity;
				} else {
					// TODO we will assume a PriceUpdateTask for now
					return priceUpdateActivity;
				}
			}
		};
	}
}
