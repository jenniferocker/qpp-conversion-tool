package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class PiProportionDenominatorEncoderTest {

	private Node piProportionDenominatorNode;
	private Node numeratorDenominatorValueNode;
	private List<Node> nodes;
	private JsonWrapper json;

	@BeforeEach
	void createNode() {
		Node ensureOrderIsNotOfConcern = new Node(TemplateId.DEFAULT);

		numeratorDenominatorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorDenominatorValueNode.putValue("aggregateCount", "600");

		piProportionDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		piProportionDenominatorNode.addChildNode(ensureOrderIsNotOfConcern);
		piProportionDenominatorNode.addChildNode(numeratorDenominatorValueNode);

		nodes = new ArrayList<>();
		nodes.add(piProportionDenominatorNode);

		json = new JsonWrapper();
	}

	@Test
	void testEncoder() {
		runEncoder();

		assertThat(json.getInteger("denominator"))
				.isEqualTo(600);
	}

	@Test
	void testEncoderWithoutChild() {
		piProportionDenominatorNode.getChildNodes().remove(numeratorDenominatorValueNode);
		runEncoder();

		assertThat(json.getInteger("denominator"))
				.isNull();
	}

	@Test
	void testEncoderWithoutValue() {
		numeratorDenominatorValueNode.putValue("aggregateCount", null);
		runEncoder();
		
		// QPPCT-1008 wrappers are protected from null
		assertThat(json.toString())
				.isEqualTo("{ }");
	}

	private void runEncoder() {
		PiProportionDenominatorEncoder encoder = new PiProportionDenominatorEncoder(new Context());
		try {
			encoder.internalEncode(json, piProportionDenominatorNode);
		} catch (EncodeException e) {
			throw new RuntimeException(e);
		}
		encoder.setNodes(nodes);
	}
}
