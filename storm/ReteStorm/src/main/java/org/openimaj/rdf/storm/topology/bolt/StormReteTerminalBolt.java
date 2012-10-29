package org.openimaj.rdf.storm.topology.bolt;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openimaj.rdf.storm.bolt.RETEStormNode;
import org.openimaj.rdf.storm.topology.rules.ReteTopologyRuleContext;

import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.impl.BindingVector;
import com.hp.hpl.jena.reasoner.rulesys.impl.RETERuleContext;
import com.hp.hpl.jena.reasoner.rulesys.impl.RETETerminal;

/**
 * Rather than encapsulating the functionality of (though not an instance of)
 * {@link RETETerminal}. If the rule should fire when events are recieved, the
 * head of the rule is fired.
 *
 * @author David Monks <dm11g08@ecs.soton.ac.uk>, Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public class StormReteTerminalBolt extends StormReteBolt {

	protected final static Logger logger = Logger.getLogger(ReteTerminalBolt.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -7975764795094395787L;
	
	/**
	 * A terminal bolt with a rule
	 * @param rule
	 */
	public StormReteTerminalBolt(Rule rule) {
		super(rule);
	}

	@Override
	public RETEStormNode clone(Map<RETEStormNode, RETEStormNode> netCopy,
			RETERuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Tuple input) {
		Rule rule = this.getRule();
		BindingVector env = new BindingVector((Node[]) input.getValues().subList(0, rule.getNumVars()).toArray());
		ReteTopologyRuleContext context = new ReteTopologyRuleContext.IgnoreAdd(rule, env);
		logger.debug("Checking rule functors");
		if(!context.shouldFire(true)) {
			collector.ack(input);
			return;
		}
		Object environment = env.getEnvironment();

		Values bindingsRule = new Values(environment,rule.toString());
		this.collector.emit(input, bindingsRule);
		collector.ack(input);
	}

	@Override
	public void prepare() {
	}

}