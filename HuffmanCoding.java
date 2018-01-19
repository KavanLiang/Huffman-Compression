import java.util.*;
import java.io.*;
import java.nio.file.Files;

/**
 * A simple huffman coding application
 * 
 * @BYTE_LENGTH: the length of a byte
 */
public class HuffmanCoding
{
    final private static int BYTE_LENGTH = 8;

    /**
     * The main compression method
     * @param input: the directory of the input to be compressed
     * @param output: the directory of the output file
     */
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

    /**
     * Converts a binary String to bytes
     * @param s: a binary String
     * @return: a byte array that corresponds to the binary String
     */
    private static byte[] binStringToByte(String s){
        byte[] bytes = new byte[s.length()/BYTE_LENGTH];
        for(int i = 0; i < bytes.length; i++)
        {
            Integer byteVal = Integer.parseInt(s.substring(i * BYTE_LENGTH, i * BYTE_LENGTH + BYTE_LENGTH), 2);
            bytes[i] = byteVal.byteValue();
        }
        return bytes;
    }

    /**
     * Main decompression method
     * @param input: directory of the input file to be decompressed
     * @param output: directory for the output file
     */
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

    /**
     * Reads a binary string from a file
     * @param f: the File to be read
     * @return: a binary String
     */
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

    /**
     * Reads a given file and returns a string representation of the contents of that file
     * @param f: the file to be read
     * @return: A string representation of the contents of <f>
     */
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

    /**
     * A frequency map of characters
     * @param characters: the array of characters to have their frequencies mapped
     * @return: a frequency map for the given imput, in which the keys are the characters, and
     *  each character maps to an integer representing how many times they appear in the array.
     */
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

    /**
     * A huffman heap to be used for compression
     * 
     * @root_ the root of this heap
     * @huffmanEncodings: the "compression" key; each Character maps to a String
     */
    public static class HuffmanHeap
    {
        private Node root_;
        private Map<Character, String> huffmanEncodings;

        /**
         * Construct a new Huffman heap given a Frequency Map
         * @param cFreq: an input frequency map
         */
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
        
        /**
         * Decodes/Decompresses a string with this huffman tree.
         * @param s: the string to be decoded
         * @return: the uncompressed/decoded string
         */
        public static String decode(String s)
        {
            s = s.substring(s.indexOf("1"));
            StringBuilder sb = new StringBuilder();
            HuffmanHeap h = new HuffmanHeap(new Scanner(s), sb);
            return sb.toString();
        }

        /**
         * Creates an instance of HuffmanHeap for decompression.
         * @param sc: the scanner connected to an input file
         * @param s: the Stringbuilder to append decompressed output to
         */
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

        /**
         * @return: the mappings used for compression
         */
        public Map<Character, String> getCodes()
        {
            return huffmanEncodings;
        }

        /**
         * @return the root of this huffman tree
         */
        public Node getRoot()
        {
            return root_;
        }

        /**
         * Genereates HuffmanEncodings based on the structure of this tree
         * @param l:
         */
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

        /**
         * A string representation of this Heap for visualization purposes
         * @return: a string representation of this Heap
         */
        public String dump()
        {
            StringBuilder sb = new StringBuilder();
            dumpHelper("", sb, true, root_);
            return sb.toString();
        }

        /**
         * A recursive helper for the "dump" function
         * @param prefix: the previous Node
         * @param sb: The stringbuilder to append to
         * @param isTail: whether or not the previous prefix was a tail
         * @param n: a Node to be "written"
         */
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

        /**
         * A Node in the Huffman Heap; pretty self explanitory
         */
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
