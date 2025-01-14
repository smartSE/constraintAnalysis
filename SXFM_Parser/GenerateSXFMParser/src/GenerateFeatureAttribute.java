import java.util.ArrayList;

import fm.FeatureGroup;
import fm.FeatureModel;
import fm.FeatureModelException;
import fm.FeatureTreeNode;
import fm.RootNode;
import fm.SolitaireFeature;
import fm.XMLFeatureModel;

public class GenerateFeatureAttribute {
	FeatureModel featureModel;
	ArrayList<String> featureID;
	ArrayList<String> leafID;
	String[] grouped;
	String[] unleaf;
	int groupCount = 0;
	int unleafCount = 0;

	public static void main(String[] args) throws FeatureModelException {
		GenerateFeatureAttribute gen = new GenerateFeatureAttribute();
		gen.featureID = new ArrayList<String>();
		gen.leafID = new ArrayList<String>();
		gen.grouped = new String[50]; // At most 50 feature groups at this time
		gen.unleaf = new String[500]; // At most 500 unleaf feature at this time
		gen.groupCount = 0;
		gen.unleafCount = 0;
		gen.loadModel();

	}

	public void loadModel() throws FeatureModelException {
		String featureModelFile = "Web_portal_FM.xml";
		FeatureModel featureModel = new XMLFeatureModel(featureModelFile,
				XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		// Load the XML file and creates the feature model
		featureModel.loadModel();
		// traverse all the feature
		traverseFeature(featureModel.getRoot());

		// print all the Features
		// for (String s : featureID)
		// System.out.println(s);
		for (int i = 0; i < leafID.size(); i++)
			System.out.println(leafID.get(i) + " = x(" + (i + 1) + ");");
		for (int i = 0; i < groupCount; i++)
			System.out.println(grouped[i] + "; x(" + (leafID.size() + i + 1)
					+ ") = " + grouped[i].split(" ")[0] + ";");
		for (int i = unleafCount - 1; i >= 0; i--)
			System.out.println(unleaf[i] + ";  x("
					+ (leafID.size() + groupCount + unleafCount - i) + ") = "
					+ unleaf[i].split(" ")[0] + ";");

		// for(int i = 0; i < featureID.size();i++)
		// System.out.print(featureID.get(i)+"+");
		// System.out.println();
	}

	public void traverseFeature(FeatureTreeNode node) {
		if (node.isLeaf())
			this.leafID.add(node.getID());
		// Root Feature
		if (node instanceof RootNode) {
			this.featureID.add(node.getID());
			this.unleaf[unleafCount] = (node.getID()) + " = "
					+ getunLeafExpression(node);
			unleafCount++;
		}
		// Solitaire Feature
		else if (node instanceof SolitaireFeature) {
			// Optional Feature
			if (((SolitaireFeature) node).isOptional()) {
				this.featureID.add(node.getID());
				if (!node.isLeaf() && !getunLeafExpression(node).equals("-1")) {
					this.unleaf[unleafCount] = (node.getID()) + " = "
							+ getunLeafExpression(node);
					unleafCount++;
				}
			}
			// Mandatory Feature
			else {
				this.featureID.add(node.getID());
				if (!node.isLeaf() && !getunLeafExpression(node).equals("-1")) {
					this.unleaf[unleafCount] = (node.getID()) + " = "
							+ getunLeafExpression(node);
					unleafCount++;
				}
			}
		}
		// Feature Group
		else if (node instanceof FeatureGroup) {
			this.grouped[groupCount] = ((FeatureTreeNode) node.getParent())
					.getID() + " = ";
			int i;
			for (i = 0; i < node.getChildCount() - 1; i++)
				this.grouped[groupCount] += ((FeatureTreeNode) node
						.getChildAt(i)).getID() + " + ";
			if (((FeatureGroup) node).getMax() == 1)
				this.grouped[groupCount] += ((FeatureTreeNode) node
						.getChildAt(i)).getID() + " == 1";
			else
				this.grouped[groupCount] += ((FeatureTreeNode) node
						.getChildAt(i)).getID() + " > 1";
			groupCount++;
		}
		// Grouped feature
		else {
			this.featureID.add(node.getID());
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			traverseFeature((FeatureTreeNode) node.getChildAt(i));
		}
	}

	private String getunLeafExpression(FeatureTreeNode node) {
		ArrayList<String> oString = new ArrayList<String>();
		ArrayList<String> mString = new ArrayList<String>();
		for (int i = 0; i < node.getChildCount(); i++) {
			FeatureTreeNode ch = (FeatureTreeNode) node.getChildAt(i);
			if (ch instanceof SolitaireFeature) {
				if (((SolitaireFeature) ch).isOptional())
					oString.add(ch.getID());
				else
					mString.add(ch.getID());
			}
		}
		String result = "";
		if (mString.size() == 0 && oString.size() == 0)
			return "-1";
		if (mString.size() > 0) {
			int i;
			for (i = 0; i < mString.size() - 1; i++)
				result += mString.get(i) + " & ";
			result += mString.get(i);
		} else {
			int i;
			for (i = 0; i < oString.size() - 1; i++)
				result += oString.get(i) + " | ";
			result += oString.get(i);
		}
		return result;
	}
}
