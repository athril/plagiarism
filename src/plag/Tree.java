package plag;
//http://www.allisons.org/ll/AlgDS/Tree/Suffix/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tree {
	private Word[] text;
	private Node root;

	private class Trans {
		public int k, p;
		public Node n;

		public Trans(int k, int p, Node n) {
			this.k = k;
			this.p = p;
			this.n = n;
		}

		public String toString() {
			StringBuffer b = new StringBuffer();
			for (int i = k; i <= p; i++) {
				b.append(" ");
				b.append(text[i]);
			}
			return b.toString();
		}
	}

	private class Node {
		private String name;
		public Node back;
		private Map<Word, Trans> down;
		public int depth;

		public Node(String n) {
			name = n;
			back = this;
			down = new HashMap<Word, Trans>();
		}

		public Node() {
			this("");
		}

		public String toString() {
			return name;
		}

		public void add(int b, int e, Node n) {
			Trans trans = new Trans(b, e, n);
			down.put(text[b], trans);
			if (n.name.equals(""))
				n.name = trans.toString();
		}

		public Trans find(Word w) {
			return (Trans) down.get(w);
		}

		public void setDepth(int d) {
			depth = d;
			Iterator<Trans> iter = down.values().iterator();
			while (iter.hasNext()) {
				Trans t = (Trans) iter.next();
				t.n.setDepth(d + t.p - t.k + 1);
			}
		}

		public void print(String indent) {
			Iterator<Trans> iter = down.values().iterator();
			while (iter.hasNext()) {
				Trans t = (Trans) iter.next();
				StringBuffer b = new StringBuffer();
				b.append(indent);
				for (int i = t.k; i <= t.p; i++) {
					b.append(" ");
					b.append(text[i].toString());
				}
				System.out.println(b.toString());
				t.n.print(indent + "  ");
			}
		}
	}

	private class Pair {
		public Node s;
		public int k;

		public Pair(Node s, int k) {
			this.s = s;
			this.k = k;
		}
	}

	private class Split {
		public boolean e;
		public Node s;

		public Split(boolean e, Node s) {
			this.e = e;
			this.s = s;
		}
	}

	private Pair update(Node s, int k, int i) {
		Node oldr = root;
		// (s, (k, i-1)) is the canonical reference pair for the active point
		Split split = split(s, k, i - 1, text[i]);
		while (!split.e) {
			// adds an edge from state r, labelling the edge with a substring.
			// New txt[i]-transitions must be "open" transitions of the form
			// (L,∞)
			split.s.add(i, text.length - 1, new Node());
			if (oldr != root)
				oldr.back = split.s;
			oldr = split.s;
			Pair pair = canonize(s.back, k, i - 1);
			s = pair.s;
			k = pair.k;
			split = split(s, k, i - 1, text[i]);
		}
		if (oldr != root)
			oldr.back = s;
		return new Pair(s, k);
	}

	/*
	 * Where necessary, split(...) replaces edges s--->s1 with s--->r--->s1 for
	 * a new node r. This makes r=(s,(k,p)) explicit.
	 */

	private Split split(Node s, int k, int p, Word t) {
		if (k > p)
			return new Split(s.find(t) != null, s);
		// find the t_k transition g'(s,(k',p'))=s' from s
		// k1 is k' p1 is p' in Ukkonen '95
		Trans trans = s.find(text[k]);
		if (t == text[trans.k + p - k + 1])
			return new Split(true, s);
		else {
			Node r = new Node();
			s.add(trans.k, trans.k + p - k, r);
			r.add(trans.k + p - k + 1, trans.p, trans.n);
			return new Split(false, r);
		}
	}

	/*
	 * Canonize(...) takes (s,w)=(s,(k,p)) and steps over intermediate nodes by
	 * spelling out the characters of w=txt[k..p] for as far as possible.
	 */
	private Pair canonize(Node s, int k, int p) {
		if (p < k)
			return new Pair(s, k);
		// find the t_k transition g'(s,(k',p'))=s' from s
		// k1 is k', p1 is p' in Ukk' '95
		Trans trans = s.find(text[k]);
		while (trans.p - trans.k <= p - k) {
			k += trans.p - trans.k + 1;
			s = trans.n;
			if (k <= p)
				trans = s.find(text[k]);
		}
		return new Pair(s, k);
	}

	/*
	 * The main controlling routine repeatedly takes the next character, updates
	 * the sites on the active path and finds and canonizes the new active
	 * point:
	 */
	public Tree(Word[] text) {
		this.text = text;
		root = new Node("(ROOT)");
		Node bottom = new Node("(BOTTOM)");
		// Want to create transitions for all possible chars
		// from bottom to root
		for (int i = 0; i < text.length; i++)
			bottom.add(i, i, root);
		root.back = bottom;
		Pair pair = new Pair(root, 0);
		for (int i = 0; i < text.length; i++) {
			pair = update(pair.s, pair.k, i);
			pair = canonize(pair.s, pair.k, i);
		}
		root.setDepth(0);
	}

	/*
	 * Interface used for searching for plagiarism
	 */
	public interface Search {
		int analyze(Word word);
	}

	private class SearchImpl implements Search {
		private Node node;
		private Trans trans;
		private int part;

		public SearchImpl() {
			node = root;
			trans = null;
			part = 0;
		}

		public int analyze(Word word) {
			while (true) {
				int b = 0, e = 0;
				if (trans == null)
					trans = node.find(word);
				else if (text[trans.k + part] != word) {
					b = trans.k;
					e = b + part;
					trans = null;
				}
				if (trans != null)
					break;
				if (node == root) {
					part = 0;
					return 0;
				}
				node = node.back;
				part = 0;
				for (int i = b; i < e; i++)
					analyze(text[i]);
			}
			part++;
			if (trans.p - trans.k + 1 == part) {
				node = trans.n;
				trans = null;
				part = 0;
			}
			return node.depth + part;
		}
	}

	public Search newSearch() {
		return new SearchImpl();
	}

	public void print() {
		root.print("");
	}
}
