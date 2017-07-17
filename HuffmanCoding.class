import java.util.*;
import java.io.*;
import java.nio.file.Files;
public class HuffmanCoding 
{
	final private static int BYTE_LENGTH = 8;
	
	public static void compress(String input, String output)
	{
		System.out.println("Running compression...");
		File read = new File(input);
		File write = new File(output);
		char[] data = read(read).toCharArray();
		HuffmanHeap h = new HuffmanHeap(getFreqMap(data));
		StringBuilder sb = new StringBuilder();
		sb.append(h);
		Map<Character, String> codes = h.getCodes();
		for(char c : data)
		{
			sb.append(codes.get(c));
		}
		sb = sb.reverse();
		sb.append(0);
		while(sb.length() % BYTE_LENGTH != 0)
		{
			sb.append(0);
		}
		sb = sb.reverse();
		try
		{
			FileOutputStream fos = new FileOutputStream(write);
			fos.write(binStringToByte(sb.toString()));
			fos.flush();
			fos.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		System.out.println("Done!");
	}
	
	private static byte[] binStringToByte(String s){
        byte[] bytes = new byte[s.length()/BYTE_LENGTH];
        for(int i = 0; i < bytes.length; i++)
        {
        	Integer byteVal = Integer.parseInt(s.substring(i * BYTE_LENGTH, i * BYTE_LENGTH + BYTE_LENGTH), 2);
        	bytes[i] = byteVal.byteValue();
        }
        return bytes;
	}
	
	public static void decompress(String input, String output)
	{
		System.out.println("Running decompression...");
		File read = new File(input);
		File write = new File(output);
		String s = readBinString(read);
		try
		{
			PrintWriter pw = new PrintWriter(write);
			pw.print(HuffmanHeap.decode(s));
			pw.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		System.out.println("Done!");
	}
	
	public static String readBinString(File f)
	{
		byte[] bytes = null;
		StringBuilder sb = new StringBuilder();
		try 
		{
			bytes = Files.readAllBytes(f.toPath());
		} 
		catch (IOException e) {
			System.out.println(e);
		}
		for(byte b : bytes)
		{
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return sb.toString();
	}
	
	public static String read(File f)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine())
			{
				sb.append(sc.nextLine());
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		return sb.toString();
	}
	
	private static Map<Character, Integer> getFreqMap(char[] characters)
	{
		Map<Character, Integer> m = new HashMap<>();
		for(char c : characters)
		{
			if(!m.containsKey(c))
				m.put(c, 1);
			else
				m.put(c, m.get(c) + 1);
		}
		return m;
	}
	
	public static class HuffmanHeap
	{
		private Node root_;
		private Map<Character, String> huffmanEncodings;
		
		public HuffmanHeap(Map<Character, Integer> cFreq)
		{
			PriorityQueue<Node> q = new PriorityQueue<>();
			huffmanEncodings = new HashMap<>();
			LinkedList<Node> charNodes = new LinkedList<>();
			for(Map.Entry<Character, Integer> entry: cFreq.entrySet())
			{
				q.offer(new Node(entry.getKey(), entry.getValue()));
			}
			while(q.size() > 1)
			{
				Node a = q.remove();
				if(a.getC() != null)
					charNodes.add(a);
				Node b = q.remove();
				if(b.getC() != null)
					charNodes.add(b);
				Node branch = new Node(a, b);
				q.offer(branch);
			}
			root_ = q.remove();
			buildHuffmanCodes(charNodes);
		}
		
		public static String decode(String s)
		{
			s = s.substring(s.indexOf("1"));
			StringBuilder sb = new StringBuilder();
			HuffmanHeap h = new HuffmanHeap(new Scanner(s), sb);
			return sb.toString();
		}
		
		private HuffmanHeap(Scanner sc, StringBuilder s)
		{
			sc.useDelimiter("");
			Stack<Node> nodeStack = new Stack<>();
			LinkedList<Node> leafNodes = new LinkedList<>();
			root_ = new Node();
			nodeStack.push(root_);
			boolean pop = false;
			while(nodeStack.size() > 0)
			{
				while(nodeStack.size() > 0 && nodeStack.peek().getLeft() != null && nodeStack.peek().getRight() != null)
					nodeStack.pop();
				if(nodeStack.size() > 0)
				{
					int check = Integer.valueOf(sc.next());
					if(check == 1)
					{
						Node n = new Node();
						if(nodeStack.peek().getLeft() == null)
						{
							nodeStack.peek().setLeftChild(n);
						}
						else
						{
							nodeStack.peek().setRightChild(n);
						}
						nodeStack.push(n);
					}
					else
					{
						if(pop)
						{
							pop = false;
							leafNodes.add(nodeStack.pop());
						}
						else
						{
							pop = true;
						}
					}
				}
			}
			for(Node n : leafNodes)
			{
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < 8; i++)
				{
					sb.append(sc.next());
				}
				n.setChar((char)(Integer.parseInt(sb.toString(), 2)));
			}
			Node check = root_;
			while(sc.hasNext())
			{
				int x = Integer.valueOf(sc.next());
				if(x == 1)
					check = check.getLeft();
				else if (x == 0)
					check = check.getRight();
				if(check.isLeaf())
				{
					s.append(check.c_);
					check = root_;
				}
			}
		}
		
		public Map<Character, String> getCodes()
		{
			return huffmanEncodings;
		}
		
		public Node getRoot()
		{
			return root_;
		}
		
		public void buildHuffmanCodes(LinkedList<Node> l)
		{
			for(Node n : l)
			{
				Node check = n;
				Character c = n.getC();
				huffmanEncodings.put(c, "");
				while(check.getParent() != null)
				{
					Node s = check.getParent();
					if(s.getLeft() == check)
						huffmanEncodings.put(c, 1 + huffmanEncodings.get(c));
					else if(s.getRight() == check)
						huffmanEncodings.put(c, 0 + huffmanEncodings.get(c));
					check = s;
				}
			}
		}
		
		public String dump()//for visualization purposes
		{
			StringBuilder sb = new StringBuilder();
			dumpHelper("", sb, true, root_);
			return sb.toString();
		}
		
		private void dumpHelper(String prefix, StringBuilder sb, boolean isTail, Node n)
		{
			sb.append(prefix + (isTail ? "└── " : "├── ") + n + "\n");
			if(n.getLeft() != null)
			{
				dumpHelper(prefix + (isTail ? "    " : "│   "), sb, false, n.getLeft());
			}
			if(n.getRight() != null)
			{
				dumpHelper(prefix + (isTail ? "    " : "│   "), sb, true, n.getRight());
			}
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			StringBuilder sa = new StringBuilder();
			toStringHelper(sb, sa, root_);
			return sb.toString() + sa.toString();
		}
		
		private void toStringHelper(StringBuilder sb, StringBuilder sa, Node n)
		{
			if(n == null)
			{
				sb.append(0);
				return;
			}
			if(n.getC() != null)
			{
				String stringByteRepresentation = Integer.toString((int)n.getC(), 2);
				while(stringByteRepresentation.length() < BYTE_LENGTH)
					stringByteRepresentation = 0 + stringByteRepresentation;
				sa.append(stringByteRepresentation);
			}
			if(n.getLeft() != null)
				sb.append(1);
			toStringHelper(sb, sa, n.getLeft());
			if(n.getRight() != null)
				sb.append(1);
			toStringHelper(sb, sa, n.getRight());
		}
		
		public class Node implements Comparable<Node>
		{
			Character c_;
			private int freq_;
			private Node parent, left, right;
			
			public Node(char c, int freq)
			{
				c_ = c;
				freq_ = freq;
				left = null;
				right = null;
				parent = null;
			}
			
			public Node(Node a, Node b)
			{
				freq_ = a.freq_ + b.freq_;
				setLeftChild(a);
				setRightChild(b);
				c_ = null;
				a.setParent(this);
				b.setParent(this);
			}
			
			public Node(char c, Node p)
			{
				c_ = c;
				parent = p;
			}
			
			public Node(char c)
			{
				c_ = c;
			}
			
			public Node()
			{
				
			}
			
			public void setChar(Character c)
			{
				c_ = c;
			}
			
			public String toString()
			{
				return (this.getC() == null ? "" : this.getC() + ",") + this.freq_;
			}
			
			private void setParent(Node n)
			{
				parent = n;
			}
			
			private void setRightChild(Node n)
			{
				right = n;
			}
			
			private void setLeftChild(Node n)
			{
				left = n;
			}
			
			public Node getLeft()
			{
				return left;
			}
			
			public Node getRight()
			{
				return right;
			}
			
			public Node getParent()
			{
				return parent;
			}
			
			public int compareTo(Node n)
			{
				return Integer.compare(this.freq_, n.freq_);
			}
			
			public boolean isLeaf()
			{
				return getLeft() == null && getRight() == null;
			}
			
			public Character getC()
			{
				return c_;
			}
			
		}
	}
}
